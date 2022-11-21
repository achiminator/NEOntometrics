// ignore_for_file: prefer_const_constructors

import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/routing/local_navigator.dart';
import 'package:neonto_frontend/analytic/widgets/Sidemenu/menu.dart';

class LargeScreen extends StatelessWidget {
  const LargeScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Row(children: [
      Expanded(
        child: const Menu(),
      ),
      Expanded(
          flex: 5,
          child: Container(
            child: localNavigator(),
          ))
    ]);
  }
}
