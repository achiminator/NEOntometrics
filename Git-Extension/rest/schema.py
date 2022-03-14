from logging import Filter
import queue
import graphene
from graphene import relay
import rest.metricOntologyHandler, rest.queueInformation
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
    tasFinished = graphene.Boolean()
    taskStarted = graphene.Boolean()
    queuePosition = graphene.Int()
    url = graphene.String()
    repository = graphene.String()
    service = graphene.String()
    fileName = graphene.String()

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
    queueInformation = graphene.Field(QueueInformationNode, url=graphene.String(required=True))
    repositories = DjangoFilterConnectionField(RepositoryNode)
    
    def resolve_queueInformation(root, info, url):
        """Gathers the information on the queue of the file.

        Args:
            root (_type_): _description_
            info (_type_): _description_
            url (graphene.String(required=True)): The URL to the required OntologyFile 
        """
        queueInfo = queueInfo = rest.queueInformation.QueueInformation(url)
        return QueueInformationNode(
            tasFinished = queueInfo.taskFinished,
            taskStarted = queueInfo.taskStarted,
            queuePosition = queueInfo.queuePosition,
            url = queueInfo.url.url if queueInfo.url else None,
            repository = queueInfo.url.repository if queueInfo.url else None,
            service = queueInfo.url.service if queueInfo.url else None,
            fileName = queueInfo.url.file if queueInfo.url else None,
        )
        
        

schema = graphene.Schema(query=Query)
