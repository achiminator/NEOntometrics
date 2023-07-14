import 'dart:convert';
import 'package:flutter/cupertino.dart';

import "settings.dart";
import 'package:http/http.dart' as http;

/// Containing all the information for the Metric Explorer Page.
class MetricExplorerItem {
  ///Flattens the Hierachy of the hierachical structure of the [MetricExplorerItem]. The element onlyCalculatableClasses
  // asks if only such elmeents are shown, that are possible to calculate.
  static List<MetricExplorerItem> getLeafItems(MetricExplorerItem input,
      {bool onlyCalculatableClasses = false}) {
    List<MetricExplorerItem> leafItems = [];
    if (input.subClass.isEmpty) {
      if (!onlyCalculatableClasses ||
          input.calculation != "" ||
          input.implentationName != "") {
        leafItems.add(input);
      }
    } else {
      for (var item in input.subClass) {
        leafItems
            .addAll(getLeafItems(input = item, onlyCalculatableClasses: true));
      }
    }

    return leafItems;
  }

  MetricExplorerItem(
      {required this.itemName,
      required this.description,
      required this.definition,
      required this.interpretation,
      required this.seeAlso,
      this.calculation,
      this.implentationName}) {
    subClass = [];
  }
  bool toggled = false;
  String itemName;
  String description;
  String definition;
  String interpretation;
  String? calculation;
  String? implentationName;
  String seeAlso;
  late List<MetricExplorerItem> subClass;
}

/// Calls the Ontology-Query on the Metric Calculation Endpoint.
class MetricExplorerItemFactory {
  Future<List<MetricExplorerItem>> getMetricExplorerData() async {
    final response =
        await http.get(Uri.parse("${Settings().apiUrl}/metricexplorer"));
    final dynamic body;
    List<MetricExplorerItem> metricExplorerItems = [];
    if (response.statusCode != 200) {
      throw Exception(
          "Connection to Ontology-Endpoint was not Successfull (internal error)");
    } else {
      body = jsonDecode(response.body);
    }
    metricExplorerItems.addAll(createMetricItems(body["thing"]["children"]));
    return metricExplorerItems;
  }

  /// Takes a [json] arraylist and converts it into the internal object [List<MetricExplorerItem>].
  /// Uses recoursive means for data conversion.
  List<MetricExplorerItem> createMetricItems(List<dynamic> json) {
    List<MetricExplorerItem> metricExplorerItem = [];
    List<MetricExplorerItem> metricExplorerItemWithoutSubclasses = [];
    for (Map<String, dynamic> element in json) {
      if (element["item"] != null) {
        element = element["item"] as Map<String, dynamic>;
      } else {
        throw Exception("No Data Item in the Ontology Response");
      }
      MetricExplorerItem currentMetricExplorerItem;
      if (element["data"] != null) {
        Map<String, dynamic> dataElement =
            element["data"] as Map<String, dynamic>;
        currentMetricExplorerItem = MetricExplorerItem(
          itemName: dataElement["itemName"] ?? "",
          description: dataElement["description"] ?? "",
          definition: dataElement["definition"] ?? "",
          interpretation: dataElement["interpretation"] ?? "",
          seeAlso: dataElement["seeAlso"] ?? "",
          calculation: dataElement["calculation"] ?? "",
          implentationName: dataElement["implementationName"],
        );
      } else {
        throw Exception("No Data Item in the Ontology Response");
      }
      if (element["children"] != null) {
        currentMetricExplorerItem.subClass
            .addAll(createMetricItems(element["children"] as List<dynamic>));
        metricExplorerItem.add(currentMetricExplorerItem);
      } else {
        // To ensure a proper functioning of the Expansion-Panel, elements without
        //subclasses must be at the end of the metricItems list. Thus, wee  need to make this distinction below and add them
        // at the end.
        metricExplorerItemWithoutSubclasses.add(currentMetricExplorerItem);
      }
    }
    metricExplorerItem.addAll(metricExplorerItemWithoutSubclasses);
    return metricExplorerItem;
  }
}

