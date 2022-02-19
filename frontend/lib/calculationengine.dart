import 'dart:convert';
import 'dart:html';
import "calculationview.dart";
import 'package:http/http.dart' as http;

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:neonto_frontend/markdownhandler.dart';
import 'package:neonto_frontend/metricData.dart';

import 'settings.dart';

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
                        color: Theme.of(context).backgroundColor,
                        shape: BoxShape.circle),
                    child: Icon(
                      Icons.live_help_outlined,
                      size: 115,
                      color: Theme.of(context).secondaryHeaderColor,
                    )),
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
                      color: Theme.of(context).primaryColorDark,
                    ),
                    child: const Text(
                      "Run Calculation",
                      style: TextStyle(color: Colors.white, fontSize: 20),
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
                  TextFormField(
                    autovalidateMode: AutovalidateMode.onUserInteraction,
                    controller: urlController,
                    decoration: InputDecoration(
                        suffixIcon: IconButton(
                          onPressed: () {
                            if (selectedElementsForCalculation.isEmpty) {
                              displayErrorSnackBar(
                                  "Please select at least one metric.",
                                  context);
                            } else if (urlController.text == "" ||
                                !urlController.text.contains("http")) {
                              displayErrorSnackBar(
                                  "No valid ontology given. Please enter an URL to a valid Ontology.",
                                  context);
                            } else {
                              var httpFut = http.get(Uri.parse(
                                  "${Settings().apiUrl}/git?reasoner=$reasoner&url=${urlController.text}"));
                              httpFut.then((httpResponse) {
                                if (httpResponse.statusCode != 200) {
                                  displayErrorSnackBar(
                                      jsonDecode(httpResponse.body)["info"],
                                      context);
                                } else {
                                  //Things to show if there is not error but also not yet the result
                                  if (httpResponse.body
                                          .contains("taskFinished") &&
                                      httpResponse.body
                                          .contains("queuePosition")) {
                                    Map<String, dynamic> jsonResponse =
                                        json.decode(httpResponse.body);
                                    SnackBar snack = SnackBar(
                                        backgroundColor:
                                            Theme.of(context).focusColor,
                                        duration: const Duration(seconds: 10),
                                        content: ListTile(
                                          iconColor: Colors.white,
                                          textColor: Colors.white,
                                          leading: (jsonResponse[
                                                      "taskIsStarted"] ==
                                                  true)
                                              ? const Icon(
                                                  Icons.wb_incandescent_sharp)
                                              : const Icon(Icons.query_builder),
                                          title: Text(
                                              "Calculation not yet finished. Queue position: ${jsonResponse["queuePosition"]}"),
                                          //The progress bar for the current state of analyzed ontologies shall only appear
                                          //if the data is in the json response.
                                          subtitle: (jsonResponse["progress"]
                                                  .isNotEmpty)
                                              ? ProgressBarIndicator(
                                                  jsonResponse: jsonResponse)
                                              : null,
                                        ));
                                    ScaffoldMessenger.of(context)
                                        .showSnackBar(snack);
                                  } else {
                                    List<dynamic> jsonResponse =
                                        json.decode(httpResponse.body);
                                    Navigator.push(
                                        context,
                                        MaterialPageRoute(
                                            builder: (context) =>
                                                CalculationView(jsonResponse,
                                                    urlController.text)));
                                  }
                                }
                              });
                            }

                            // http.get(urlController.text);
                          },
                          icon: const Icon(Icons.send),
                          tooltip: "Send",
                        ),
                        icon: const Icon(Icons.public),
                        hintText:
                            "Paste the URL to the Ontology or the Repository here",
                        helperText: "URL to Ontology"),
                    toolbarOptions:
                        const ToolbarOptions(paste: true, selectAll: true),
                  ),
                ],
              )))
        ]));
  }

  void displayErrorSnackBar(String message, BuildContext context,
      {String subMessage = ""}) {
    var snack = SnackBar(
      content: ListTile(
        iconColor: Colors.white,
        textColor: Colors.white,
        title: Text("Error:  $subMessage"),
        leading: const Icon(Icons.warning_amber),
        subtitle: Text(message),
      ),
      backgroundColor: Theme.of(context).errorColor,
      duration: const Duration(seconds: 10),
      padding: const EdgeInsets.all(20),
      dismissDirection: DismissDirection.vertical,
    );
    ScaffoldMessenger.of(context).showSnackBar(snack);
  }

  Future<String> _homeDescription(String location) async {
    return await rootBundle.loadString("webpage/$location");
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
  Widget buildCalculationSetting(MetricExplorerItem data) {
    var leafElements =
        MetricExplorerItem.getLeafItems(data, onlyCalculatableClasses: true);
    return Container(
        padding: const EdgeInsets.all(10),
        child: CheckboxListTile(
          title: Text(data.itemName),
          secondary: const Icon(Icons.account_tree_outlined),
          subtitle: Wrap(
            spacing: 3,
            runSpacing: 3,
            children: buildChips(leafElements),
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

  List<Widget> buildChips(List<MetricExplorerItem> data) {
    List<Widget> chips = [];
    for (MetricExplorerItem item in data) {
      chips.add(Tooltip(
          message: (item.definition) != "" ? item.definition : item.description,
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
          Text(
              "Analyzed ${jsonResponse["progress"]["analyzedOntologies"]} of ${jsonResponse["progress"]["analysableOntologies"]} ontologies:  "),
          Expanded(
              child: LinearProgressIndicator(
            value: jsonResponse["progress"]["analysableOntologies"] /
                jsonResponse["progress"]["analyzedOntologies"],
            minHeight: 5,
          ))
        ]),
        Row(children: [
          Text(
              "Analyzed ${jsonResponse["progress"]["ananlyzedCommits"]} of ${jsonResponse["progress"]["commitsForThisOntology"]} Commits of this ontology:  "),
          Expanded(
              child: LinearProgressIndicator(
            value: jsonResponse["progress"]["ananlyzedCommits"] /
                jsonResponse["progress"]["commitsForThisOntology"],
            minHeight: 5,
          ))
        ])
      ]),
    );
  }
}
