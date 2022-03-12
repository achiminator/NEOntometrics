import graphene
from graphene_django import DjangoObjectType, DjangoListField
from .models import Source, Metrics, ClassMetrics


class MetricsType(DjangoObjectType):
    class Meta:
        model = Metrics
        fields = "__all__"


class SourceType(DjangoObjectType):
    class Meta:
        metricSource = MetricsType
        model = Source

        #filter_fields = ['repository', 'fileName']
        # interfaces = (graphene.relay.Node, )
        fields = ('created',
                  'repository',
                  'fileName',
                  'branch',
                  'wholeRepositoryAnalyzed',
                  'metricSource'
                  )


class Query(graphene.ObjectType):
    # source = graphene.List(SourceType)
    source = graphene.List(
        SourceType, repository=graphene.String(required=True))

    metrics = graphene.List(MetricsType)

    def resolve_source(root, info, **kwargs):
        # Querying a list
        repository = kwargs.get("repository")
        if(repository != None):
            return Source.objects.filter(repository=repository)
        else:
            return Source.objects.all()

    def resolve_metrics(root, info, **kwargs):
        # Querying a list
        return Metrics.objects.all()


schema = graphene.Schema(query=Query)
