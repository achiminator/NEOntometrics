///the new one

import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/analytic_view.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/helpers/availableNames.dart';
import 'package:provider/provider.dart';
import 'package:syncfusion_flutter_charts/charts.dart';

class LineChart extends StatefulWidget {
  const LineChart({Key? key}) : super(key: key);

  @override
  _LineChartState createState() => _LineChartState();
}

class _LineChartState extends State<LineChart> {
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
      model.changeName();
    }
    List<LineSeries> chartData = [];
    for (var metricName in analyticController.listString) {
      List<OntologyData> metricList = [];

      for (var ontologyFile in selectedFile) {
        if (ontologyFile[metricName] != "null") {
          var metricResult = double.parse(ontologyFile[metricName]);

          /* double metricResult = (ontologyFile[metricName] == null ||
                ontologyFile[metricName] == false)
            ? 0
            :  */
          commitTime = (ontologyFile[metricName] == null ||
                  ontologyFile[metricName] == false ||
                  ontologyFile[metricName] == 'null')
              ? 0
              : ontologyFile['CommitTime'];
          metricList.add(OntologyData(
              metricName, DateTime.parse(commitTime), metricResult));
        }
      }
      chartData.add(LineSeries<OntologyData, DateTime>(
          name: metricName,
          dataSource: metricList,
          xValueMapper: (OntologyData salesdata, _) => salesdata.year,
          yValueMapper: (OntologyData salesdata, _) => salesdata.sales,
          markerSettings: MarkerSettings(isVisible: true)));
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
          Container(
            width: MediaQuery.of(context).size.width / 1.3,
            height: MediaQuery.of(context).size.height / 1.4,
            child: SfCartesianChart(
              title: ChartTitle(text: 'Line Chart'),
              legend: Legend(isVisible: true),
              tooltipBehavior: _tooltipBehavior,
              zoomPanBehavior: _zoomPanBehavior,
              series: chartData,
              primaryXAxis: DateTimeAxis(
                autoScrollingDeltaType: DateTimeIntervalType.auto,
                edgeLabelPlacement: EdgeLabelPlacement.shift,
              ),
              primaryYAxis: NumericAxis(
                labelFormat: '{value}',
                //  numberFormat: NumberFormat.simpleCurrency(decimalDigits: 0)
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

class OntologyData {
  OntologyData(this.name, this.year, this.sales);
  final name;
  final year;
  final sales;
}
