import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/analytic_view.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/helpers/availableNames.dart';
import 'package:neonto_frontend/analytic/models/data.dart';
import 'package:provider/provider.dart';
import 'package:syncfusion_flutter_charts/charts.dart';

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

    List selectedFile = [];
    var chart;
    var commitTime;

    for (var file in analyticController.theSelectedOntologyFile) {
      selectedFile.add(file);
    }
    List<LineSeries> chartData = [];
    for (var metricName in analyticController.listString) {
      List<OntologyData> metricList = [];

      for (var ontologyFile in selectedFile) {
        if (ontologyFile[metricName] != "null") {
          var metricResult = double.parse(ontologyFile[metricName]);
          commitTime = (ontologyFile[metricName] == null ||
                  ontologyFile[metricName] == false ||
                  ontologyFile[metricName] == 'null')
              ? 0
              : ontologyFile['CommitTime'];
          metricList.add(OntologyData(
              metricName, metricResult, DateTime.parse(commitTime)));
          metricList.sort((a, b) => a.year.compareTo(b.year));
        }
      }
      chartData.add(LineSeries<OntologyData, DateTime>(
          name: metricName,
          dataSource: metricList,
          xValueMapper: (OntologyData salesdata, _) => salesdata.year,
          yValueMapper: (OntologyData salesdata, _) => salesdata.metricResult,
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
                    onChanged: (value) => setState(() {
                      activeMetricFile = value as int;

                      var t = analyticController
                          .repositoryData.ontologyFiles[value].metrics;

                      analyticController.theSelectedOntologyFile = t;
                      model.changeName();
                    }),
                  ),
                ))
          ],
        ),
      ),
      body: chart = Column(
        children: [
          Padding(
            padding: const EdgeInsets.only(top: 20.0, left: 20),
            child: Container(
              width: MediaQuery.of(context).size.width / 1.2,
              height: MediaQuery.of(context).size.height / 1.3,
              child: SfCartesianChart(
                title: ChartTitle(
                    text:
                        'the development of metrics in an ontology file over time \n number of commits: ${analyticController.theSelectedOntologyFile.length}'),
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
    ));
    return ChangeNotifierProvider(
      create: (context) => Model(),
      child: chart,
    );
  }
}