///Class for storing the calculated metrics
///
///The constructor unnpacks the GraphQL response, and the object is used by the detailedView for further
///displaying of the calculated values.
class RepositoryData extends ChangeNotifier{
  /// Unpack the GraphQL reponse and create an object containing the ontology metrics with the constructor.
  RepositoryData(Map<String, dynamic>? graphqlInput) {
    if (graphqlInput?.containsKey("getRepository") ?? false) {
      Map<String, dynamic> root =
          graphqlInput?["getRepository"]["edges"][0]["node"];
      repository = root["repository"] ?? "";
      for (Map<String, dynamic> fileNode in root["ontologyfile_set"]
          ?["edges"]) {
        Map<String, dynamic> file = fileNode["node"];
        var commits = <Map<String, String>>[];
        var metrics = <String, dynamic>{};
        var metricsConverted = <String, String>{};
        for (Map<String, dynamic> commitNode in file["commit"]["edges"]) {
          metrics.addAll(commitNode["node"]);
          for (var key in metrics.keys) {
            metricsConverted[key] = metrics[key].toString();
          }
          commits.add(Map.from(metricsConverted));
        }
        ontologyFiles.add(OntologyData(file["fileName"], commits));
      }
    } else if (graphqlInput?.containsKey("getOntologyFile") ?? false) {
      Map<String, dynamic> root =
          graphqlInput?["getOntologyFile"]["edges"][0]["node"];
      repository = "Single Ontology File";

      var commits = <Map<String, String>>[];
      var metrics = <String, dynamic>{};
      var metricsConverted = <String, String>{};
      for (Map<String, dynamic> commitNode in root["commit"]["edges"]) {
        metrics.addAll(commitNode["node"]);
        for (var key in metrics.keys) {
          metricsConverted[key] = metrics[key].toString();
        }
        commits.add(Map.from(metricsConverted));
      }
      ontologyFiles.add(OntologyData(root["fileName"], commits));
    }
  }

  /// The Name (or URL) of the given repository
  String repository = "";

  /// The various calculated ontology files, thus their ID for identification, their name in the
  /// repository and a list of Key, Value pairs for the given metrics
  var ontologyFiles = <OntologyData>[];
}

/// Contains information and metrics of one ontology.
class OntologyData {
  OntologyData(this.fileName, this.metrics);

  /// The name and relative location of the file in the repo.
  String fileName;

  /// The Ontology Metrics.
  List<Map<String, String>> metrics;

  String getPKFromCommit(String commitID) {
    for (var item in metrics) {
      if (item["CommitID"] == commitID) {
        return item["pk"] as String;
      }
    }
    return "0";
  }

  /// Prints the ontology metrics prettyfied.
  List<Map<String, String>> getDisplayMetrics() {
    var returnList = <Map<String, String>>[];
    for (var metric in metrics) {
      var returnMap = <String, String>{};
      for (var metricName in metric.keys) {
        var newResult = "";
        if (metricName == "pk") {
          continue;
        } else if (metricName == "CommitID") {
          returnMap.addAll({"Commit ID": metric[metricName] ?? ""});
          continue;
        } else if (metricName == "CommitTime") {
          newResult = metric[metricName]!.split("+")[0];
        } else {
          newResult = metric[metricName]!.split("+")[0];
        }

        var newMetricName = "";
        var firstChar = true;
        for (var char in metricName.split("")) {
          if (firstChar) {
            newMetricName += char.toUpperCase();
            firstChar = false;
            continue;
          }
          if (char == "_") {
            continue;
          }
          if (char.toUpperCase() == char) {
            newMetricName += " ";
          }
          newMetricName += char;
        }
        returnMap.addAll({newMetricName: newResult});
      }
      returnList.add(returnMap);
    }
    if (returnList.length == 1) {
      Map<String, String> newReturnMap = {};
      if (returnList[0]["Commit Time"] == "null") {
        for (var element in returnList[0].keys) {
          if (returnList[0][element] != "null") {
            newReturnMap[element] = returnList[0][element] ?? "";
          }
        }
        returnList[0] = newReturnMap;
      }
    }
    return returnList;
  }
}

/// Represents the QueueInformation Query for the status on a update or analyzing operation (if applicable)
class QueueInformation {
  QueueInformation(Map<String, dynamic>? graphQLInput)
      : taskFinished =
            graphQLInput?["queueInformation"]?["taskFinished"] ?? false,
        performsUpdate =
            graphQLInput?["queueInformation"]?["performsUpdate"] ?? false,
        taskStarted =
            graphQLInput?["queueInformation"]?["taskStarted"] ?? false,
        urlInSystem =
            graphQLInput?["queueInformation"]?["urlInSystem"] ?? false,
        error = graphQLInput?["queueInformation"]?["error"] ?? true,
        errorMessage = graphQLInput?["queueInformation"]?["errorMessage"] ??
            "Error While retrieving the Data",
        analyzedCommits = graphQLInput?["queueInformation"]?["analyzedCommits"],
        totalCommits = graphQLInput?["queueInformation"]?["totalCommits"],
        analyzedOntologies =
            graphQLInput?["queueInformation"]?["analyzedOntologies"],
        analyzsableOntologies =
            graphQLInput?["queueInformation"]?["analysableOntologies"],
        queuePosition = graphQLInput?["queueInformation"]?["queuePosition"],
        repository = graphQLInput?["queueInformation"]?["repository"] ?? "",
        fileName = graphQLInput?["queueInformation"]?["fileName"] ?? "";

  bool taskStarted;
  bool taskFinished;
  bool urlInSystem;
  bool performsUpdate;
  bool error;
  String errorMessage;
  String repository;
  String fileName;
  int? queuePosition;
  int? analyzedCommits;
  int? totalCommits;
  int? analyzedOntologies;
  int? analyzsableOntologies;
}
