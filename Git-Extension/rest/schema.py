from logging import Filter
import queue
from urllib.parse import urlparse
import graphene
from graphene import relay
from rest.CalculationManager import CalculationManager
from rest.GitHelper import GitHelper, GitUrlParser
import rest.metricOntologyHandler
import rest.queueInformation
from graphene_django import DjangoObjectType, DjangoListField
from .models import Source, Metrics, ClassMetrics
from django.db.models import Prefetch
from graphene_django.filter import DjangoFilterConnectionField


class MetricsNode(DjangoObjectType):

    # This part publishes the calculated Metrics (see the function in the model) to the GraphQL-Endpoint
    metrics = rest.metricOntologyHandler.ontologyhandler.getMetricDict()
    for element in metrics:
        if(len(element["metricCalculation"]) > 0):
            metricName = element["metric"].replace(" ", "_").replace(
                "-", "").replace("(", "").replace(")", "")
            exec("{0} = graphene.Float(source=\"{0}\")".format(metricName))

    class Meta:
        model = Metrics
        filter_fields = {"reasonerActive": ["exact"]}
        #fields = "__all__"
        exclude = ["id", "metricSource"]

        interfaces = (relay.Node, )


class QueueInformationNode(graphene.ObjectType):
    urlInSystem = graphene.Boolean()
    tasFinished = graphene.Boolean()
    taskStarted = graphene.Boolean()
    queuePosition = graphene.Int()
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
        filter_fields = {"repository": ["icontains"],
                         "fileName": ["icontains"]}
        interfaces = (relay.Node, )


class Query(graphene.ObjectType):
    """Responsible for the graphQL-Metric Endoint
    """
    queueInformation = graphene.Field(
        QueueInformationNode, url=graphene.String(required=True))
    repositories = DjangoFilterConnectionField(RepositoryNode)

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
            tasFinished=queueInfo.taskFinished,
            taskStarted=queueInfo.taskStarted,
            queuePosition=queueInfo.queuePosition,
            url=queueInfo.url.url,
            repository=queueInfo.url.repository,
            service=queueInfo.url.service,
            fileName=queueInfo.url.file,
            error=queueInfo.error,
            errorMessage=queueInfo.errorMessage
        )


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
            tasFinished=queueInfo.taskFinished,
            taskStarted=queueInfo.taskStarted,
            queuePosition=queueInfo.queuePosition,
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


schema = graphene.Schema(query=Query, mutation=Mutation)
