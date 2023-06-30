import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/analytic_view.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/helpers/availableNames.dart';
import 'package:neonto_frontend/analytic/models/data.dart';
import 'package:neonto_frontend/metric_data.dart';
import 'package:provider/provider.dart';
import 'package:syncfusion_flutter_charts/charts.dart';

//import '../../metric_data.dart';

class LineChartPage extends StatefulWidget {
  const LineChartPage({Key? key}) : super(key: key);

  @override
  State<LineChartPage> createState() => _LineChartPageState();
}

class _LineChartPageState extends State<LineChartPage> {
  TooltipBehavior? _tooltipBehavior;
  late ZoomPanBehavior _zoomPanBehavior;

  @override
  void initState() {
    _zoomPanBehavior = ZoomPanBehavior(
        enablePinching: true,
        zoomMode: ZoomMode.xy,
        enablePanning: true,
        enableDoubleTapZooming: true);
    _tooltipBehavior = TooltipBehavior(enable: true);
    super.initState();
  }

  @override
  int activeMetricFile = 0;
  Widget build(BuildContext context) {
    var model = Provider.of<Model>(context);
    setActiveFile(activeMetricFile, model);
    List selectedFile = [];
    var chart;
    var commitTime;

    for (var file in analyticController.theSelectedOntologyFile) {
      selectedFile.add(file);
    }
    List<LineSeries> chartData = [];
    for (var metricName in analyticController.listString) {
      List<OntologyChartData> metricList = [];

      for (var ontologyFile in selectedFile) {
        if (ontologyFile[metricName] != "null" &&
            ontologyFile[metricName] != null) {
          var metricResult = double.parse(ontologyFile[metricName]);
          commitTime = (ontologyFile[metricName] == null ||
                  ontologyFile[metricName] == false ||
                  ontologyFile[metricName] == 'null')
              ? 0
              : ontologyFile['CommitTime'];
          metricList.add(OntologyChartData(
              metricName, metricResult, DateTime.parse(commitTime)));
          metricList.sort((a, b) => a.year.compareTo(b.year));
        }
      }
      chartData.add(LineSeries<OntologyChartData, DateTime>(
          name: metricName,
          dataSource: metricList,
          xValueMapper: (OntologyChartData salesdata, _) => salesdata.year,
          yValueMapper: (OntologyChartData salesdata, _) =>
              salesdata.metricResult,
          markerSettings: const MarkerSettings(isVisible: true)));
    }

    SafeArea(
        child: chart = Scaffold(
            appBar: AppBar(
              automaticallyImplyLeading: false,
              elevation: 0.0,
              title: Row(
                children: [
                  Expanded(
                      flex: 2,
                      child: ListTile(
                        iconColor: Theme.of(context).colorScheme.onPrimary,
                        textColor: Theme.of(context).colorScheme.onPrimary,
                        title: const Text("Analytic View"),
                        subtitle: Text(analyticController.repositoryName),
                        leading: const Icon(Icons.add_chart_outlined),
                      )),
                  Expanded(
                      flex: 4,
                      child: ListTile(
                        textColor: Theme.of(context).colorScheme.onPrimary,
                        iconColor: Theme.of(context).colorScheme.onPrimary,
                        leading: const Icon(Icons.filter_none_outlined),
                        title: DropdownButton(
                          dropdownColor: Theme.of(context).colorScheme.primary,
                          style: TextStyle(
                              color: Theme.of(context).colorScheme.onPrimary),
                          value: activeMetricFile,
                          items: AvailableNames.getAvailableFileNames(
                              analyticController.repositoryData),
                          onChanged: (value) =>
                              setState(() => setActiveFile(value, model)),
                        ),
                      ))
                ],
              ),
            ),
            body: chart = SingleChildScrollView(
              scrollDirection: Axis.vertical,
              child: Column(
                children: [
                  Padding(
                    padding: const EdgeInsets.only(top: 15.0),
                    child: Container(
                      height: MediaQuery.of(context).size.height / 6,
                      width: MediaQuery.of(context).size.width / 1.3,
                      child: const Center(
                        child: Text(
                            'On this page, the visualization is presented in a different way. The development of the selected metrics over time is visualized in a line chart.\n '
                            '• By clicking on the dropdown menu the ontology file can be selected.\n'
                            ' • By clicking on the legend below the metric can be selected or deselected.\n '
                            '• The chart can also be scrolled to the right to view all metrics.\n'
                            ' • By double clicking the diagram can be enlarged.',
                            style: TextStyle(
                                fontWeight: FontWeight.w300, fontSize: 16)),
                      ),
                    ),
                  ),
                  const Divider(
                    indent: 50,
                    endIndent: 50,
                  ),
                  Padding(
                    padding: const EdgeInsets.only(top: 20.0, left: 20),
                    child: Container(
                      width: MediaQuery.of(context).size.width / 1.3,
                      height: MediaQuery.of(context).size.height / 1.4,
                      child: SfCartesianChart(
                        title: ChartTitle(
                            text:
                                'number of commits: ${analyticController.theSelectedOntologyFile.length}',
                            textStyle: TextStyle(
                                color: Theme.of(context)
                                    .colorScheme
                                    .primaryContainer,
                                fontWeight: FontWeight.w400)),
                        legend: Legend(
                            isVisible: true,
                            overflowMode: LegendItemOverflowMode.wrap,
                            position: LegendPosition.bottom),
                        tooltipBehavior: _tooltipBehavior,
                        zoomPanBehavior: _zoomPanBehavior,
                        series: chartData,
                        primaryXAxis: DateTimeAxis(
                          autoScrollingDeltaType: DateTimeIntervalType.auto,
                          edgeLabelPlacement: EdgeLabelPlacement.shift,
                        ),
                        primaryYAxis: NumericAxis(
                          labelFormat: '{value}',
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            )));
    return ChangeNotifierProvider(
      create: (context) => Model(),
      child: chart,
    );
  }

  /// Sets the [OntologyFile]-id of the [RepositoryData] class, retrieves the values
  /// and sets the necessary variables for the Lineplot-View
  void setActiveFile(Object? value, Model model) {
    activeMetricFile = value as int;
    var t = analyticController.repositoryData.ontologyFiles[value].metrics;
    analyticController.theSelectedOntologyFile = t;
    model.changeName();
  }
}
