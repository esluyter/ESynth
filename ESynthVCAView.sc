ESynthVCAView : ESynthModule {
  var <vca;
  var <amp, <pan, <env, <vel, <at, <dt, <sl, <rt;

  *new { |parent, bounds, vca|
    ^super.new(parent, bounds).init2(vca);
  }

  init2 { |vca|
    this.titleString_("VCA");

    at = ESynthKnob(view, Rect(0, 32, 34, 67), "A", [0.001, 20, 8], 0.1);
    dt = ESynthKnob(view, Rect(40, 32, 34, 67), "D", [0.001, 20, 8, 0.0, 0.5], 0.1);
    sl = ESynthKnob(view, Rect(80, 32, 34, 67), "S", \amp.asSpec.copy.default_(1));
    rt = ESynthKnob(view, Rect(120, 32, 34, 67), "R", [0.001, 20, 8], 0.1);

    env = ESynthKnob(view, Rect(180, 32, 34, 67), "Env", \amp.asSpec.copy.default_(1), 0.005);
    vel = ESynthKnob(view, Rect(220, 32, 34, 67), "Vel", \amp, 0.005);
    amp = ESynthKnob(view, Rect(260, 32, 34, 67), "Amp", \amp, 0.005);
    pan = ESynthKnob(view, Rect(300, 32, 34, 67), "Pan", [-1, 1, \lin, 0.0, 0], centered: true);

    this.vca_(vca);
  }

  vca_ { |value|
    vca = value;
    vca.params.do { |param|
      var knob = this.perform(param);
      knob.value_(vca.perform(param));
      knob.action_({ |value| vca.perform((param ++ "_").asSymbol, value) });
    };
  }
}
