LFOView : SCViewHolder {
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
      5.do { |i|
        Pen.addRoundedRect(Rect(13 + (i * 30), 1, 6, 4), 1, 1);
      };
      Pen.addRoundedRect(Rect(13, v.bounds.height - 5, 6, 4), 1, 1);
      Pen.fill;
      /*
      Pen.fillColor = Color.white;
      Pen.strokeColor = Color.gray;
      Pen.width = 1.5;
      Pen.addRoundedRect(Rect(13, v.bounds.height - 5, 6, 4), 1, 1);
      Pen.fillStroke;
      */
    };
    PopUpMenu(view, Rect(4, 7, 76, 12)).items_(["LFO Sin", "LFO Tri", "LFO Saw", "LFO RevSaw", "LFO Sqr", "LFO Rand", "LFO Env"]).font_(Font.monospace.size_(8));
    PopUpMenu(view, Rect(102, 7, 46, 12)).items_(["sync", "key", "rand"]).font_(Font.monospace.size_(8));
    5.do { |i|
      Knob(view, Rect(4 + (30 * i), 20, 25, 25));
      NumberBox(view, Rect(4 + (30 * i), 44, 25, 10)).font_(Font.monospace.size_(8)).align_(\center);
      StaticText(view, Rect(4 + (30 * i), 55, 25, 10)).string_(["freq", "phase", "delay", "width"][i]).font_(Font.sansSerif.size_(8)).align_(\center);
    };
  }

  getInletPoint { |num = 0|
    ^Point(view.bounds.left + 16 + (30 * num), view.bounds.top + 3);
  }

  getOutletPoint { |num = 0|
    ^Point(view.bounds.left + 16 + (30 * num), view.bounds.top + view.bounds.height - 3);
  }
}