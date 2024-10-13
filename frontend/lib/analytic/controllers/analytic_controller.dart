import 'package:get/get.dart';
import 'package:neonto_frontend/metric_data.dart';
import 'package:neonto_frontend/metric_explorer.dart';

class AnalyticController extends GetxController {
  static AnalyticController instance = Get.find();

  List listData = [];
  OntologyData? theSelectedOntologyFile;
  late RepositoryData repositoryData;
  var repositoryName;
  List<Map<String, String>> firstTwoCommit = [];
  Set<MetricExplorerItem> selectedMeasures = {};
  RepositoryData? repositoryNameForCommits;
  int? theNumberOfChangedFiles;
  List<String> getImplementationNameOfSelectedMeasuers() {
    List<String> implementationNames = [];
    for (var measure in selectedMeasures) {
      implementationNames.add(measure.itemName!);
    }
    return implementationNames;
  }
}
