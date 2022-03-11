import graphene
from graphene_django import DjangoObjectType, DjangoListField
from .models import Source, Metrics, ClassMetrics


class SourceType(DjangoObjectType):
    class Meta:
        model = Source
        fields = ('created',
                  'repository',
                  'fileName',
                  'branch',
                  'wholeRepositoryAnalyzed',
                  )



class MetricsType(DjangoObjectType):
    class Meta:
        model = Metrics
        fields = (
        'metricSource',
        'CommitTime',
        'CommitMessage',
        'AuthorEmail',
        'AuthorName',
        'CommiterEmail',
        'CommitterName',
        'Size',
        'ReadingError',
        'absoluteBreadth',
        'absoluteDepth',
        'absoluteLeafCardinality',
        'absoluteSiblingCardinality',
        'annotationAssertionsAxioms',
        'annotationPropertyRangeaxioms',
        'annotationPropertyDomainAxioms',
        'anonymousClasses',
        'asymmetricObjectPropertyAxioms',
        'axioms',
        'classAnnotations',
        'classAssertionaxioms',
        'classes',
        'classesThatShareARelation',
        'classesWithIndividuals',
        'classesWithMoreThanOneAncestor',
        'classesWithMultipleInheritance',
        'classesWithObjectProperties',
        'classesWithSubClasses',
        'circleHierachies',
        'dataProperties',
        'dataPropertyAnnotations',
        'dataPropertyAssertionAxioms',
        'dataPropertyDomainAxioms',
        'dataPropertyRangeAxioms',
        'datatypeAnnotations',
        'differentIndividualsAxioms',
        'disjointClassesAxioms',
        'disjointDataPropertiesAxioms',
        'disjointObjectPropertyAxioms',
        'DLExpressivity',
        'equivalentClassesAxioms',
        'equivalentDataPropertiesAxioms',
        'equivalentObjectPropertyAxioms',
        'functionalDataPropertyAxioms',
        'functionalObjectPropertyAxioms',
        'GCICount',
        'generalAnnotationAxioms',
        'HiddenGCICount',
        'individuals',
        'inverseFunctionalObjectPropertyAxioms',
        'inverseObjectPropertyAxioms',
        'inverseRelationsRatio',
        'individualAnnotation',
        'irreflexiveObjectPropertyAxioms',
        'irreflexiveObjectPropertyAxioms',
        'logicalAxioms',
        'maxFanoutnessOfLeafClasses',
        'maximalBreadth',
        'maximalDepth',
        'maxSubClassesOfAClass',
        'maxSuperClassesOfAClass',
        'minimumBreath',
        'minimumDepth',
        'negativeDataPropertyAssertionAxioms',
        'negativeObjectPropertyAssertionAxioms',
        'numberOfConnectedGraphs',
        'objectProperties',
        'objectPropertiesOnClasses',
        'objectPropertiesOnClasses',
        'objectPropertyAssertionaxioms',
        'objectPropertyDomainAxioms',
        'objectPropertyRangeAxioms',
        'pathsToLeafClasses',
        'reflexiveObjectPropertyAxioms',
        'rootClasses',
        'sameIndividualsAxioms',
        'subDataPropertyOfAxioms',
        'subObjectPropertyOfAxioms',
        'subPropertyChainOfAxioms',
        'sumOfDirectAncestorsWithMoreThanOneDirectAncestor',
        'superClasses',
        'superClassesOfClassesWithMultipleInheritance',
        'subClassOfAxioms',
        'subClassuperClassesOfLeafClassessOfAxioms',
        'symmetricObjectPropertyAxioms',
        'transitiveObjectPropertyAxioms',
        'superClassesOfLeafClasses',
        'objectPropertyAnnotations',
        'reasonerActive',
        'consistencyCheckSuccessful',
        )


class ClassMetricsType(DjangoObjectType):
    class Meta:
        model = ClassMetrics
        fields = (
            'metric',
            'Classiri',
            'Classconnectivity',
            'Classfulness',
            'Classimportance',
            'Classinheritancerichness',
            'Classreadability',
            'Classrelationshiprichness',
            'Classchildrencount',
            'Classinstancescount',
            'Classpropertiescount',
        )


class Query(graphene.ObjectType):
    source = graphene.List(SourceType)
    metrics = graphene.List(MetricsType)
    classMetrics = graphene.List(ClassMetricsType)


    def resolve_source(root, info, **kwargs):
        # Querying a list
        return Source.objects.all()

    def resolve_metrics(root, info, **kwargs):
        # Querying a list
        return Metrics.objects.all()

    def resolve_classMetrics(root, info, **kwargs):
        # Querying a list
        return ClassMetrics.objects.all()


schema = graphene.Schema(query=Query)