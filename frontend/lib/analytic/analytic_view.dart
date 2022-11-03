import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/helpers/reponsiveness.dart';
import 'package:neonto_frontend/analytic/widgets/large_screen.dart';
import 'package:neonto_frontend/analytic/widgets/Sidemenu/menu.dart';
import 'package:neonto_frontend/analytic/widgets/small_screen.dart';
import 'package:multiselect/multiselect.dart';

class AnalyticView extends StatefulWidget {
  final String repositoryName;

  const AnalyticView(this.repositoryName, {Key? key}) : super(key: key);

  @override
  State<AnalyticView> createState() => _AnalyticViewState();
}

class _AnalyticViewState extends State<AnalyticView> {
  final GlobalKey<ScaffoldState> scaffoldKey = GlobalKey();

  @override
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
                  title: DropDownMultiSelect(
                    options: ontologyfiles,
                    whenEmpty: 'Select an ontology file',
                    //what we select is saved in value
                    onChanged: (value) {
                      analyticController.selectedOptionList.value = value;
                      analyticController.selectedOption.value = "";
                      for (var element
                          in analyticController.selectedOptionList.value) {
                        analyticController.selectedOption.value =
                            analyticController.selectedOption.value +
                                "" +
                                element;
                      }
                    },
                    selectedValues: analyticController.selectedOptionList.value,
                  ),
                ),
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
}
