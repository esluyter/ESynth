OscView : ModuleView {
  classvar <spacerSpots = #[4, 8], <maxParams = 12;

  prDropSetup {
    view.canReceiveDragHandler = true;
    view.receiveDragHandler = { |v, x, y|
      if (y > (view.bounds.height - 10)) {
        var to, inlet, arInlet;
        # to, inlet, arInlet = View.currentDrag;
        if (inlet.isNil) {
          model.arPatchTo(to, arInlet);
        } {
          "Can't patch an Oscillator to a LFO input".warn;
        };
      };
    };
  }
}
