import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:neonto_frontend/analytic/constants/style.dart';
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
  Widget returnChartImage(String itemName) {
    switch (itemName) {
      case verticalChartPageRoute:
        return Container(
          child: Image.asset(
            './vertical_chart.png',
            width: 170,
            height: 170,
          ),
        );

      case lineChartPageRoute:
        return Container(
          child: Image.asset(
            './line_chart.png',
            width: 170,
            height: 170,
          ),
        );
      case differencesPageRoute:
        return Container(
          child: Image.asset(
            './Show_me_the_differences.png',
            width: 170,
            height: 170,
          ),
        );

      default:
        return Text('default');
    }
  }

  Widget retrunTooltip(String itemName) {
    switch (itemName) {
      case verticalChartPageRoute:
        return Container(
            child: IconButton(
          onPressed: () {},
          tooltip: 'to compare the ontology files',
          icon: Icon(
            Icons.info,
            color: active,
          ),
        ));

      case lineChartPageRoute:
        return Container(
            child: IconButton(
          onPressed: () {},
          tooltip: 'to see the development of metrics over time',
          icon: Icon(
            Icons.info,
            color: active,
          ),
        ));
      case differencesPageRoute:
        return Container(
            child: IconButton(
          onPressed: () {},
          tooltip:
              'to see which metrics have changed compared to the previous version (commit)',
          icon: Icon(
            Icons.info,
            color: active,
          ),
        ));

      default:
        return Text('default');
    }
  }
}
