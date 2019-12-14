LFOView : ModuleView {
  classvar <spacerSpots = #[], <maxParams = 5;
  var globalButt;

  // narrower menus for LFO view
  prMakeMenus {
    classMenu = PopUpMenu(view, Rect(4, 7, 64, 12))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .font_(Font.monospace.size_(8));
    typeMenu = PopUpMenu(view, Rect(74, 7, 60, 12))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .font_(Font.monospace.size_(8));
  }

  prMakeExtraMenus {
    // TODO: make this work
    globalButt = Button(view, Rect(136, 7, 12, 12))
      .states_([
        ["L", Color.white, Color.grey(0.04)],
        ["G", Color.black, Color.grey(0.8)]])
      .font_(Font.monospace.size_(8));
  }

  prPopulateExtraMenus {
    globalButt.visible_(model.def.notNil).value_(model.global.asInt);
    globalButt.signal(\value).connectTo(model.methodSlot("global_(value == 1)"));
    model.signal(\global).connectTo(globalButt.methodSlot("value_(value.asInt)"));
  }

  // only LFOs can receive drops
  prDropSetup {
    view.canReceiveDragHandler = true;
    view.receiveDragHandler = { |v, x, y|
      if (y > (view.bounds.height - 10)) {
        var toModule, inlet;
        # toModule, inlet = View.currentDrag;
        model.patchTo(toModule, inlet);
      }
    };
  }
}
