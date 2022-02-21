from django.db import models

# Create your models here.


class Source(models.Model):
    created = models.DateTimeField(auto_now_add=True)
    repository = models.CharField(max_length=350, default= None, null=True)
    fileName = models.CharField(max_length=500)
    branch = models.CharField(max_length=150, default=None, null=True)
    wholeRepositoryAnalyzed = models.BooleanField(default=False)


class Metrics(models.Model):
    metricSource = models.ForeignKey(Source, on_delete=models.CASCADE)
    CommitTime = models.DateTimeField(default=None, null=True)
    CommitMessage = models.TextField(default=None, null=True)
    AuthorEmail = models.EmailField(default=None, null=True)
    AuthorName = models.TextField(max_length=150, default=None, null=True)
    CommiterEmail = models.EmailField(default=None, null=True)
    CommitterName = models.TextField(max_length=150, default=None, null=True)
    Size = models.PositiveIntegerField(default = None, null=True)
    ReadingError = models.TextField(default=False)
    absoluteBreadth = models.PositiveBigIntegerField(default = None, null=True)
    absoluteDepth = models.PositiveBigIntegerField(default = None, null=True)
    absoluteLeafCardinality = models.PositiveIntegerField(default = None, null=True)
    annotationAssertionsAxioms = models.PositiveBigIntegerField(default = None, null=True)
    annotationPropertyRangeaxioms = models.PositiveBigIntegerField(default = None, null=True)
    annotationPropertyDomainAxioms = models.PositiveBigIntegerField(default = None, null=True)
    anonymousClasses = models.PositiveBigIntegerField(default = None, null=True)
    asymmetricObjectPropertyAxioms = models.PositiveBigIntegerField(default = None, null=True)
    axioms = models.PositiveBigIntegerField(default = None, null=True)
    classAnnotations = models.PositiveBigIntegerField(default = None, null=True)
    classAssertionaxioms = models.PositiveBigIntegerField(default = None, null=True)
    classes = models.PositiveBigIntegerField(default = None, null=True)
    classesThatShareARelation = models.PositiveBigIntegerField(default = None, null=True)
    classesWithIndividuals = models.PositiveBigIntegerField(default = None, null=True)
    classesWithMultipleInheritance = models.PositiveBigIntegerField(default = None, null=True)
    classesWithObjectProperties = models.PositiveBigIntegerField(default = None, null=True)
    classesWithSubClasses = models.PositiveBigIntegerField(default = None, null=True)
    circleHierachies = models.SmallIntegerField(default = None, null=True)
    dataProperties = models.PositiveBigIntegerField(default = None, null=True)
    dataPropertyAnnotations = models.PositiveBigIntegerField(default = None, null=True)
    dataPropertyAssertionAxioms = models.PositiveBigIntegerField(default = None, null=True)
    dataPropertyDomainAxioms = models.PositiveBigIntegerField(default = None, null=True)
    dataPropertyRangeAxioms = models.PositiveBigIntegerField(default = None, null=True)
    datatypeAnnotations  = models.PositiveBigIntegerField(default = None, null=True)
    differentIndividualsAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    disjointClassesAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    disjointDataPropertiesAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    disjointObjectPropertyAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    dlExpressivity  = models.TextField(max_length=20, default = None, null=True)
    equivalentClassesAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    equivalentDataPropertiesAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    equivalentObjectPropertyAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    functionalDataPropertyAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    functionalObjectPropertyAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    gciCount  = models.PositiveBigIntegerField(default = None, null=True)
    generalAnnotationAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    hiddengciCount  = models.PositiveBigIntegerField(default = None, null=True)
    individuals  = models.PositiveBigIntegerField(default = None, null=True)
    inverseFunctionalObjectPropertyAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    inverseObjectPropertyAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    irreflexiveObjectPropertyAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    irreflexiveObjectPropertyAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    logicalAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    maxFanoutnessOfLeafClasses  = models.PositiveBigIntegerField(default = None, null=True)
    maximalBreadth  = models.PositiveBigIntegerField(default = None, null=True)
    maximalDepth  = models.PositiveBigIntegerField(default = None, null=True)
    maxSubClassesOfAClass  = models.PositiveBigIntegerField(default = None, null=True)
    maxSuperClassesOfAClass  = models.PositiveBigIntegerField(default = None, null=True)
    minimumBreath  = models.PositiveBigIntegerField(default = None, null=True)
    minimumDepth  = models.PositiveBigIntegerField(default = None, null=True)
    negativeDataPropertyAssertionAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    negativeObjectPropertyAssertionAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    numberOfConnectedGraphs  = models.PositiveBigIntegerField(default = None, null=True)
    objectProperties  = models.PositiveBigIntegerField(default = None, null=True)
    objectPropertiesOnClasses  = models.PositiveBigIntegerField(default = None, null=True)
    objectPropertiesOnClasses  = models.PositiveBigIntegerField(default = None, null=True)
    objectPropertyAssertionaxioms  = models.PositiveBigIntegerField(default = None, null=True)
    objectPropertyDomainAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    objectPropertyRangeAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    pathsToLeafClasses  = models.PositiveBigIntegerField(default = None, null=True)
    reflexiveObjectPropertyAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    rootClasses  = models.PositiveBigIntegerField(default = None, null=True)
    sameIndividualsAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    subDataPropertyOfAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    subObjectPropertyOfAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    subPropertyChainOfAxioms  = models.PositiveBigIntegerField(default = None, null=True) 
    superClasses  = models.PositiveBigIntegerField(default = None, null=True)
    superClassesOfClassesWithMultipleInheritance  = models.PositiveBigIntegerField(default = None, null=True)
    subClassOfAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    symmetricObjectPropertyAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    transitiveObjectPropertyAxioms  = models.PositiveBigIntegerField(default = None, null=True)
    superClassesOfLeafClasses  = models.PositiveBigIntegerField(default = None, null=True)
    objectPropertyAnnotations  = models.PositiveBigIntegerField(default = None, null=True)
    reasonerActive = models.BooleanField(default=False)
    inconsistentClasses = models.PositiveIntegerField(default=0)
    consistencyCheckSuccessful = models.BooleanField(default=False)
  

class ClassMetrics(models.Model):
    metric = models.ForeignKey(Metrics, on_delete=models.CASCADE)
    Classiri = models.CharField(default = None, null=True, max_length=400)
    Classconnectivity = models.PositiveSmallIntegerField(default=0)
    Classfulness = models.FloatField(default = 0)
    Classimportance = models.FloatField(default = 0)
    Classinheritancerichness = models.FloatField(default = 0)
    Classreadability = models.PositiveSmallIntegerField(default = 0)
    Classrelationshiprichness = models.PositiveSmallIntegerField(default = 0)
    Classchildrencount = models.PositiveSmallIntegerField(default = 0)
    Classinstancescount = models.PositiveSmallIntegerField(default = 0)
    Classpropertiescount = models.PositiveSmallIntegerField(default = 0)