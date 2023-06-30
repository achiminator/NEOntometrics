import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/widgets/large_screen.dart';
import 'package:neonto_frontend/analytic/widgets/menu.dart';
import 'package:neonto_frontend/graphql.dart';
import 'package:neonto_frontend/metric_data.dart';
import 'package:neonto_frontend/notifications.dart';
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
  RepositoryData? lastTwoVersions;

  @override
  Widget build(BuildContext context) {
    if (lastTwoVersions == null) {
      GraphQLHandler()
          .getLastTwoCommits(widget.repositoryData.repository)
          .then((value) {
        if (value.hasException) {
          Snacks(context)
              .displayErrorSnackBar((value.exception.toString()), context);
          return;
        }
        lastTwoVersions = RepositoryData(value.data);
        analyticController.repositoryNameForCommits = lastTwoVersions;
      });
    }
    return ChangeNotifierProvider(
      create: (context) => Model(),
      child: InnerAnalyticView(widget.repositoryName, widget.repositoryData),
    );
  }
}

class InnerAnalyticView extends StatefulWidget {
  final String repositoryName;
  RepositoryData repositoryData;
  InnerAnalyticView(
    this.repositoryName,
    this.repositoryData, {
    Key? key,
  }) : super(key: key);

  @override
  State<InnerAnalyticView> createState() => _InnerAnalyticViewState();
}

class _InnerAnalyticViewState extends State<InnerAnalyticView> {
  final GlobalKey<ScaffoldState> scaffoldKey = GlobalKey();

  @override
  Widget build(BuildContext context) {
    analyticController.repositoryData = widget.repositoryData;
    analyticController.repositoryName = widget.repositoryName;
    return Scaffold(
      drawer: Drawer(child: Menu()),
      body: ChangeNotifierProvider(create: (context) => widget.repositoryData, child: LargeScreen()),
    );
  }
}

class Model extends ChangeNotifier {
  var name = analyticController.theSelectedOntologyFile;

  changeName() {
    name = analyticController.theSelectedOntologyFile;

    notifyListeners();
  }
}
