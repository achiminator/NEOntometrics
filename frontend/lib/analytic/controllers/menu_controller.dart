import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:neonto_frontend/analytic/routing/local_navigator.dart';

class MenuuController extends GetxController {
  static MenuuController instance = Get.find();
  var activeElement = verticalChartPageRoute
      .obs; // at the beginning this Page will be the ActivPage
  var hoverElement = "".obs;

  // This is a Method to allow us to change the ActivePage "BarChartPage"
  ChangeActiveElementPage(String itemName) {
    activeElement.value = itemName;
  }

  onHover(String itemName) {
    if (!isActive(itemName)) hoverElement.value = itemName;
  }

  isActive(String itemName) => activeElement.value == itemName;
  isHovering(String itemName) => hoverElement.value == itemName;

  //a widegt going to return a method basis of the itemName
  Widget returnChartIcon(String itemName) {
    switch (itemName) {
      case verticalChartPageRoute:
        return Container(
            child: IconButton(
                onPressed: () {},
                tooltip: 'to compare the ontology files',
                icon: Icon(Icons.bar_chart, color: Colors.white)));

      case lineChartPageRoute:
        return Container(
          child: IconButton(
              onPressed: () {},
              tooltip: 'to see the development of metrics over time',
              icon: Icon(
                Icons.stacked_line_chart_outlined,
                color: Colors.white,
              )),
        );
      case differencesPageRoute:
        return Container(
            child: IconButton(
                onPressed: () {},
                tooltip:
                    'to see which metrics have changed compared to the previous version (commit)',
                icon: Icon(Icons.difference, color: Colors.white)));

      default:
        return Text('default');
    }
  }
}
