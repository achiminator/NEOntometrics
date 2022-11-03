/// Bar chart with example of a legend with customized position, justification,
/// desired max rows, padding, and entry text styles. These options are shown as
/// an example of how to use the customizations, they do not necessary have to
/// be used together in this way. Choosing [end] as the position does not
/// require the justification to also be [endDrawArea].
// ignore_for_file: unused_local_variable, prefer_const_constructors

import 'package:flutter/material.dart';
import 'package:charts_flutter/flutter.dart' as charts;
import 'package:neonto_frontend/analytic/constants/color.dart';
import 'package:neonto_frontend/analytic/constants/style.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/models/data.dart';
import 'package:screenshot/screenshot.dart';
import 'package:neonto_frontend/analytic/helpers/save_file_web.dart';
import 'package:syncfusion_flutter_pdf/pdf.dart';

class LegendOptions extends StatefulWidget {
  final List<charts.Series<dynamic, String>> seriesList;
  final bool? animate;

  LegendOptions(this.seriesList, {this.animate});

  factory LegendOptions.withSampleData() {
    return LegendOptions(
      _createSampleData(),
      animate: true,
    );}
  @override
  State<LegendOptions> createState() => _LegendOptionsState();
/// Create series list with multiple series
  static List<charts.Series<OntologyData, String>> _createSampleData() {
    List<charts.Series<OntologyData, String>> ResultList = [];
     int index = 0;
    //List data are Ontologyfiles
    for (var ontologyFile in analyticController.listData) {
      List<OntologyData> OntologyDataList = [];
      //listString are metrics
      for (var metricName in analyticController.listString) {
        double metricResultNumber =
            (ontologyFile['commit'][metricName] == null ||
                    ontologyFile['commit'][metricName] == false)
                ? 0
                : double.parse(ontologyFile['commit'][metricName].toString());
        OntologyDataList.add(
            OntologyData(metricName, metricResultNumber, colorList[index]));
      }
      charts.Series<OntologyData, String> chart =
          charts.Series<OntologyData, String>(
        id: ontologyFile['fileName'],
        labelAccessorFn: (OntologyData ontologyData, _) =>
            '${ontologyData.metricResult.toStringAsFixed(2)}',
        domainFn: (OntologyData ontologyData, _) => ontologyData.metric,
        measureFn: (OntologyData ontologyData, _) => ontologyData.metricResult,
        colorFn: (OntologyData ontologyData, _) => ontologyData.color,
        data: OntologyDataList,
      );
      index++;
      ResultList.add(chart);
    }
    return ResultList;
  }
}

class _LegendOptionsState extends State<LegendOptions> {
  ScreenshotController screenshotController = ScreenshotController();

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        buildChart(),
        Padding(
          padding: const EdgeInsets.only(top: 8.0),
          child: IconButton(
            onPressed: () {
              _exportAsPdf();
            },
            icon: Icon(
              Icons.download,
              color: Theme.of(context).colorScheme.secondaryContainer,
            ),
            tooltip: "Export as PDF",
          ),
        ),
      ],
    );
  }

  Widget buildChart() => Screenshot(
        controller: screenshotController,
        child: Padding(
          padding: const EdgeInsets.only(left: 20, top: 30),
          child: Container(
            width: MediaQuery.of(context).size.width / 1.3,
            height: MediaQuery.of(context).size.height / 1.4,
            decoration: BoxDecoration(
                color: lightGrey,
                borderRadius: BorderRadius.all(Radius.circular(6))),
            child: charts.BarChart(
              widget.seriesList,
              animate: widget.animate,
              domainAxis: charts.OrdinalAxisSpec(
                renderSpec: charts.SmallTickRendererSpec(
                    minimumPaddingBetweenLabelsPx: 5,
                    labelAnchor: charts.TickLabelAnchor.before,
                    labelStyle: charts.TextStyleSpec(
                      fontSize: 10,
                      color: charts.MaterialPalette.black,
                    ),
                    // labelRotation: 60,
                    // Change the line colors to match text color.
                    lineStyle: charts.LineStyleSpec(
                        color: charts.MaterialPalette.black)),
                //  viewport: new charts.OrdinalViewport('', 2),
              ),

              barRendererDecorator: charts.BarLabelDecorator<String>(),

              barGroupingType: charts.BarGroupingType.grouped,

              // Add the legend behavior to the chart to turn on legends.
              // This example shows how to change the position and justification of
              // the legend, in addition to altering the max rows and padding.
              behaviors: [
                charts.SeriesLegend(
                  // Positions for "start" and "end" will be left and right respectively
                  // for widgets with a build context that has directionality ltr.
                  // For rtl, "start" and "end" will be right and left respectively.
                  // Since this example has directionality of ltr, the legend is
                  // positioned on the right side of the chart.
                  position: charts.BehaviorPosition.bottom,

                  // For a legend that is positioned on the left or right of the chart,
                  // setting the justification for [endDrawArea] is aligned to the
                  // bottom of the chart draw area.
                  outsideJustification:
                      charts.OutsideJustification.startDrawArea,
                  // By default, if the position of the chart is on the left or right of
                  // the chart, [horizontalFirst] is set to false. This means that the
                  // legend entries will grow as new rows first instead of a new column.
                  horizontalFirst: false,
                  // By setting this value to 2, the legend entries will grow up to two
                  // rows before adding a new column.
                  desiredMaxRows: 5,
                  // This defines the padding around each legend entry.
                  cellPadding: EdgeInsets.only(right: 4.0, bottom: 8.0),
                  // Render the legend entry text with custom styles.
                  entryTextStyle: charts.TextStyleSpec(
                      color: charts.Color(r: 127, g: 63, b: 191),
                      fontFamily: 'Georgia',
                      fontSize: 11),
                ),

                //  new charts.SlidingViewport(),
                //   charts.PanAndZoomBehavior(),
              ],
            ),
          ),
        ),
      );

  Future<void> _exportAsPdf() async {
    final image = await screen();

    final PdfBitmap pfdbit = PdfBitmap(image);
    final PdfDocument document = PdfDocument();
    document.pageSettings.size = Size(750, 750);
    final PdfPage page = document.pages.add();
    final Size pageSize = page.getClientSize();
    page.graphics.drawImage(pfdbit, Rect.fromLTWH(0, 0, 700, 700));
    await FileSave.fileSave(await document.save(), 'chart.pdf');
    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
      behavior: SnackBarBehavior.floating,
      shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.all(Radius.circular(5))),
      duration: Duration(milliseconds: 2000),
      content: Text('Chart has been exported as PDF document.'),
    ));
  }

  screen() {
    var image = screenshotController.capture();
    return image;
  }
}
