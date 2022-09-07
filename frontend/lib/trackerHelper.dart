import 'dart:html' as html;

import 'package:matomo_tracker/matomo_tracker.dart';

///Classes that enrich often used functions (like outwards link) with tracking functionalitiy
class TrackerHelper {

  /// Opens an html window (Equivalent to html.window.open), but also sends the information to the Matomo tracker.
  static void htmlOpenWindow(String link, String linkDescription) {
    MatomoTracker.instance.trackOutlink(link);
    html.window.open(link, linkDescription);
  }
}
