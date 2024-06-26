// ignore: unused_import
import 'package:flutter/foundation.dart';

class Settings {
  /// Provides basic settings for the functioning of the app.
  /// Comparable to global variables.
  
  // final String apiUrl = (kDebugMode) ?  "http://localhost:8000": "http://api.neontometrics.informatik.uni-rostock.de";
  final String apiUrl = "http://localhost:8086";
  final String reasoningLimit = "0,3MB";
  static  String matomoWebsite = "http://localhost:6377/matomo.php";
  static int matomoWebsiteID = 2;
}
