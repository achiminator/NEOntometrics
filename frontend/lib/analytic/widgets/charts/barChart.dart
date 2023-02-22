import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/analytic_view.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/models/data.dart';
import 'package:provider/provider.dart';
import 'package:syncfusion_flutter_charts/charts.dart';

class Barchart extends StatefulWidget {
  Barchart({Key? key}) : super(key: key);

  @override
  State<Barchart> createState() => _BarchartState();
}

class _BarchartState extends State<Barchart> {
  late TooltipBehavior _tooltipBehavior;
  late ZoomPanBehavior _zoomPanBehavior;

  @override
  void initState() {
    _zoomPanBehavior = ZoomPanBehavior(
        enablePinching: true,
        zoomMode: ZoomMode.x,
        enablePanning: true,
        enableDoubleTapZooming: true);
    _tooltipBehavior = TooltipBehavior(enable: true);

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    var model = Provider.of<Model>(context);
    var chart;

    List<ChartSeries> chartData = [];

    for (var ontologyFile in analyticController.listData) {
      List<OntologyData> metricList = [];
      for (var metricName in analyticController.listString) {
        if (ontologyFile['commit'].isEmpty) continue;
        double metricResultNumber =
            (ontologyFile['commit']['edges'].last['node'][metricName] == null ||
                    ontologyFile['commit']['edges'].last['node'][metricName] ==
                        false)
                ? 0
                : double.parse(ontologyFile['commit']['edges']
                    .last['node'][metricName]
                    .toString());
        metricList.add(OntologyData(metricName, metricResultNumber));
      }
      chartData.add(
        ColumnSeries<OntologyData, dynamic>(
            enableTooltip: true,
            name: ontologyFile['fileName'],
            dataSource: metricList,
            xValueMapper: (OntologyData data, _) => data.metric,
            yValueMapper: (OntologyData data, _) => data.metricResult,
            spacing: 0.1),
      );
    }

    SafeArea(
      child: chart = Scaffold(
          appBar: AppBar(
            automaticallyImplyLeading: false,
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
              ],
            ),
          ),
          body: Column(
            children: [
              Padding(
                padding: const EdgeInsets.only(top: 20.0, left: 20),
                child: Container(
                  width: MediaQuery.of(context).size.width / 1.2,
                  height: MediaQuery.of(context).size.height / 1.3,
                  child: SfCartesianChart(
                    title: ChartTitle(
                        text:
                            'the comparison between the ontology files in a repository \n  number of ontology files: ${analyticController.listData.length}'),
                    tooltipBehavior: _tooltipBehavior,
                    zoomPanBehavior: _zoomPanBehavior,
                    legend: (Legend(
                        toggleSeriesVisibility: true,
                        isVisible: true,
                        overflowMode: LegendItemOverflowMode.wrap,
                        position: LegendPosition.bottom)),
                    series: chartData,
                    primaryXAxis: CategoryAxis(
                      autoScrollingDelta: 2,
                      majorGridLines: const MajorGridLines(width: 0),
                      majorTickLines: const MajorTickLines(width: 0),
                      maximumLabelWidth: 60,
                      edgeLabelPlacement: EdgeLabelPlacement.shift,
                    ),
                    primaryYAxis: NumericAxis(
                      edgeLabelPlacement: EdgeLabelPlacement.shift,
                    ),
                  ),
                ),
              )
            ],
          )),
    );

    return ChangeNotifierProvider(
      create: (context) => Model(),
      child: chart,
    );
  }
}
