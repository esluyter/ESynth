(
Window.closeAll;
w = Window.new("Lines", Rect(1200, 200, 740, 800)).front;
v = UserView.new(w, Rect(0, 0, w.bounds.width, w.bounds.height));

~patchCord = { |p1, p2, makeKnob = true|
  var offset = Point(0, max(((p2.y - p1.y) / 2), 40));
  Pen.moveTo(p1);
  Pen.curveTo(p2, p1 + offset, p2 - offset);
  if (makeKnob) {
    Knob(v, Rect(p2.x - 8, p2.y - 16, 16, 16));
  };
};

~lfos = [
  LFOView(v, Rect(14, 12, 153, 75)), // 1px extra for line, 3px extra for dots
  LFOView(v, Rect(184, 12, 153, 75)),
  LFOView(v, Rect(34, 107, 153, 75)),
  LFOView(v, Rect(204, 107, 153, 75)),

  LFOView(v, Rect(354, 12, 153, 75)),
  LFOView(v, Rect(524, 12, 153, 75)),
  LFOView(v, Rect(374, 107, 153, 75)),
  LFOView(v, Rect(544, 107, 153, 75))
];

~oscs = [
  OscView(v, Rect(14, 212, 253, 75)),
  OscView(v, Rect(34, 307, 253, 75)),

  OscView(v, Rect(284, 212, 253, 75)),
  OscView(v, Rect(304, 307, 253, 75)),
];

~filts = [
  FiltView(v, Rect(14, 427, 253, 115)),
  FiltView(v, Rect(14, 562, 253, 115)),
  FiltView(v, Rect(284, 427, 253, 115)),
  FiltView(v, Rect(284, 562, 253, 115)),
];

~amp = AmpView(v, Rect(14, 707, 283, 75));

v.drawFunc = { |v|
  var plusin = 30@405;
  var plusout = 30@405;
  Pen.strokeColor = Color.black;
  Pen.width = 1.5;
  ~patchCord.(~lfos[0].getOutletPoint(0), ~lfos[1].getInletPoint(3));
  ~patchCord.(~lfos[0].getOutletPoint(0), ~lfos[2].getInletPoint(0));
  ~patchCord.(~lfos[1].getOutletPoint(0), ~lfos[2].getInletPoint(3));
  ~patchCord.(~lfos[1].getOutletPoint(0), ~oscs[0].getInletPoint(0));
  ~patchCord.(~lfos[1].getOutletPoint(0), ~oscs[1].getInletPoint(0));
  ~patchCord.(~lfos[2].getOutletPoint(0), ~oscs[0].getInletPoint(2));
  Pen.stroke;
  Pen.strokeColor = Color.gray;
  ~oscs.do { |osc|
    ~patchCord.(osc.getOutletPoint(0), plusin, false);
  };
  ~patchCord.(plusout, ~filts[0].getInletPoint(0), false);
  ~patchCord.(plusout, ~filts[2].getInletPoint(0), false);
  ~patchCord.(~filts[0].getOutletPoint(0), ~filts[1].getInletPoint(0), false);
  ~patchCord.(~filts[2].getOutletPoint(0), ~filts[3].getInletPoint(0), false);
  ~patchCord.(~filts[1].getOutletPoint(0), ~amp.getInletPoint(0), false);
  ~patchCord.(~filts[3].getOutletPoint(0), ~amp.getInletPoint(1), false);
  Pen.stroke;
  /*
  Pen.width = 1;
  Pen.strokeColor = Color.black;
  Pen.fillColor = Color.white;
  Pen.addOval(Rect(20, 395, 20, 20));
  Pen.fillStroke;
  */
};
)