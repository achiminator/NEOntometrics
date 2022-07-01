// ignore: unused_import
import 'package:flutter/foundation.dart';

class Settings {
  /// Provides basic settings for the functioning of the app.
  /// Comparable to global variables.
  
  final String apiUrl = (kDebugMode) ?  "http://localhost:8086": "http://api.neontometrics.informatik.uni-rostock.de";
  // final String apiUrl = "http://api.neontometrics.informatik.uni-rostock.de";
  final String reasoningLimit = "0,3MB";
}
