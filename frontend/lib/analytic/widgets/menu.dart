import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/constants/custom_text.dart';
import 'package:neonto_frontend/analytic/constants/style.dart';
import 'package:neonto_frontend/analytic/helpers/reponsiveness.dart';
import 'package:neonto_frontend/analytic/routing/local_navigator.dart';

class Menu extends StatelessWidget {
  const Menu({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    double _width = MediaQuery.of(context).size.width;
    return Container(
      color: light,
      child: ListView(children: [
        if (ResponsiveWidget.isSmallScreen(context))
          Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              SizedBox(height: 40),
              Row(
                children: [
                  SizedBox(width: _width / 48),
                  Padding(padding: EdgeInsets.only(right: 12)),
                  Flexible(
                    child: CustomText(
                      text: "Select a chart type",
                      size: 20,
                      color: dark,
                    ),
                  ),
                  SizedBox(
                    width: _width / 48,
                  )
                ],
              ),
            ],
          ),
        Column(
          mainAxisSize: MainAxisSize.min,
          //To fill the menu with the SideMenuItems
          children: sideMenuItems
              .map((itemName) => SideMenuItem(
                    itemName: itemName,
                    onTap: () {
                      if (!menuController.isActive(itemName)) {
                        menuController.ChangeActiveElementPage(itemName);

                        navigationController.navigateTo(itemName);
                      }
                    },
                  ))
              .toList(),
        )
      ]),
    );
  }
}

class DisplayedMenuItem extends StatelessWidget {
  final String? itemName;
  final onTap;
  const DisplayedMenuItem({Key? key, this.itemName, this.onTap})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      onHover: (value) {
        value
            ? menuController.onHover(itemName!)
            : menuController.onHover("not hovering");
      },
      child: Obx(() => Container(
            color: menuController.isHovering(itemName!)
                ? lightGrey.withOpacity(.1)
                : Colors.transparent,
            child: Row(
              children: [
                Visibility(
                  visible: menuController.isHovering(itemName!) ||
                      menuController.isActive(itemName!),
                  child: Container(
                    width: 3,
                    height: 72,
                    color: dark,
                  ),
                  maintainSize: true,
                  maintainState: true,
                  maintainAnimation: true,
                ),
                Expanded(
                    child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Padding(
                      padding: EdgeInsets.all(16),
                      child: menuController.returnChartImage(itemName!),
                    ),
                    menuController.retrunTooltip(itemName!),
                    if (!menuController.isActive(itemName!))
                      Flexible(
                          child: CustomText(
                        text: itemName,
                        color: menuController.isHovering(itemName!)
                            ? dark
                            : lightGrey,
                      ))
                    else
                      Flexible(
                          child: CustomText(
                        text: itemName,
                        color: dark,
                        size: 18,
                        weight: FontWeight.bold,
                      ))
                  ],
                ))
              ],
            ),
          )),
    );
  }
}

//the items that are displayed in the menu.
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
