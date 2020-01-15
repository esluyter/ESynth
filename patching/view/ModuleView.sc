ModuleView : SCViewHolder {
  var curInlet = nil;
  var connections;
  var <classMenu, <typeMenu, <knobs, <boxes, <names, tooltip;
  var <model;

  classvar spacer = 10;
  classvar <inletOffset = 0; // override this to offset
  classvar <arInlets = 0; // override to add audio ins
  /*
    N.B. define in subclasses
    classvar spacerSpots, maxParams;
  */

  *new { |parent, bounds, model|
    ^super.new.init(parent, bounds).model_(model);
  }

  init { |parent, bounds|
    bounds = bounds ?? Rect(0, 0, parent.bounds.width, parent.bounds.height);
    view = UserView(parent, bounds).onClose_({ connections.free });

    this.prMakeMenus;
    this.prMakeExtraMenus;
    this.prMakeParams;
    this.prMouseSetup(parent);
    this.prDropSetup;
  }

  prMakeMenus {
    classMenu = PopUpMenu(view, Rect(4, 7, 96, 12))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .font_(Font.monospace.size_(8));
    typeMenu = PopUpMenu(view, Rect(this.bounds.width - 71, 7, 66, 12))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .font_(Font.monospace.size_(8));
  }

  prMakeExtraMenus { } // override this

  prMakeParams {
    knobs = this.class.maxParams.collect { |i|
      Knob(view, Rect(4 + this.prOffsetX(i), 22, 25, 25))
        .mode_(\vert)
        .color_([Color.gray(0.8), Color.white, Color.clear, Color.black]);
    };
    boxes = this.class.maxParams.collect { |i|
      NumberBox(view, Rect(3 + this.prOffsetX(i), 46, 27, 11))
        .font_(Font.monospace.size_(8))
        .background_(Color.grey(0.04))
        .stringColor_(Color.white)
        .normalColor_(Color.white)
        .typingColor_(Color.hsv(0, 0.5, 1))
        .align_(\center)
        .maxDecimals_(4);
    };
    names = this.class.maxParams.collect { |i|
      StaticText(view, Rect(2 + this.prOffsetX(i), 58, 29, 10))
        .font_(Font.sansSerif.size_(8))
        .align_(\center)
        .stringColor_(Color.white);
    };
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
        tooltip.string_(model.params[curInlet + this.class.inletOffset].name)
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
          if (curInlet.notNil) { model.patchFrom(nil, curInlet) }
        };
      };
    };

    view.mouseLeaveAction = {
      curInlet = nil;
      tooltip.visible_(false);
      view.refresh;
    };
    [classMenu, typeMenu].do(_.mouseEnterAction_({
      curInlet = nil;
      tooltip.visible_(false);
      view.refresh;
    }));

    view.beginDragAction = {
      //("Dragging from " ++ curInlet).postln;
      [model, curInlet]
    };
  }

  prDropSetup { } // override if you want to be able to drop on this module

  prOffsetX { |index|
    var numspacers = this.class.spacerSpots.select(_ <= index).size;
    ^(30 * index + (numspacers * spacer));
  }

  prOffsetXInlet { |index|
    ^this.prOffsetX(index + this.class.inletOffset);
  }

  model_ { |value|
    model = value;
    (knobs ++ boxes ++ names).do(_.visible_(false));
    connections.free;
    connections = ConnectionList.make {
      model.params.do { |param, i|
        [knobs[i], boxes[i], names[i]].do(_.visible_(true));
        names[i].string_(param.displayName);
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
      classMenu.items_(model.displayNames)
        .value_(model.displayNames.indexOf(model.displayName))
        .visible_(model.defs.size > 0);
      classMenu.signal(\value).connectTo(model.methodSlot("defInput_(value)"));
      model.signal(\def).connectTo(this.methodSlot("model_(value)"));
      typeMenu.items_(model.types)
        .value_(model.type)
        .visible_(model.types.size > 0);
      typeMenu.signal(\value).connectTo(model.methodSlot("type_(value)"));
      model.signal(\type).connectTo(typeMenu.valueSlot);
      this.prPopulateExtraMenus;
    };
    this.prMakeDrawFunc;
  }

  prPopulateExtraMenus { } // override this

  prMakeDrawFunc {
    view.drawFunc = { |v|
      // border
      Pen.addRoundedRect(Rect(1, 3, v.bounds.width - 2, v.bounds.height - 6), 5, 5);
      Pen.strokeColor = Color.white;
      Pen.width = 1;
      Pen.fillColor = if (model.displayName == "- empty -") { Color(0.1, 0, 0.1, 0.5) } { Color.gray(0.18, 0.9) };
      Pen.fillStroke;

      // inlets
      this.class.arInlets.do { |i|
        Pen.addRoundedRect(Rect(13 + this.prOffsetX(i), 1, 6, 4), 1, 1);
        Pen.fillColor = Color(0.1, 0, 0.1);
        Pen.strokeColor = Color.white;
        Pen.width = 1;
        Pen.fillStroke;
      };
      (model.params.size - this.class.inletOffset).do { |i|
        Pen.addRoundedRect(Rect(13 + this.prOffsetXInlet(i), 1, 6, 4), 1, 1);
        if (curInlet == i) {
          Pen.fillColor = Color.white;
          Pen.strokeColor = Color.white;
          Pen.width = 1;
          Pen.fillStroke;
        } {
          Pen.fillColor = Color.gray;
          Pen.fill;
        };
      };

      // outlet
      Pen.addRoundedRect(Rect(13, v.bounds.height - 5, 6, 4), 1, 1);
      if (model.rate == \kr) {
        Pen.fillColor = Color.gray;
        Pen.fill;
      } {
        Pen.fillColor = Color(0.1, 0, 0.1);
        Pen.strokeColor = Color.white;
        Pen.width = 1;
        Pen.fillStroke;
      };
    };
    view.refresh;
  }

  getInletNum { |point|
    var inlet;
    (model.params.size - this.class.inletOffset).do { |i|
      inlet = Point(16 + this.prOffsetXInlet(i), 3);
      if (((inlet.x - point.x).abs < 5) && ((inlet.y - point.y).abs < 4)) {
        ^i;
      };
    };
    ^nil;
  }

  getInletPointNoOffset { |num = 0|
    ^Point(view.bounds.left + 16 + this.prOffsetX(num), view.bounds.top + 3);
  }

  getInletPoint { |num = 0|
    ^Point(view.bounds.left + 16 + this.prOffsetXInlet(num), view.bounds.top + 3);
  }

  getOutletPoint { |num = 0|
    ^Point(view.bounds.left + 16 + this.prOffsetX(num), view.bounds.top + view.bounds.height - 3);
  }
}
