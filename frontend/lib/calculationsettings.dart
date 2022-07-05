import 'package:neonto_frontend/settings.dart';
import "calculationview.dart";
import "graphql.dart";
import 'package:graphql/client.dart';
import 'package:flutter/material.dart';
import 'package:neonto_frontend/markdown_handler.dart';
import 'package:neonto_frontend/metric_data.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';

import 'notifications.dart';

class CalculationEngine extends StatefulWidget {
  final Future<List<MetricExplorerItem>> metricData;
  const CalculationEngine(this.metricData, {Key? key}) : super(key: key);

  @override
  _CalculationEngineState createState() => _CalculationEngineState();
}

class _CalculationEngineState extends State<CalculationEngine> {
  bool reasoner = false;
  Set<MetricExplorerItem> selectedElementsForCalculation = {};
  _CalculationEngineState();
  late Widget markDownDescription = const CircularProgressIndicator();
  var urlController = TextEditingController();
  var graphQL = GraphQLHandler();
  @override
  void initState() {
    super.initState();
    markDownDescription =
        MarkDownHandler().buildMarkDownElement("calculationdescription.md");
  }

  @override
  Widget build(
    BuildContext context,
  ) {
    return SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(children: [
          SizedBox(
            height: 310,
            width: 1200,
            child:
                Row(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
              Expanded(
                child: Container(
                    height: 100,
                    margin: const EdgeInsets.all(50.0),
                    decoration: BoxDecoration(
                        //borderRadius: BorderRadius.circular(50),
                        color: Theme.of(context).colorScheme.secondaryContainer,
                        shape: BoxShape.circle),
                    child: Icon(Icons.live_help_outlined,
                        size: 115,
                        color: Theme.of(context).colorScheme.onSecondary)),
              ),
              Expanded(flex: 3, child: markDownDescription)
            ]),
          ),
          const Divider(),
          Container(
              margin: const EdgeInsets.all(15),
              padding: const EdgeInsets.all(20),
              child: Form(
                  child: Column(
                children: [
                  Container(
                    height: 50,
                    alignment: Alignment.center,
                    padding: const EdgeInsets.all(3),
                    margin: const EdgeInsets.only(top: 10, bottom: 20),
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(15),
                      color: Theme.of(context).colorScheme.secondary,
                    ),
                    child: Text(
                      "Run Calculation",
                      style: TextStyle(
                          color: Theme.of(context).colorScheme.onSecondary,
                          fontSize: 20),
                    ),
                  ),
                  SizedBox(
                    child: RepaintBoundary(
                      child: SwitchListTile(
                          value: reasoner,
                          secondary: const Icon(Icons.auto_graph),
                          contentPadding:
                              const EdgeInsets.symmetric(horizontal: 50),
                          title: const Text("Reasoner Active"),
                          onChanged: (value) {
                            if (value == true) {
                              var dialog = AlertDialog(
                                title: const ListTile(
                                    leading: Icon(Icons.warning),
                                    title: Text("Activating Reasoner")),
                                content: Text(
                                    "On larger ontologies, the reasoning takes a large amount of time. On ontologies > ${Settings().reasoningLimit}, the reasoning is deactivated. We recommend the reasoning capabilities just for smaller ontologies and repositories."),
                                actions: [
                                  TextButton(
                                    child: const Text("Yes, active reasoning"),
                                    onPressed: () {
                                      setState(() {
                                        reasoner = true;
                                      });
                                      Navigator.pop(context);
                                    },
                                  ),
                                  TextButton(
                                      onPressed: () => Navigator.pop(context),
                                      child: const Text("Abort"))
                                ],
                              );
                              showDialog(
                                  context: context,
                                  builder: (BuildContext context) => dialog);
                            } else {
                              setState(() {
                                reasoner = value;
                              });
                            }
                          }),
                    ),
                  ),
                  FutureBuilder(
                    future: widget.metricData,
                    builder: (context, snapshot) {
                      if (snapshot.connectionState != ConnectionState.done) {
                        return const Center(child: RefreshProgressIndicator());
                      } else {
                        return (Container(
                          padding: const EdgeInsets.all(20.0),
                          child: Container(
                              alignment: Alignment.topCenter,
                              child: buildCalculationSettings(
                                snapshot.data as List<MetricExplorerItem>,
                              )),
                        ));
                      }
                    },
                  ),
                  Row(
                    children: [
                      Container(
                        width: 300,
                        padding: const EdgeInsets.only(right: 25),
                        child: OutlinedButton(
                            onPressed: () {
                              var dialog =
                                  const AlreadyCalculatedSelectionOverlay();
                              showDialog(
                                      barrierDismissible: true,
                                      context: context,
                                      builder: (BuildContext context) {
                                        return dialog;
                                      })
                                  .then((value) =>
                                      {urlController.text = value ?? ""});
                            },
                            child: const ListTile(
                              leading: Icon(Icons.list),
                              title: Text("Already Calculated"),
                            )),
                      ),
                      Expanded(
                        child: TextFormField(
                          //This is the textfield. It triggers the analysis.
                          autovalidateMode: AutovalidateMode.onUserInteraction,
                          controller: urlController,
                          decoration: InputDecoration(
                              suffixIcon: IconButton(
                                onPressed: () {
                                  if (urlController.text == "") {
                                    Snacks(context).displayErrorSnackBar(
                                        "No valid ontology given. Please enter an URL to a valid Ontology.",
                                        context);
                                  } else {
                                    // At first, we ask the service if the ontology is already known in the system.
                                    Future<QueryResult<dynamic>> response =
                                        graphQL
                                            .queueFromAPI(urlController.text);
                                    response.then((graphQlResponse) {
                                      if (graphQlResponse.hasException) {
                                        Snacks(context).displayErrorSnackBar(
                                            graphQlResponse.exception
                                                .toString(),
                                            context);
                                      }
                                      var queueInformation = QueueInformation(
                                          graphQlResponse.data);
                                      if (queueInformation.error) {
                                        Snacks(context).displayErrorSnackBar(
                                            queueInformation.errorMessage,
                                            context);
                                      }
                                      // The reponse urlInSystem = False states that there is no information on the given ontology already stored in the system.
                                      // Thus, we ask the user if we shall put it into the queue.
                                      else if (!queueInformation.urlInSystem) {
                                        showDialog(
                                            context: context,
                                            builder:
                                                (BuildContext context) =>
                                                    AlertDialog(
                                                      title: const Text(
                                                          "Data not yet in Database"),
                                                      content: Text(
                                                          "There is no data yet in the system. Would you like to calculate ontology metrics for the given URL?\n${urlController.text}"),
                                                      actions: [
                                                        TextButton(
                                                            child: const Text(
                                                                "Yes, Put in Queue"),
                                                            onPressed: () {
                                                              response = graphQL
                                                                  .putInQueue(
                                                                      urlController
                                                                          .text,
                                                                      reasoner);
                                                              response.then(
                                                                  (jsonResponse) {
                                                                if (jsonResponse
                                                                    .hasException) {
                                                                  Snacks(context).displayErrorSnackBar(
                                                                      jsonResponse
                                                                          .exception
                                                                          .toString(),
                                                                      context);
                                                                } else {
                                                                  Snacks(context).progressSnackBar(
                                                                      QueueInformation(
                                                                          jsonResponse
                                                                              .data?["update_queueInfo"]));
                                                                }
                                                              });
                                                              Navigator.pop(
                                                                  context);
                                                            }),
                                                        TextButton(
                                                            child: const Text(
                                                                "Abort"),
                                                            onPressed: () =>
                                                                Navigator.pop(
                                                                    context))
                                                      ],
                                                    ));
                                      } else if (!queueInformation
                                              .taskFinished &&
                                          !queueInformation.performsUpdate) {
                                        Snacks(context).progressSnackBar(queueInformation);
                                      }
                                      // If the first query on the queue information state that the metrics are already in the ontology file,
                                      // then, retrieve the date with another GraphQL query.F
                                      else if (queueInformation.taskFinished ||
                                          queueInformation.performsUpdate) {
                                        String graphQlQueryAppender = graphQL
                                            .selectedMetrics2GraphQLInsertion(
                                                selectedElementsForCalculation);

                                        Future<QueryResult<dynamic>>
                                            futureResonse =
                                            graphQL.getMetricsFromAPI(
                                                urlController.text,
                                                graphQlQueryAppender);
                                        futureResonse.then((graphQlResponse) {
                                          if (graphQlResponse.hasException) {
                                            EasyLoading.dismiss();
                                            Snacks(context).displayErrorSnackBar(
                                                graphQlResponse.exception
                                                    .toString(),
                                                context);
                                          } else {
                                            EasyLoading.dismiss();
                                            Navigator.push(context,
                                                MaterialPageRoute(
                                                    builder: (context) {
                                              return CalculationView(
                                                  RepositoryData(
                                                      graphQlResponse.data),
                                                  urlController.text, queueInformation, reasoner);
                                            }));
                                          }
                                        });
                                        EasyLoading.show(
                                            status:
                                                "We're fetching the Ontology Data...");
                                      }
                                    });
                                  }
                                },
                                icon: const Icon(Icons.send),
                                tooltip: "Send",
                              ),
                              icon: const Icon(Icons.public),
                              hintText:
                                  "Paste the URL to the Ontology or the Repository here",
                              helperText: "URL to Ontology"),
                          toolbarOptions: const ToolbarOptions(
                              paste: true, selectAll: true),
                        ),
                      ),
                    ],
                  ),
                ],
              )))
        ]));
  }



  /// Takes the root element of the [List<MetricExplorerItem>] after the return call.
  /// First extracts the elemental metrics, afterwards iterates over all other subClasses.
  Widget buildCalculationSettings(List<MetricExplorerItem> data) {
    List<Widget> calculationCategorySetting = [];
    //at first, add the elemental metrics, which are always at the top of the list.
    calculationCategorySetting.add(buildCalculationSetting(data[0]));
    for (var element in data[1].subClass) {
      calculationCategorySetting
          //The expanded is necessary because the elements will be wrapped in a Collumn Item.
          .add(buildCalculationSetting(element));
    }

    return Column(
      children: calculationCategorySetting,
    );
  }

  ///Builds the Selection Widget for each metric category and the associated sub metrics on the bases of [MetricExplorerItem].
  Widget buildCalculationSetting(MetricExplorerItem data,
      [bool initiallyActive = false]) {
    var leafElements =
        MetricExplorerItem.getLeafItems(data, onlyCalculatableClasses: true);
    if (initiallyActive) {
      selectedElementsForCalculation.addAll(leafElements);
    }
    return Container(
        padding: const EdgeInsets.all(10),
        child: CheckboxListTile(
          title: Text(data.itemName),
          secondary: const Icon(Icons.account_tree_outlined),
          subtitle: Wrap(
            spacing: 3,
            runSpacing: 3,
            children: (List<MetricExplorerItem> data) {
              List<Widget> chips = [];
              for (MetricExplorerItem item in data) {
                chips.add(RepaintBoundary(
                  child: Tooltip(
                      message: (item.definition) != ""
                          ? item.definition
                          : item.description,
                      child: FilterChip(
                        label: Text(item.itemName),
                        selected: selectedElementsForCalculation.contains(item),
                        onSelected: (bool value) {
                          setState(() {
                            if (value) {
                              selectedElementsForCalculation.add(item);
                            } else {
                              selectedElementsForCalculation.remove(item);
                            }
                          });
                        },
                      )),
                ));
              }
              return chips;
            }(leafElements),
          ),
          value: selectedElementsForCalculation.containsAll(leafElements),
          onChanged: (value) => setState(
            () {
              if (value ?? false) {
                selectedElementsForCalculation.addAll(leafElements);
              } else {
                selectedElementsForCalculation.removeAll(leafElements);
              }
            },
          ),
        ));
  }
}

