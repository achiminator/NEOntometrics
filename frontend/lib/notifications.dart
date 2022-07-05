import 'package:flutter/material.dart';

import 'metric_data.dart';

class Snacks{
  Snacks(this.context);
  BuildContext context;

    void progressSnackBar(QueueInformation queueInformation) {
    SnackBar snack = SnackBar(
        backgroundColor: Theme.of(context).colorScheme.secondaryContainer,
        duration: const Duration(seconds: 10),
        content: ListTile(
          iconColor: Theme.of(context).colorScheme.onSecondary,
          textColor: Theme.of(context).colorScheme.onSecondary,
          leading: (queueInformation.taskStarted)
              ? const Icon(Icons.wb_incandescent_sharp)
              : const Icon(Icons.query_builder),
          title: Text(
              "Calculation not yet finished${(queueInformation.taskStarted) ? ", but started and currently in Progress." : ". Queue position: ${queueInformation.queuePosition}"} "),
          //The progress bar for the current state of analyzed ontologies shall only appear
          //if the data is in the json response.
          subtitle: (queueInformation.totalCommits != null)
              ? ProgressBarIndicator(queueInformation: queueInformation)
              : null,
        ));
    ScaffoldMessenger.of(context).showSnackBar(snack);
  }

  void displayErrorSnackBar(String message, BuildContext context,
      {String subMessage = ""}) {
    var snack = SnackBar(
      content: ListTile(
        iconColor: Theme.of(context).colorScheme.onError,
        textColor: Theme.of(context).colorScheme.onError,
        title: Text("Error:  $subMessage"),
        leading: const Icon(Icons.warning_amber),
        subtitle: Text(message),
      ),
      backgroundColor: Theme.of(context).colorScheme.error,
      duration: const Duration(seconds: 10),
      padding: const EdgeInsets.all(20),
      dismissDirection: DismissDirection.vertical,
    );
    ScaffoldMessenger.of(context).showSnackBar(snack);
  }
}

class ProgressBarIndicator extends StatelessWidget {
  const ProgressBarIndicator({
    Key? key,
    required this.queueInformation,
  }) : super(key: key);

  final QueueInformation queueInformation;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 40,
      child: Column(children: [
        Row(children: [
          Expanded(
            child: Text(
                "Analyzed ${queueInformation.analyzedOntologies} of ${queueInformation.analyzsableOntologies} ontologies: "),
          ),
          Expanded(
              flex: 3,
              child: LinearProgressIndicator(
                value: (queueInformation.analyzedOntologies! /
                    queueInformation.analyzsableOntologies!),
                minHeight: 6,
              ))
        ]),
        Row(children: [
          Expanded(
            child: Text(
                "Analyzed ${queueInformation.analyzedCommits} of ${queueInformation.totalCommits} Commits of this ontology:  "),
          ),
          Expanded(
              flex: 3,
              child: LinearProgressIndicator(
                value: queueInformation.analyzedCommits! /
                    queueInformation.totalCommits!,
                minHeight: 5,
              ))
        ])
      ]),
    );
  }
}
