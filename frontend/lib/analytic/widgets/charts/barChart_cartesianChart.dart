import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/analytic_view.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/helpers/multiSelectionDialog.dart';

import 'package:neonto_frontend/analytic/models/data.dart';

import 'package:provider/provider.dart';
import 'package:syncfusion_flutter_charts/charts.dart';

class BarChartCa extends StatefulWidget {
  BarChartCa({Key? key}) : super(key: key);

  @override
  State<BarChartCa> createState() => _BarChartCaState();
}

class _BarChartCaState extends State<BarChartCa> {
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
    List<Color> listofColor = [];
    var model = Provider.of<Model>(context);
    var chart;
    List data = [];
    List<MultiSelectDialogItem<int>> multiItem = [];

    void populateMultiselect() {
      //to get all ontologyFiles
      int v = 0;
      for (var item in analyticController.listData) {
        multiItem
            .add(MultiSelectDialogItem(v, item['fileName'], item['commit']));
        v++;
      }
    }

    void getvaluefromkey(var selection) {
      if (selection != null) {
        for (var x in selection.toList()) {
          Map multiItemValue = {};
          multiItemValue.addAll({
            'value': multiItem[x].value,
            'label': multiItem[x].label,
            'data': multiItem[x].data
          });
          data.add(multiItemValue);
        }
        analyticController.listSelectedData = data;
        model.changeName();
      }
    }

    void _showMultiSelect(BuildContext context) async {
      multiItem = [];
      populateMultiselect();

      final items = multiItem;

      final selectedValues = await showDialog<Set<int>>(
        context: context,
        builder: (BuildContext context) {
          return MultiSelectDialog(
            items: items,
            initialSelectedValues: [0, 1].toSet(),
          );
        },
      );

      print(selectedValues);

      getvaluefromkey(selectedValues);
    }

    List<ChartSeries> chartData = [];

    int r = 38;
    int g = 199;
    int b = 172;
    if (analyticController.listSelectedData.isNotEmpty) {
      for (var ontologyFile in analyticController.listSelectedData) {
        Color theColor = Color.fromRGBO(r, g, b, 0.7);
        List<OntologyData1> metricList = [];
        for (var metricName in analyticController.listString) {
          if (ontologyFile['data'].isEmpty) continue;
          double metricResultNumber =
              (ontologyFile['data']['edges'].last['node'][metricName] == null ||
                      ontologyFile['data']['edges'].last['node'][metricName] ==
                          false)
                  ? 0
                  : double.parse(ontologyFile['data']['edges']
                      .last['node'][metricName]
                      .toString());
          metricList
              .add(OntologyData1(metricName, metricResultNumber, listofColor));
        }
        chartData.add(
          ColumnSeries<OntologyData1, dynamic>(
              enableTooltip: true,
              name: ontologyFile['label'],
              dataSource: metricList,
              xValueMapper: (OntologyData1 s, _) => s.metric,
              yValueMapper: (OntologyData1 s, _) => s.metricResult),
        );
        listofColor.add(theColor);
        r = r - 3;
        g = g - 5;
        b = b - 7;
      }
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
                      title: ElevatedButton(
                          style: ElevatedButton.styleFrom(elevation: 3),
                          child: Text('Select an ontology File'),
                          onPressed: () {
                            _showMultiSelect(context);
                          }),
                    ))
              ],
            ),
          ),
          body: Column(
            children: [
              Padding(
                padding: const EdgeInsets.only(top: 20.0, left: 20),
                child: Container(
                  width: MediaQuery.of(context).size.width / 1.3,
                  height: MediaQuery.of(context).size.height / 1.4,
                  child: SfCartesianChart(
                    tooltipBehavior: _tooltipBehavior,
                    zoomPanBehavior: _zoomPanBehavior,
                    legend: (Legend(
                        isVisible: true,
                        overflowMode: LegendItemOverflowMode.wrap,
                        position: LegendPosition.bottom)),
                    series: chartData,
                    primaryXAxis: CategoryAxis(
                      autoScrollingDelta: 2,
                      majorGridLines: MajorGridLines(width: 0),
                      majorTickLines: MajorTickLines(width: 0),
                      maximumLabelWidth: 60,
                      edgeLabelPlacement: EdgeLabelPlacement.shift,
                    ),
                    // palette: listofColor,
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

class Model2 with ChangeNotifier {
  var data = analyticController.listSelectedData;
  changeName() {
    data = analyticController.listSelectedData;
    notifyListeners();
  }
}

class MultiSelectDialogItem<V> {
  const MultiSelectDialogItem(this.value, this.label, this.data);

  final V value;
  final String label;
  final data;
}
