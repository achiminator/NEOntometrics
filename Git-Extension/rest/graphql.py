from pydoc import describe
from django.conf import settings
import graphene, django_filters
from graphene import relay
from rest.CalculationManager import CalculationManager
from rest.CalculationHelper import GitUrlParser
import rest.metricOntologyHandler, rest.queueInformation
from graphene_django import DjangoObjectType
from .models import Repository, OntologyFile, Commit
from django.db.models import Count, F
import graphene_django.filter as filter


class RepositoryFilter(django_filters.FilterSet):
    """A custom django-repository filter. Filters the Repository for the filenames and Repository names that are part of a git-url

    Args:
        django_filters (_type_): 

    Returns:
        django_filter.queryset.filter: A django-filter set for filtering django Models
"""
    repository = django_filters.CharFilter(field_name="repository", method="repoFilter", required=True)
    fileName = django_filters.CharFilter(field_name="fileName", method="fileFilter")
    

    def repoFilter(self, queryset, name, value):
        urlObject = GitUrlParser()
        urlObject.parse(value)
        return queryset.filter(repository = urlObject.repository)
        
    def fileFilter(self, queryset, name, value):
        urlObject = GitUrlParser()
        urlObject.parse(value)
        return queryset.filter(fileName = urlObject.file)

class QueueInformationNode(graphene.ObjectType):
    """Gets information on the status of an ontology in the system, meaning if it already calcualted,
    in the queue (including its position in the queue) or not in the system at all.
    """
    urlInSystem = graphene.Boolean(description="Is False, if the repository is not known to the system at all.")
    taskFinished = graphene.Boolean(description="Is True if the repository is fully analyzed and stored in the database")
    performsUpdate = graphene.Boolean(description="Whether an object is already finished with analyzing and performing an update")
    taskStarted = graphene.Boolean(description="Is True if the calculation task has already started")
    queuePosition = graphene.Int(description="The current position in the calculation backlog. Gets calculated if it reaches 0")
    analyzedCommits = graphene.Int(description="Information on the current state of the repository calculation. The number of analyzed commits for a given repository file. If is not yet started, the value remains Null.")
    totalCommits = graphene.Int(description="Information on the current state of the repository calculation. The number of commits that the repository has. If is not yet started, the value remains Null.")
    analyzedOntologies = graphene.Int(description="Information on the current state of the repository calculation. The number of already analyzed ontologies in the repository. If is not yet started, the value remains Null.")
    ontologyFileOnly = graphene.Boolean(description="Wether the given URL ONLY contains an ontology file, without a git-repository.")
    analysableOntologies = graphene.Int(description="Information on the current state of the repository calculation. the number of ontologies that are getting analyzed in the repository. If is not yet started, the value remains Null.")
    url = graphene.String(description="The requested URL.")
    repository = graphene.String(description="The requested repository")
    service = graphene.String(description="The underlying service on where the repository/ontology can be found.")
    fileName = graphene.String()
    error = graphene.Boolean(description="Whether an error occured during repository/ontology analysis.")
    errorMessage = graphene.String(description="The detailed Error-Message for a given Error.")

class RepositoryInformationNode(graphene.ObjectType):
    """A brief information on the repositories and their commits already analzed.
    """
    repository = graphene.String(description="The repository (including all its files)")
    analyzedOntologyCommits = graphene.Int(description="The number of file-commits analyzed in this repository. Summed up all analyzed commits for every file.")
    ontologyFiles = graphene.Int(description="The number of ontology files of a repository")

class CommitNode(DjangoObjectType):
    """The calculated Metrics for a ontology file. It is nested in a repository node.

    """
    # This part publishes the calculated Metrics (see the function in the model) to the GraphQL-Endpoint
    metrics = rest.metricOntologyHandler.ontologyhandler.getMetricDict()
    pk = graphene.Int(source="pk")
    for element in metrics:
        if(len(element["metricCalculation"]) > 0):
            description = element["metricDefinition"] if len(element["metricDefinition"]) > 0 else element["metricDescription"]
            description = description.replace("\n", "")
            metricName = element["metric"].replace(" ", "_").replace(
                "-", "").replace("(", "").replace(")", "")
            # If there is a division going on in the calculation, the resulting values will be
            # a float. (As all the originating values are integers, there is no other possibility)
            if("/" in element["metricCalculation"]):
                exec("{0} = graphene.Float(source=\"{0}\", description=\"{1}\")".format(metricName, description))
            # Unfortunately, for one of the values we need to make an exception. DLExpressivity is a string,
            # (the only one).
            elif("dlExpressivity" in element["metricCalculation"]):
                exec("{0} = graphene.String(source=\"{0}\", description=\"{1}\")".format(metricName, description))
            else:
                exec("{0} = graphene.Int(source=\"{0}\", description=\"{1}\")".format(metricName, description)) 

    class Meta:
        model = Commit
        filter_fields = {"reasonerActive": ["exact"]}
        fields = "__all__"
        # exclude = ["id", "commit"]
        
        interfaces = (relay.Node, )


class OntologyNode(DjangoObjectType):
    """Contains the basic information on the ontology file.
    """
    class Meta:
        model = OntologyFile
        exclude = ["repository"]
        filter_fields = ["fileName"]
        interfaces =(relay.Node,)

class RepositoryNode(DjangoObjectType):
    """The root node of the calculated files. Contains the basic information like the fileName, 
    repositoryname, date of calculation, etc.
    """
    ontologies = OntologyNode
    class Meta:
        model = Repository
        exclude =["gitRepositoryFile"]
        filter_fields = ["repository"]
        interfaces = (relay.Node, )


