import 'package:graphql/client.dart';
import 'package:neonto_frontend/settings.dart';
import 'metric_data.dart';

class GraphQLHandler {
  final _graphQlClient = GraphQLClient(
      link: HttpLink(Settings().apiUrl + "/graphql"), cache: GraphQLCache());

  Future<QueryResult<dynamic>> queueFromAPI(String url) {
    var response = _graphQlClient.query(QueryOptions(document: gql("""{
  queueInformation(url: "$url"){ 
        urlInSystem
        taskFinished
        taskStarted
        queuePosition
        repository
        error
        errorMessage
        analyzedCommits
        commitsForThisOntology
        analyzedOntologies
        analysableOntologies
      }
    }""")));
    return response;
  }
  Future<QueryResult<dynamic>> putInQueue(String url, bool reasoner) {
    var mutation = """mutation{
  update_queueInfo(
    reasoner: $reasoner
    url: "$url"
  ) {
    error
    errorMessage
    queueInfo {
      urlInSystem
      taskFinished
      taskStarted
      queuePosition
      urlj
      repository
      service
      fileName 
      error
      errorMessage
    }
  }
}""";
    var response = _graphQlClient.mutate(MutationOptions(document: gql(mutation)));
    return response;
  }
  Future<QueryResult<dynamic>> getRepositoryList() {
    var graphQLQuery = """
{
  repositoriesInformation {
    repository
    analyzedOntologyCommits
  }
}""";
 var futureResonse = 
        _graphQlClient.query(QueryOptions(document: gql(graphQLQuery)));
    return futureResonse;
  

  }
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

  Future<QueryResult<dynamic>> getMetricsFromAPI(
      String url, String graphQlQueryAppender) {
    var graphQlMetricQuery = """{
  getRepository (repository: "$url") {
    edges {
      node {
        repository
        fileName
        metrics {
          edges {
            node {
              CommitTime
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
    }""";
    var futureResonse =
        _graphQlClient.query(QueryOptions(document: gql(graphQlMetricQuery)));
    return futureResonse;
  }
}
