import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/models/differences_model.dart';
import 'package:neonto_frontend/metric_data.dart';

class Differences extends StatefulWidget {
  const Differences({Key? key}) : super(key: key);

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
              padding: const EdgeInsets.only(top: 15.0),
              child: SizedBox(
                height: MediaQuery.of(context).size.height / 6,
                width: MediaQuery.of(context).size.width / 1.3,
                child: const Center(
                  child: Text(
                      'The focus here is on the last two commits. The visualization results are not dependent on which metrics are selected in the calculation engine (as in the first two visualizations Typs),'
                      ' rather all metrics in the last two commits are compared with each other and if a metric has changed '
                      'compared to the previous version, then this change will be displayed.  ',
                      style:
                          TextStyle(fontWeight: FontWeight.w300, fontSize: 16)),
                ),
              ),
            ),
            const Divider(
              indent: 50,
              endIndent: 50,
            ),
            Padding(
              padding: const EdgeInsets.all(20.0),
              child: Column(
                children: [
                  Container(
                    alignment: Alignment.bottomCenter,
                    child: Text(
                      'all ontology files: ${analyticController.listData.length}, changed files: ${analyticController.theNumberOfChangedFiles}',
                      style: TextStyle(
                          color: Theme.of(context).colorScheme.primaryContainer,
                          fontSize: 18,
                          fontWeight: FontWeight.w400),
                    ),
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
            _ontologyFiles![index].isExpanded = !_ontologyFiles![index].isExpanded;
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
    theCurrentversion = metrics.metrics.last.values.toList();
    thePreviousVersion = metrics.metrics.first.values.toList();
    theMetricName = metrics.metrics.first.keys.toList();

    for (var i = 0; theCurrentversion.length > i; i++) {
      if (theCurrentversion[i] == 'null' || thePreviousVersion[i] == 'null') {
        continue;
      }

      if (double.tryParse(theCurrentversion[i]) ==
          double.tryParse(thePreviousVersion[i])) {
        continue;
      }
      if ((double.tryParse(theCurrentversion[i]) ?? 0) >
          (double.tryParse(thePreviousVersion[i]) ?? 0)) {
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
      } else {}
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
