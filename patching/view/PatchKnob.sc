PatchKnob : SCViewHolder {
  var overInlet = false;
  var connections;
  var <knob, tooltip;
  var <model;

  *new { |parent, bounds, model|
    ^super.new.init(parent, bounds, model);
  }

  init { |parent, bounds, argmodel|
    bounds = bounds ?? Rect(0, 0, parent.bounds.width, parent.bounds.height);
    model = argmodel;

    view = UserView(parent, bounds).onClose_({ connections.free })
      .drawFunc_({ |v|
        Pen.addRoundedRect(Rect(0, 0, 6, 4), 1, 1);
        if (overInlet) {
          Pen.fillColor = Color.white;
          Pen.strokeColor = Color.white;
          Pen.width = 1;
          Pen.fillStroke;
        } {
          Pen.fillColor = Color.gray;
          Pen.fill;
        };
      });

    knob = Knob(view, Rect(0, 4, 16, 16))
      .mode_(\vert)
      .color_([Color.gray(0.8), Color.white, Color.clear, Color.black])
      .centered_(true)
      .mouseDownAction_({ |v, x, y, mod, buttNum, clickCount|
        if (buttNum == 0 && (clickCount == 2)) {
          model.amt_(0);
        };
      })
      .value_(model.params[0].cv.input);

    connections = ConnectionList.make {
      model.params[0].cv.signal(\input).connectTo(knob.valueSlot);
      knob.signal(\value).connectTo(model.params[0].cv.inputSlot);
    };

    this.prMouseSetup(parent);
  }

  prMouseSetup { |parent|
    var inletPoint = Point(view.bounds.left + 3, view.bounds.top + 2);

    tooltip = StaticText(parent, Rect(inletPoint.x - 15, inletPoint.y - 13, 30, 10))
      .visible_(false)
      .font_(Font.sansSerif.size_(8))
      .stringColor_(Color.white)
      .align_(\center)
      .string_("amt");

    view.mouseOverAction = { |v, x, y|
      overInlet = this.isOverInlet(x@y);

      if (overInlet) {
        tooltip.visible_(true);
      } {
        tooltip.visible_(false);
      };
      view.refresh;
    };

    view.mouseDownAction = { |v, x, y, mod, buttnum, clickcount|
      if (buttnum == 0) {
        if (overInlet) { this.beginDrag(x, y) };
      } {
        if (clickcount > 1) {
          if (overInlet) { model.patchFrom(nil, 0) }
        };
      };
    };

    view.mouseLeaveAction = {
      overInlet = false;
      tooltip.visible_(false);
      view.refresh;
    };

    knob.mouseEnterAction_({
      overInlet = false;
      tooltip.visible_(false);
      view.refresh;
    });

    view.beginDragAction = {
      //("Dragging from 0").postln;
      [model, 0]
    };
  }

  isOverInlet { |point|
    var inlet = Point(3, 2);
    if (((inlet.x - point.x).abs < 5) && ((inlet.y - point.y).abs < 4)) {
      ^true;
    };
    ^false;
  }
}