class QueueMutation(graphene.Mutation):
    """Allows to add an element to the queue (if it is not in there, already).
    """
    class Arguments:
        url = graphene.String(required=True, description="The URL with the repository or ontology that shall be analyzed or where information is needed on.")
        reasoner = graphene.Boolean(required=True, description="States whether the repository calculation shall use the Repository. Be aware that the reasoner can cause performance issues. The size is capped to "+ str(round(settings.REASONINGLIMIT/1024)) +" KB")
        update = graphene.Boolean(required=False, description="Triggers the update of a repository.")


    error = graphene.Boolean(description="True if altering the queue was not successful.")
    errorMessage = graphene.String(description="Detailed information on an unsuccessful queue mutation.")
    queueInformation = graphene.Field(QueueInformationNode)

    @classmethod
    def mutate(self, root, info, url, reasoner, update):
        queueInfo = rest.queueInformation.QueueInformation(url)
        if not(hasattr(self, "update")):
            self.update= False

        if queueInfo.urlInSystem and update == False:
            self.error = True
            self.errorMessage = "URL is already in the Queue"
        elif queueInfo.url.validResource == False:
            self.error = True
            self.errorMessage = "Under the given URL, there is no available and_or accessible resource"
        else:
            urlObject = GitUrlParser()
            urlObject.parse(url)
            queueInfo = rest.queueInformation.QueueInformation(urlObject.url)
            if queueInfo.performsUpdate == True:
                self.error = False
                self.errorMessage = "URL is already in the Queue"
            else:
                CalculationManager.putInQueue(
                    url=urlObject,  reasonerSelected=reasoner)
                queueInfo = rest.queueInformation.QueueInformation(urlObject.url)
                self.error = False
                self.errorMessage = ""

        queueInfo = QueueInformationNode(
            urlInSystem=queueInfo.urlInSystem,
            performsUpdate=queueInfo.performsUpdate,
            taskFinished=queueInfo.taskFinished,
            taskStarted=queueInfo.taskStarted,
            queuePosition=queueInfo.queuePosition,
            analyzedCommits=queueInfo.analyzedCommits,
            totalCommits=queueInfo.totalCommits,
            analyzedOntologies=queueInfo.analyzedOntologies,
            analysableOntologies=queueInfo.analysableOntologies,
            url=queueInfo.url.url,
            repository=queueInfo.url.repository,
            service=queueInfo.url.service,
            ontologyFileOnly = queueInfo.ontologyFileOnly,
            fileName=queueInfo.url.file,
            error=queueInfo.error,
            errorMessage=queueInfo.errorMessage
        )

        return QueueMutation(queueInformation=queueInfo, error=self.error, errorMessage=self.errorMessage)


class Mutation(graphene.ObjectType):
    """Alter the queue for calculation.
    """
    update_queueInfo = QueueMutation.Field()


class Query(graphene.ObjectType):
    """Query Metric Information
    """
    queueInformation = graphene.Field(
        QueueInformationNode, description="Prints out the current status of an object in system. Useful to determine whether the repository is already in the queue, in the database or not known at all.", url=graphene.String(required=True))
    getOntologyFile = filter.DjangoFilterConnectionField(
        OntologyNode, description="The Endpoint for querying a SINGLE ontology. If you're interested in a single ontology within a git-Repository, please use the Repository Node.") 
    getRepository = filter.DjangoFilterConnectionField(
        RepositoryNode, description="The entrypoint for gathering metric Data. Contains the main information of an ontology file, the calculated metrics are nested in a \"metrics\" field.",
        filterset_class=RepositoryFilter)
    repositoriesInformation = graphene.List(RepositoryInformationNode, description="Prints out the repositories already calculated in the database.")

    def resolve_repositoriesInformation(root, info):
        v = Commit.objects.select_related("metricSource__repository")
        v = v.values(allAnalyzed = F("metricSource__repository__wholeRepositoryAnalyzed"), repository = F("metricSource__repository__repository")).annotate(ontologyFiles=Count("metricSource__fileName",distinct=True), analyzedOntologyCommits=Count("CommitID")).filter(repository__isnull = False, allAnalyzed=True)

        return v

    def resolve_queueInformation(root, info, url):
        """Gathers the information on the queue of the file.
        Args:
            root (_type_): _description_
            info (_type_): Gathers the information on the queue of the file.
            url (graphene.String(required=True)): The URL to the required OntologyFile 
        """
        queueInfo = rest.queueInformation.QueueInformation(url)
        return QueueInformationNode(
            urlInSystem=queueInfo.urlInSystem,
            taskFinished=queueInfo.taskFinished,
            taskStarted=queueInfo.taskStarted,
            queuePosition=queueInfo.queuePosition,
            analyzedCommits=queueInfo.analyzedCommits,
            totalCommits=queueInfo.totalCommits,
            performsUpdate=queueInfo.performsUpdate,
            analyzedOntologies=queueInfo.analyzedOntologies,
            analysableOntologies=queueInfo.analysableOntologies,
            ontologyFileOnly=queueInfo.ontologyFileOnly,
            validUrl=queueInfo.url.validResource,
            url=queueInfo.url.url,
            repository=queueInfo.url.repository,
            service=queueInfo.url.service,
            fileName=queueInfo.url.file,
            error=queueInfo.error,
            errorMessage=queueInfo.errorMessage
        )


schema = graphene.Schema(query=Query, mutation=Mutation, auto_camelcase=False)
