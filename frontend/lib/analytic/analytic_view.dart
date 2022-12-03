import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/helpers/reponsiveness.dart';
import 'package:neonto_frontend/analytic/widgets/large_screen.dart';
import 'package:neonto_frontend/analytic/widgets/Sidemenu/menu.dart';
import 'package:neonto_frontend/analytic/widgets/small_screen.dart';
import 'package:multiselect/multiselect.dart';
import 'package:neonto_frontend/metric_data.dart';

class AnalyticView extends StatefulWidget {
  final String repositoryName;
  RepositoryData repositoryData;
  AnalyticView(this.repositoryName, this.repositoryData, {Key? key})
      : super(key: key);

  @override
  State<AnalyticView> createState() => _AnalyticViewState();
}

class _AnalyticViewState extends State<AnalyticView> {
  final GlobalKey<ScaffoldState> scaffoldKey = GlobalKey();

  @override
  int activeMetricFile = 0;
  Widget build(BuildContext context) {
    List<String> ontologyfiles = [];
    for (var item in analyticController.listData) {
      var name = item['fileName'];
      ontologyfiles.add(name);
    }

    return Scaffold(
        key: scaffoldKey,
        appBar: AppBar(
          title: Row(
            children: [
              Expanded(
                  flex: 3,
                  child: ListTile(
                    iconColor: Theme.of(context).colorScheme.onPrimary,
                    textColor: Theme.of(context).colorScheme.onPrimary,
                    title: const Text("Analytic View"),
                    subtitle: Text(widget.repositoryName),
                    leading: const Icon(Icons.add_chart_outlined),
                  )),
              Expanded(
                flex: 5,
                child: ListTile(
                    textColor: Theme.of(context).colorScheme.onPrimary,
                    iconColor: Theme.of(context).colorScheme.onPrimary,
                    leading: const Icon(Icons.filter_none_outlined),
                    title: DropdownButton(
                        dropdownColor: Theme.of(context).colorScheme.primary,
                        style: TextStyle(
                            color: Theme.of(context).colorScheme.onPrimary),
                        value: activeMetricFile,
                        items: getAvailableFileNames(widget.repositoryData),
                        onChanged: (value) => setState(() {
                              activeMetricFile = value as int;
                              //  print(widget.repositoryData.ontologyFiles[value].fileName);
                              var t = widget
                                  .repositoryData.ontologyFiles[value].metrics;

                              // print(t);
                              //  print(analyticController.theSelectedOntologyFile);
                              analyticController.theSelectedOntologyFile = t;
                            }))),
              ),
            ],
          ),
        ),
        drawer: ResponsiveWidget.isSmallScreen(context)
            ? const Drawer(child: Menu())
            : null,
        body: const ResponsiveWidget(
          largeScreen: LargeScreen(),
          smallScreen: SmallScreen(),
        ));
  }

  List<DropdownMenuItem<int>> getAvailableFileNames(
      RepositoryData repositoryData) {
    List<DropdownMenuItem<int>> ontologyFiles = [];
    int counter = 0;
    for (OntologyData ontologyData in repositoryData.ontologyFiles) {
      ontologyFiles.add(
        DropdownMenuItem(
          child: Text(ontologyData.fileName),
          value: counter,
        ),
      );
      //   print(ontologyData.fileName);
      //   print(ontologyData);
      counter++;
    }

    return ontologyFiles;
  }
}
