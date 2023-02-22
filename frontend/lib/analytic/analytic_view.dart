import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/helpers/reponsiveness.dart';
import 'package:neonto_frontend/analytic/widgets/large_screen.dart';
import 'package:neonto_frontend/analytic/widgets/Sidemenu/menu.dart';
import 'package:neonto_frontend/analytic/widgets/small_screen.dart';
import 'package:neonto_frontend/metric_data.dart';
import 'package:provider/provider.dart';

class AnalyticView extends StatefulWidget {
  final String repositoryName;
  RepositoryData repositoryData;
  AnalyticView(this.repositoryName, this.repositoryData, {Key? key})
      : super(key: key);

  @override
  State<AnalyticView> createState() => _AnalyticViewState();
}

class _AnalyticViewState extends State<AnalyticView> {
  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => Model(),
      child: WidgetChild(widget.repositoryName, widget.repositoryData),
    );
  }
}

class WidgetChild extends StatefulWidget {
  final String repositoryName;
  RepositoryData repositoryData;
  WidgetChild(
    this.repositoryName,
    this.repositoryData, {
    Key? key,
  }) : super(key: key);

  @override
  State<WidgetChild> createState() => _WidgetChildState();
}

class _WidgetChildState extends State<WidgetChild> {
  final GlobalKey<ScaffoldState> scaffoldKey = GlobalKey();
  @override
  Widget build(BuildContext context) {
    analyticController.repositoryData = widget.repositoryData;
    analyticController.repositoryName = widget.repositoryName;
    return Scaffold(
        drawer: ResponsiveWidget.isSmallScreen(context)
            ? const Drawer(child: Menu())
            : null,
        body: const ResponsiveWidget(
          largeScreen: LargeScreen(),
          smallScreen: SmallScreen(),
        ));
  }
}

class Model extends ChangeNotifier {
  var name = analyticController.theSelectedOntologyFile;
  var data = analyticController.listSelectedData;

  changeName() {
    name = analyticController.theSelectedOntologyFile;
    data = analyticController.listSelectedData;

    notifyListeners();
  }
}
