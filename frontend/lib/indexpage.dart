import 'package:flutter/material.dart';
import 'package:matomo_tracker/matomo_tracker.dart';
import 'package:neonto_frontend/markdown_handler.dart';
import 'package:neonto_frontend/settings.dart';
import 'package:neonto_frontend/trackerHelper.dart';
import 'package:youtube_player_iframe/youtube_player_iframe.dart';

//import 'markdown_handler.dart';
class IndexPage extends StatefulWidget {
  const IndexPage({Key? key}) : super(key: key);

  @override
  State<IndexPage> createState() => _IndexPageState();
}

class _IndexPageState extends State<IndexPage> {
  final videoURL = 'https://www.youtube.com/watch?v=OPQGNFV8M9g';

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    // MatomoTracker.instance.trackEvent(eventName: "MainPageVisit", action: "visit", eventCategory: "Main");

    MatomoTracker.instance.trackScreen(context, eventName: "Main");
    var ytController = YoutubePlayerController.fromVideoId(videoId: "OPQGNFV8M9g", params: YoutubePlayerParams(showFullscreenButton: true));
    ytController.playVideo();
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 150),
      child: SingleChildScrollView(
        child: Column(children: [
          const Padding(
            padding: EdgeInsets.all(10.0),
            child: Text(
              "NEOntometrics",
              style: TextStyle(fontSize: 80, fontWeight: FontWeight.bold),
              textAlign: TextAlign.center,
              softWrap: false,
            ),
          ),
          const Text("A new way to calculate Ontology Metrics",
              style: TextStyle(fontSize: 30)),
          const SizedBox(
            height: 30,
          ),
          const Text(
            "...Quickly calculate a variety of metrics for ontology files and git-based ontology repositories",
            style: TextStyle(fontSize: 20, fontStyle: FontStyle.italic),
            textAlign: TextAlign.center,
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
                      onPressed: () => TrackerHelper.htmlOpenWindow(
                          "mailto:achim.reiz@uni-rostock.de",
                          "Contact by Mail"),
                      icon: const Icon(Icons.mail))),
              Expanded(
                  child: IconButton(
                      tooltip: "GraphQL-Endpoint",
                      iconSize: 50,
                      onPressed: () => TrackerHelper.htmlOpenWindow(
                          Settings().apiUrl + "/graphql", "GraphQL"),
                      icon: const Icon(Icons.compare_arrows_outlined))),
              Expanded(
                child: IconButton(
                    tooltip: "Contact Me LinkedIn",
                    onPressed: () => TrackerHelper.htmlOpenWindow(
                        "https://www.linkedin.com/in/achim-reiz-9a1aaa94/",
                        "Contact me by LinkedIn"),
                    iconSize: 40,
                    icon: Image.asset("linkedin.png")),
              ),
              // While the Newsletter is technically working, it does not look good (yet). So it is not yet published on the page.
              // Expanded(
              //     child: IconButton(
              //         tooltip: "Newsletter",
              //         iconSize: 50,
              //         onPressed: () => html.window.open(Settings().apiUrl + "/newsletter/neontometrics/subscribe", "Newsletter"),
              //         icon: const Icon(Icons.outgoing_mail))),
              Expanded(
                  child: IconButton(
                      tooltip: "GitHub",
                      iconSize: 50,
                      onPressed: () => TrackerHelper.htmlOpenWindow(
                          "https://github.com/Uni-Rostock-Win/NEOntometrics",
                          "GitHub"),
                      icon: Image.asset("GitHub.png"))),
              Expanded(
                  child: IconButton(
                      tooltip: "Rostock University",
                      iconSize: 50,
                      onPressed: () => TrackerHelper.htmlOpenWindow(
                          "http://www.wirtschaftsinformatik.uni-rostock.de/",
                          "Rostock University"),
                      icon: const Icon(Icons.school))),
              const Expanded(
                child: Text(""),
              ),
            ],
          ),
          const Divider(),
          Padding(
            padding: const EdgeInsets.all(13.0),
            child: Container(
                height: 500, child: YoutubePlayer(controller: ytController)),
          ),
          MarkDownHandler().buildMarkDownElement("homepage.md")
        ]),
      ),
    );
  }
}
