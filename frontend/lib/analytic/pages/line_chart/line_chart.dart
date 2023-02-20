import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/widgets/charts/line_chart.dart';

class LineChartPage extends StatelessWidget {
  const LineChartPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(child: LineChart());
  }
}
