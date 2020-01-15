ESynth {
  var <globals, <voices, <model, <numVoices;
  var <server, <group, <treeFunc;
  var <roundRobinIndex = 0;

  *initClass {
    Class.initClassTree(ESynthDef);

    ESynthDef.lfo(\Sin,
      [\random, \gate],
      \delay, [\kr, [0, 10, 4], 0.03],
      \freq, [\ar, [[0.01, 200, 6, 0, 2], [0.1, 10000, 6, 0, 100]], [0.5, 8]],
      \key, \kr,
      \phase, [\ar, [0, 1]],
      {
        var env = Integrator.kr(ControlDur.ir / ~delay, 1 - (Changed.kr(~gate) * ~gate)).clip(0, 1);
        var trig = ~gate * ~type;
        var phase = Select.kr(~type, [Rand(0, 1), DC.kr(0)]) + ~phase;
        var phasor = (Phasor.kr(trig, ~freq * (((~note - 48) * ~key).midiratio) * 2pi / ControlRate.ir, 0, 2pi) + (phase * 2pi)).wrap(0, 2pi);
        SinOsc.kr(0, phasor) * env;
      }, {
        var env = Integrator.kr(ControlDur.ir / ~delay, 1 - (Changed.kr(~gate) * ~gate)).clip(0, 1);
        var trig = ~gate * ~type;
        var phase = Select.kr(~type, [Rand(0, 1), DC.kr(0)]) + ~phase;
        var phasor = (Phasor.ar(trig, ~freq * (((~note - 48) * ~key).midiratio) * 2pi / SampleRate.ir, 0, 2pi) + (phase * 2pi)).wrap(0, 2pi);
        SinOsc.ar(0, phasor) * env;
      }
    );

    ESynthDef.lfo(\Noise,
      [\cubic, \linear, \none],
      \delay, [\kr, [0, 10, 4], 0.03],
      \freq, [\ar, [[0.01, 200, 6, 0, 2], [0.1, 10000, 6, 0, 100]], [0.5, 8]],
      {
        var env = Integrator.kr(ControlDur.ir / ~delay, 1 - (Changed.kr(~gate) * ~gate)).clip(0, 1);
        Select.kr(~type, [LFDNoise3.kr(~freq), LFDNoise0.kr(~freq), LFDNoise1.kr(~freq)]) * env;
      }, {
        var env = Integrator.kr(ControlDur.ir / ~delay, 1 - (Changed.kr(~gate) * ~gate)).clip(0, 1);
        Select.ar(~type, [LFDNoise3.ar(~freq), LFDNoise0.ar(~freq), LFDNoise1.ar(~freq)]) * env;
      }
    );

    // TODO: make this work
    ESynthDef.lfo(\Env,
      [\sustain, \oneshot, \retrig],
      \del, [\kr, [0, 10, 4], 0.03],
      \atk, [\kr, [0.001, 20, 8], 0.1],
      \dec, [\kr, [0.001, 20, 8, 0.0, 0.5], 0.1],
      \sus, [\kr,  \amp.asSpec.copy.default_(1)],
      \rel, [\kr, [0.001, 20, 8], 0.1],
      {
        var loopNode = Select.kr(~type, [-99, -99, 0]);
        var relNode = Select.kr(~type, [2, -99, 2]);
        var gateDel = Env([0, 0, 0, 1, 0], [0, ~del, 0, 0], \lin, 3).kr(0, ~gate);
        Env([0, 1, ~sus, 0], [~atk, ~dec, ~rel], -4, relNode, loopNode).kr(0, gateDel);
      },
      {
        var loopNode = Select.kr(~type, [-99, -99, 0]);
        var relNode = Select.kr(~type, [2, -99, 2]);
        var gateDel = Env([0, 0, 0, 1, 0], [0, ~del, 0, 0], \lin, 3).kr(0, ~gate);
        Env([0, 1, ~sus, 0], [~atk, ~dec, ~rel], -4, relNode, loopNode).ar(0, gateDel);
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
      ['LP 24db', 'LP 18db', 'LP 12db', 'LP 6db', 'HP 24db', 'BP 24db', 'N 24db', 'bypass'],
      \cutoff, [\ar, \freq.asSpec.copy.default_(20000), 25],
      \res, \kr,
      {
        ~cutoff = ~cutoff * ((~env + ~vel) * 100 + (~key * 1.05)).midiratio;
        HouvilainenFilter.ar(~in, ~cutoff, ~res, (~type + 1) % 8);
      }
    );

    ESynthDef.amp(\VCA,
      \pan, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
      {
        (Pan2.ar(~inmono, ~pan) + Balance2.ar(~instereo[0], ~instereo[1], ~pan)) * ~env * ~key.dbamp;
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
      voices = { ESVoice(group) } ! numVoices;
      globals = ESGlobals(group);
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
    var fromUnit, toUnit;
    var globalLFO = globals.lfos[lfoIndex].notNil;
    var globalToUnit = true;
    if (globalLFO.not and: (voices[0].lfos[lfoIndex].isNil)) {
      ^false;
    };
    try {
      globalToUnit = toUnitFunc.(voices[0]).isNil;
    };
    [globalLFO, globalToUnit].postln;
    if (globalLFO and: globalToUnit) {
      fromUnit = globals.lfos[lfoIndex];
      toUnit = toUnitFunc.(globals);
      globals.modulate(fromUnit, toUnit, param, amt);
    };
    if (globalLFO and: globalToUnit.not) {
      fromUnit = globals.lfos[lfoIndex];
      voices.do({ |v|
        toUnit = toUnitFunc.(v);
        v.modulate(fromUnit, toUnit, param, amt);
      });
    };
    if (globalLFO.not and: globalToUnit) {
      fromUnit = voices[0].lfos[lfoIndex];
      toUnit = toUnitFunc.(globals);
      globals.modulate(fromUnit, toUnit, param, amt);
    };
    if (globalLFO.not and: globalToUnit.not) {
      voices.do({ |v|
        fromUnit = v.lfos[lfoIndex];
        toUnit = toUnitFunc.(v);
        v.modulate(fromUnit, toUnit, param, amt);
      });
    }
  }

  freeMod { |toUnitFunc, param|
    var globalToUnit = true;
    try {
      globalToUnit = toUnitFunc.(voices[0]).isNil;
    };
    if (globalToUnit) {
      var toUnit = toUnitFunc.(globals);
      toUnit.putMod(param, nil);
    } {
      voices.do({ |v|
        var toUnit = toUnitFunc.(v);
        toUnit.putMod(param, nil);
      });
    };
  }

  setModAmt { |toUnitFunc, param, amt|
    var globalToUnit = true;
    try {
      globalToUnit = toUnitFunc.(voices[0]).isNil;
    };
    if (globalToUnit) {
      var toUnit = toUnitFunc.(globals);
      toUnit.modAt(param).set(\amt, amt);
    } {
      voices.do({ |v|
        var toUnit = toUnitFunc.(v);
        toUnit.modAt(param).set(\amt, amt);
      });
    };
  }

  putLFO { |index, name, rate = 'control', args = (#[]), global = false|
    if (global) {
      globals.putLFO(index, name, rate, args);
      voices.do(_.putLFO(index, nil));
    } {
      globals.putLFO(index, nil);
      voices.do(_.putLFO(index, name, rate, args));
    };
  }

  setLFO { |index ...args|
    if (globals.lfos[index].notNil) {
      globals.setLFO(index, *args);
    } {
      voices.do(_.setLFO(index, *args));
    };
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

  putAmp { |...args|
    voices.do(_.putAmp(*args));
  }

  setAmp { |...args|
    voices.do(_.setAmp(*args));
  }

  numVoices_ { |value|
    numVoices = value;
    treeFunc.();
  }

  noteOn { |note = 60, vel = 100|
    var i = 0;
    while { voices[roundRobinIndex].inUse && (i < numVoices) } {
      roundRobinIndex = (roundRobinIndex + 1) % numVoices;
      i = i + 1;
    };
    voices[roundRobinIndex].noteOn(note, vel);
    globals.noteOn(note, vel);
    roundRobinIndex = (roundRobinIndex + 1) % numVoices;
  }

  noteOff { |num|
    globals.noteOff(num);
    voices.do(_.noteOff(num));
  }

  bend_ { |value|
    globals.bend_(value);
    voices.do(_.bend_(value));
  }

  mod_ { |value|
    globals.mod_(value);
    voices.do(_.mod_(value));
  }

  portamento_ { |value|
    globals.portamento_(value);
    voices.do(_.portamento_(value));
  }
}
