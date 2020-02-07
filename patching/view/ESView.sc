ESView : SCViewHolder {
  var connections;
  var <model, <patchKnobs, <lfoViews, <oscViews, <filtViews, <ampViews;

  *new { |parent, bounds, model|
    ^super.new.init(parent, bounds).model_(model);
  }

  init { |parent, bounds|
    bounds = bounds ?? Rect(0, 0, parent.bounds.width, parent.bounds.height);
    view = UserView(parent, bounds)
      .background_(Color(0.1, 0, 0.1))
      .onClose_({
        connections.free;
        [lfoViews, oscViews, filtViews, ampViews].do { |a| a.do(_.remove) };
      });
  }

  model_ { |value|
    model = value;

    [lfoViews, oscViews, filtViews, ampViews].do { |a| a.do(_.remove) };

    lfoViews = model.lfos.collect { |lfo, i|
      var row = i.div(5);
      var col = i - (row * 5);
      var indent = row % 2 * 20;
      LFOView(view, Rect(170 * col + indent + 14, 105 * row + 12, 153, 75), lfo);
      // 1px extra for line, 3px extra for dots
    };

    oscViews = model.oscs.collect { |osc, i|
      var row = i.div(3);
      var col = i - (row * 3);
      var indent = row % 2 * 20;
      OscView(view, Rect(270 * col + indent + 14, 105 * row + 442, 253, 75), osc);
    };

    filtViews = model.filts.collect { |filt, i|
      var row = i.div(2);
      var col = i - (row * 2);
      FiltView(view, Rect(370 * col + 14, 105 * row + 667, 353, 75), filt);
    };

    ampViews = [AmpView(view, Rect(14, 887, 293, 75), model.amps[0])];

    this.prMakePatches.();

    connections.free;
    connections = ConnectionList.make {
      model.signal(\patchCords).connectTo(this.methodSlot("prMakePatches"));
    };
  }

  prMakePatches {
    //"ESView::prMakePatches begins".postln;

    patchKnobs.do(_.remove);
    patchKnobs = [];

    view.drawFunc = { |v|
      var plusin = 30@645;
      var plusout = 30@645;

      Pen.width = 2.5;
      model.lfos.patchCords.do { |patchCord|
        this.prDrawPatchCord(
          lfoViews[patchCord.fromIndex].getOutletPoint,
          lfoViews[patchCord.toIndex].getInletPoint(patchCord.toInlet),
          patchCord
        );
      };
      model.oscs.patchCords.do { |patchCord|
        this.prDrawPatchCord(
          lfoViews[patchCord.fromIndex].getOutletPoint,
          oscViews[patchCord.toIndex].getInletPoint(patchCord.toInlet),
          patchCord
        );
      };
      model.filts.patchCords.do { |patchCord|
        this.prDrawPatchCord(
          lfoViews[patchCord.fromIndex].getOutletPoint,
          filtViews[patchCord.toIndex].getInletPoint(patchCord.toInlet),
          patchCord
        );
      };
      model.amps.patchCords.do { |patchCord|
        this.prDrawPatchCord(
          lfoViews[patchCord.fromIndex].getOutletPoint,
          ampViews[patchCord.toIndex].getInletPoint(patchCord.toInlet),
          patchCord
        );
      };

      Pen.width = 2;
      Pen.strokeColor = Color.gray(0.4);
      oscViews.do { |osc|
        this.prDrawPatchCord(osc.getOutletPoint(0), plusin);
      };
      this.prDrawPatchCord(plusout, filtViews[0].getInletPointNoOffset(0));
      this.prDrawPatchCord(plusout, filtViews[1].getInletPointNoOffset(0));
      this.prDrawPatchCord(
        filtViews[0].getOutletPoint(0),
        filtViews[2].getInletPointNoOffset(0)
      );
      this.prDrawPatchCord(
        filtViews[1].getOutletPoint(0),
        filtViews[3].getInletPointNoOffset(0)
      );
      this.prDrawPatchCord(
        filtViews[2].getOutletPoint(0),
        ampViews[0].getInletPointNoOffset(0)
      );
      this.prDrawPatchCord(
        filtViews[3].getOutletPoint(0),
        ampViews[0].getInletPointNoOffset(1)
      );
      Pen.stroke;
    };

    view.refresh;

    //"ESView::prMakePatches ends".postln;
  }

  prDrawPatchCord { |p1, p2, patchCord|
    var offset = Point(0, max(((p2.y - p1.y) / 2), 40));
    //"prDrawPatchCord begins".postln;
    Pen.moveTo(p1);
    Pen.curveTo(p2, p1 + offset, p2 - offset);
    if (patchCord.notNil) {
      Pen.strokeColor = patchCord.color;
    };
    //"path drawn".postln;
    Pen.stroke;
    //"stroked".postln;
    if (patchCord.notNil) {
      //"ready to add knob".postln;
      patchKnobs = patchKnobs.add(
        PatchKnob(view, Rect(p2.x - 8, p2.y - 20, 16, 20), patchCord)
      );
      //"knob added".postln;
      if (patchCord.patchCords.size > 0) {
        patchCord.patchCords.postln;
        this.prDrawPatchCord(
          lfoViews[patchCord.patchCords[0].fromIndex].getOutletPoint,
          Point(p2.x - 5, p2.y - 18),
          patchCord.patchCords[0]
        );
      };
    };
    //"prDrawPatchCord ends".postln;
  }
}
