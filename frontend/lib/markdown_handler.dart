import 'package:flutter/material.dart';
import 'package:flutter_markdown/flutter_markdown.dart';
import 'package:flutter/services.dart' show rootBundle;
import 'package:neonto_frontend/trackerHelper.dart';

class MarkDownHandler {
  Widget buildMarkDownElement(String mdLocation) {
    return FutureBuilder(
      future: _homeDescription(mdLocation),
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          return Center(
              child: Container(
                  padding: const EdgeInsets.all(20),
                  child: MarkdownBody(
                    data: snapshot.data as String,
                    fitContent: true,
                    styleSheet: MarkdownStyleSheet(
                        textScaler: const TextScaler.linear(1.2)),
                    onTapLink: (text, href, title) =>
                        TrackerHelper.htmlOpenWindow(href!, "resouce"),
                  )));
        } else {
          return const Center(child: CircularProgressIndicator());
        }
      },
    );
  }

  Future<String> _homeDescription(String location) async {
    return await rootBundle.loadString("assets/webpage/$location");
  }
}
