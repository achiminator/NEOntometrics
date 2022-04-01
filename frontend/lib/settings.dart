import 'package:flutter/foundation.dart';

class Settings {
  /// Provides basic settings for the functioning of the app.
  /// Comparable to global variables.
  
  final String apiUrl = (kDebugMode) ?  "http://localhost:8000": "http://localhost:8012";
  final String reasoningLimit = "0,3MB";
}
