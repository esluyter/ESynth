/////////////////      LFOs       ////////////////

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

ESynthDef.lfo(\Sqr,
  [\random, \gate],
  \delay, [\kr, [0, 10, 4], 0.03],
  \freq, [\ar, [[0.01, 200, 6, 0, 2], [0.1, 10000, 6, 0, 100]], [0.5, 8]],
  \key, \kr,
  \phase, [\ar, [0, 1]],
  \duty, [\kr, [0, 1, 'linear', 0.0, 0.5]],
  {
    var env = Integrator.kr(ControlDur.ir / ~delay, 1 - (Changed.kr(~gate) * ~gate)).clip(0, 1);
    var trig = ~gate * ~type;
    var phase = Select.kr(~type, [Rand(0, 1), DC.kr(0)]) + ~phase;
    var phasor = (Phasor.kr(trig, ~freq * (((~note - 48) * ~key).midiratio) * 2pi / ControlRate.ir, 0, 2pi) + (phase * 2pi)).wrap(0, 2pi);
    ((phasor > (~duty * 2pi)) * 2 - 1) * env;
    //LFPulse.kr(~freq, 0, ~duty) * env;
  }, {
    var env = Integrator.kr(ControlDur.ir / ~delay, 1 - (Changed.kr(~gate) * ~gate)).clip(0, 1);
    LFPulse.ar(~freq * (((~note - 48) * ~key).midiratio), 0, ~duty) * env;
  }
);

ESynthDef.lfo(\Saw,
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
    (phasor / pi - 1) * env;
    //LFPulse.kr(~freq, 0, ~duty) * env;
  }, {
    var env = Integrator.kr(ControlDur.ir / ~delay, 1 - (Changed.kr(~gate) * ~gate)).clip(0, 1);
    Saw.ar(~freq * (((~note - 48) * ~key).midiratio)) * env;
  }
);

