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
  Future<QueryResult<dynamic>> getLastTwoCommits(
      String url, List<MetricExplorerItem> calculatableItems) {
    // I know that hard coding all the available measures is less than ideal, but it is a quick fix so that Randa can continue to work on the visualizations. It will be fixed later on.
    List<String> graphQLNames = [];
    for (var calculatableItem in calculatableItems) {
      graphQLNames.add(calculatableItem.implentationName!);
    }
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
                   ${graphQLNames.join("\n")}
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
