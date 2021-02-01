FiltView : ModuleView {
  classvar <spacerSpots = #[3, 8], <maxParams = 11;
  classvar <inletOffset = 3, <arInlets = 1;
  var envMenu;

  prMakeExtraMenus {
    envMenu = PopUpMenu(view, Rect(110 + leftOffset, 7, 66, 12))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .font_(Font.monospace.size_(8));
  }

  prPopulateExtraMenus {
    envMenu.items_(model.envTypes).value_(model.envType).visible_(model.envTypes.size > 0);
    envMenu.signal(\value).connectTo(model.methodSlot("envType_(value)"));
    model.signal(\envType).connectTo(envMenu.valueSlot);
  }

  prDropSetup {
    view.canReceiveDragHandler = true;
    view.receiveDragHandler = { |v, x, y|
      if (y > (view.bounds.height - 10)) {
        var to, inlet, arInlet;
        # to, inlet, arInlet = View.currentDrag;
        if (inlet.isNil) {
          model.arPatchTo(to, arInlet);
        } {
          "WARNING: Can't patch a Filter to a LFO input".postln;
        };
      };
    };
  }
}