class AlreadyCalculatedSelectionOverlay extends StatelessWidget {
  const AlreadyCalculatedSelectionOverlay({
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final graphQL = GraphQLHandler();
    var f = graphQL.getRepositoryList();

    return FutureBuilder(
        future: f,
        builder: (context, snapshot) {
          if (!snapshot.hasData) {
            EasyLoading.show(status: "We're fetching existing Repositories");
            return const Text("");
          }
          if (snapshot.hasData) {
            var result = snapshot.data as QueryResult<dynamic>;
            var simpleDialogOptions = <SimpleDialogOption>[];

            for (var element in result.data!["repositoriesInformation"]) {
              simpleDialogOptions.add(SimpleDialogOption(
                child: Row(
                  children: [
                    Expanded(child: Text(element["repository"])),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 2),
                      child: Chip(
                        label: Text(
                          element["ontologyFiles"].toString() + " ",
                          textAlign: TextAlign.right,
                        ),
                        avatar: const Icon(Icons.filter_none),
                      ),
                    ),
                    Chip(
                      label: Text(
                        element["analyzedOntologyCommits"].toString(),
                        textAlign: TextAlign.right,
                      ),
                      avatar: const Icon(Icons.network_ping),
                    ),
                  ],
                ),
                onPressed: () =>
                    Navigator.pop(context, element["repository"] ?? ""),
              ));
            }

            Widget simpleDialog = SimpleDialog(
                title: const Text(
                    " Select one of the already calcualated ontology repositories:"),
                children: simpleDialogOptions);
            EasyLoading.dismiss();
            return simpleDialog;
          }
          throw Exception("Failure in Fechting Repository Information");
        });

    //EasyLoading.dismiss();
  }
}

