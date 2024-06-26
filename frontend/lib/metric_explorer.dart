import 'package:flutter/material.dart';
import 'package:flutter_math_fork/flutter_math.dart';
import 'package:matomo_tracker/matomo_tracker.dart';
import 'package:neonto_frontend/metric_data.dart';
import 'package:neonto_frontend/trackerHelper.dart';

class _MetricExplorerState extends State<MetricExplorer>
    with TraceableClientMixin {
  @override
  String get traceName => 'Open Metric Explorer';

  @override
  String get traceTitle => "MetricExplorer";
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
                  Expanded(child: buildDetails()),
                ],
                crossAxisAlignment: CrossAxisAlignment.center,
              )));
        }
      },
    );
  }

  String _buildLatexFunction(String input) {
    if (input == "") return input;
    var fracts = input.split("/");
    if (fracts.length == 1) {
      return fracts[0];
    }
    var fractResult = "";
    int openBrackets = 0;
    var notClosedBracket = false;
    var other = "";
    var otherCalcSymbol = "";
    int counter = -1;
    for (var f in fracts[0].split("")) {
      counter++;
      if (f == "(") {
        openBrackets++;
      } else if (f == ")") {
        openBrackets--;
      }
      if (openBrackets == 0 && (f == "*" || f == "+" || f == "-")) {
        other = fracts[0].substring(0, counter - 1);
        otherCalcSymbol = f;
      }
    }
    notClosedBracket = openBrackets != 0;
    if (otherCalcSymbol != "") {
      fracts[0] = fracts[0].replaceFirst(otherCalcSymbol, "");
    }
    counter = -1;
    fracts[0] = fracts[0].substring(other.length);

    for (var f in fracts[1].split("")) {
      counter++;
      if (f == "(") {
        openBrackets++;
      } else if (f == ")") {
        openBrackets--;
      }
      if (openBrackets == 0 && notClosedBracket) {
        other += fracts[1].substring(counter + 1);
        fracts[1] = fracts[1].substring(0, counter + 1);
        if (notClosedBracket) {
          fracts[0] = fracts[0].replaceFirst("(", "");
          fracts[1] = fracts[1]
              .split("")
              .reversed
              .join()
              .replaceFirst(")", "")
              .split("")
              .reversed
              .join();
        }

        break;
      }
    }
    fractResult =
        "\\frac {${fracts[0].trim()}}{${fracts[1].trim()}} $otherCalcSymbol ${other.trim()}";
    return (fractResult);
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
          color: Theme.of(context).colorScheme.secondary,
        ),
        child: Text(
          "Details: " + activeItemName,
          style: TextStyle(
              color: Theme.of(context).colorScheme.onSecondary, fontSize: 20),
        ),
      ),
    );
    if (activeItemName == "") {
      elementsInDetails.add(
        const ListTile(
            leading: Icon(Icons.info_outline),
            title: Text(
                "Click on details-icon of an element to see further information")),
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
            child: MouseRegion(
              cursor: SystemMouseCursors.click,
              child: ListTile(
                leading: const Icon(Icons.link),
                title: Text(seeAlso,
                    style: const TextStyle(
                        color: Colors.blue,
                        decoration: TextDecoration.underline)),
                subtitle: const Text("See Also"),
              ),
            ),
            onTap: () => TrackerHelper.htmlOpenWindow(seeAlso, "resouce"),
          ),
        );
      } else {
        elementsInDetails.add(
          ListTile(
            leading: const Icon(Icons.link),
            title: SelectableText(seeAlso),
            subtitle: const Text("See Also"),
          ),
        );
      }
    }
    return Card(
      child: Container(
          alignment: Alignment.center,
          padding: const EdgeInsets.all(10),
          width: 500,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            children: elementsInDetails,
          )),
    );
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

    var expansionPanelList = RepaintBoundary(
        child: ExpansionPanelList(
      expansionCallback: (panelIndex, isExpanded) {
        setState(() {
          items[panelIndex].toggled = !isExpanded;
        });
      },
      children: panelList,
    ));
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
          MatomoTracker.instance.trackEvent(
              eventInfo: EventInfo(
                  category: "Main",
                  action: "Open Explorer Element + $activeItemName"));

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
