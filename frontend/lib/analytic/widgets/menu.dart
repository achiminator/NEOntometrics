import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/constants/custom_text.dart';
import 'package:neonto_frontend/analytic/constants/style.dart';
import 'package:neonto_frontend/analytic/routing/local_navigator.dart';

class Menu extends StatelessWidget {
  const Menu({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    double width = MediaQuery.of(context).size.width;
    return Container(
      color: Theme.of(context).colorScheme.primaryContainer,
      child: ListView(children: [
        Column(
          mainAxisSize: MainAxisSize.min,
          //To fill the menu with the SideMenuItems
          children: sideMenuItems
              .map((itemName) => Center(
                    child: SideMenuItem(
                      itemName: itemName,
                      onTap: () {
                        if (!menuController.isActive(itemName)) {
                          menuController.ChangeActiveElementPage(itemName);

                          navigationController.navigateTo(itemName);
                        }
                      },
                    ),
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
    return Center(
      child: InkWell(
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
                    maintainSize: true,
                    maintainState: true,
                    maintainAnimation: true,
                    child: Center(
                      child: Container(
                        width: 3,
                        height: 72,
                        color: dark,
                      ),
                    ),
                  ),
                  Expanded(
                      child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Padding(
                        padding: const EdgeInsets.only(
                            top: 30, bottom: 30, left: 16, right: 16),
                        child: menuController.returnChartIcon(itemName!),
                      ),
                      if (!menuController.isActive(itemName!))
                        Flexible(
                            child: Center(
                          child: CustomText(
                            size: 16,
                            weight: FontWeight.w400,
                            text: itemName,
                            color: menuController.isHovering(itemName!)
                                ? dark
                                : lightGrey,
                          ),
                        ))
                      else
                        Flexible(
                            child: CustomText(
                          text: itemName,
                          color: dark,
                          size: 17,
                          weight: FontWeight.w400,
                        ))
                    ],
                  ))
                ],
              ),
            )),
      ),
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
    return Center(
      child: DisplayedMenuItem(
        itemName: itemName,
        onTap: onTap,
      ),
    );
  }
}
