import 'package:flutter/material.dart';
import 'package:csv/csv.dart';
import 'dart:html' as html;
import 'dart:convert';
import 'package:url_launcher/url_launcher.dart';

class CalculationView extends StatefulWidget {
  final List<dynamic> metricData;
  final String ontologyName;
  const CalculationView(this.metricData, this.ontologyName, {Key? key})
      : super(key: key);

  @override
  State<CalculationView> createState() => _CalculationViewState();
}

class _CalculationViewState extends State<CalculationView> {
  int activeMetricFile = 0;
  final scrollController = ScrollController();
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
            title: Row(
              
          children: [
            Expanded(
                flex: 3,
                child: ListTile(
                  iconColor: Theme.of(context).secondaryHeaderColor,
                  textColor: Theme.of(context).secondaryHeaderColor,
                  title: const Text("Metric View"),
                  subtitle: Text(widget.ontologyName),
                  leading: const Icon(Icons.add_chart_outlined),
                )),
            Expanded(
              flex: 5,
              child: ListTile(
                textColor: Theme.of(context).secondaryHeaderColor,
                  leading: const Icon(Icons.filter_none_outlined),
                  title: DropdownButton(
                    focusColor: Theme.of(context).secondaryHeaderColor,
                    iconEnabledColor: Theme.of(context).secondaryHeaderColor,
                      value: activeMetricFile,
                      items: getAvailableFileNames(widget.metricData),
                      onChanged: (value) => setState(() {
                            activeMetricFile = value as int;
                          }))),
            ),
            Expanded(
                flex: 1,
                child: OutlinedButton(
                  
                  onHover: (value) => MaterialStateMouseCursor.clickable,
                  onPressed: () => downloadMetricFile(activeMetricFile),
                  child:  ListTile(
                    iconColor: Theme.of(context).secondaryHeaderColor,
                    textColor: Theme.of(context).secondaryHeaderColor,
                    title: const Text("Download"),
                    leading: const Icon(Icons.download),
                  ),
                ))
          ],
        )),
        body: metricWidgetBuilder(activeMetricFile));
  }

  List<DropdownMenuItem<int>> getAvailableFileNames(List<dynamic> metricData) {
    List<DropdownMenuItem<int>> ontologyFiles = [];
    int counter = 0;
    for (Map<String, dynamic> filesInMetricData in metricData) {
      ontologyFiles.add(
        DropdownMenuItem(
          child: Text(filesInMetricData["fileName"]),
          value: counter,
        ),
      );
      counter++;
    }
    return ontologyFiles;
  }

  Widget metricWidgetBuilder(int activeFile) {
    List<DataColumn> columns = [];
    List<DataRow> tableRows = [];
    Map<String, dynamic> metricDataForOntologyFile =
        widget.metricData[activeFile];
    List<dynamic> metricsForOntologyFile = metricDataForOntologyFile["metrics"];
    if (metricsForOntologyFile.isEmpty) {
      return Center(
          child: Container(
              height: 400,
              alignment: Alignment.center,
              width: 400,
              child: const ListTile(
                title: Text("No Ontology Metrics Found"),
                subtitle: Text(
                  "We were not able to calculate ontology metrics to this File. It is possible that the serialization of the ontology contained errors or that we had an internal error at calculation time.",
                  softWrap: true,
                ),
                leading: Icon(
                  Icons.do_not_disturb_alt,
                  color: Colors.red,
                ),
              )));
    }
    for (Map<String, dynamic> metricForOntologyFile in metricsForOntologyFile) {
      if (columns.isEmpty) {
        for (String key in metricForOntologyFile.keys) {
          columns.add(DataColumn(label: Text(key)));
        }
      }
      List<DataCell> cells = [];
      for (var metric in metricForOntologyFile.values) {
        cells.add(DataCell(Text(metric.toString())));
      }
      tableRows.add(DataRow(cells: cells));
    }
    return (InteractiveViewer(
        scaleEnabled: false,
        constrained: false,
        //controller: this.scrollController,
        clipBehavior: Clip.hardEdge,
        child: 
        
        PaginatedDataTable( 
          source: 
          columns: columns,
          rows: tableRows,
        )));
  }

  downloadMetricFile(int activeMetricFile) {
    Map<String, dynamic> metricDataForOntologyFile =
        widget.metricData[activeMetricFile];
    List<String> header = [];
    List<List<dynamic>> rows = [];
    bool firstElement = true;
    if (metricDataForOntologyFile["metrics"].isEmpty) {
      return null;
    }
    for (Map<String, dynamic> metricForOntologyFile
        in metricDataForOntologyFile["metrics"]) {
      if (firstElement) {
        rows.add(metricForOntologyFile.keys.toList());
        firstElement = false;
      }
      rows.add(metricForOntologyFile.values.toList());
    }

    var csv = const ListToCsvConverter().convert(rows);
    final bytes = utf8.encode(csv);
    final blob = html.Blob([bytes]);
    final url = html.Url.createObjectUrlFromBlob(blob);
    final anchor = html.document.createElement('a') as html.AnchorElement
      ..href = url
      ..style.display = 'none'
      ..download = 'metrics.csv';
    html.document.body?.children.add(anchor);

    // download
    anchor.click();

    // cleanup
    html.document.body?.children.remove(anchor);
    html.Url.revokeObjectUrl(url);
  }
}
