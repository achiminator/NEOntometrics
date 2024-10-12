import 'package:flutter/material.dart';
import 'package:neonto_frontend/metric_data.dart';

class AvailableNames {
  static List<DropdownMenuItem<int>> getAvailableFileNames(var repositoryData) {
    List<DropdownMenuItem<int>> ontologyFiles = [];
    int counter = 0;
    for (OntologyData ontologyData in repositoryData.ontologyFiles) {
      ontologyFiles.add(
        DropdownMenuItem(
          child: Text(ontologyData.fileName),
          value: counter,
        ),
      );
      counter++;
    }

    return ontologyFiles;
  }
}
  