import 'package:graphql/client.dart';
import 'package:gql/language.dart';
import 'package:neonto_frontend/calculationsettings.dart';
import 'package:neonto_frontend/settings.dart';
import 'metric_data.dart';

class GraphQLHandler {
  final _graphQlClient = GraphQLClient(
      link: HttpLink("${Settings().apiUrl}/graphql"),
      cache: GraphQLCache(),
      alwaysRebroadcast: true);

  /// Gather information on the status of a repository or ontologyFile in the backend. Mainly, whether it is already calculated,
  /// already in the queue or if it is finished with its Calculation. the [url] it the input parameter pointing to
  /// the calculating repo/ontologyFile
  Future<QueryResult<dynamic>> queueFromAPI(String url) {
    var response = _graphQlClient.query(QueryOptions(
        fetchPolicy: FetchPolicy.networkOnly, document: parseString("""{
  queueInformation(url: "$url"){ 
        urlInSystem
        taskFinished
        taskStarted
        queuePosition
        performsUpdate
        repository
        fileName
        error
        errorMessage
        analyzedCommits
        totalCommits
        analyzedOntologies
        analysableOntologies
      }
    }""")));
    return response;
  }

  /// Puts data actively into the queue of the backend. Returns the current state of the task.
  Future<QueryResult<dynamic>> putInQueue(String url, bool reasoner,
      {bool update = false}) {
    var mutation = """mutation{
  update_queueInfo(
    reasoner: $reasoner
    url: "$url"
    update: $update
      ) {
    error
    errorMessage
    queueInformation {
        urlInSystem
        taskFinished
        taskStarted
        queuePosition
        repository
        fileName
        error
        errorMessage
    }
  }
}""";
    var response = _graphQlClient.mutate(MutationOptions(
        fetchPolicy: FetchPolicy.networkOnly, document: parseString(mutation)));
    return response;
  }

  /// Necessary for the "already Calculated"-button. Queries the available, already calculated repositories.
  Future<QueryResult<dynamic>> getRepositoryList() {
    var graphQLQuery = """
{
  repositoriesInformation {
    repository
    ontologyFiles
    analyzedOntologyCommits
  }
}""";
    var futureResonse = _graphQlClient.query(QueryOptions(
        fetchPolicy: FetchPolicy.networkOnly,
        document: parseString(graphQLQuery)));
    return futureResonse;
  }

  /// Takes the selection chips from the [CalculationEngine] class and converts it into items that
  /// allow filtering for the GraphQL engine.
  String selectedMetrics2GraphQLInsertion(
      Set<MetricExplorerItem> selectedElementsForCalculation) {
    String graphQlQueryAppender = "";
    for (var selectedItem in selectedElementsForCalculation) {
      String metricToAdd;
      if (selectedItem.implentationName != "") {
        metricToAdd = selectedItem.implentationName!;
      } else {
        metricToAdd = selectedItem.itemName
            .replaceAll(" ", "_")
            .replaceAll("-", "")
            .replaceAll("(", "")
            .replaceAll(")", "");
      }
      graphQlQueryAppender += "$metricToAdd\n";
    }
    return graphQlQueryAppender;
  }

  /// calls for the calculated ontology metrics to be displayed in the frontend. Requires the return value of [selectedMetrics2GraphQLInsertion].
  Future<QueryResult<dynamic>> getRepositoryMetricsFromAPI(
      String url, String graphQlQueryAppender) {
    var graphQlMetricQuery = """{
  getRepository(repository: "$url") {
    edges {
      node {
        repository
        ontologyfile_set {
          edges {
            node {
              id
              fileName
              branch
              commit {
                edges {
                  node {
                    pk
                    CommitTime
                    CommitID
                    CommitMessage
                    AuthorEmail
                    AuthorName
                    CommiterEmail
                    CommitterName
                    Size
                    ReadingError
                    $graphQlQueryAppender
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}""";
    var futureResonse = _graphQlClient.query(QueryOptions(
        fetchPolicy: FetchPolicy.networkOnly,
        document: parseString(graphQlMetricQuery)));
    return futureResonse;
  }

