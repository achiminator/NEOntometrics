import 'dart:math';

import 'package:flutter/material.dart';
import 'package:charts_flutter/flutter.dart';
import 'package:charts_flutter/src/text_element.dart' as texte;
import 'package:charts_flutter/src/text_style.dart' as style;
import 'package:charts_flutter/flutter.dart' as charts;
import 'package:neonto_frontend/analytic/controllers/controllers.dart';

class CustomCircleSymbolRenderer extends CircleSymbolRenderer {
  @override
  void paint(ChartCanvas canvas, Rectangle bounds,
      {List? dashPattern,
      charts.Color? fillColor,
      charts.FillPatternType? fillPattern,
      charts.Color? strokeColor,
      double? strokeWidthPx}) {
    super.paint(canvas, bounds,
        fillColor: fillColor,
        strokeColor: strokeColor,
        strokeWidthPx: strokeWidthPx);
    canvas.drawRect(
        Rectangle(bounds.left - 5, bounds.top - 30, bounds.width + 10,
            bounds.height + 10),
        fill: Color.white);
    var textStyle = style.TextStyle();
    textStyle.color = Color.black;
    textStyle.fontSize = 15;
    canvas.drawText(
        texte.TextElement(analyticController.metricresult,
            style: textStyle), //es soll hier die M
        (bounds.left).round(),
        (bounds.top - 28).round());
  }
}