ESynthDef.lfo(\Tri,
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
    ((phasor * 2 / pi - 2).abs - 1) * env;
    //LFPulse.kr(~freq, 0, ~duty) * env;
  }, {
    var env = Integrator.kr(ControlDur.ir / ~delay, 1 - (Changed.kr(~gate) * ~gate)).clip(0, 1);
    VarSaw.ar(~freq * (((~note - 48) * ~key).midiratio)) * env;
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

ESynthDef.lfo(\Mod,
  [\positive, \bipolar],
  {
    Lag.kr(In.kr(\modbus.ir) * 2 - ~type, 0.05);
  }
);

ESynthDef.lfo(\Vel,
  [\positive, \bipolar],
  {
    Lag.kr(In.kr(\velbus.ir) * 2 - ~type, 0.05);
  }
);

ESynthDef.lfo(\Key,
  [\positive, \bipolar],
  {
    var sig = ~note.linlin(0, 127, 0.0, 2.0);
    Lag.kr(sig - ~type, 0.05);
  }
);

ESynthDef.lfo(\Mult,
  \in1, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  \in2, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  {
    ~in1 * ~in2
  },
  {
    ~in1 * ~in2
  }
);

ESynthDef.lfo(\Add,
  \in1, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  \in2, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  {
    ~in1 + ~in2
  },
  {
    ~in1 + ~in2
  }
);

ESynthDef.lfo(\Sub,
  \in1, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  \in2, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  {
    ~in1 - ~in2
  },
  {
    ~in1 - ~in2
  }
);




/////////////////      OSCs       ////////////////

ESynthDef.osc(\VCO,
  \tune, [\ar, [-48, 48, \lin, 0.0, 0], 1, 12, true],
  \fine, [\ar, [-2, 2, \lin, 0.0, 0], 0.01, 10, true],
  \duty, [\kr, [0, 1, \lin, 0.0, 0.5], 0.01, 10, true],
  \slop, [\kr, [0.001, 1, \exp, 0.0, 0.01]],
  \sin, \kr,
  \tri, \kr,
  \saw, \kr,
  \sqr, \kr,
  \key, [\kr, [-1, 1, \lin, 0, 1], 0.01, 10, true],
  {
    ~note = ((~note - 48) * ~key) + 48;
    ~freq = (~note + ~tune + ~fine).midicps;
    EVCO.ar(~freq, ~duty, ~slop, ~saw, ~sqr, ~sin, ~tri, ~chain);
  }
);

/*ESynthDef.osc(\SuperSaw,
  \tune, [\ar, [-48, 48, \lin, 0.0, 0], 1, 12, true],
  \fine, [\ar, [-2, 2, \lin, 0.0, 0], 0.01, 10, true],
  \duty, [\kr, [0, 1, \lin, 0.0, 0.5], 0.01, 10, true],
  \detune, [\kr, [0.1, 10, \exp, 0.0, 1.0]],
  {
    ~freq = (~note + ~tune + ~fine).midicps;
    ESuperSaw.ar(~freq, ~duty, ~detune);
  }
);*/
ESynthDef.osc(\SuperSaw,
  \tune, [\ar, [-48, 48, \lin, 0.0, 0], 1, 12, true],
  \fine, [\ar, [-2, 2, \lin, 0.0, 0], 0.01, 10, true],
  \n, [\kr, [0, 12, \lin, 0.0, 6], 1, 10, true],
  \detune, [\kr, [0.01, 10, \exp, 0.0, 0.2], 0.1],
  \vari, [\kr, [0.01, 10, \exp, 0.0, 0.01], 0.1],
  \varHz, [\kr, [0.01, 10, \exp, 0.0, 0.2], 0.1],
  \side, [\kr,  \amp.asSpec.copy.default_(0.5)],
  \sideAtt, [\kr, [-32, 32, \lin, 0.0, 0.0], 0.1, 1, true],
  {
    var maxsize = 25;
    var midpoint = (maxsize + 1) / 2;
    var note = ~note + ~tune + ~fine;
    var primes = [-1.91, -1.51, -1.39, -1.27, -1.09, -0.83, -0.67, -0.47, -0.37, -0.29, -0.17, -0.07, 0.0, 0.05, 0.13, 0.23, 0.31, 0.43, 0.61, 0.79, 1.07, 1.13, 1.37, 1.49, 1.81];

    var n = ~n;
    var detune = (~detune * BinaryOpUGen('!=', n, 0)) * pow(2, (midpoint - n).dbamp) * 0.5;
    var variation = ~vari;
    var varSpeed = ~varHz;
    var sideAmt = ~side;
    var sideSlope = ~sideAtt / (n + 1);

    var nOffset = (maxsize - 1) * 0.5 - n;
    var amps = maxsize.collect { |i| (i >= nOffset) * (i < (maxsize - nOffset)) * ((BinaryOpUGen('==', i, midpoint) * 0.7) + (BinaryOpUGen('!=', i, midpoint) * sideAmt * ((i - midpoint).abs * sideSlope).dbamp)) };
    var freqs = maxsize.collect({ |i| (note + (primes[i] * detune) + (BinaryOpUGen('!=', i, midpoint) * LFDNoise3.kr(varSpeed) * variation)).midicps });
    var saws = Saw.ar(freqs);
    Normalizer.ar((saws * amps).sum, 0.1, 0.01)
  }
);

ESynthDef.osc(\SimplexOsc,
  \tune, [\ar, [-48, 48, \lin, 0.0, 0], 1, 12, true],
  \fine, [\ar, [-2, 2, \lin, 0.0, 0], 0.01, 10, true],
  \freqscale, [\ar, [0, 10, 4, 0.0, 1], 0.1, 1],
  \oct, [\ar, [1, 5], 1],
  \offsetX, [\ar, [-48, 48, \lin, 0.0, 0], 0.01, 12, true],
  \offsetY, [\ar, [-48, 48, \lin, 0.0, 0], 0.01, 12, true],
  \amp, \kr,
  {
    ~freq = (~note + ~tune + ~fine).midicps;
    SimplexSynth.ar(~freq, ~offsetX, ~offsetY, ~freqscale, ~oct) * ~amp;
  }
);

ESynthDef.osc(\SimplexNoise,
  \freq, [\ar, [0.001, 1, \exp, 0.0, 0.1]],
  \radius, [\ar, [1, 10000, \exp, 0.0, 1000], 10],
  \oct, [\ar, [1, 5], 1],
  \amp, \kr,
  \offsetX, [\ar, [-48, 48, \lin, 0.0, 0], 0.01, 12, true],
  \offsetY, [\ar, [-48, 48, \lin, 0.0, 0], 0.01, 12, true],
  {
    SimplexSynth.ar(~freq, ~offsetX, ~offsetY, ~radius, ~oct) * ~amp;
  }
);

ESynthDef.osc(\PrayerBell,
  \tune, [\ar, [-48, 48, \lin, 0.0, 0], 1, 12, true],
  \fine, [\ar, [-2, 2, \lin, 0.0, 0], 0.01, 10, true],
  \decay, [\kr, [0.01, 2, \exp, 0.0, 1], 0.1],
  \harmonics, \kr,
  \mallet, [\kr, \amp.asSpec.copy.default_(0.1)],
  \sing, \kr,
  {
    var t_trig = ~gate, decayscale = ~decay;
    var sig, input, first, freqscale, mallet, sing;
    ~freq = (~note + ~tune + ~fine).midicps;
    freqscale = ~freq / 2434;

    mallet = LPF.ar(Trig.ar(t_trig, SampleDur.ir), 10000 * freqscale) * ~mallet;
    sing = LPF.ar(
      LPF.ar(
        {
          PinkNoise.ar * Integrator.kr(~gate * 0.001, 0.999).linexp(0, 1, 0.01, 1) * ~sing
        },
        2434 * freqscale
      ) + Dust.ar(0.1), 10000 * freqscale
    ) * LFNoise1.kr(0.5).range(-45, -30).dbamp;
    input = mallet + sing;


    sig = DynKlank.ar(`[
      [
        (first = LFNoise1.kr(0.5).range(2424, 2444)) + Line.kr(20, 0, 0.5),
        first + LFNoise1.kr(0.5).range(1,3),
        LFNoise1.kr(1.5).range(5435, 5440) - Line.kr(35, 0, 1),
        LFNoise1.kr(1.5).range(5480, 5485) - Line.kr(10, 0, 0.5),
        LFNoise1.kr(2).range(8435, 8445) + Line.kr(15, 0, 0.05),
        LFNoise1.kr(2).range(8665, 8670),
        LFNoise1.kr(2).range(8704, 8709),
        LFNoise1.kr(2).range(8807, 8817),
        LFNoise1.kr(2).range(9570, 9607),
        LFNoise1.kr(2).range(10567, 10572) - Line.kr(20, 0, 0.05),
        LFNoise1.kr(2).range(10627, 10636) + Line.kr(35, 0, 0.05),
        LFNoise1.kr(2).range(14689, 14697) - Line.kr(10, 0, 0.05)
      ],
      [
        LFNoise1.kr(1).range(-10, -5).dbamp,
        LFNoise1.kr(1).range(-20, -10).dbamp,
        LFNoise1.kr(1).range(-12, -6).dbamp,
        LFNoise1.kr(1).range(-12, -6).dbamp,
        -20.dbamp,
        -20.dbamp,
        -20.dbamp,
        -25.dbamp,
        -10.dbamp,
        -20.dbamp,
        -20.dbamp,
        -25.dbamp
      ],
      [
        20 * freqscale.pow(0.2),
        20 * freqscale.pow(0.2),
        5,
        5,
        0.6,
        0.5,
        0.3,
        0.25,
        0.4,
        0.5,
        0.4,
        0.6
      ] * freqscale.reciprocal.pow(0.5)
    ], input, freqscale, 0, decayscale);
    sig;
  }
);

ESynthDef.osc('Operator 4',
  ['ALGO 1', 'ALGO 2', 'ALGO 3', 'ALGO 4', 'ALGO 5', 'ALGO 6', 'ALGO 7', 'ALGO 8'],
  'ratio 1', [\kr, [1, 20, \exp, 0.0, 0], 0.1],
  'amp 1', [\kr, [0, 20, 6, 0.0, 1], 0.1],
  'ratio 2', [\kr, [1, 20, \exp, 0.0, 0], 0.1],
  'amp 2', [\kr, [0, 20, 6, 0.0, 0], 0.1],
  'ratio 3', [\kr, [1, 20, \exp, 0.0, 0], 0.1],
  'amp 3', [\kr, [0, 20, 6, 0.0, 0], 0.1],
  'ratio 4', [\kr, [1, 20, \exp, 0.0, 0], 0.1],
  'amp 4', [\kr, [0, 20, 6, 0.0, 0], 0.1],
  \tune, [\ar, [-48, 48, \lin, 0.0, 0], 1, 12, true],
  \fine, [\ar, [-2, 2, \lin, 0.0, 0], 0.01, 10, true],
  'fdback', [\kr, [0, 4], 0.05],
  {
    var feedback = ~fdback;
    var chans = [[0], [0], [0], [0], [0, 2], [0, 1, 2], [0, 1, 2], [0, 1, 2, 3]][~type];
    var algo = [
      FM7.modMatrix(
        [0, 1, 1],
        [1, 2, 1],
        [2, 3, 1],
        [3, 3, feedback]
      ),
      FM7.modMatrix(
        [0, 1, 1],
        [1, 2, 1],
        [1, 3, 1],
        [3, 3, feedback]
      ),
      FM7.modMatrix(
        [0, 1, 1],
        [1, 2, 1],
        [0, 3, 1],
        [3, 3, feedback]
      ),
      FM7.modMatrix(
        [0, 1, 1],
        [0, 2, 1],
        [2, 3, 1],
        [3, 3, feedback]
      ),
      FM7.modMatrix(
        [0, 1, 1],
        [2, 3, 1],
        [3, 3, feedback]
      ),
      FM7.modMatrix(
        [0, 3, 1],
        [1, 3, 1],
        [2, 3, 1],
        [3, 3, feedback]
      ),
      FM7.modMatrix(
        [2, 3, 1],
        [3, 3, feedback]
      ),
      FM7.modMatrix(
        [3, 3, feedback]
      ),
    ][~type];
    ~freq = (~note + ~tune + ~fine).midicps;
    FM7.ar([
        [~freq * ~ratio_1, 0, ~amp_1],
        [~freq * ~ratio_2, 0, ~amp_2],
        [~freq * ~ratio_3, 0, ~amp_3],
        [~freq * ~ratio_4, 0, ~amp_4],
        [0, 0, 0],
        [0, 0, 0],
      ], algo).slice(chans).asArray.sum * 0.1
  }
);

ESynthDef.osc(\Noise,
  \white, \kr,
  \pink, \kr,
  {
    WhiteNoise.ar(~white) + PinkNoise.ar(~pink)
  }
);




/////////////////      FILTs       ////////////////

ESynthDef.filt(\Houvilainen,
  ['LP 24db', 'LP 18db', 'LP 12db', 'LP 6db', 'HP 24db', 'BP 24db', 'N 24db', 'bypass'],
  \cutoff, [\ar, \freq.asSpec.copy.default_(20000), 25],
  \res, \kr,
  {
    ~cutoff = ~cutoff * ((~env + ~vel) * 100 + (~key * 1.05)).midiratio;
    HouvilainenFilter.ar(~in, ~cutoff, ~res, (~type + 1) % 8);
  }
);

ESynthDef.filt(\Decimator,
  \rate, [\ar, [1000, 48000], 25],
  \bits, [\kr, [1, 16]],
  {
    //~cutoff = ~cutoff * ((~env + ~vel) * 100 + (~key * 1.05)).midiratio;
    Decimator.ar(~in * 10, ~rate, ~bits) * 0.1;
  }
);




/////////////////      AMPs       ////////////////

ESynthDef.amp(\VCA,
  \pan, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  \gain, \kr,
  {
    (Pan2.ar(~inmono, ~pan) + Balance2.ar(~instereo[0], ~instereo[1], ~pan)) * (~env * ~key.dbamp + ~gain);
  }
);