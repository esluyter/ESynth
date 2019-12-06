ESynth {
  var <globals, <voices, <model;
  var <server, <group, <treeFunc;

  *initClass {
    ServerBoot.add {
      SynthDef(\modulatekr, {
        var in = In.kr(\in.ir);
        var amt = \amt.kr * (\amtmod.kr + 1);
        var out = \out.ir;
        Out.kr(out, in * amt);
      }).add;

      SynthDef(\modulatear, {
        var in = InFeedback.ar(\in.ir);
        var amt = \amt.kr * (\amtmod.ar + 1);
        var out = \out.ir;
        Out.ar(out, in * amt);
      }).add;

      SynthDef(\lfosin, {
        var delay = \delay.kr;
        var freq = \freq.kr(2) * (\freqmod.kr * 72).midiratio;
        var phase = \phase.kr;
        var key = \key.kr;

        var out = \out.ir;
        var sig = SinOsc.kr(freq, phase) * XLine.kr(0.01, 1, delay);
        Out.kr(out, sig);
      }).add;

      SynthDef(\lfonoise, {
        var delay = \delay.kr;
        var freq = \freq.kr(2) * (\freqmod.kr * 72).midiratio;
        var interp = \interp.kr;

        var out = \out.ir;
        var sig = Select.kr(interp, [LFDNoise0.kr(freq), LFDNoise1.kr(freq), LFNoise2.kr(freq), LFDNoise3.kr(freq)]) * XLine.kr(0.01, 1, delay);
        Out.kr(out, sig);
      }).add;

      SynthDef(\lfoarsin, {
        var delay = \delay.kr;
        var freq = \freq.kr(2) * (\freqmod.ar * 72).midiratio;
        var phase = \phase.ar;
        var key = \key.kr;

        var out = \out.ir;
        var sig = SinOsc.ar(freq, phase) * XLine.kr(0.01, 1, delay);
        Out.ar(out, sig);
      }).add;

      SynthDef(\oscnoise, {
        var whiteamt = \white.kr;
        var pinkamt = \pink.kr;

        var out = \out.ir;
        var sig = WhiteNoise.ar(whiteamt) + PinkNoise.ar(pinkamt);
        Out.ar(out, sig);
      }).add;

      SynthDef(\filter, {
        var keyamt = \key.kr;
        var velamt = \vel.kr;
        var envamt = \env.kr;
        var cutoff = \cutoff.kr * (\cutoffmod.ar * 12).midiratio;
        var res = \res.kr;
        var modamt = \mod.kr;

        var type = \type.kr(1);
        var out = \out.ir;
        var in = In.ar(\in.ir);
        var sig = HouvilainenFilter.ar(in, cutoff, res, type);
        Out.ar(out, sig);
      }).add;

      SynthDef(\vca, {
        var keyamt = \key.kr;
        var velamt = \vel.kr;
        var envamt = \env.kr;
        var pan = \pan.kr;

        var out = \out.ir;
        var inmono = In.ar(\inmono.ir);
        var instereo = In.ar(\instereo.ir, 2);
        var sig = Pan2.ar(inmono, pan) + Balance2.ar(instereo[0], instereo[1], pan);
        Out.ar(out, sig);
      }).add;
    }
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
