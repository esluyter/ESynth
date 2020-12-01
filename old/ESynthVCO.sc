ESynthVCO {
  var <synth, <isRunning;
  var <tune = 0, <fine = 0, <duty = 0.5, <slop = 0.01, <saw = 0, <sqr = 0, <sin = 0, <tri = 0;
  var <params = #[\tune, \fine, \duty, \slop, \saw, \sqr, \sin, \tri];

  *prMakeSynthDef {
    SynthDef(\ESynthVCO, { |out, notebus|
      var note = In.kr(notebus);
      var tune = \tune.kr(0, 0.1);
      var fine = \fine.kr(0, 0.1);
      var duty = \duty.kr(0.5, 0.1);
      var slop = \slop.kr(0.01, 0.1);
      var sawAmt = \saw.kr(0, 0.1);
      var sqrAmt = \sqr.kr(0, 0.1);
      var sinAmt = \sin.kr(0, 0.1);
      var triAmt = \tri.kr(0, 0.1);
      var freq = (note + tune + fine).midicps;
      var sig = EVCO.ar(freq, duty, slop, sawAmt, sqrAmt, sinAmt, triAmt);
      Out.ar(out, sig);
    }).add;
  }

  guiClass { ^ESynthVCOView }

  type { ^"VCO" }

  *new { |group, outbus, notebus|
    ^super.new.init(group, outbus, notebus);
  }

  init { |group, outbus, notebus|
    synth = Synth(\ESynthVCO, [out: outbus, notebus: notebus], group, \addToTail);
    isRunning = true;
  }

  free { synth.free; isRunning = false }

  tune_ { |value|
    tune = value;
    synth.set(\tune, value);
  }

  fine_ { |value|
    fine = value;
    synth.set(\fine, value);
  }

  duty_ { |value|
    duty = value;
    synth.set(\duty, value);
  }

  slop_ { |value|
    slop = value;
    synth.set(\slop, value);
  }

  saw_ { |value|
    saw = value;
    synth.set(\saw, value);
  }

  sqr_ { |value|
    sqr = value;
    synth.set(\sqr, value);
  }

  sin_ { |value|
    sin = value;
    synth.set(\sin, value);
  }

  tri_ { |value|
    tri = value;
    synth.set(\tri, value);
  }
}
