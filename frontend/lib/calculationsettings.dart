import 'dart:convert';
import 'dart:io';
import "calculationview.dart";
import "graphql.dart";
import 'package:http/http.dart' as http;
import 'package:graphql/client.dart';

import 'package:flutter/material.dart';
import 'package:neonto_frontend/markdown_handler.dart';
import 'package:neonto_frontend/metric_data.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';

class CalculationEngine extends StatefulWidget {
  final Future<List<MetricExplorerItem>> metricData;
  const CalculationEngine(this.metricData, {Key? key}) : super(key: key);

  @override
  _CalculationEngineState createState() => _CalculationEngineState();
}

class _CalculationEngineState extends State<CalculationEngine> {
  bool reasoner = true;
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
            height: 200,
            child:
                Row(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
              Expanded(
                child: Container(
                    height: 100,
                    decoration: BoxDecoration(
                        //borderRadius: BorderRadius.circular(50),
                        color: Theme.of(context).colorScheme.secondaryVariant,
                        shape: BoxShape.circle),
                    child: Icon(Icons.live_help_outlined,
                        size: 115,
                        color: Theme.of(context).colorScheme.onSecondary)),
              ),
              Expanded(child: markDownDescription)
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
                    child: SwitchListTile(
                        value: reasoner,
                        secondary: const Icon(Icons.auto_graph),
                        contentPadding:
                            const EdgeInsets.symmetric(horizontal: 50),
                        title: const Text("Reasoner Active"),
                        onChanged: (value) => setState(() {
                              reasoner = value;
                            })),
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
                              var dialog = Dialog();
                              showDialog(
                                  barrierDismissible: true,
                                  context: context,
                                  builder: (BuildContext context) {
                                    return dialog;
                                  }).then((value) => {urlController.text = value??""});
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
                                  if (selectedElementsForCalculation.isEmpty) {
                                    displayErrorSnackBar(
                                        "Please select at least one metric.",
                                        context);
                                  } else if (urlController.text == "") {
                                    displayErrorSnackBar(
                                        "No valid ontology given. Please enter an URL to a valid Ontology.",
                                        context);
                                  } else {
                                    // At first, we ask the service if the ontology is already known in the system.
                                    Future<QueryResult<dynamic>> response =
                                        graphQL
                                            .queueFromAPI(urlController.text);
                                    response.then((graphQlResponse) {
                                      if (graphQlResponse.hasException) {
                                        displayErrorSnackBar(
                                            graphQlResponse.exception
                                                .toString(),
                                            context);
                                      }
                                      if (graphQlResponse
                                                  .data?["queueInformation"]
                                              ?["error"] ==
                                          true) {
                                        displayErrorSnackBar(
                                            graphQlResponse
                                                    .data?["queueInformation"]
                                                ?["errorMessage"],
                                            context);
                                      }
                                      // The reponse urlInSystem = False states that there is no information on the given ontology already stored in the system.
                                      // Thus, we ask the user if we shall put it into the queue.
                                      else if (graphQlResponse
                                                  .data?["queueInformation"]
                                              ?["urlInSystem"] ==
                                          false) {
                                        showDialog(
                                            context: context,
                                            builder: (BuildContext context) =>
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
                                                              displayErrorSnackBar(
                                                                  jsonResponse
                                                                      .exception
                                                                      .toString(),
                                                                  context);
                                                            } else if (jsonResponse
                                                                    .data?[
                                                                "error"]) {
                                                              displayErrorSnackBar(
                                                                  jsonResponse
                                                                          .data?[
                                                                      "errorMessage"],
                                                                  context);
                                                            } else {
                                                              progressSnackBar(
                                                                  jsonResponse
                                                                          .data![
                                                                      "queueInfo"]);
                                                            }
                                                          });
                                                          Navigator.pop(
                                                              context);
                                                        }),
                                                    TextButton(
                                                        child:
                                                            const Text("Abort"),
                                                        onPressed: () =>
                                                            Navigator.pop(
                                                                context))
                                                  ],
                                                ));
                                      } else if (graphQlResponse
                                                  .data?["queueInformation"]
                                              ?["taskFinished"] ==
                                          false) {
                                        progressSnackBar(graphQlResponse
                                            .data?["queueInformation"]);
                                      }
                                      // If the first query on the queue information state that the metrics are already in the ontology file,
                                      // then, retrieve the date with another GraphQL query.F
                                      else if (graphQlResponse
                                              .data?["queueInformation"]
                                          ?["taskFinished"]) {
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
                                            displayErrorSnackBar(
                                                graphQlResponse.exception
                                                    .toString(),
                                                context);
                                          } else {
                                            EasyLoading.dismiss();
                                            Navigator.push(
                                                context,
                                                MaterialPageRoute(
                                                    builder: (context) =>
                                                        CalculationView(
                                                            graphQlResponse
                                                                        .data?[
                                                                    "getRepository"]
                                                                ["edges"],
                                                            urlController
                                                                .text)));
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

  void progressSnackBar(Map<String, dynamic> queueInformation) {
    SnackBar snack = SnackBar(
        backgroundColor: Theme.of(context).colorScheme.secondaryVariant,
        duration: const Duration(seconds: 10),
        content: ListTile(
          iconColor: Theme.of(context).colorScheme.onSecondary,
          textColor: Theme.of(context).colorScheme.onSecondary,
          leading: (queueInformation["taskIsStarted"] == true)
              ? const Icon(Icons.wb_incandescent_sharp)
              : const Icon(Icons.query_builder),
          title: Text(
              "Calculation not yet finished${(queueInformation["taskIsStarted"] == true) ? ", but started and currently in Progress." : ""}. Queue position: ${queueInformation["queuePosition"]}"),
          //The progress bar for the current state of analyzed ontologies shall only appear
          //if the data is in the json response.
          subtitle: (queueInformation.containsKey("commitsForThisOntology"))
              ? ProgressBarIndicator(jsonResponse: queueInformation)
              : null,
        ));
    ScaffoldMessenger.of(context).showSnackBar(snack);
  }

  void displayErrorSnackBar(String message, BuildContext context,
      {String subMessage = ""}) {
    var snack = SnackBar(
      content: ListTile(
        iconColor: Theme.of(context).colorScheme.onError,
        textColor: Theme.of(context).colorScheme.onError,
        title: Text("Error:  $subMessage"),
        leading: const Icon(Icons.warning_amber),
        subtitle: Text(message),
      ),
      backgroundColor: Theme.of(context).colorScheme.error,
      duration: const Duration(seconds: 10),
      padding: const EdgeInsets.all(20),
      dismissDirection: DismissDirection.vertical,
    );
    ScaffoldMessenger.of(context).showSnackBar(snack);
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
                chips.add(Tooltip(
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
                    )));
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

class Dialog extends StatelessWidget {
  Dialog({
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final graphQL = GraphQLHandler();
    var f = graphQL.getRepositoryList();
    

    return FutureBuilder(
        future: f,
        builder: (context, snapshot) {
          if(!snapshot.hasData){
            EasyLoading.show(status: "We're fetching existing Repositories");
            return Text("");
          }
          if (snapshot.hasData) {
            var result = snapshot.data as QueryResult<dynamic>;
            var simpleDialogOptions = <SimpleDialogOption>[];

            for (var element in result.data!["repositoriesInformation"]) {
              simpleDialogOptions.add(SimpleDialogOption(
                  child: Row(
                    children: [
                      Expanded(child: Text(element["repository"])),
                      Text(element["analyzedOntologyCommits"].toString(), textAlign: TextAlign.right,),
                    ],
                  ),
                  onPressed: () =>Navigator.pop(context, element["repository"] ?? ""),
                  ));
            }

            Widget simpleDialog = SimpleDialog(
              title: const Text(
                  " Select one of the already calcualated ontology repositories:"),
              children:simpleDialogOptions
            );
            EasyLoading.dismiss();
            return simpleDialog;
          }
          throw Exception("Failure in Fechting Repository Information");
        });
    
    //EasyLoading.dismiss();
  }
}

class ProgressBarIndicator extends StatelessWidget {
  const ProgressBarIndicator({
    Key? key,
    required this.jsonResponse,
  }) : super(key: key);

  final Map<String, dynamic> jsonResponse;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 40,
      child: Column(children: [
        Row(children: [
          Expanded(
            child: Text(
                "Analyzed ${jsonResponse["analyzedOntologies"]} of ${jsonResponse["analysableOntologies"]} ontologies:  "),
          ),
          Expanded(
              flex: 3,
              child: LinearProgressIndicator(
                value: jsonResponse["analyzedOntologies"] /
                    jsonResponse["analysableOntologies"],
                minHeight: 6,
              ))
        ]),
        Row(children: [
          Expanded(
            child: Text(
                "Analyzed ${jsonResponse["analyzedCommits"]} of ${jsonResponse["commitsForThisOntology"]} Commits of this ontology:  "),
          ),
          Expanded(
              flex: 3,
              child: LinearProgressIndicator(
                value: jsonResponse["analyzedCommits"] /
                    jsonResponse["commitsForThisOntology"],
                minHeight: 5,
              ))
        ])
      ]),
    );
  }
}
