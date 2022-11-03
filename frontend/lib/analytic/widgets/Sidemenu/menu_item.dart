// ignore_for_file: prefer_typing_uninitialized_variables

import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/widgets/Sidemenu/displayed_menu_item.dart';

//Hier sind die Items, die im Menu zu zeigen sind. es ist  von ScreenSize abhängig
class SideMenuItem extends StatelessWidget {
  final String? itemName;

  final onTap;
  const SideMenuItem({Key? key, this.itemName, this.onTap}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return DisplayedMenuItem(
      itemName: itemName,
      onTap: onTap,
    );
  }
}
