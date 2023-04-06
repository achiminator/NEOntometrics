import 'package:flutter/material.dart';

class Item {
  List<Items> expandedMetrics;
  String ontologyFileName;
  bool isExpanded;
  Item({
    required this.expandedMetrics,
    required this.ontologyFileName,
    this.isExpanded = false,
  });
}

class Items {
  String fileName;
  String metricName;
  String previousVersion;
  String currentVersion;
  Icon theicon;

  Items(this.fileName, this.metricName, this.theicon,
      {required this.currentVersion, required this.previousVersion});
}
