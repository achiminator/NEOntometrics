import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/widgets/charts/barChart.dart';

class VerticalChartPage extends StatelessWidget {
  const VerticalChartPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(child: Barchart());
  }
}