  /// Queries *all* metrics for a given ontology, but only shows the last two commits. Used to outline the changes made recently.
  Future<QueryResult<dynamic>> getLastTwoCommits(String url) {
    // I know that hard coding all the available measures is less than ideal, but it is a quick fix so that Randa can continue to work on the visualizations. It will be fixed later on.
    var graphQlMetricQuery = """{
  getRepository(repository: "$url") {
    edges {
      node {
        repository
        ontologyfile_set {
          edges {
            node {
              id
              fileName
              branch
              commit (last:2){
                edges {
                  node {
                    CommitTime
                    CommitID
                    CommitMessage
                    AuthorEmail
                    AuthorName
                    CommiterEmail
                    CommitterName
                    Size
                    branch
                    renamedFrom
                    ReadingError
                    absoluteBreadth
                    absoluteDepth
                    absoluteLeafCardinality
                    annotationAssertionsAxioms
                    annotationPropertyRangeaxioms
                    annotationPropertyDomainAxioms
                    anonymousClasses
                    asymmetricObjectPropertyAxioms
                    axioms
                    classAnnotations
                    classAssertionaxioms
                    classes
                    classesThatShareARelation
                    classesWithIndividuals
                    classesWithMultipleInheritance
                    classesWithObjectProperties
                    classesWithSubClasses
                    circleHierachies
                    dataProperties
                    dataPropertyAnnotations
                    dataPropertyAssertionAxioms
                    dataPropertyDomainAxioms
                    dataPropertyRangeAxioms
                    datatypeAnnotations
                    differentIndividualsAxioms
                    disjointClassesAxioms
                    disjointDataPropertiesAxioms
                    disjointObjectPropertyAxioms
                    dlExpressivity
                    equivalentClassesAxioms
                    equivalentDataPropertiesAxioms
                    equivalentObjectPropertyAxioms
                    functionalDataPropertyAxioms
                    functionalObjectPropertyAxioms
                    gciCount
                    generalAnnotationAxioms
                    hiddengciCount
                    individuals
                    inverseFunctionalObjectPropertyAxioms
                    inverseObjectPropertyAxioms
                    irreflexiveObjectPropertyAxioms
                    logicalAxioms
                    maxFanoutnessOfLeafClasses
                    maximalBreadth
                    maximalDepth
                    maxSubClassesOfAClass
                    maxSuperClassesOfAClass
                    minimumBreath
                    minimumDepth
                    negativeDataPropertyAssertionAxioms
                    negativeObjectPropertyAssertionAxioms
                    numberOfConnectedGraphs
                    objectProperties
                    objectPropertiesOnClasses
                    objectPropertyAssertionaxioms
                    objectPropertyDomainAxioms
                    objectPropertyRangeAxioms
                    pathsToLeafClasses
                    reflexiveObjectPropertyAxioms
                    rootClasses
                    sameIndividualsAxioms
                    subDataPropertyOfAxioms
                    subObjectPropertyOfAxioms
                    subPropertyChainOfAxioms
                    superClasses
                    superClassesOfClassesWithMultipleInheritance
                    subClassOfAxioms
                    recursiveSubClasses
                    symmetricObjectPropertyAxioms
                    transitiveObjectPropertyAxioms
                    superClassesOfLeafClasses
                    objectPropertyAnnotations
                    individualAnnotations
                    allActualImports
                    directImports
                    declaredImports
                    reasonerActive
                    deprecatedDataTypes
                    deprecatedObjectProperties
                    deprecatedClasses
                    deprecatedDataProperties
                    deprecatedIndividuals
                    inconsistentClasses
                    consistencyCheckSuccessful
                    CohesionMetrics_ADITLN
                    CohesionMetrics_NoL
                    CohesionMetrics_NoR
                    ComplexityMetrics_Average_Path_Length
                    ComplexityMetrics_Average_Paths_Per_Concept
                    ComplexityMetrics_Average_Relationships_Per_Concept
                    ComplexityMetrics_Max_Path_Length
                    ComplexityMetrics_TNOC
                    ComplexityMetrics_TNOP
                    ComplexityMetrics_TNOR
                    FernandezEtAl_Average_Breath
                    FernandezEtAl_Average_Depth
                    FernandezEtAl_Breath_Variance
                    FernandezEtAl_Depth_Variance
                    FernandezEtAl_Maximum_Breath
                    FernandezEtAl_Maximum_Depth
                    FernandezEtAl_Minimum_Breath
                    FernandezEtAl_Minimum_Depth
                    FernandezEtAl_Number_Of_Classes
                    FernandezEtAl_Number_Of_Individuals
                    FernandezEtAl_Number_Of_Properties
                    OQuaRE_ANOnto
                    OQuaRE_AROnto
                    OQuaRE_CBOnto
                    OQuaRE_CBOnto2
                    OQuaRE_CROnto
                    OQuaRE_DITOnto
                    OQuaRE_INRonto
                    OQuaRE_NACOnto
                    OQuaRE_NOCOnto
                    OQuaRE_NOMOnto
                    OQuaRE_POnto
                    OQuaRE_PROnto
                    OQuaRE_RFCOnto
                    OQuaRE_RROnto
                    OQuaRE_TMOnto
                    OQuaRE_TMOnto2
                    OQuaRE_WMCOnto
                    OQuaRE_WMCOnto2
                    OQual_Absolute_Breath
                    OQual_Absolute_Depth
                    OQual_Absolute_Leaf_Cardinality
                    OQual_Absolute_Sibling_Cardinality
                    OQual_Anonymous_classes_ratio
                    OQual_Average_Breath
                    OQual_Average_Depth
                    OQual_Average_Sibling_FanOutness
                    OQual_Axiomclass_ratio
                    OQual_Class_relation_ratio
                    OQual_Generic_complexity
                    OQual_Inverse_relations_ratio
                    OQual_Maximal_Breath
                    OQual_Maximal_Depth
                    OQual_Maximal_Leaf_FanOutness
                    OQual_Maximal_Sibling_FanOutness
                    OQual_Modularity_rate
                    OQual_Ratio_of_Leaf_FanOutness
                    OQual_Ratio_of_Sibling_FanOutness
                    OQual_Ratio_of_sibling_nodes_featuring_a_shared_differentia_specifica
                    OQual_Weighted_Ratio_Of_Sibling_FanOutness
                    OQual_Weighted_Ratio_of_Leaf_FanOutness
                    OntoQA_Attribute_Richness
                    OntoQA_Average_Population
                    OntoQA_Class_Inheritance_Richness
                    OntoQA_Class_Utilization
                    OntoQA_Cohesion
                    OntoQA_Inheritance_Richness
                    OntoQA_Relationship_Diversity
                    OntoQA_Relationship_Richness
                    OntoQA_Schema_Deepness
                    OrmeEtAl_Average_Fanout_of_NonLeaf_Classes_AFNL
                    OrmeEtAl_Average_Fanout_of_Root_Class_AFR
                    OrmeEtAl_Average_Fanout_per_Class_AFC
                    OrmeEtAl_Average_Properties_per_Class_APC
                    OrmeEtAl_Maximum_Depth_of_Inheritance_Tree_MaxDIT
                    OrmeEtAl_Number_of_Properties_NOP
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}""";
    var futureResonse = _graphQlClient.query(QueryOptions(
        fetchPolicy: FetchPolicy.networkOnly,
        document: parseString(graphQlMetricQuery)));
    return futureResonse;
  }

  /// Returns Ontology Metrics from a single Ontology File (Not a Repository).
  Future<QueryResult<dynamic>> getOntologyMetricsFromAPI(
      String url, String graphQlQueryAppender) {
    var graphQlMetricQuery = """{
  getOntologyFile(
    fileName: "$url"
  ) {
    edges {
      node {
        fileName
        commit {
          edges {
            node {
                    pk
                    CommitTime
                    CommitID
                    CommitMessage
                    AuthorEmail
                    AuthorName
                    CommiterEmail
                    CommitterName
                    Size
                    ReadingError
                    $graphQlQueryAppender
                              }
          }
        }
      }
    }
  }
}
""";
    var futureResonse = _graphQlClient.query(QueryOptions(
        fetchPolicy: FetchPolicy.networkOnly,
        document: parseString(graphQlMetricQuery)));
    return futureResonse;
  }
}
