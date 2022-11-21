import 'package:charts_flutter/flutter.dart' as charts;

class OntologyData {
  final String metric;
  final double metricResult;
  final charts.Color color;

  OntologyData(
    this.metric,
    this.metricResult,
    this.color,
  );
}
