FiltView : ModuleView {
  classvar <spacerSpots = #[3, 8], <maxParams = 11;
  classvar <inletOffset = 3, <arInlets = 1;
  var envMenu;

  prMakeExtraMenus {
    envMenu = PopUpMenu(view, Rect(282, 7, 66, 12))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .font_(Font.monospace.size_(8));
  }

  prPopulateExtraMenus {
    envMenu.items_(model.envTypes).value_(model.envType).visible_(model.envTypes.size > 0);
  }
}
