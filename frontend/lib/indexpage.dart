import 'package:flutter/material.dart';
import 'package:neonto_frontend/markdown_handler.dart';
import 'dart:html' as html;

import 'package:neonto_frontend/settings.dart';

//import 'markdown_handler.dart';
class indexPage extends StatelessWidget {
  const indexPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 200),
      child: Column(children: [
        const Padding(
          padding: EdgeInsets.all(10.0),
          child: Text(
            "NEOntometrics",
            style: TextStyle(fontSize: 80, fontWeight: FontWeight.bold),
            textAlign: TextAlign.center,
          ),
        ),
        const Text("A new way to calculate Ontology Metrics",
            style: TextStyle(fontSize: 30)),
        const SizedBox(
          height: 30,
        ),
        const Text(
          "Neontometrics allows you to quickly calculate a variety of mntology metrics for ontology files and git-based ontology repositories",
          style: TextStyle(fontSize: 20, fontStyle: FontStyle.italic),
        ),
        Row(
          children: [
            const Expanded(
              child: Text(""),
            ),
            Expanded(
                child: IconButton(
                    tooltip: "Write an E-Mail",
                    iconSize: 50,
                    onPressed: () => html.window.open("mailto:achim.reiz@uni-rostock.de", "Contact by Mail"),
                    icon: const Icon(Icons.mail))),
            Expanded(
              child: IconButton(
                  tooltip: "Contact Me LinkedIn",
                  onPressed: () => html.window.open("https://www.linkedin.com/in/achim-reiz-9a1aaa94/", "Contact me by LinkedIn"),
                  iconSize: 40,
                  icon: Image.asset("linkedin.png")),
            ),
            Expanded(
                child: IconButton(
                    tooltip: "Newsletter",
                    iconSize: 50,
                    onPressed: () => html.window.open(Settings().apiUrl + "/newsletter", "Newsletter"),
                    icon: const Icon(Icons.outgoing_mail))),
            Expanded(
                child: IconButton(
                    tooltip: "GitHub",
                    iconSize: 50,
                    onPressed: () => html.window.open("https://github.com/Uni-Rostock-Win/NEOntometrics", "GitHub"),
                    icon: Image.asset("GitHub.png"))),
            Expanded(
                child: IconButton(
                    tooltip: "Rostock University",
                    iconSize: 50,
                    onPressed: () => html.window.open("wirtschaftsinformatik.uni-rostock.de/", "Rostock University"),
                    icon: const Icon(Icons.school))),
            const Expanded(
              child: Text(""),
            ),
          ],
        ),
        const Divider(),
        Expanded(child: MarkDownHandler().buildMarkDownElement("homepage.md"))
      ]),
    );
  }
}
