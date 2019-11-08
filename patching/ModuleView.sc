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
    ^super.new.init(parent, bounds, model);
  }

  init { |parent, bounds, argmodel|
    bounds = bounds ?? Rect(0, 0, parent.bounds.width, parent.bounds.height);
    view = UserView(parent, bounds).onClose_({ connections.free });

    this.prMakeMenus;
    this.prMakeParams;
    this.prMouseSetup(parent);
    this.prDropSetup;
    this.model_(argmodel);
  }

  prMakeMenus {
    classMenu = PopUpMenu(view, Rect(4, 7, 96, 12))
      .font_(Font.monospace.size_(8));
    typeMenu = PopUpMenu(view, Rect(110, 7, 66, 12))
      .font_(Font.monospace.size_(8));
    this.prMakeExtraMenus;
  }

  prMakeExtraMenus { } // override this

  prMakeParams {
    knobs = this.class.maxParams.collect { |i|
      Knob(view, Rect(4 + this.prOffsetX(i), 22, 25, 25))
        .mode_(\vert);
    };
    boxes = this.class.maxParams.collect { |i|
      NumberBox(view, Rect(3 + this.prOffsetX(i), 46, 27, 11))
        .font_(Font.monospace.size_(8))
        .align_(\center)
        .maxDecimals_(4);
    };
    names = this.class.maxParams.collect { |i|
      StaticText(view, Rect(2 + this.prOffsetX(i), 57, 29, 10))
        .font_(Font.sansSerif.size_(8))
        .align_(\center);
    };
  }

  prMouseSetup { |parent|
    tooltip = StaticText(parent, Rect(0, 0, 30, 10))
      .visible_(false)
      .font_(Font.sansSerif.size_(8))
      .align_(\center);

    view.mouseOverAction = { |v, x, y|
      curInlet = this.getInletNum(x@y);

      if (curInlet.notNil) {
        var inletPoint = this.getInletPoint(curInlet);
        tooltip.string_(model.params[curInlet].name)
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
      ("Dragging from " ++ curInlet).postln;
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
    classMenu.items_(model.classes.collect(_.displayName))
      .value_(model.classes.indexOf(model.class))
      .visible_(model.classes.size > 0);
    typeMenu.items_(model.types)
      .value_(model.type)
      .visible_(model.types.size > 0);
    this.prPopulateExtraMenus;
    (knobs ++ boxes ++ names).do(_.visible_(false));
    connections.free;
    connections = ConnectionList.make {
      model.params.do { |param, i|
        [knobs[i], boxes[i], names[i]].do(_.visible_(true));
        names[i].string_(param.name);
        knobs[i]
          .centered_(param.centered)
          .step_(param.step / param.spec.range)
          .shift_scale_(param.shift_scale)
          .mouseDownAction_({ |v, x, y, mod, buttNum, clickCount|
            if (buttNum == 0 && (clickCount == 2)) {
              param.value_(param.spec.default);
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
              param.value_(param.spec.default);
            };
          });
        param.cv.signal(\value).connectTo(boxes[i].valueSlot);
        param.cv.signal(\input).connectTo(knobs[i].valueSlot);
        knobs[i].signal(\value).connectTo(param.cv.inputSlot);
        boxes[i].signal(\value).connectTo(param.cv.valueSlot);
        knobs[i].value_(param.cv.input);
      };
      model.signal(\replaced).connectTo(this.methodSlot("model_(value)"));
      classMenu.signal(\value).connectTo(model.methodSlot("classInput_(value)"));
    };
    this.prMakeDrawFunc;
  }

  prPopulateExtraMenus { } // override this

  prMakeDrawFunc {
    view.drawFunc = { |v|
      // border
      Pen.addRoundedRect(Rect(1, 3, v.bounds.width - 2, v.bounds.height - 6), 5, 5);
      Pen.strokeColor = Color.black;
      Pen.width = 1;
      Pen.fillColor = Color.white;
      Pen.fillStroke;

      // inlets
      this.class.arInlets.do { |i|
        Pen.addRoundedRect(Rect(13 + this.prOffsetX(i), 1, 6, 4), 1, 1);
        Pen.fillColor = Color.white;
        Pen.strokeColor = Color.gray;
        Pen.width = 1.5;
        Pen.fillStroke;
      };
      (model.params.size - this.class.inletOffset).do { |i|
        Pen.addRoundedRect(Rect(13 + this.prOffsetXInlet(i), 1, 6, 4), 1, 1);
        if (curInlet == (i + this.class.inletOffset)) {
          Pen.fillColor = Color.white;
          Pen.strokeColor = Color.black;
          Pen.width = 1;
          Pen.fillStroke;
        } {
          Pen.fillColor = Color.black;
          Pen.fill;
        };
      };

      // outlet
      Pen.addRoundedRect(Rect(13, v.bounds.height - 5, 6, 4), 1, 1);
      if (model.rate == \kr) {
        Pen.fillColor = Color.black;
        Pen.fill;
      } {
        Pen.fillColor = Color.white;
        Pen.strokeColor = Color.gray;
        Pen.width = 1.5;
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
        ^(i + this.class.inletOffset);
      };
    };
    ^nil;
  }

  getInletPoint { |num = 0|
    ^Point(view.bounds.left + 16 + this.prOffsetX(num), view.bounds.top + 3);
  }

  getOutletPoint { |num = 0|
    ^Point(view.bounds.left + 16 + this.prOffsetX(num), view.bounds.top + view.bounds.height - 3);
  }
}
