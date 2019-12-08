ESynth {
  var <globals, <voices, <model;
  var <server, <group, <treeFunc;

  *initClass {
    Class.initClassTree(ESynthDef);

    ESynthDef.lfo(\Sin,
      \delay, [\kr, [0, 10, 4], 0.03],
      \freq, [\ar, [0.01, 200, 6, 0, 2], 0.5],
      \key, \kr,
      \phase, [\ar, [0, 1]],
      {
        SinOsc.kr(~freq, ~phase) * XLine.kr(0.01, 1, ~delay)
      }, {
        SinOsc.ar(~freq, ~phase) * XLine.kr(0.01, 1, ~delay)
      }
    );

    ESynthDef.lfo(\Noise,
      \delay, [\kr, [0, 10, 4], 0.03],
      \freq, [\ar, [0.01, 200, 6, 0, 2], 0.5],
      \interp, [\kr, [0, 3], 1],
      {
        Select.kr(~interp, [LFDNoise0.kr(~freq), LFDNoise1.kr(~freq), LFNoise2.kr(~freq), LFDNoise3.kr(~freq)]) * XLine.kr(0.01, 1, ~delay);
      }, {
        Select.ar(~interp, [LFDNoise0.ar(~freq), LFDNoise1.ar(~freq), LFNoise2.ar(~freq), LFDNoise3.ar(~freq)]) * XLine.kr(0.01, 1, ~delay);
      }
    );

    ESynthDef.osc(\VCO,
      \tune, [\ar, [-48, 48, \lin, 0.0, 0], 1, 12, true],
      \fine, [\ar, [-2, 2, \lin, 0.0, 0], 0.01, 10, true],
      \duty, [\kr, [0, 1, \lin, 0.0, 0.5], 0.01, 10, true],
      \slop, [\kr, [0.001, 1, \exp, 0.0, 0.01]],
      \sin, \kr,
      \tri, \kr,
      \saw, \kr,
      \sqr, \kr,
      {
        ~freq = (~note + ~tune + ~fine).midicps;
        EVCO.ar(~freq, ~duty, ~slop, ~saw, ~sqr, ~sin, ~tri);
      }
    );

    ESynthDef.osc(\Noise,
      \white, \kr,
      \pink, \kr,
      {
        WhiteNoise.ar(~white) + PinkNoise.ar(~pink)
      }
    );

    ESynthDef.filt(\Houvilainen,
      ['bypass', 'LP 24db', 'LP 18db', 'LP 12db', 'LP 6db', 'HP 24db', 'BP 24db', 'N 24db'],
      \cutoff, [\ar, \freq.asSpec.copy.default_(20000), 25],
      \res, \kr,
      \mod, \kr,
      {
        ~cutoff = ~cutoff * (~env * 100).midiratio;
        HouvilainenFilter.ar(~in, ~cutoff, ~res, ~type);
      }
    );

    ESynthDef.amp(\VCA,
      \pan, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
      {
        (Pan2.ar(~inmono, ~pan) + Balance2.ar(~instereo[0], ~instereo[1], ~pan)) * ~env;
      }
    );
  }

  *new { |server, model, numVoices = 1|
    ^super.new.init(server, model, numVoices);
  }

  init { |argserver, argmodel, numVoices = 1|
    server = argserver ?? Server.default;
    model = argmodel;

    globals = ESGlobals();
    voices = { ESVoice() } ! numVoices;

    treeFunc = {
      group = Group(server);
      globals.start(group, 20);
      voices.do(_.start(group, 20, 6, 4));
    };
    treeFunc.();
    ServerTree.add(treeFunc, server);
  }

  free {
    group.free;
    ServerTree.remove(treeFunc, server);
  }

  putLFO { |index, lfo, global = false|

  }

  patchLFO { |index, inlet, outLFO|

  }

  putOsc { |index, osc|
    voices.do(_.putOsc(index, osc));
  }

  patchOsc { |index, inlet, outLFO|

  }

  putFilt { |index, filt|

  }

  patchFilt { |index, inlet, outLFO|

  }

  patchAmp { |index, inlet, outLFO|

  }

  numVoices_ { |value|

  }

  noteOn { |vel, num|

  }

  noteOff { |num|

  }

  bend { |val|

  }

  mod { |val|

  }
}
