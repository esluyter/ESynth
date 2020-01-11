ESParam {
  // rate = \control or \audio
  var <name, <rate, spec, step, <shift_scale, <centered, altSpec, altStep;

  *new { |name, rate = \control, spec = \amp, step = 0.005, shift_scale = 10, centered = false|
    var altSpec = nil;
    var altStep = nil;
    try {
      if (spec[0].isArray) {
        altSpec = spec[1].asSpec;
        spec = spec[0];
      }
    };
    try {
      if (step.isArray) {
        altStep = step[1];
        step = step[0];
      };
    };
    ^super.newCopyArgs(name, rate, spec.asSpec, step, shift_scale, centered, altSpec, altStep);
  }

  step { |rate|
    if ((rate == \audio) and: (altStep.notNil)) { ^altStep };
    ^step;
  }

  spec { |rate|
    if ((rate == \audio) and: (altSpec.notNil)) { ^altSpec };
    ^spec;
  }

  modName {
    ^(name ++ '_mod').asSymbol
  }
}
