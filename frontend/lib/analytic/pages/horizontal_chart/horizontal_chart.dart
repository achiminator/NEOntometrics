import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/widgets/charts/grouped_horizontal_chart.dart';

class HorizontalChartPage extends StatelessWidget {
  const HorizontalChartPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      child: HorizontalBarChartWithSecondaryAxis.withSampleData(),
    );
  }
}
