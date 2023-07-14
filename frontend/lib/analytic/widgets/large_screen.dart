// ignore_for_file: prefer_const_constructors

import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/routing/local_navigator.dart';
import 'package:neonto_frontend/analytic/widgets/menu.dart';

class LargeScreen extends StatelessWidget {
  const LargeScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Row(children: [
      Expanded(
        child: Scaffold(
          appBar: AppBar(),
          body: Menu(),
        ),
      ),
      Expanded(
          flex: 10,
          child: Container(
            child: localNavigator(),
          ))
    ]);
  }
}
