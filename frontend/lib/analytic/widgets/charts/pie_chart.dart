import 'package:neonto_frontend/analytic/constants/style.dart';
import 'package:neonto_frontend/analytic/helpers/save_file_web.dart';
import 'package:pie_chart/pie_chart.dart';
import 'package:flutter/material.dart';
import 'package:screenshot/screenshot.dart';
import 'package:syncfusion_flutter_pdf/pdf.dart';

class PieChartPages extends StatefulWidget {
  const PieChartPages({Key? key}) : super(key: key);

  @override
  State<PieChartPages> createState() => _PieChartPagesState();
}

class _PieChartPagesState extends State<PieChartPages> {
  ScreenshotController screenshotController = ScreenshotController();
  Map<String, double> dataMap = {
    "Classes": 30,
    "SuperClasses": 25,
    "Anonymous Classes": 10,
    "Axioms": 5,
  };

  List<Color> colorList = [
    Color.fromARGB(179, 90, 243, 200),
    Color.fromARGB(158, 38, 201, 155),
    Color.fromARGB(171, 33, 145, 113),
    Color.fromARGB(149, 7, 85, 63),
    Color.fromARGB(177, 3, 52, 38),
  ];

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
          padding: const EdgeInsets.only(left: 20, top: 80),
          child: Center(
            child: Container(
                width: MediaQuery.of(context).size.width / 1.5,
                height: MediaQuery.of(context).size.height / 1.5,
                decoration: BoxDecoration(
                    color: lightGrey,
                    borderRadius: BorderRadius.all(Radius.circular(4))),
                child: PieChart(
                  dataMap: dataMap,
                  animationDuration: Duration(milliseconds: 800),
                  chartLegendSpacing: 32,
                  chartRadius: MediaQuery.of(context).size.width / 3.2,
                  colorList: colorList,
                  initialAngleInDegree: 0,
                  chartType: ChartType.ring,
                  ringStrokeWidth: 32,
                  // centerText: "Mertics",
                  legendOptions: LegendOptions(
                    showLegendsInRow: false,
                    legendPosition: LegendPosition.right,
                    showLegends: true,
                    legendTextStyle: TextStyle(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  chartValuesOptions: ChartValuesOptions(
                    showChartValueBackground: true,
                    showChartValues: true,
                    showChartValuesInPercentage: false,
                    showChartValuesOutside: false,
                    decimalPlaces: 1,
                  ),
                )),
          ),
        ),
      );

  Future<void> _exportAsPdf() async {
    final image = await screen();

    final PdfBitmap pfdbit = PdfBitmap(image);
    final PdfDocument document = PdfDocument();
    document.pageSettings.size = Size(600, 500);
    final PdfPage page = document.pages.add();
    final Size pageSize = page.getClientSize();
    page.graphics.drawImage(pfdbit, Rect.fromLTWH(0, 0, 500, 400));
    await FileSave.fileSave(await document.save(), 'chart.pdf');
    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
      behavior: SnackBarBehavior.floating,
      shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.all(Radius.circular(4))),
      duration: Duration(milliseconds: 2000),
      content: Text('Chart has been exported as PDF document.'),
    ));
  }

  screen() {
    var image = screenshotController.capture();
    return image;
  }
}
