import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/widgets/charts/grouped_vertical_chart.dart';

class VerticalChartPage extends StatelessWidget {
  const VerticalChartPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      child: LegendOptions.withSampleData(),
    );
  }
}
