import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/models/differences_model.dart';
import 'package:neonto_frontend/metric_data.dart';

class Differences extends StatefulWidget {
  Differences({Key? key}) : super(key: key);

  @override
  _DifferencesState createState() => _DifferencesState();
}

class _DifferencesState extends State<Differences> {
  final List<Item>? _ontologyFiles = generateItems(
      analyticController.repositoryNameForCommits!.ontologyFiles.length);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        automaticallyImplyLeading: false,
        elevation: 0.0,
        title: Row(
          children: [
            Expanded(
                flex: 2,
                child: ListTile(
                  iconColor: Theme.of(context).colorScheme.onPrimary,
                  textColor: Theme.of(context).colorScheme.onPrimary,
                  title: const Text("Analytic View"),
                  subtitle: Text(analyticController.repositoryName),
                  leading: const Icon(Icons.add_chart_outlined),
                )),
          ],
        ),
      ),
      body: SingleChildScrollView(
        scrollDirection: Axis.vertical,
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.all(20.0),
              child: Column(
                children: [
                  Container(
                    alignment: Alignment.bottomCenter,
                    child: Text(
                      'all ontology files: ${analyticController.listData.length}, changed files: ${analyticController.theNumberOfChangedFiles}',
                      style: const TextStyle(
                          fontSize: 18, fontWeight: FontWeight.w300),
                    ),
                  ),
                  const Padding(
                    padding: EdgeInsets.all(8.0),
                    child: Divider(),
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(20.0),
              child: Container(
                child: _buildPanel(),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPanel() {
    if (_ontologyFiles!.isNotEmpty) {
      return ExpansionPanelList(
        expansionCallback: (int index, bool isExpanded) {
          setState(() {
            _ontologyFiles![index].isExpanded = !isExpanded;
          });
        },
        children: _ontologyFiles!.map<ExpansionPanel>((Item item) {
          return ExpansionPanel(
            headerBuilder: (BuildContext context, bool isExpanded) {
              return ListTile(
                title: Text(item.ontologyFileName),
              );
            },
            body: Column(
              children: [
                for (var i in item.expandedMetrics)
                  ListTile(
                      title: Text(i.metricName),
                      subtitle: Text(
                          'previous version: ${i.previousVersion.toString()}, current version: ${i.currentVersion.toString()}'),
                      leading: i.theicon),
              ],
            ),
            isExpanded: item.isExpanded,
          );
        }).toList(),
      );
    } else {
      return Center(
        child: Container(
            alignment: Alignment.center,
            width: 400,
            height: 400,
            child: ListTile(
              title: const Text("No Ontology Metrics Found"),
              subtitle: const Text(
                "there are no changes between the last two versions ",
                softWrap: true,
              ),
              leading: Icon(
                Icons.do_not_disturb_alt,
                color: Theme.of(context).colorScheme.error,
              ),
            )),
      );
    }
  }
}

List<String> theMetricName = [];
List<String> theCurrentversion = [];
List<String> thePreviousVersion = [];

List<Item>? generateItems(int numberOfItems) {
  List<List<Items>> itemsOfItems = [];
  List fileName = [];
  RepositoryData? data = analyticController.repositoryNameForCommits;

  for (var metrics in data!.ontologyFiles) {
    List<Items> items = [];
    int i = 0;
    //if only one commit (i.e. one version) available in the ontolgyfile --> does not need to be added to the item list
    if (metrics.metrics.length == 1) {
      numberOfItems = numberOfItems - 1;
      continue;
    }
    //if no commit available in the ontolgy file --> does not need to be added to the item list
    if (metrics.metrics.isEmpty) {
      numberOfItems = numberOfItems - 1;
      continue;
    }
// insert the metrics in lists
    theCurrentversion =
        metrics.metrics.last.values.toList().getRange(11, 170).toList();
    thePreviousVersion =
        metrics.metrics.first.values.toList().getRange(11, 170).toList();
    theMetricName =
        metrics.metrics.first.keys.toList().getRange(11, 170).toList();

    for (var metricItem in theCurrentversion) {
      // i == 28,73,74,75,76,83, 133 the metrics here return a string
      if (i != 28 &&
          i != 73 &&
          i != 74 &&
          i != 75 &&
          i != 76 &&
          i != 83 &&
          i != 133) {
        if (theCurrentversion[i] == 'null') {
          theCurrentversion[i] = 0.toString();
        } else if (thePreviousVersion[i] == 'null') {
          thePreviousVersion[i] = 0.toString();
        } else if (double.parse(theCurrentversion[i]) ==
            double.parse(thePreviousVersion[i])) {
          i++;
        } else if (double.parse(theCurrentversion[i]) >
            double.parse(thePreviousVersion[i])) {
          items.add(
            Items(
              metrics.fileName,
              theMetricName[i].toString(),
              const Icon(
                Icons.arrow_upward_outlined,
                size: 20,
                color: Colors.green,
              ),
              currentVersion: theCurrentversion[i].toString(),
              previousVersion: thePreviousVersion[i].toString(),
            ),
          );
          i++;
        } else if (double.parse(theCurrentversion[i]) <
            double.parse(thePreviousVersion[i])) {
          items.add(
            Items(
              metrics.fileName,
              theMetricName[i].toString(),
              const Icon(
                Icons.arrow_downward_outlined,
                size: 20,
                color: Colors.red,
              ),
              currentVersion: theCurrentversion[i].toString(),
              previousVersion: thePreviousVersion[i].toString(),
            ),
          );
          i++;
        }
      } else if (i == 28 || i == 73 || i == 74 || i == 75 || i == 133) {
        // the metrics here return a link
        if (theCurrentversion[i] == 'null') {
          theCurrentversion[i] = '[]';
        }
        if (thePreviousVersion[i] == 'null') {
          thePreviousVersion[i] = '[]';
        }

        if (theCurrentversion[i] != thePreviousVersion[i]) {
          if (thePreviousVersion[i] == '[]') {
            thePreviousVersion[i] = 'no data';
          }
          if (theCurrentversion[i] == '[]') {
            theCurrentversion[i] = 'no data';
          }
          items.add(
            Items(
              metrics.fileName,
              theMetricName[i].toString(),
              const Icon(
                Icons.link,
                size: 20,
                color: Colors.blue,
              ),
              currentVersion: theCurrentversion[i].toString(),
              previousVersion: thePreviousVersion[i].toString(),
            ),
          );
        }
        i++;
      }
      // the metrics here return a boolen
      else if (i == 76 || i == 83) {
        if (theCurrentversion[i] != thePreviousVersion[i]) {
          items.add(
            Items(
              metrics.fileName,
              theMetricName[i].toString(),
              const Icon(
                Icons.question_mark,
                size: 20,
                color: Colors.pink,
              ),
              currentVersion: theCurrentversion[i].toString(),
              previousVersion: thePreviousVersion[i].toString(),
            ),
          );
        }
        i++;
      }
    }
    if (items.isEmpty) {
      numberOfItems = numberOfItems - 1;
    } else {
      fileName.add(metrics.fileName);
      itemsOfItems.add(items);
    }
  }

  analyticController.theNumberOfChangedFiles = itemsOfItems.length;
  return List.generate(numberOfItems, (int index) {
    return Item(
        ontologyFileName: 'Ontology file ${index + 1}: ${fileName[index]}',
        expandedMetrics: itemsOfItems[index]);
  });
}
