import 'package:flutter/material.dart';
import 'package:csv/csv.dart';
import 'package:download/download.dart';

class CalculationView extends StatefulWidget {
  final List<dynamic> metricData;
  final String ontologyName;
  const CalculationView(this.metricData, this.ontologyName, {Key? key})
      : super(key: key);

  @override
  State<CalculationView> createState() => _CalculationViewState();
}

class TableData extends DataTableSource {
  TableData(this.tableRows);
  List<DataRow> tableRows;

  @override
  DataRow? getRow(int index) {
    return tableRows[index];
  }

  @override
  bool get isRowCountApproximate => false;

  @override
  int get rowCount => tableRows.length;

  @override
  int get selectedRowCount => 0;
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
                  iconColor: Theme.of(context).colorScheme.onPrimary,
                  textColor: Theme.of(context).colorScheme.onPrimary,
                  title: const Text("Metric View"),
                  subtitle: Text(widget.ontologyName),
                  leading: const Icon(Icons.add_chart_outlined),
                )),
            Expanded(
              flex: 5,
              child: ListTile(
                  textColor: Theme.of(context).colorScheme.onPrimary,
                  iconColor: Theme.of(context).colorScheme.onPrimary,
                  leading: const Icon(Icons.filter_none_outlined),
                  title: DropdownButton(
                      dropdownColor: Theme.of(context).colorScheme.onPrimary,
                      focusColor: Theme.of(context).colorScheme.onPrimary,
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
                  child: ListTile(
                    iconColor: Theme.of(context).colorScheme.onPrimary,
                    textColor: Theme.of(context).colorScheme.onPrimary,
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
          child: Text(filesInMetricData["node"]["fileName"]),
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
        widget.metricData[activeFile]?["node"];
    List<dynamic> metricsForOntologyFile =
        metricDataForOntologyFile["metrics"]["edges"];
    if (metricsForOntologyFile.isEmpty) {
      return Center(
          child: Container(
              height: 400,
              alignment: Alignment.center,
              width: 400,
              child: ListTile(
                title: const Text("No Ontology Metrics Found"),
                subtitle: const Text(
                  "We were not able to calculate ontology metrics to this File. It is possible that the serialization of the ontology contained errors or that we had an internal error at calculation time.",
                  softWrap: true,
                ),
                leading: Icon(
                  Icons.do_not_disturb_alt,
                  color: Theme.of(context).colorScheme.error,
                ),
              )));
    }
    for (Map<String, dynamic> metricForOntologyFile in metricsForOntologyFile) {
      if (columns.isEmpty) {
        for (String key in metricForOntologyFile["node"].keys) {
          columns.add(DataColumn(label: Text(key)));
        }
      }
      List<DataCell> cells = [];
      for (var metric in metricForOntologyFile["node"].values) {
        cells.add(DataCell(Text(metric.toString())));
      }
      tableRows.add(DataRow(cells: cells));
    }
    var data = TableData(tableRows);

    return (PaginatedDataTable(
      showFirstLastButtons: true,
      source: data,
      columns: columns,
      //rows: tableRows,
    ));
  }

  downloadMetricFile(int activeMetricFile) {
    Map<String, dynamic> metricDataForOntologyFile =
        widget.metricData[activeMetricFile];
    List<List<dynamic>> rows = [];
    bool firstElement = true;
    if (metricDataForOntologyFile["node"]["metrics"].isEmpty) {
      return null;
    }
    for (Map<String, dynamic> metricForOntologyFile
        in metricDataForOntologyFile["node"]["metrics"]["edges"]) {
      if (firstElement) {
        rows.add(metricForOntologyFile["node"].keys.toList());
        firstElement = false;
      }
      rows.add(metricForOntologyFile["node"].values.toList());
    }

    var csv = const ListToCsvConverter().convert(
      rows,
    );
    final stream =
        Stream.fromIterable(csv.toString().replaceAll("\n", ". ").codeUnits);
    download(stream, "metrics.csv");
  }
}
