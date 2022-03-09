import 'dart:async' show Future;
import 'dart:ui';
import 'package:flutter/services.dart' show rootBundle;
import 'package:flutter/material.dart';
import 'package:flutter_math_fork/ast.dart';
import "metricexplorer.dart";
import "calculationengine.dart";
import "markdownhandler.dart";
import "metricData.dart";

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      scrollBehavior: MyCustomWebScrollBehavior(),
      title: 'NEOntometrics',
      theme: ThemeData(

        colorScheme: ColorScheme.fromSwatch(primarySwatch: Colors.teal),
      ),
      home: EntryPage(),
    );
  }
}

/// This custom class is necessary to enable scrolling not only with touch, but also with the
/// mouse. Especially the metric detail views need that.
class MyCustomWebScrollBehavior extends MaterialScrollBehavior {
  @override
  Set<PointerDeviceKind> get dragDevices => {
        PointerDeviceKind.mouse,
        PointerDeviceKind.stylus,
        PointerDeviceKind.touch
      };
}

class EntryPage extends StatelessWidget {
  Future<List<MetricExplorerItem>> metricData =
      MetricExplorerItemFactory().getMetricExplorerData();
  EntryPage({Key? key}) : super(key: key);

  // Future<String> homeDescription(String location) async {
  //   return await rootBundle.loadString("webpage/$location");
  // }

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,
      child: Scaffold(
        bottomNavigationBar: Container(
            color: Theme.of(context).colorScheme.primaryVariant,
            padding: const EdgeInsets.all(8),
            child: Text(
              "Made with 💖 by Achim Reiz.",
              textAlign: TextAlign.center,
              style: TextStyle(color: Theme.of(context).colorScheme.onPrimary),
            )),
        appBar: AppBar(
          bottom: const TabBar(
            tabs: [
              Tab(
                icon: Icon(Icons.home),
                text: "About NEOntometrics",
              ),
              Tab(icon: Icon(Icons.explore), text: "Metric Explorer"),
              Tab(
                  icon: Icon(Icons.calculate_outlined),
                  text: "Calculation Engine")
            ],
          ),
          title: const Text("NEOntometrics"),
          centerTitle: true,
        ),
        body: TabBarView(children: [
          MarkDownHandler().buildMarkDownElement("homepage.md"),
          MetricExplorer(metricData),
          CalculationEngine(metricData),
        ]),
      ),
    );
  }
}
