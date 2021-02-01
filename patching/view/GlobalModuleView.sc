GlobalModuleView : SCViewHolder {
  var curInlet = nil;
  var connections;
  var <knobs, <boxes, <names, <tooltip;
  var <model;

  classvar leftOffset = 5, rightOffset = 5, numInlets = 2;
  classvar paramNames = #["tune", "fine"];

  *new { |parent, bounds, model|
    ^super.new.init(parent, bounds).model_(model);
  }

  init { |parent, bounds|
    bounds = bounds ?? Rect(0, 0, parent.bounds.width, parent.bounds.height);
    view = UserView(parent, bounds).onClose_({ connections.free });
    StaticText(view, Rect(0, 4, bounds.width, 20)).string_("Global Tuning").stringColor_(Color.gray(0.7)).font_(Font.monospace.size_(9)).align_(\center);

    this.prMakeParams;
    this.prMouseSetup(parent);
  }

  model_ { |value|
    model = value;
    connections.free;
    connections = ConnectionList.make {
      2.do { |i|
        var param = model.params[i + model.def.modOffset];
        knobs[i]
          .centered_(param.centered)
          .step_(param.step / param.spec(model.rate).range)
          .shift_scale_(param.shift_scale)
          .mouseDownAction_({ |v, x, y, mod, buttNum, clickCount|
            if (buttNum == 0 && (clickCount == 2)) {
              param.value_(param.spec(model.rate).default);
            };
          });
        boxes[i]
          .step_(param.step)
          .scroll_step_(param.step)
          .shift_scale_(param.shift_scale)
          .clipLo_(param.spec.minval)
          .clipHi_(param.spec.maxval)
          .mouseDownAction_({ |v, x, y, mod, buttNum, clickCount|
            if (buttNum == 0 && (clickCount == 2)) {
              param.value_(param.spec(model.rate).default);
            };
          });
        param.cv.signal(\value).connectTo(boxes[i].valueSlot);
        param.cv.signal(\input).connectTo(knobs[i].valueSlot);
        knobs[i].signal(\value).connectTo(param.cv.inputSlot);
        boxes[i].signal(\value).connectTo(param.cv.valueSlot);
        knobs[i].value_(param.cv.input);
      };
    };
    this.prMakeDrawFunc;
  }

  prMakeParams {
    knobs = paramNames.collect { |name, i|
      Knob(view, Rect(4 + leftOffset + this.prOffsetX(i), 22, 25, 25))
        .mode_(\vert)
        .color_([Color.gray(0.8), Color.white, Color.clear, Color.black]);
    };
    boxes = paramNames.collect { |name, i|
      NumberBox(view, Rect(3 + leftOffset + this.prOffsetX(i), 46, 27, 11))
        .font_(Font.monospace.size_(8))
        .background_(Color.grey(0.04))
        .stringColor_(Color.white)
        .normalColor_(Color.white)
        .typingColor_(Color.hsv(0, 0.5, 1))
        .align_(\center)
        .maxDecimals_(4);
    };
    names = paramNames.collect { |name, i|
      StaticText(view, Rect(2 + leftOffset + this.prOffsetX(i), 58, 29, 10))
        .font_(Font.sansSerif.size_(8))
        .align_(\center)
        .stringColor_(Color.white)
        .string_(name);
    };
  }

  prOffsetX { |index|
    ^(40 * index + 10);
  }

  prMouseSetup { |parent|
    tooltip = StaticText(parent, Rect(0, 0, 30, 10))
      .visible_(false)
      .font_(Font.sansSerif.size_(8))
      .stringColor_(Color.white)
      .align_(\center);

    view.mouseOverAction = { |v, x, y|
      curInlet = this.getInletNum(x@y);

      if (curInlet.notNil) {
        var inletPoint = this.getInletPoint(curInlet);
        tooltip.string_(paramNames[curInlet])
          .visible_(true)
          .bounds_(Rect(inletPoint.x - 15, inletPoint.y - 13, 30, 10));
      } {
        tooltip.visible_(false);
      };
      view.refresh;
    };

    view.mouseDownAction = { |v, x, y, mod, buttnum, clickcount|
      if (buttnum == 0) {
        if (curInlet.notNil) { this.beginDrag(x, y) };
      } {
        if (clickcount > 1) {
          if (curInlet.notNil) { model.patchFrom(nil, curInlet) };
        };
      };
    };

    view.mouseLeaveAction = {
      curInlet = nil;
      tooltip.visible_(false);
      view.refresh;
    };

    view.beginDragAction = {
      //("Dragging from " ++ curInlet).postln;
      [model, curInlet, nil]
    };
  }

  prMakeDrawFunc {
    view.drawFunc = { |v|
      // border
      Pen.addRoundedRect(Rect(3, 3, v.bounds.width - 6, v.bounds.height - 6), 5, 5);
      Pen.width = 1;
      Pen.fillColor = Color.gray(0.18, 0.9);
      Pen.strokeColor = Color.gray(0.7);
      Pen.fillStroke;

      // inlets
      numInlets.do { |i|
        Pen.addRoundedRect(Rect(13 + leftOffset + this.prOffsetX(i), 1, 6, 4), 1, 1);
        if (curInlet == i) {
          Pen.fillColor = Color.white;
          Pen.strokeColor = Color.white;
          Pen.width = 1;
          Pen.fillStroke;
        } {
          Pen.fillColor = Color.gray(0.6);
          Pen.fill;
        };
      };
    };
    view.refresh;
  }

  getInletPoint { |num = 0|
    ^Point(view.bounds.left + 16 + leftOffset + this.prOffsetX(num), view.bounds.top + 3);
  }

  getInletNum { |point|
    var inlet;
    numInlets.do { |i|
      inlet = Point(16 + leftOffset + this.prOffsetX(i), 3);
      if (((inlet.x - point.x).abs < 5) && ((inlet.y - point.y).abs < 4)) {
        ^i;
      };
    };
    ^nil;
  }
}
