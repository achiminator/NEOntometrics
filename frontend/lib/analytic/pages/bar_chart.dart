import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/analytic_view.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/models/data.dart';
import 'package:provider/provider.dart';
import 'package:syncfusion_flutter_charts/charts.dart';

class VerticalChartPage extends StatefulWidget {
  const VerticalChartPage({Key? key}) : super(key: key);

  @override
  State<VerticalChartPage> createState() => _VerticalChartPageState();
}

class _VerticalChartPageState extends State<VerticalChartPage> {
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
    Scaffold chart;

    List<CartesianSeries> chartData = [];

    for (var ontologyFile in analyticController.repositoryData!.ontologyFiles) {
      List<OntologyChartData> metricList = [];
      if (ontologyFile.metrics.isEmpty) continue;
      ontologyFile.metrics.last.forEach((key, value) {
        var result = double.tryParse(value);
        if (result != null) {
          metricList.add(OntologyChartData(key, result));
        }
      });
      if (metricList.isEmpty) continue;
      chartData.add(
        ColumnSeries<OntologyChartData, dynamic>(
            enableTooltip: true,
            name: ontologyFile.fileName,
            dataSource: metricList,
            xValueMapper: (OntologyChartData data, _) => data.metric,
            yValueMapper: (OntologyChartData data, _) => data.metricResult,
            spacing: 0.1),
      );
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
          ],
        ),
      ),
      body: SingleChildScrollView(
          scrollDirection: Axis.vertical,
          child: Column(
            children: [
              Padding(
                padding: const EdgeInsets.only(top: 15.0),
                child: SizedBox(
                  height: MediaQuery.of(context).size.height / 6,
                  width: MediaQuery.of(context).size.width / 1.3,
                  child: const Center(
                    child: Text(
                        'This diagram can be used to compare the ontology files. '
                        'All ontology files stored in the repository are displayed on the X-axis.'
                        ' On the \n Y-axis metrics are displayed that are already selected in calculation Engine.\n • By clicking on the legend below the ontology files can be selected or deselected \n '
                        '• The chart can also be scrolled to the right to view all metrics.\n'
                        ' • By double clicking the diagram can be enlarged',
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
                child: SizedBox(
                  width: MediaQuery.of(context).size.width / 1.3,
                  height: MediaQuery.of(context).size.height / 1.4,
                  child: SfCartesianChart(
                    title: ChartTitle(
                        text:
                            'number of ontology files: ${analyticController.listData.length}',
                        textStyle: TextStyle(
                            color:
                                Theme.of(context).colorScheme.primaryContainer,
                            fontWeight: FontWeight.w400)),
                    tooltipBehavior: _tooltipBehavior,
                    zoomPanBehavior: _zoomPanBehavior,
                    legend: (const Legend(
                        toggleSeriesVisibility: true,
                        isVisible: true,
                        overflowMode: LegendItemOverflowMode.wrap,
                        position: LegendPosition.bottom)),
                    series: chartData,
                    primaryXAxis: const CategoryAxis(
                      autoScrollingDelta: 2,
                      majorGridLines: MajorGridLines(width: 0),
                      majorTickLines: MajorTickLines(width: 0),
                      maximumLabelWidth: 60,
                      edgeLabelPlacement: EdgeLabelPlacement.shift,
                    ),
                    primaryYAxis: const NumericAxis(
                      edgeLabelPlacement: EdgeLabelPlacement.shift,
                    ),
                  ),
                ),
              )
            ],
          )),
    ));

    return ChangeNotifierProvider(
      create: (context) => Model(),
      child: chart,
    );
  }
}
