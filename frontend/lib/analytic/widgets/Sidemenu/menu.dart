import 'package:flutter/material.dart';
import 'package:neonto_frontend/analytic/controllers/controllers.dart';
import 'package:neonto_frontend/analytic/constants/custom_text.dart';
import 'package:neonto_frontend/analytic/constants/style.dart';
import 'package:neonto_frontend/analytic/helpers/reponsiveness.dart';
import 'package:neonto_frontend/analytic/routing/routes.dart';
import 'package:neonto_frontend/analytic/widgets/Sidemenu/menu_item.dart';

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
