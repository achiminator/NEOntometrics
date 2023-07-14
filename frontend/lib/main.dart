import 'dart:async' show Future;
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';
import 'package:get/get.dart';
import 'package:neonto_frontend/analytic/controllers/analytic_controller.dart';
import 'package:neonto_frontend/analytic/controllers/menu_controller.dart';
import 'package:neonto_frontend/analytic/controllers/navigation_controller.dart';
import 'package:neonto_frontend/indexpage.dart';
import 'package:neonto_frontend/settings.dart';
import 'package:neonto_frontend/trackerHelper.dart';
import 'metric_explorer.dart';
import 'calculationsettings.dart';
import 'metric_data.dart';
import 'package:matomo_tracker/matomo_tracker.dart';

void main() {
  Get.put(MenuuController());
  Get.put(NavigationController());
  Get.put(AnalyticController());
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    MatomoTracker.instance.initialize(
      siteId: Settings.matomoWebsiteID,
      url: Settings.matomoWebsite,
    );

    return MaterialApp(
      scrollBehavior: MyCustomWebScrollBehavior(),
      title: 'NEOntometrics',
      builder: EasyLoading
          .init(), // Necessary for showing the loading screen on the Calculation-Page
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
  final Future<List<MetricExplorerItem>> metricData =
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
            color: Theme.of(context).colorScheme.primaryContainer,
            padding: const EdgeInsets.all(8),
            child: Row(mainAxisAlignment: MainAxisAlignment.center, children: [
              Text(
                "NEOntometrics.",
                textAlign: TextAlign.center,
                style:
                    TextStyle(color: Theme.of(context).colorScheme.onPrimary),
              ),
              TextButton(
                  onPressed: () => TrackerHelper.htmlOpenWindow(
                      "https://www.wirtschaftsinformatik.uni-rostock.de/footer/impressum/",
                      "Impressum"),
                  child: Text(
                    "Impressum",
                    style: TextStyle(
                        color: Theme.of(context).colorScheme.onPrimary,
                        decoration: TextDecoration.underline),
                  )),
              TextButton(
                  onPressed: () => TrackerHelper.htmlOpenWindow(
                      "https://www.wirtschaftsinformatik.uni-rostock.de/en/footer/imprint/",
                      "Impressum"),
                  child: Text(
                    "Imprint",
                    style: TextStyle(
                        color: Theme.of(context).colorScheme.onPrimary,
                        decoration: TextDecoration.underline),
                  ))
            ])),
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
        body: contentOrToSmall(context),
      ),
    );
  }

  /// Implements a check on whether the screen size is sufficient to show the content.
  Widget contentOrToSmall(BuildContext context) {
    if (MediaQuery.of(context).size.width < 1000 ||
        MediaQuery.of(context).size.height < 600) {
      return const Center(
        child: ListTile(
            title: Text("Desktop App"),
            subtitle: Text(
              "The NEOntometrics application is optimized for desktop computers. Please visit it with a device with proper screen size.",
              softWrap: true,
            ),
            leading: Icon(
              Icons.desktop_windows,
            )),
      );
    } else {
      return TabBarView(children: [
        const IndexPage(),
        MetricExplorer(metricData),
        CalculationEngine(metricData),
      ]);
    }
  }
}
