///the new one

import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/helpers/save_file_web.dart';
import 'package:screenshot/screenshot.dart';
import 'package:syncfusion_flutter_charts/charts.dart';
import 'package:intl/intl.dart';
import 'package:syncfusion_flutter_pdf/pdf.dart';

class LineChart extends StatefulWidget {
  LineChart({Key? key}) : super(key: key);

  @override
  _LineChartState createState() => _LineChartState();
}

class _LineChartState extends State<LineChart> {
  TooltipBehavior? _tooltipBehavior;
  ScreenshotController screenshotController = ScreenshotController();

  @override
  void initState() {
    _tooltipBehavior = TooltipBehavior(enable: true);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    List selectedFile = [];
    var chart;
    DateTime commitTime;
    var metricResult;

    for (var file in analyticController.theSelectedOntologyFile) {
      selectedFile.add(file);
    }
    List<LineSeries> chartData = [];
    for (var metricName in analyticController.listString) {
      List<OntologyData> metricList = [];

      for (var ontologyFile in selectedFile) {
        metricResult = (ontologyFile == null || ontologyFile == false)
            ? 0
            : ontologyFile[metricName];
        commitTime = DateTime.parse(ontologyFile['CommitTime']);
        metricList.add(
            OntologyData(metricName, commitTime, double.parse(metricResult)));
      }
      chartData.add(LineSeries<OntologyData, num>(
        name: metricName,
        dataSource: metricList,
        xValueMapper: (OntologyData salesdata, _) => salesdata.year.year,
        yValueMapper: (OntologyData salesdata, _) => salesdata.sales,
      ));
    }

    SafeArea(
        child: Scaffold(
      body: chart = Column(
        children: [
          Container(
            width: MediaQuery.of(context).size.width / 1.3,
            height: MediaQuery.of(context).size.height / 1.4,
            child: Screenshot(
                child: chart = SfCartesianChart(
                  title: ChartTitle(text: 'Line Chart'),
                  legend: Legend(isVisible: true),
                  tooltipBehavior: _tooltipBehavior,
                  series: chartData,
                  primaryXAxis: NumericAxis(
                    edgeLabelPlacement: EdgeLabelPlacement.shift,
                  ),
                  primaryYAxis: NumericAxis(
                    labelFormat: '{value}',
                    //  numberFormat: NumberFormat.simpleCurrency(decimalDigits: 0)
                  ),
                ),
                controller: screenshotController),
          ),
          IconButton(
            onPressed: () {
              _exportAsPdf();
            },
            icon: Icon(
              Icons.download,
              color: Theme.of(context).colorScheme.secondaryContainer,
            ),
            tooltip: "Export as PDF",
          )
        ],
      ),
    ));
    return chart;
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

class OntologyData {
  OntologyData(this.name, this.year, this.sales);
  final name;
  final DateTime year;
  final double sales;
}
