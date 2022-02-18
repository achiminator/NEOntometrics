import 'package:flutter/material.dart';
import 'package:flutter_markdown/flutter_markdown.dart';
import 'package:flutter/services.dart' show rootBundle;

class MarkDownHandler {
  Widget buildMarkDownElement(String mdLocation) {
    return FutureBuilder(
      future: _homeDescription(mdLocation),
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          return Center(
              child: Container(
                  padding: const EdgeInsets.all(20),
                  child: Markdown(data: snapshot.data as String)));
        } else {
          return const Center(child: CircularProgressIndicator());
        }
      },
    );
  }

  Future<String> _homeDescription(String location) async {
    return await rootBundle.loadString("webpage/$location");
  }
}