import 'dart:convert';
import "settings.dart";
import 'package:http/http.dart' as http;

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

class RepositoryData {
  RepositoryData(Map<String, dynamic>? graphqlInput) {
    // Unpack the GraphQL reponse and create an object containing the ontology metrics.
    Map<String, dynamic> root = graphqlInput?["getRepository"]["edges"][0]["node"];
    repository = root["repository"] ?? "";
    for (Map<String, dynamic> fileNode in root["ontologyfile_set"]?["edges"]) {
      Map<String, dynamic> file = fileNode["node"];
      var commits = <Map<String, dynamic>>[];
      var metrics = <String, dynamic>{};
      for (Map<String, dynamic> commitNode in file["commit"]["edges"]) {
        metrics.addAll(commitNode["node"]);
        commits.add(metrics);
      }
      ontologyFiles.add(OntologyData(file["id"], file["fileName"], commits));
    }
  }
  String repository = "";
  var ontologyFiles = <OntologyData>[];
}

class OntologyData {
  OntologyData(this.id, this.fileName, this.metrics);
  String id;
  String fileName;
  List<Map<String, dynamic>> metrics;
}
