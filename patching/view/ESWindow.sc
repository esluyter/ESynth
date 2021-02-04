ESWindow : SCViewHolder {
  var <win, <sv, <esv, <presetMenu, <saveButt;

  *new { |name = "", bounds, model|
    ^super.new.init(name, bounds, model);
  }

  init { |name, bounds, model|
    bounds = bounds ?? { Rect(0, 200, min(1500, Window.screenBounds.width), min(1155, Window.screenBounds.height - 25)) };

    win = Window.new(name, bounds)
      .background_(Color(0.1, 0, 0.1))
      .front;
    win.acceptsMouseOver = true;

    view = win.view;

    sv = ScrollView(win, win.bounds.resizeBy(0, -30).origin_(0@30))
      .hasBorder_(false)
      .background_(Color(0.1, 0, 0.1));
    UserView(sv, Rect(0, 0, 1000, 415))
      .background_(Color.gray(0.2, 0.5))
      .drawFunc_({ |v|
        Pen.rotate(-pi/2, 0, 240);
        Pen.stringAtPoint("LFO", 0@238, Font.sansSerif.boldVariant.size_(44), Color.gray(1, 0.3));
      });
    UserView(sv, Rect(0, 430, 1000, 310))
      .background_(Color.gray(0.2, 0.5))
      .drawFunc_({ |v|
        Pen.rotate(-pi/2, 0, 200);
        Pen.stringAtPoint("OSC", 0@198, Font.sansSerif.boldVariant.size_(44), Color.gray(1, 0.3));
      });
    UserView(sv, Rect(0, 755, 1000, 235))
      .background_(Color.gray(0.2, 0.5))
      .drawFunc_({ |v|
        Pen.rotate(-pi/2, 0, 165);
        Pen.stringAtPoint("FILT", 0@163, Font.sansSerif.boldVariant.size_(44), Color.gray(1, 0.3));
      });
    UserView(sv, Rect(0, 1005, 1000, 110))
      .background_(Color.gray(0.2, 0.5))
      .drawFunc_({ |v|
        Pen.rotate(-pi/2, 0, 105);
        Pen.stringAtPoint("AMP", 0@103, Font.sansSerif.boldVariant.size_(44), Color.gray(1, 0.3));
      });

    esv = ESView(sv, Rect(50, 0, 930, 1115), model);

    presetMenu = PopUpMenu(win, Rect(610, 5, 260, 20))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .font_(Font.monospace.size_(8))
      .items_(ESMPresets.all.keys.asArray.sort)
      .action_({ this.changed(\preset, presetMenu.item) });
    saveButt = Button(win, Rect(530, 5, 70, 20))
      .states_([["Save As...", Color.white, Color.grey(0.04)]])
      .font_(Font.monospace.size_(8))
      .action_({
        var saveWin = Window("Save preset", Rect(win.bounds.left + 200, win.bounds.top + win.bounds.height - 250, 480, 200))
          .background_(Color(0.1, 0, 0.1))
          .front;
        var nameField = TextField(saveWin, Rect(50, 75, 380, 30))
          .font_(Font.monospace.size_(13))
          .background_(Color.gray(0.2))
          .stringColor_(Color.white);
        Button(saveWin, Rect(50, 130, 100, 20))
          .states_([["Save", Color.white, Color.gray(0.2)]])
          .font_(Font.monospace.size_(10))
          .action_({
            var name = nameField.string.asSymbol;
            ESMPresets.put(name, this.model.asEvent);
            presetMenu.items_(ESMPresets.all.keys.asArray.sort)
              .value_(ESMPresets.all.keys.asArray.sort.indexOf(name));
            saveWin.close;
        });
      StaticText(saveWin, Rect(50, 35, 400, 20))
        .string_("Save preset")
        .font_(Font.monospace.size_(20))
        .stringColor_(Color.white);
    });
  }

  model { ^esv.model }

  setPresetName { |presetName|
    presetMenu.value_(ESMPresets.all.keys.asArray.sort.indexOf(presetName));
  }
}
