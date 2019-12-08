ESynth {
  var <globals, <voices, <model, <numVoices;
  var <server, <group, <treeFunc;
  var <roundRobinIndex = 0;

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

  init { |argserver, argmodel, nVoices = 1|
    server = argserver ?? Server.default;
    model = argmodel;
    numVoices = nVoices;

    treeFunc = {
      if (voices.notNil) {
        voices.do(_.free);
      };
      group.free;

      roundRobinIndex = 0;
      group = Group(server);
      //globals = ESGlobals();
      voices = { ESVoice(group) } ! numVoices;
      // parse model..... :/
    };
    treeFunc.();
    ServerTree.add(treeFunc, server);
  }

  free {
    voices.do(_.free);
    group.free;
    ServerTree.remove(treeFunc, server);
  }

  addMod { |lfoIndex, toUnitFunc, param, amt|
    voices.do({ |v|
      var fromUnit = v.lfos[lfoIndex];
      var toUnit = toUnitFunc.(v);
      v.modulate(fromUnit, toUnit, param, amt);
    });
  }

  freeMod { |toUnitFunc, param|
    voices.do({ |v|
      var toUnit = toUnitFunc.(v);
      toUnit.putMod(param, nil);
    });
  }

  setModAmt { |toUnitFunc, param, amt|
    voices.do({ |v|
      var toUnit = toUnitFunc.(v);
      toUnit.modAt(param).set(\amt, amt);
    });
  }

  putLFO { |index, name, rate = 'control', args = (#[])|
    voices.do(_.putLFO(index, name, rate, args));
  }

  setLFO { |index ...args|
    voices.do(_.setLFO(index, *args));
  }

  putOsc { |index, name, args = (#[])|
    voices.do(_.putOsc(index, name, args));
  }

  setOsc { |index ...args|
    voices.do(_.setOsc(index, *args));
  }

  putFilt { |index, name, args = (#[])|
    voices.do(_.putFilt(index, name, args));
  }

  setFilt { |index ...args|
    voices.do(_.setFilt(index, *args));
  }

  setAmp { |...args|
    voices.do(_.setAmp(*args));
  }

  numVoices_ { |value|
    numVoices = value;
    treeFunc.();
  }

  noteOn { |vel, num|
    voices[roundRobinIndex].noteOn(vel, num);
    roundRobinIndex = (roundRobinIndex + 1) % numVoices
  }

  noteOff { |num|
    voices.do(_.noteOff(num));
  }

  bend_ { |value|
    voices.do(_.bend_(value));
  }

  mod_ { |value|
    voices.do(_.mod_(value));
  }
}
