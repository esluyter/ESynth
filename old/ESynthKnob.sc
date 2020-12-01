ESynthKnob : SCViewHolder {
  var <spec, <name, <step, <shift_scale;
  var <title, <knob, <numberBox;
  var <>action;

  *new { |parent, bounds, name = "Knob", spec = (#[0, 1]), step = 0.01, shift_scale = 10, centered = false|
    ^super.new.init(parent, bounds, name, spec, step, shift_scale, centered)
  }

  init { |parent, bounds, argname, argspec, argstep, argshift_scale, centered|
    spec = argspec.asSpec;
    name = argname;
    step = argstep;
    shift_scale = argshift_scale;

    view = UserView(parent, bounds);
    title = StaticText(view, Rect(0, 0, 34, 15))
      .string_(name)
      .align_(\center)
      .stringColor_(Color.white)
      .font_(Font(ESynthModule.font, 11, true));
    knob = Knob(view, Rect(2, 17, 30, 30))
      .color_([Color.white, Color.white, Color.clear, Color.black])
      .mode_(\vert)
      .centered_(centered)
      .step_(step / spec.range)
      .shift_scale_(shift_scale)
      .value_(spec.unmap(spec.default))
      .mouseDownAction_({ |v, x, y, mod, buttNum, clickCount|
        if (buttNum == 0 && (clickCount == 2)) {
          v.valueAction_(spec.unmap(spec.default))
        };
      })
      .action_({ |v|
        numberBox.value_(spec.map(v.value));
        action.(spec.map(v.value));
      });
    numberBox = NumberBox(view, Rect(0, 52, 34, 15))
      .background_(Color.grey(0.1))
      .stringColor_(Color.white)
      .normalColor_(Color.white)
      .typingColor_(Color.hsv(0, 0.5, 1))
      .font_(Font(ESynthModule.monofont, 10))
      .align_(\center)
      .step_(step)
      .scroll_step_(step)
      .shift_scale_(shift_scale)
      .clipLo_(spec.minval)
      .clipHi_(spec.maxval)
      .maxDecimals_(4)
      .value_(spec.default)
      .mouseDownAction_({ |v, x, y, mod, buttNum, clickCount|
        if (buttNum == 0 && (clickCount == 2)) {
          v.valueAction_(spec.default)
        };
      })
      .action_({ |v|
        knob.value_(spec.unmap(v.value));
        action.(v.value);
      });
  }

  value_ { |value|
    numberBox.value_(value);
    knob.value_(spec.unmap(value));
  }

  doesNotUnderstand { |selector ... args|
    knob.performList(selector, args);
  }

}
