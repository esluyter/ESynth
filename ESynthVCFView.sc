ESynthVCFView : ESynthModule {
  var <vcf;
  var <menu, <type, <cutoff, <res, <key, <env, <vel, <mod, <at, <dt, <sl, <rt;

  *new { |parent, bounds, vcf|
    ^super.new(parent, bounds).init2(vcf);
  }

  init2 { |vcf|
    this.titleString_("VCF");
    menu = ESynthMenu(view, Rect(17, 3, 140, 15), "", ["RLPF", "SVF", "DFM1", "BLowPass", "BLowPass4", "Houvilainen"]);
    menu.action = { |view| view.item.postln };
    type = ESynthMenu(view, Rect(167, 3, 120, 15), "Type", ["Bypass", "LP 24db", "LP 18db", "LP 12db", "LP 6db", "HP 24db", "BP 24db", "N 24db"]).value_(vcf.type);
    type.action = { |view| vcf.type_(view.value) };

    cutoff = ESynthKnob(view, Rect(0, 32, 34, 67), "Cutoff", \freq.asSpec.copy.default_(20000), 15);
    res = ESynthKnob(view, Rect(40, 32, 34, 67), "Res", \amp, 0.005);
    key = ESynthKnob(view, Rect(100, 32, 34, 67), "Key", \amp, 0.005);
    env = ESynthKnob(view, Rect(140, 32, 34, 67), "Env", \amp, 0.005);
    vel = ESynthKnob(view, Rect(180, 32, 34, 67), "Vel", \amp, 0.005);
    mod = ESynthKnob(view, Rect(220, 32, 34, 67), "Mod", \amp, 0.005);

    at = ESynthKnob(view, Rect(0, 112, 34, 67), "A", [0.001, 20, 8], 0.1);
    dt = ESynthKnob(view, Rect(40, 112, 34, 67), "D", [0.001, 20, 8, 0.0, 0.5], 0.1);
    sl = ESynthKnob(view, Rect(80, 112, 34, 67), "S", \amp);
    rt = ESynthKnob(view, Rect(120, 112, 34, 67), "R", [0.001, 20, 8], 0.1);

    this.vcf_(vcf);
  }

  vcf_ { |value|
    vcf = value;
    vcf.params.reject(_ == \type).do { |param|
      var knob = this.perform(param);
      knob.value_(vcf.perform(param));
      knob.action_({ |value| vcf.perform((param ++ "_").asSymbol, value) });
    };
  }

  menuValue_ { |value|
    menu.value_(value);
  }
}
