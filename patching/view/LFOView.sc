LFOView : ModuleView {
  classvar <spacerSpots = #[], <maxParams = 5;

  // narrower menus for LFO view
  prMakeMenus {
    classMenu = PopUpMenu(view, Rect(4, 7, 76, 12))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .font_(Font.monospace.size_(8));
    typeMenu = PopUpMenu(view, Rect(87, 7, 61, 12))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
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
