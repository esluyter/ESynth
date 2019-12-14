(
~lfos = ESModuleList(\lfo, 20);
~oscs = ESModuleList(\osc, 6);
~oscs[0].def_(\VCO);
~oscs[1].def_(\VCO);
~oscs[3].def_(\VCO);
~oscs[4].def_(\Noise);
~filts = ESModuleList(\filt, 4);
~filts[0].def_(\Houvilainen);
~amps = ESModuleList(\amp, 1);
~amps[0].def_(\VCA);

Window.closeAll;
w = Window.new("Lines", Rect(0, 200, 880, 975)).front;
w.acceptsMouseOver = true;
w.background_(Color(0.1, 0, 0.1));
v = UserView.new(w, Rect(0, 0, w.bounds.width, w.bounds.height));

~patchCord = { |p1, p2, makeKnob = true, patchCord|
  var offset = Point(0, max(((p2.y - p1.y) / 2), 40));
  Pen.moveTo(p1);
  Pen.curveTo(p2, p1 + offset, p2 - offset);
  if (patchCord.notNil) {
    Pen.strokeColor = patchCord.color;
  };
  Pen.stroke;
  if (makeKnob) {
    var view = UserView(v, Rect(p2.x - 8, p2.y - 20, 16, 20))
    .drawFunc_({ |v|
      Pen.addRoundedRect(Rect(0, 0, 6, 4), 1, 1);
      Pen.fillColor = Color.gray;
      Pen.fill;
    });
    var knob = Knob(view, Rect(0, 4, 16, 16))
    .mode_(\vert)
    .color_([Color.gray(0.8), Color.white, Color.clear, Color.black])
    .centered_(true)
    .value_(0.5)
    .mouseDownAction_({ |v, x, y, mod, buttNum, clickCount|
      if (buttNum == 0 && (clickCount == 2)) {
        //param.value_(param.spec.default);
        knob.value_(0.5);
      };
    });
    ~patchKnobs = ~patchKnobs.add(view);
    if (patchCord.notNil) {
      if (patchCord.patchCords.size > 0) {
        ~patchCord.(~lfoviews[patchCord.patchCords[0].fromIndex].getOutletPoint, Point(p2.x - 5, p2.y - 18), patchCord: patchCord.patchCords[0]);
      };
    };
  };
};

~makePatches = {
  ~patchKnobs.do(_.remove);
  ~patchKnobs = [];

  v.drawFunc = { |v|
    var plusin = 30@645;
    var plusout = 30@645;

    Pen.width = 2.5;
    ~lfos.patchCords.do { |patchCord|
      ~patchCord.(~lfoviews[patchCord.fromIndex].getOutletPoint, ~lfoviews[patchCord.toIndex].getInletPoint(patchCord.toInlet), patchCord: patchCord);
    };
    ~oscs.patchCords.do { |patchCord|
      ~patchCord.(~lfoviews[patchCord.fromIndex].getOutletPoint, ~oscviews[patchCord.toIndex].getInletPoint(patchCord.toInlet), patchCord: patchCord);
    };
    ~filts.patchCords.do { |patchCord|
      ~patchCord.(~lfoviews[patchCord.fromIndex].getOutletPoint, ~filtviews[patchCord.toIndex].getInletPoint(patchCord.toInlet), patchCord: patchCord);
    };
    ~amps.patchCords.do { |patchCord|
      ~patchCord.(~lfoviews[patchCord.fromIndex].getOutletPoint, ~ampviews[patchCord.toIndex].getInletPoint(patchCord.toInlet), patchCord: patchCord);
    };

    Pen.width = 2;
    Pen.strokeColor = Color.gray(0.4);
    ~oscviews.do { |osc|
      ~patchCord.(osc.getOutletPoint(0), plusin, false);
    };
    ~patchCord.(plusout, ~filtviews[0].getInletPointNoOffset(0), false);
    ~patchCord.(plusout, ~filtviews[1].getInletPointNoOffset(0), false);
    ~patchCord.(~filtviews[0].getOutletPoint(0), ~filtviews[2].getInletPointNoOffset(0), false);
    ~patchCord.(~filtviews[1].getOutletPoint(0), ~filtviews[3].getInletPointNoOffset(0), false);
    ~patchCord.(~filtviews[2].getOutletPoint(0), ~ampviews[0].getInletPointNoOffset(0), false);
    ~patchCord.(~filtviews[3].getOutletPoint(0), ~ampviews[0].getInletPointNoOffset(1), false);
    Pen.stroke;
  };

  v.refresh;
};

~lfoviews = ~lfos.collect { |lfo, i|
  var row = i.div(5);
  var col = i - (row * 5);
  var indent = row % 2 * 20;
  LFOView(v, Rect(170 * col + indent + 14, 105 * row + 12, 153, 75), lfo);
  // 1px extra for line, 3px extra for dots
};

~oscviews = ~oscs.collect { |osc, i|
  var row = i.div(3);
  var col = i - (row * 3);
  var indent = row % 2 * 20;
  OscView(v, Rect(270 * col + indent + 14, 105 * row + 442, 253, 75), osc);
};

~filtviews = ~filts.collect { |filt, i|
  var row = i.div(2);
  var col = i - (row * 2);
  FiltView(v, Rect(370 * col + 14, 105 * row + 667, 353, 75), filt);
};

~ampviews = [AmpView(v, Rect(14, 887, 293, 75), ~amps[0])];

~makePatches.();

~lfos.signal(\patchCords).connectTo(~makePatches.methodSlot("value"));
~oscs.signal(\patchCords).connectTo(~makePatches.methodSlot("value"));
~filts.signal(\patchCords).connectTo(~makePatches.methodSlot("value"));
~amps.signal(\patchCords).connectTo(~makePatches.methodSlot("value"));
)


~lfos[0].patchTo(~oscs[0], \duty)
~lfos[1].patchTo(~oscs[0].patchAt(\duty))
~lfos[5].patchTo(~oscs[0].patchAt(\duty).patchAt(0))
~oscs[0].patchAt(\duty).patchAt(0).patchFrom(nil)
~oscs[0].patchAt(\duty).patchFrom(nil)
~oscs[0].patchFrom(nil, \duty)