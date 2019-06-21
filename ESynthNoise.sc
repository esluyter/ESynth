ESynthNoise {
  var <synth, <isRunning;
  var <pink = 0, <white = 0;
  var <params = #[\pink, \white];

  *prMakeSynthDef {
    SynthDef(\ESynthNoise, { |out|
      Out.ar(out, ENoise.ar(\pink.kr(0, 0.1), \white.kr(0, 0.1)));
    }).add;
  }

  guiClass { ^ESynthNoiseView }

  type { ^"Noise" }

  *new { |group, outbus, notebus|
    ^super.new.init(group, outbus, notebus);
  }

  init { |group, outbus, notebus|
    synth = Synth(\ESynthNoise, [out: outbus], group, \addToTail);
    isRunning = true;
  }

  free { synth.free; isRunning = false }

  pink_ { |value|
    pink = value;
    synth.set(\pink, value);
  }

  white_ { |value|
    white = value;
    synth.set(\white, value);
  }
}
