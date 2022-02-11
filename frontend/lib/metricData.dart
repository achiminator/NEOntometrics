import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';

import "settings.dart";
import 'package:http/http.dart' as http;

class MetricExplorerItem {
  static List<MetricExplorerItem> getLeafItems(MetricExplorerItem input,
      {bool onlyCalculatableClasses = false}) {
    List<MetricExplorerItem> leafItems = [];
    if (input.subClass.isEmpty) {
      if (!onlyCalculatableClasses || input.calculation != "") {
        leafItems.add(input);
      }
    } else {
      for (var item in input.subClass) {
        leafItems.addAll(getLeafItems(item));
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
    final body;
    List<MetricExplorerItem> metricExplorerItems = [];
    if (response.statusCode != 200) {
      throw Exception(
          "Connection to Ontology-Endpoint was not Successfull (internal error)");
    } else {
      print("Fetch Done");
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
