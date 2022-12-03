import 'package:flutter/material.dart';
import 'package:charts_flutter/flutter.dart' as charts;
import 'package:neonto_frontend/analytic/constants/color.dart';
import 'package:neonto_frontend/analytic/constants/style.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/models/data.dart';
import 'package:neonto_frontend/analytic/helpers/save_file_web.dart';
import 'package:screenshot/screenshot.dart';
import 'package:syncfusion_flutter_pdf/pdf.dart';

/// Example of using a primary and secondary axis (left & right respectively)
/// for a set of grouped bars. This is useful for comparing Series that have
/// different units (revenue vs clicks by region), or different magnitudes (2017
/// revenue vs 1/1/2017 revenue by region).
///
/// The first series plots using the primary axis to position its measure
/// values (bar height). This is the default axis used if the measureAxisId is
/// not set.
///
/// The second series plots using the secondary axis due to the measureAxisId of
/// secondaryMeasureAxisId.
///
/// Note: primary and secondary may flip left and right positioning when
/// RTL.flipAxisLocations is set.
class HorizontalBarChartWithSecondaryAxis extends StatefulWidget {
  static const secondaryMeasureAxisId = 'secondaryMeasureAxisId';
  final List<charts.Series<OntologyData, String>> seriesList;
  final bool? animate;

  HorizontalBarChartWithSecondaryAxis(this.seriesList, {this.animate});

  factory HorizontalBarChartWithSecondaryAxis.withSampleData() {
    return new HorizontalBarChartWithSecondaryAxis(
      _createSampleData(),
      animate: true,
    );
  }

  @override
  State<HorizontalBarChartWithSecondaryAxis> createState() =>
      _HorizontalBarChartWithSecondaryAxisState();

  /// Create series list with multiple series
  static List<charts.Series<OntologyData, String>> _createSampleData() {
    List<charts.Series<OntologyData, String>> resultList = [];

    int index = 0;
    for (var ontologyFile in analyticController.listData) {
      List<OntologyData> ontologyDataList = [];
      for (var metricName in analyticController.listString) {
        double metricResultNumber =
            (ontologyFile['commit']['edges'].last['node'][metricName] == null ||
                    ontologyFile['commit']['edges'].last['node'][metricName] ==
                        false)
                ? 0
                : double.parse(ontologyFile['commit']['edges']
                    .last['node'][metricName]
                    .toString());
        ontologyDataList.add(
            OntologyData(metricName, metricResultNumber, colorList1[index]));
      }
      charts.Series<OntologyData, String> chart =
          charts.Series<OntologyData, String>(
        id: ontologyFile['fileName'],
        labelAccessorFn: (OntologyData ontologyData, _) =>
            '${ontologyData.metricResult.toStringAsFixed(2)}',
        domainFn: (OntologyData ontologyData, _) => ontologyData.metric,
        measureFn: (OntologyData ontologyData, _) => ontologyData.metricResult,
        colorFn: (OntologyData ontologyData, _) => ontologyData.color,
        data: ontologyDataList,
      );
      index++;
      resultList.add(chart);
    }
    return resultList;
  }
}

class _HorizontalBarChartWithSecondaryAxisState
    extends State<HorizontalBarChartWithSecondaryAxis> {
  ScreenshotController screenshotController = ScreenshotController();

  @override
  Widget build(BuildContext context) {
    // For horizontal bar charts, set the [vertical] flag to false.
    return Column(
      children: [
        buildchart(),
        /*  Padding(
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
        */
      ],
    );
  }

  Widget buildchart() {
    return Screenshot(
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
              viewport: charts.OrdinalViewport('', 2),
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
                outsideJustification: charts.OutsideJustification.startDrawArea,
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
              charts.InitialSelection(selectedDataConfig: [
                charts.SeriesDatumConfig<String>('Clicks', '')
              ]),
              charts.SlidingViewport(),
              charts.PanAndZoomBehavior(),
            ],
            vertical: false,
            // It is important when using both primary and secondary axes to choose
            // the same number of ticks for both sides to get the gridlines to line
            // up.
            primaryMeasureAxis: charts.NumericAxisSpec(
                tickProviderSpec:
                    charts.BasicNumericTickProviderSpec(desiredTickCount: 3)),
            secondaryMeasureAxis: charts.NumericAxisSpec(
                tickProviderSpec:
                    charts.BasicNumericTickProviderSpec(desiredTickCount: 3)),
          ),
        ),
      ),
    );
  }

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
    var image;
    image = screenshotController.capture();
    return image;
  }
}
