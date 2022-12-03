/// Example of a combo time series chart with two series rendered as lines, and
/// a third rendered as points along the top line with a different color.
///
/// This example demonstrates a method for drawing points along a line using a
/// different color from the main series color. The line renderer supports
/// drawing points with the "includePoints" option, but those points will share
/// the same color as the line.
import 'dart:js_util';

import 'package:charts_flutter/flutter.dart' as charts;
import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/constants/color.dart';
import 'package:neonto_frontend/analytic/constants/style.dart';

import 'package:neonto_frontend/analytic/controllers/controllers.dart';

import 'package:neonto_frontend/analytic/helpers/save_file_web.dart';
import 'package:neonto_frontend/metric_data.dart';
import 'package:screenshot/screenshot.dart';
import 'package:syncfusion_flutter_pdf/pdf.dart';

class DateTimeComboLinePointChart extends StatefulWidget {
  final List<charts.Series<dynamic, DateTime>> seriesList;
  final bool? animate;

  DateTimeComboLinePointChart(this.seriesList, {this.animate});

  /// Creates a [TimeSeriesChart] with sample data and no transition.
  factory DateTimeComboLinePointChart.withSampleData() {
    return DateTimeComboLinePointChart(
      _createSampleData(),
      animate: true,
    );
  }

  @override
  State<DateTimeComboLinePointChart> createState() =>
      _DateTimeComboLinePointChartState();

  /// Create one series with sample hard coded data.
  static List<charts.Series<TimeSeriesSales, DateTime>> _createSampleData() {
    List<charts.Series<TimeSeriesSales, DateTime>> ResultList = [];
    int index = 0;
    List files = [];
    List firstOntologyFile = [];
    for (var file in analyticController.listData) {
      files.add(file['fileName']);
    }

    if (files.length > 1) {
      firstOntologyFile.add(analyticController.listData.first);
    } else {
      firstOntologyFile.add(analyticController.listData.first);
    }
    List selectedFile = [];
    for (var f in analyticController.theSelectedOntologyFile) {
      selectedFile.add(f);
    }

    print(selectedFile);
    //print(firstOntologyFile);

    for (var ontolgyFile in selectedFile) {
      List<List<TimeSeriesSales>> OntologyDataList = [];
      List<TimeSeriesSales> metricList = [];
      print(ontolgyFile);

      for (var metricName in analyticController.listString) {
        print(metricName);
        DateTime commitTime =
            DateTime.parse(ontolgyFile['CommitTime'].toString());
        print(commitTime);
        var metricResult = (ontolgyFile == null || ontolgyFile == false)
            ? 0
            : ontolgyFile[metricName];
        print(metricResult);

        // print('${metricName}, ${commitTime}, ${metricResult}');
        metricList.add(TimeSeriesSales(
            commitTime, int.parse(metricResult), colorList[index]));
      }
      OntologyDataList.add(metricList);
      charts.Series<TimeSeriesSales, DateTime> chart =
          charts.Series<TimeSeriesSales, DateTime>(
              data: metricList,
              id: 'metricName',
              domainFn: (TimeSeriesSales sales, _) => sales.time,
              measureFn: (TimeSeriesSales sales, _) => sales.sales,
              colorFn: (TimeSeriesSales sales, _) => sales.color,
              labelAccessorFn: (TimeSeriesSales sales, _) => '${sales.sales}');

      ResultList.add(chart);
      index++;
    }

    return ResultList;
  }
}

class _DateTimeComboLinePointChartState
    extends State<DateTimeComboLinePointChart> {
  ScreenshotController screenshotController = ScreenshotController();
  String? value;

  @override
  Widget build(BuildContext context) {
    List<String> metrics = [];

    for (var item2 in analyticController.listString) {
      metrics.add(item2);
    }

    return Stack(
      children: [
        Positioned(
          left: 40,
          top: 20,
          child: Column(
            children: [
              buildchart(),
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
          ),
        ),
        Positioned(
          left: 850,
          top: 50,
          child: Container(
            width: 300,
            padding: EdgeInsets.symmetric(horizontal: 12, vertical: 4),
            decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.secondaryContainer,
                borderRadius: BorderRadius.circular(8),
                border:
                    Border.all(color: Theme.of(context).colorScheme.secondary)),
            child: DropdownButtonHideUnderline(
              child: DropdownButton<String>(
                  hint: Text(
                    "select a metric",
                    style: TextStyle(
                        color: Theme.of(context).colorScheme.onSecondary),
                  ),
                  dropdownColor:
                      Theme.of(context).colorScheme.secondaryContainer,
                  isExpanded: true,
                  value: value,
                  items: metrics.map(buildMenuItem).toList(),
                  onChanged: (value) => setState(() {
                        this.value = value;
                      })),
            ),
          ),
        ),
      ],
    );
  }

  DropdownMenuItem<String> buildMenuItem(String item) => DropdownMenuItem(
      value: item,
      child: Text(
        item,
        style: TextStyle(color: Theme.of(context).colorScheme.onSecondary),
      ));

  buildchart() {
    return Screenshot(
        controller: screenshotController,
        child: Padding(
            padding: const EdgeInsets.only(left: 20, top: 30),
            child: Container(
                width: MediaQuery.of(context).size.width / 1.9,
                height: MediaQuery.of(context).size.height / 1.4,
                decoration: BoxDecoration(
                    color: lightGrey,
                    borderRadius: BorderRadius.all(Radius.circular(6))),
                child: charts.TimeSeriesChart(
                  widget.seriesList,
                  animate: widget.animate,

                  // Configure the default renderer as a line renderer. This will be used
                  // for any series that does not define a rendererIdKey.
                  //
                  // This is the default configuration, but is shown here for  illustration.
                  defaultRenderer: charts.LineRendererConfig(),
                  // Custom renderer configuration for the point series.
                  customSeriesRenderers: [
                    charts.PointRendererConfig(
                        // ID used to link series to this renderer.
                        customRendererId: 'customPoint')
                  ],

                  // Optionally pass in a [DateTimeFactory] used by the chart. The factory
                  // should create the same type of [DateTime] as the data provided. If none
                  // specified, the default creates local date time.
                  dateTimeFactory: const charts.LocalDateTimeFactory(),
                ))));
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
    var image = screenshotController.capture();
    return image;
  }
}

/// Sample time series data type.
class TimeSeriesSales {
  final DateTime time;
  final int sales;
  final charts.Color color;

  TimeSeriesSales(
    this.time,
    this.sales,
    this.color,
  );
}
