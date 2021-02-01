BusView : SCViewHolder {
  var <hasOutlet = true, <name;
  var <curArInlet, <tooltip;
  var <model;

  *new { |parent, bounds, model|
    ^super.new.init(parent, bounds, model);
  }

  init { |parent, bounds, argmodel|
    model = argmodel;
    bounds = bounds ?? Rect(0, 0, parent.bounds.width, parent.bounds.height);
    view = UserView(parent, bounds);

    name = StaticText(view, Rect(5, 7, 10, 15)).stringColor_(Color.white).font_(Font.monospace.size_(15)).align_(\center);

    this.prMakeDrawFunc;
    this.prMouseSetup(parent);
    this.prDropSetup;
  }

  prMakeDrawFunc {
    view.drawFunc = { |v|
      Pen.addOval(Rect(1, 5, 18, 18));
      Pen.strokeColor_(Color.gray(0.7));
      Pen.fillColor = Color.gray(0.18, 0.9);
      Pen.width_(1);
      Pen.fillStroke;

      Pen.addRoundedRect(Rect(7, 1, 6, 4), 1, 1);
      if (curArInlet == 0) {
        Pen.strokeColor = Color.white;
        Pen.fillColor = Color.white;
      } {
        Pen.strokeColor = Color.gray(0.8);
        Pen.fillColor = Color(0.1, 0, 0.1);
      };
      Pen.width = 1;
      Pen.fillStroke;

      if (hasOutlet) {
        Pen.addRoundedRect(Rect(7, 23, 6, 4), 1, 1);
      };
      Pen.fillColor = Color(0.1, 0, 0.1);
      Pen.strokeColor = Color.gray(0.8);
      Pen.width = 1;
      Pen.fillStroke;
    };
    view.refresh;
  }

  prMouseSetup { |parent|
    tooltip = StaticText(parent, Rect(view.bounds.left - 10, view.bounds.top - 10, view.bounds.width + 20, 10))
      .visible_(false)
      .string_("audio in")
      .font_(Font.sansSerif.size_(8))
      .stringColor_(Color.white)
      .align_(\center);

    name.acceptsMouse = false;
    view.mouseOverAction = { |v, x, y|
      curArInlet = this.getArInletNum(x@y);
      if (curArInlet == 0) {
        tooltip.visible = true;
      } {
        tooltip.visible = false;
      };
      view.refresh;
    };

    view.mouseLeaveAction = {
      curArInlet = nil;
      tooltip.visible = false;
      view.refresh;
    };

    view.mouseDownAction = { |v, x, y, mod, buttnum, clickcount|
      if (buttnum == 0) {
        if (curArInlet.notNil) { this.beginDrag(x, y) };
      } {
        if (clickcount > 1) {
          if (curArInlet.notNil) {
            model.clearArInputs;
          }
        };
      };
    };

    view.beginDragAction = {
      //("Dragging from " ++ curInlet).postln;
      [model, nil, curArInlet]
    };
  }

  prDropSetup {
    view.canReceiveDragHandler = true;
    view.receiveDragHandler = { |v, x, y|
      if (y > (view.bounds.height - 10)) {
        var to, inlet, arInlet;
        # to, inlet, arInlet = View.currentDrag;
        if (inlet.isNil) {
          model.arPatchTo(to, arInlet);
        } {
          "WARNING: Can't patch a Bus to a LFO input".postln;
        };
      };
    };
  }

  getArInletNum { |point|
    var inlet = 10@3;
    if (((inlet.x - point.x).abs < 5) && ((inlet.y - point.y).abs < 4)) {
      ^0;
    };
    ^nil;
  }

  getArInletPoint { |num = 0|
    ^Point(view.bounds.left + 10, view.bounds.top + 3);
  }

  getOutletPoint { |num = 0|
    ^Point(view.bounds.left + 10, view.bounds.top + view.bounds.height - 3);
  }

  hasOutlet_ { |val|
    hasOutlet = val;
    view.refresh;
  }

  name_ { |val|
    name.string_(val);
  }
}
