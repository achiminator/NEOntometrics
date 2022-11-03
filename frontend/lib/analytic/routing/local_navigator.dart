import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/pages/horizontal_chart/horizontal_chart.dart';
import 'package:neonto_frontend/analytic/pages/line_chart/line_chart.dart';
import 'package:neonto_frontend/analytic/pages/pie_chart/pie_chart.dart';
import 'package:neonto_frontend/analytic/pages/vertical_chart/vertical_chart.dart';
import 'package:neonto_frontend/analytic/routing/routes.dart';

Navigator localNavigator() => Navigator(
      key: navigationController.navigationKey,
      initialRoute: verticalChartPageRoute,
      onGenerateRoute: onGenerateRoute,
    );

Route<dynamic> onGenerateRoute(RouteSettings settings) {
  switch (settings.name) {
    case verticalChartPageRoute:
      return _getPageRoute(const VerticalChartPage());
    case pieChartPageRoute:
      return _getPageRoute(const PieChartPage());
    case horizontalChartPageRoute:
      return _getPageRoute(const HorizontalChartPage());
    case lineChartPageRoute:
      return _getPageRoute(const LineChartPage());
    default:
      return _getPageRoute(const Text("default"));
  }
}

// return the page route
PageRoute _getPageRoute(Widget child) {
  return MaterialPageRoute(builder: (context) => child);
}
