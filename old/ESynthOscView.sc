ESynthOscView : ESynthModule {
  var <menu, <osc, <oscView;

  *new { |parent, bounds, index = 0, osc|
    ^super.new(parent, bounds).init2(index, osc);
  }

  init2 { |index, osc|
    this.titleString_("OSC " ++ index);
    menu = ESynthMenu(view, Rect(37, 3, 120, 15), "", ["VCO", "SuperSaw", "Noise"]);
    menu.action = { |view| view.item.postln };
    this.osc_(osc);
    ^this;
  }

  osc_ { |value|
    osc = value;
    this.oscClass_(osc.guiClass);
    osc.params.do { |param|
      var knob = oscView.perform(param);
      knob.value_(osc.perform(param));
      knob.action_({ |value| osc.perform((param ++ "_").asSymbol, value) });
    };
    menu.item_(osc.type);
  }

  oscClass_ { |oscViewClass|
    oscView.remove;
    oscView = oscViewClass.new(view, Rect(0, 32, this.bounds.width, 67));
  }

  /*
  menuValue_ { |value|
    menu.value_(value);
  }
  */
}
