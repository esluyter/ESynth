AmpView : SCViewHolder {
  var spacer = 10;

  *new { |parent, bounds|
    ^super.new.init(parent, bounds);
  }

  init { |parent, bounds|
    bounds = bounds ?? Rect(0, 0, parent.bounds.width, parent.bounds.height);
    view = UserView(parent, bounds);
    view.drawFunc = { |v|
      Pen.strokeColor = Color.black;
      Pen.width = 1;
      Pen.fillColor = Color.white;
      Pen.addRoundedRect(Rect(1, 3, v.bounds.width - 2, v.bounds.height - 6), 5, 5);
      Pen.fillStroke;
      Pen.fillColor = Color.black;
      6.do { |i|
        i = i + 3;
        Pen.addRoundedRect(Rect(13 + (i * 30) + (if (i < 4) { 0 } { spacer }), 1, 6, 4), 1, 1);
      };
      //Pen.addRoundedRect(Rect(13, v.bounds.height - 5, 6, 4), 1, 1);
      Pen.fill;

      Pen.fillColor = Color.white;
      Pen.strokeColor = Color.gray;
      Pen.width = 1.5;
      Pen.addRoundedRect(Rect(13, 1, 6, 4), 1, 1);
      Pen.addRoundedRect(Rect(43, 1, 6, 4), 1, 1);
      //Pen.addRoundedRect(Rect(13, v.bounds.height - 5, 6, 4), 1, 1);
      Pen.fillStroke;
    };
    PopUpMenu(view, Rect(4, 7, 96, 12)).items_(["Houvilainen", "RLPF", "SVF", "DFM1", "BLowPass"]).font_(Font.monospace.size_(8));
    PopUpMenu(view, Rect(182, 7, 66, 12)).items_(["Bypass", "LP 24db", "LP 18db", "LP 12db", "LP 6db", "HP 24db", "BP 24db", "N 24db"]).font_(Font.monospace.size_(8));
    9.do { |i|
      Knob(view, Rect(4 + (30 * i) + (if (i < 4) { 0 } { spacer }), 20, 25, 25));
      NumberBox(view, Rect(4 + (30 * i) + (if (i < 4) { 0 } { spacer }), 44, 25, 10)).font_(Font.monospace.size_(8)).align_(\center);
      StaticText(view, Rect(4 + (30 * i) + (if (i < 4) { 0 } { spacer }), 55, 25, 10)).string_(["amp", "vel", "env", "pan", "delay", "a", "d", "s", "r"][i]).font_(Font.sansSerif.size_(8)).align_(\center);
    };
  }

  getInletPoint { |num = 0|
    ^Point(view.bounds.left + 16 + (30 * num) + (if (num < 4) { 0 } { spacer }), view.bounds.top + 3);
  }

  getOutletPoint { |num = 0|
    ^Point(view.bounds.left + 16 + (30 * num) + (if (num < 4) { 0 } { spacer }), view.bounds.top + view.bounds.height - 3);
  }
}