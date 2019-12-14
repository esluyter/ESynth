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
    this.prMakeExtraMenus;
  }

  prMakeExtraMenus {
    // TODO: make this work
    globalButt = Button(view, Rect(136, 7, 12, 12))
      .states_([
        ["G", Color.white, Color.grey(0.04)],
        ["L", Color.black, Color.grey(0.8)]])
      .font_(Font.monospace.size_(8));
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
