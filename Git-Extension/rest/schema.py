from logging import Filter
import graphene
from graphene import relay
import rest.metricOntologyHandler
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

    repositories = DjangoFilterConnectionField(RepositoryNode)



schema = graphene.Schema(query=Query)
