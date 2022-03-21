from fileinput import filename
from logging import Filter
import queue
from urllib.parse import urlparse
import graphene, django_filters
from graphene import relay
from rest.CalculationManager import CalculationManager
from rest.GitHelper import GitHelper, GitUrlParser
import rest.metricOntologyHandler
import rest.queueInformation
from graphene_django import DjangoObjectType, DjangoListField
from .models import Source, Metrics, ClassMetrics
from django.db.models import Prefetch
import graphene_django.filter as filter


class MetricsNode(DjangoObjectType):

    # This part publishes the calculated Metrics (see the function in the model) to the GraphQL-Endpoint
    metrics = rest.metricOntologyHandler.ontologyhandler.getMetricDict()
    for element in metrics:
        if(len(element["metricCalculation"]) > 0):
            metricName = element["metric"].replace(" ", "_").replace(
                "-", "").replace("(", "").replace(")", "")
            # If there is a division going on in the calculation, the resulting values will be 
            # a float. (As all the originating values are integers, there is no other possibility)
            if("/" in element["metricCalculation"]):
                exec("{0} = graphene.Float(source=\"{0}\")".format(metricName))
            # Unfortunately, for one of the values we need to make an exception. DLExpressivity is a string, 
            # (the only one).
            elif("dlExpressivity" in element["metricCalculation"]):
                exec("{0} = graphene.String(source=\"{0}\")".format(metricName))
            else:
                exec("{0} = graphene.Int(source=\"{0}\")".format(metricName))

    class Meta:
        model = Metrics
        filter_fields = {"reasonerActive": ["exact"]}
        #fields = "__all__"
        exclude = ["id", "metrics"]

        interfaces = (relay.Node, )


class QueueInformationNode(graphene.ObjectType):
    urlInSystem = graphene.Boolean()
    taskFinished = graphene.Boolean()
    taskStarted = graphene.Boolean()
    queuePosition = graphene.Int()
    analyzedCommits = graphene.Int()
    commitsForThisOntology = graphene.Int()
    analyzedOntologies = graphene.Int()
    analysableOntologies = graphene.Int()
    url = graphene.String()
    repository = graphene.String()
    service = graphene.String()
    fileName = graphene.String()
    error = graphene.Boolean()
    errorMessage = graphene.String()


class RepositoryNode(DjangoObjectType):
    class Meta:
        model = Source
   #     exclude =["id"]
        filter_fields = ["repository", "fileName"]
        interfaces = (relay.Node, )

class QueueMutation(graphene.Mutation):
    class Arguments:
        url = graphene.String(required=True)
        reasoner = graphene.Boolean(required=True)
    error = graphene.Boolean()
    errorMessage = graphene.String()
    queueInfo = graphene.Field(QueueInformationNode)

    @classmethod
    def mutate(self, root, info, url, reasoner):
        queueInfo = rest.queueInformation.QueueInformation(url)
        if queueInfo.urlInSystem:
            self.error = True
            self.errorMessage = "URL is already in System"
        else:
            urlObject = GitUrlParser()
            urlObject.parse(url)
            CalculationManager.putInQueue(
                url=urlObject,  reasonerSelected=reasoner)
            queueInfo = rest.queueInformation.QueueInformation(urlObject.url)
        queueInfo = QueueInformationNode(
            urlInSystem=queueInfo.urlInSystem,
            taskFinished=queueInfo.taskFinished,
            taskStarted=queueInfo.taskStarted,
            queuePosition=queueInfo.queuePosition,
            analyzedCommits = queueInfo.analyzedCommits,
            commitsForThisOntology = queueInfo.commitsForThisOntology,
            analyzedOntologies = queueInfo.analyzedOntologies,
            analysableOntologies = queueInfo.analysableOntologies,
            url=queueInfo.url.url,
            repository=queueInfo.url.repository,
            service=queueInfo.url.service,
            fileName=queueInfo.url.file,
            error=queueInfo.error,
            errorMessage=queueInfo.errorMessage
        )

        return QueueMutation(queueInfo=queueInfo, error=self.error, errorMessage=self.errorMessage)


class Mutation(graphene.ObjectType):
    update_queueInfo = QueueMutation.Field()

class RepositoryFilter(django_filters.FilterSet):
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

class Query(graphene.ObjectType):
    """Responsible for the graphQL-Metric Endoint
    """
    queueInformation = graphene.Field(
        QueueInformationNode, url=graphene.String(required=True))
    repositories = filter.DjangoFilterConnectionField(RepositoryNode, filterset_class=RepositoryFilter)

    def resolve_queueInformation(root, info, url):
        """Gathers the information on the queue of the file.

        Args:
            root (_type_): _description_
            info (_type_): _description_
            url (graphene.String(required=True)): The URL to the required OntologyFile 
        """
        queueInfo = rest.queueInformation.QueueInformation(url)
        return QueueInformationNode(
            urlInSystem=queueInfo.urlInSystem,
            taskFinished=queueInfo.taskFinished,
            taskStarted=queueInfo.taskStarted,
            queuePosition=queueInfo.queuePosition,
            analyzedCommits = queueInfo.analyzedCommits,
            commitsForThisOntology = queueInfo.commitsForThisOntology,
            analyzedOntologies = queueInfo.analyzedOntologies,
            analysableOntologies = queueInfo.analysableOntologies,
            url=queueInfo.url.url,
            repository=queueInfo.url.repository,
            service=queueInfo.url.service,
            fileName=queueInfo.url.file,
            error=queueInfo.error,
            errorMessage=queueInfo.errorMessage
        )


schema = graphene.Schema(query=Query, mutation=Mutation, auto_camelcase=False)
