import 'package:flutter/material.dart';
import 'package:csv/csv.dart';
import 'package:download/download.dart';
import 'package:matomo_tracker/matomo_tracker.dart';
import 'package:neonto_frontend/analytic/analytic_view.dart';
import 'package:neonto_frontend/metric_data.dart';
import 'package:neonto_frontend/settings.dart';
import 'package:neonto_frontend/trackerHelper.dart';

import 'graphql.dart';
import 'notifications.dart';

class CalculationView extends StatefulWidget {
  final RepositoryData repositoryData;
  final String repositoryName;
  final QueueInformation queueInformation;
  final List<MetricExplorerItem> calculatableItems;
  final bool reasonerSelected;
  const CalculationView(this.repositoryData, this.calculatableItems, this.repositoryName,
      this.queueInformation, this.reasonerSelected,
      {Key? key})
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

class _CalculationViewState extends State<CalculationView>
    with TraceableClientMixin {
  @override
  String get traceName => 'Trigger Calculation View';

  @override
  String get traceTitle => "Calculation";

  /// indicates whether the ontology is currently in the update-queue. Null indicates that it is not.
  int activeMetricFile = 0;

  /// The information on the queue are dynamic and can be altered if we trigger an update.
  late QueueInformation queueInformation = widget.queueInformation;
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
                  subtitle: Text(widget.repositoryName),
                  leading: const Icon(Icons.add_chart_outlined),
                )),
            Expanded(
              flex: 5,
              child: ListTile(
                  textColor: Theme.of(context).colorScheme.onPrimary,
                  iconColor: Theme.of(context).colorScheme.onPrimary,
                  leading: const Icon(Icons.filter_none_outlined),
                  title: DropdownButton(
                      dropdownColor: Theme.of(context).colorScheme.primary,
                      style: TextStyle(
                          color: Theme.of(context).colorScheme.onPrimary),
                      value: activeMetricFile,
                      items: getAvailableFileNames(widget.repositoryData),
                      onChanged: (value) => setState(() {
                            activeMetricFile = value as int;
                          }))),
            ),
            updateIcon(),
            IconButton(
              onPressed: () => downloadMetricFile(activeMetricFile),
              tooltip: "Download the displayed Metrics as a .csv",
              icon: const Icon(Icons.download),
            ),
          ],
        )),
        body: metricWidgetBuilder(activeMetricFile));
  }

  /// The widget for updating the metrics. Calls [GraphQLHandler.putInQueue] and displays
  /// the corresponding [Snacks()] later on.
  Widget updateIcon() {
    if (queueInformation.queuePosition == null) {
      return IconButton(
        onPressed: () {
          GraphQLHandler()
              .putInQueue(widget.repositoryName, widget.reasonerSelected,
                  update: true)
              .then((value) {
            if (value.hasException) {
              Snacks(context)
                  .displayErrorSnackBar(value.exception.toString(), context);
            } else {
              setState(() {
                queueInformation =
                    QueueInformation(value.data?["update_queueInfo"]);
              });
              Snacks(context).progressSnackBar(queueInformation);
            }
          });
        },
        tooltip: "Put the metrics update into the queue.",
        icon: const Icon(Icons.update),
      );
    } else {
      return Tooltip(
        message:
            "The Metrics are currently in the queue for updating. Queueposition: ${queueInformation.queuePosition}",
        child: Stack(children: [
          IconButton(
            onPressed: () => setState(() {
              Snacks(context).progressSnackBar(queueInformation);
            }),
            icon: const Icon(Icons.update_disabled),
          ),
          Container(
            padding: const EdgeInsets.all(4),
            decoration: BoxDecoration(
                color: Theme.of(context).secondaryHeaderColor,
                borderRadius: const BorderRadius.all(Radius.circular(12))),
            alignment: Alignment.bottomRight,
            child: Text(queueInformation.queuePosition.toString(),
                textAlign: TextAlign.end,
                style: TextStyle(
                    color: Theme.of(context).primaryColorDark, fontSize: 10)),
          )
        ]),
      );
    }
  }

  /// Extracts the file names of the ontology for the drop down menue.
  List<DropdownMenuItem<int>> getAvailableFileNames(
      RepositoryData repositoryData) {
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

  int _rowPerPage = PaginatedDataTable.defaultRowsPerPage;
  Widget metricWidgetBuilder(int activeFile) {
    List<DataColumn> columns = [];
    List<DataRow> tableRows = [];

    OntologyData metricDataForOntologyFile =
        widget.repositoryData.ontologyFiles[activeFile];
    if (metricDataForOntologyFile.metrics.isEmpty) {
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
    bool firstRow = true;
    for (Map<String, dynamic> metricForOntologyFile
        in metricDataForOntologyFile.getDisplayMetrics()) {
      List<DataCell> cells = [];
      //Add Download Button
      if (widget.queueInformation.repository != "") {
        cells.add(DataCell(IconButton(
          onPressed: () => TrackerHelper.htmlOpenWindow(
              Settings().apiUrl +
                  "/downloadOntology/" +
                  metricDataForOntologyFile
                      .getPKFromCommit(metricForOntologyFile["Commit ID"]),
              "Download"),
          icon: const Icon(Icons.download),
          tooltip: "Download the ontology file.",
        )));
      } else {
        cells.add(DataCell(IconButton(
          onPressed: () => TrackerHelper.htmlOpenWindow(
              "http://" + widget.queueInformation.fileName, "Download"),
          icon: const Icon(Icons.download),
          tooltip: "Download the ontology file.",
        )));
      }

      for (String key in metricForOntologyFile.keys) {
        if (columns.isEmpty) {
          columns.add(DataColumn(
            label: Text(
              "File",
              style: TextStyle(
                  color: Theme.of(context).colorScheme.primaryContainer,
                  fontSize: 16,
                  fontWeight: FontWeight.w400),
            ),
            numeric: true,
          ));
        }
        if (firstRow) {
          columns.add(DataColumn(
            label: Text(
              key,
              style: TextStyle(
                  color: Theme.of(context).colorScheme.primaryContainer,
                  fontSize: 16,
                  fontWeight: FontWeight.w400),
            ),
          ));
        }
        if (key == "Commit Message" &&
            metricForOntologyFile[key].toString().contains("\n")) {
          cells.add(DataCell(Tooltip(
              message: metricForOntologyFile[key].toString(),
              textStyle: const TextStyle(fontSize: 15, color: Colors.black),
              preferBelow: true,
              decoration: BoxDecoration(
                  color: Colors.white,
                  border: Border.all(
                      width: 2,
                      color: Colors
                          .grey)), //borderRadius: BorderRadius.all(Radius.circular(1))),
              //waitDuration: const Duration(milliseconds: 300),
              child: Text(
                metricForOntologyFile[key].toString(),
                maxLines: 1,
              ))));
        } else {
          cells.add(DataCell(Text(metricForOntologyFile[key].toString())));
        }
      }
      firstRow = false;
      tableRows.add(DataRow(cells: cells));
    }
    var data = TableData(tableRows);

    return SingleChildScrollView(
      child: Column(
        children: [
          PaginatedDataTable(
            showFirstLastButtons: true,
            source: data,
            columns: columns,
            onRowsPerPageChanged: (r) {
              setState(() {
                _rowPerPage = r!;
              });
            },
            rowsPerPage: _rowPerPage,
            sortAscending: true,
          ),

          // the analytic view Button
          Padding(
            padding: const EdgeInsets.only(top: 50.0),
            child: ElevatedButton.icon(
              onPressed: () {
                var metricForOntologyFile =
                    metricDataForOntologyFile.getDisplayMetrics();
                Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (context) => AnalyticView(
                          widget.calculatableItems,
                          widget.repositoryName,
                          widget.repositoryData)),
                );
              },
              icon: const Icon(
                Icons.analytics,
                size: 24.0,
              ),
              label: const Text('show the analytic'),
            ),
          ),
        ],
      ),
    );
  }

  /// Initiates a .csv download of the displayed ontology metrics.
  downloadMetricFile(int activeMetricFile) {
    var metricDataForOntologyFile =
        widget.repositoryData.ontologyFiles[activeMetricFile].metrics;
    List<List<dynamic>> rows = [];
    bool firstElement = true;
    if (metricDataForOntologyFile.isEmpty) {
      return null;
    }
    for (Map<String, dynamic> metricForOntologyFile
        in metricDataForOntologyFile) {
      if (firstElement) {
        rows.add(metricForOntologyFile.keys.toList());
        firstElement = false;
      }
      rows.add(metricForOntologyFile.values.toList());
    }

    var csv = const ListToCsvConverter().convert(
      rows,
    );
    final stream =
        Stream.fromIterable(csv.toString().replaceAll("\n", ". ").codeUnits);
    download(stream, "metrics.csv");
  }
}
