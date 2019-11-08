(
Window.closeAll;
w = Window.new("Lines", Rect(0, 200, 880, 765)).front;
w.acceptsMouseOver = true;
//w.background_(Color.black)
v = UserView.new(w, Rect(0, 0, w.bounds.width, w.bounds.height));

~patchCord = { |p1, p2, makeKnob = true|
  var offset = Point(0, max(((p2.y - p1.y) / 2), 40));
  Pen.moveTo(p1);
  Pen.curveTo(p2, p1 + offset, p2 - offset);
  if (makeKnob) {
    //Knob(v, Rect(p2.x - 8, p2.y - 16, 16, 16));
  };
};

~lfos = ModuleList(LFONil, 10);

~lfoviews = ~lfos.collect { |lfo, i|
  var row = i.div(5);
  var col = i - (row * 5);
  var indent = row % 2 * 20;
  LFOView(v, Rect(170 * col + indent + 14, 105 * row + 12, 153, 75), lfo);
  // 1px extra for line, 3px extra for dots
};

//~lfos[0].rate

~oscs = ModuleList(OscNil, 6);
~oscs[0].class_(OscVCO);
~oscs[1].class_(OscVCO);
~oscs[3].class_(OscVCO);
~oscs[4].class_(OscNoise);

~oscviews = ~oscs.collect { |osc, i|
  var row = i.div(3);
  var col = i - (row * 3);
  var indent = row % 2 * 20;
  OscView(v, Rect(270 * col + indent + 14, 105 * row + 232, 253, 75), osc);
};

~filts = ModuleList(FiltNil, 4);
~filts[0].class_(FiltHouvilainen);

~filtviews = ~filts.collect { |filt, i|
  var row = i.div(2);
  var col = i - (row * 2);
  FiltView(v, Rect(370 * col + 14, 105 * row + 457, 353, 75), filt);
};

~amps = ModuleList(AmpVCA, 1);

~ampviews = [AmpView(v, Rect(14, 677, 283, 75), ~amps[0])];

v.drawFunc = { |v|
  var plusin = 30@435;
  var plusout = 30@435;

  /*
  ~patchCord.(~lfoviews[0].getOutletPoint(0), ~lfoviews[1].getInletPoint(3));
  ~patchCord.(~lfoviews[0].getOutletPoint(0), ~lfoviews[5].getInletPoint(0));
  ~patchCord.(~lfoviews[1].getOutletPoint(0), ~lfoviews[5].getInletPoint(3));
  ~patchCord.(~lfoviews[1].getOutletPoint(0), ~oscs[0].getInletPoint(0));
  ~patchCord.(~lfoviews[1].getOutletPoint(0), ~oscs[1].getInletPoint(0));
  ~patchCord.(~lfoviews[5].getOutletPoint(0), ~oscs[0].getInletPoint(2));
  ~patchCord.(~lfoviews[6].getOutletPoint(0), ~filts[0].getInletPoint(3));
  ~patchCord.(~lfoviews[7].getOutletPoint(0), ~filts[2].getInletPoint(3));
  */

  Pen.width = 2.5;
  ~lfos.patchCords.do { |patchCord|
    ~patchCord.(~lfoviews[patchCord.fromIndex].getOutletPoint, ~lfoviews[patchCord.toIndex].getInletPoint(patchCord.toInlet));
    Pen.strokeColor = patchCord.color;
    Pen.stroke;
  };
  ~oscs.patchCords.do { |patchCord|
    ~patchCord.(~lfoviews[patchCord.fromIndex].getOutletPoint, ~oscviews[patchCord.toIndex].getInletPoint(patchCord.toInlet));
    Pen.strokeColor = patchCord.color;
    Pen.stroke;
  };
  ~filts.patchCords.do { |patchCord|
    ~patchCord.(~lfoviews[patchCord.fromIndex].getOutletPoint, ~filtviews[patchCord.toIndex].getInletPoint(patchCord.toInlet));
    Pen.strokeColor = patchCord.color;
    Pen.stroke;
  };
  ~amps.patchCords.do { |patchCord|
    ~patchCord.(~lfoviews[patchCord.fromIndex].getOutletPoint, ~ampviews[patchCord.toIndex].getInletPoint(patchCord.toInlet));
    Pen.strokeColor = patchCord.color;
    Pen.stroke;
  };

  Pen.width = 1.5;
  Pen.strokeColor = Color.gray;
  ~oscviews.do { |osc|
    ~patchCord.(osc.getOutletPoint(0), plusin, false);
  };
  ~patchCord.(plusout, ~filtviews[0].getInletPoint(0), false);
  ~patchCord.(plusout, ~filtviews[1].getInletPoint(0), false);
  ~patchCord.(~filtviews[0].getOutletPoint(0), ~filtviews[2].getInletPoint(0), false);
  ~patchCord.(~filtviews[1].getOutletPoint(0), ~filtviews[3].getInletPoint(0), false);
  ~patchCord.(~filtviews[2].getOutletPoint(0), ~ampviews[0].getInletPoint(0), false);
  ~patchCord.(~filtviews[3].getOutletPoint(0), ~ampviews[0].getInletPoint(1), false);
  Pen.stroke;
};

~lfos.signal(\patchCords).connectTo(v.methodSlot("refresh"));
~oscs.signal(\patchCords).connectTo(v.methodSlot("refresh"));
~filts.signal(\patchCords).connectTo(v.methodSlot("refresh"));
~amps.signal(\patchCords).connectTo(v.methodSlot("refresh"));
)

FiltModel.tryPerform(\envTypes).asArray

s.actualSampleRate
s.sampleRate / s.options.blockSize

~lfos[0].patchTo(~lfos[5], 0);
~lfos[0].patchTo(~lfos[5], 3);
~lfos[0].patchTo(~lfos[1], 3);
~lfos[1].patchTo(~lfos[5], 3);
~lfos[0].patchCords[0].dump
~lfos.patchCords
~lfos[5].dump

p = Array(5)

[3, 6].select(_<=6)