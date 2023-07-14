import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/pages/bar_chart.dart';
import 'package:neonto_frontend/analytic/pages/differences.dart';
import 'package:neonto_frontend/analytic/pages/line_chart.dart';

// routes
const verticalChartPageRoute = 'Vertical Chart';
const lineChartPageRoute = 'Line Chart';
const differencesPageRoute = 'Show me the \n differences';

// the list of all routes
List sideMenuItems = [
  verticalChartPageRoute,
  lineChartPageRoute,
  differencesPageRoute
];

Navigator localNavigator() => Navigator(
      key: navigationController.navigationKey,
      initialRoute: verticalChartPageRoute,
      onGenerateRoute: onGenerateRoute,
    );

Route<dynamic> onGenerateRoute(RouteSettings settings) {
  switch (settings.name) {
    case verticalChartPageRoute:
      return _getPageRoute(const VerticalChartPage());

    case lineChartPageRoute:
      return _getPageRoute(const LineChartPage());
    case differencesPageRoute:
      return _getPageRoute(Differences());

    default:
      return _getPageRoute(const Text("default"));
  }
}

// return the page route
PageRoute _getPageRoute(Widget child) {
  return MaterialPageRoute(builder: (context) => child);
}
