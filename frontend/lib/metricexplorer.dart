import 'package:flutter/material.dart';
import 'package:flutter_math_fork/flutter_math.dart';
import 'package:flutter_math_fork/tex.dart';
import 'dart:html' as html;
import 'package:neonto_frontend/metricData.dart';

class _MetricExplorerState extends State<MetricExplorer> {
  _MetricExplorerState();

  late Future<List<MetricExplorerItem>> metricExplorerData;
  String activeItemName = "";
  String activeDescription = "";
  String activeDefinition = "";
  String activeImplementationName = "";
  String activeInterpretation = "";
  String seeAlso = "";
  String calculation = "";

  @override
  Widget build(BuildContext context) {
    return FutureBuilder(
      future: widget.metricData,
      builder: (context, snapshot) {
        if (snapshot.connectionState != ConnectionState.done) {
          return const Center(child: RefreshProgressIndicator());
        } else {
          return (Container(
              padding: const EdgeInsets.all(20.0),
              child: Row(
                children: [
                  Expanded(
                      flex: 2,
                      child: Container(
                          alignment: Alignment.topCenter,
                          child: SingleChildScrollView(
                              child: buildExpansionPanels(
                            snapshot.data as List<MetricExplorerItem>,
                          )))),
                  const VerticalDivider(
                    indent: 60,
                    endIndent: 60,
                  ),
                  buildDetails(),
                ],
                crossAxisAlignment: CrossAxisAlignment.center,
              )));
        }
      },
    );
  }

  String _buildLatexFunction(String input) {
    if (input == "") return input;
    RegExp exp = RegExp(r"\) [\/] \(");
    String calculationMethodology = exp.stringMatch(input) ?? input;
    var splitted = input.split(exp);
    for (var i = 0; i < splitted.length; i++) {
      splitted[i] = splitted[i].replaceAll("(", "");
      splitted[i] = splitted[i].replaceAll(")", "");
    }
    calculationMethodology = calculationMethodology.replaceAll(" ", "");
    calculationMethodology = calculationMethodology.replaceAll(")", "");
    calculationMethodology = calculationMethodology.replaceAll("(", "");
    if (calculationMethodology == "/") {
      return ("x = \\frac {${splitted[0]}}{${splitted[1]}}");
    } else {
      return ("x = " + splitted[0]);
    }
  }

  Widget buildDetails() {
    List<Widget> elementsInDetails = [];

    elementsInDetails.add(
      Container(
        height: 50,
        alignment: Alignment.center,
        padding: const EdgeInsets.all(3),
        margin: const EdgeInsets.only(top: 10, bottom: 20),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(15),
          color: Theme.of(context).primaryColorDark,
        ),
        child: Text(
          "Details: " + activeItemName,
          style: const TextStyle(color: Colors.white, fontSize: 20),
        ),
      ),
    );
    if (activeItemName == "") {
      elementsInDetails.add(
        const ListTile(
            leading: Icon(Icons.info_outline),
            title: Text(
                "Click on details-icon of an element to see further informaiton")),
      );
    }
    if (activeDefinition != "") {
      elementsInDetails.add(ListTile(
          leading: const Icon(Icons.confirmation_number_outlined),
          title: Text(activeDefinition),
          subtitle: const Text("Definition")));
    }
    if (activeDescription != "") {
      elementsInDetails.add(
        ListTile(
          leading: const Icon(Icons.description_outlined),
          title: Text(activeDescription),
          subtitle: const Text("Description"),
        ),
      );
    }
    if (activeInterpretation != "") {
      elementsInDetails.add(
        ListTile(
          leading: const Icon(Icons.question_answer_outlined),
          title: Text(activeInterpretation),
          subtitle: const Text("Interpretation"),
        ),
      );
    }
    if (calculation != "") {
      elementsInDetails.add(
        ListTile(
          leading: const Icon(Icons.mediation),
          title: Math.tex(
            calculation,
          ),
          subtitle: const Text("Calculation"),
        ),
      );
    }
    if (activeImplementationName != "") {
      elementsInDetails.add(ListTile(
        leading: const Icon(Icons.build),
        title: Math.tex(activeImplementationName),
        subtitle: const Text("Implementation Name"),
      ));
    }
    if (seeAlso != "") {
      if (seeAlso.contains("http")) {
        elementsInDetails.add(
          GestureDetector(
            child: ListTile(
              leading: const Icon(Icons.link),
              title: Text(seeAlso,
                  style: const TextStyle(
                      color: Colors.blue,
                      decoration: TextDecoration.underline)),
              subtitle: const Text("See Also"),
            ),
            onTap: () => html.window.open(seeAlso, "resouce"),
          ),
        );
      } else {
        elementsInDetails.add(
          ListTile(
            leading: const Icon(Icons.link),
            title: Text(seeAlso),
            subtitle: const Text("See Also"),
          ),
        );
      }
    }
    return Expanded(
        child: Card(
      child: Container(
          alignment: Alignment.center,
          padding: const EdgeInsets.all(10),
          width: 500,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            children: elementsInDetails,
          )),
    ));
  }

  /// Entrypoint for building the expandable panel on the left side.
  Widget buildExpansionPanels(List<MetricExplorerItem> items) {
    List<ExpansionPanel> panelList = [];
    List<ListTile> leafitems = [];
    for (var i = 0; i < items.length; i++) {
      Widget bodyForPanel;

      if (items[i].subClass.isNotEmpty) {
        bodyForPanel = Container(
            margin: const EdgeInsets.only(left: 8),
            child: buildExpansionPanels(items[i].subClass));
        panelList.add(
          ExpansionPanel(
            headerBuilder: (context, isExpanded) =>
                _buildLIstTileForExpansionPanel(items[i]),
            body: bodyForPanel,

            //ListTile(title: Text(items[i].description)),
            isExpanded: items[i].toggled,
          ),
        );
      } else {
        leafitems.add(_buildLIstTileForExpansionPanel(items[i]));
      }
    }

    var expansionPanelList = ExpansionPanelList(
      expansionCallback: (panelIndex, isExpanded) {
        setState(() {
          items[panelIndex].toggled = !isExpanded;
        });
      },
      children: panelList,
    );
    List<Widget> returnList = [];
    returnList.add(expansionPanelList);
    returnList.addAll(leafitems);

    return Column(children: returnList);
  }

  /// As the name suggests, this part is responsible for building the [ListTile] in the [ExpansionPanel] of the
  /// Metric Explorer. Especially important is the handling of the (i)-Icon, which then sets the state for the
  /// details page (see [buildDetails])
  _buildLIstTileForExpansionPanel(MetricExplorerItem item) {
    return ListTile(
      title: Text(item.itemName),
      subtitle: (item.implentationName != "")
          ? Text(item.implentationName ?? "")
          : null,
      // if(item.implentationName != "")
      // ()? subtitle: Text(item.implentationName)  : null,
      leading: IconButton(
        icon: const Icon(Icons.info_outline),
        onPressed: () => setState(() {
          activeItemName = item.itemName;
          activeDefinition = item.definition;
          activeDescription = item.description;
          activeInterpretation = item.interpretation;
          activeImplementationName = item.implentationName ?? "";
          seeAlso = item.seeAlso;

          calculation = _buildLatexFunction(item.calculation ?? "");
        }),
      ),
    );
  }
}

/// The class responsible for building the Metric Explorer Page.
class MetricExplorer extends StatefulWidget {
  final Future<List<MetricExplorerItem>> metricData;
  const MetricExplorer(this.metricData, {Key? key}) : super(key: key);

  @override
  State<MetricExplorer> createState() => _MetricExplorerState();
}
