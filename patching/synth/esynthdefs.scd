/////////////////      LFOs       ////////////////
/*TODO: LF SIN gated phasor only works after synth patch is reloaded?? */

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
    //phasor.poll;
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

ESynthDef.lfo(\EnvTr,
  [\sustain, \oneshot, \retrig],
  \in, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  \atk, [\kr, [0.001, 20, 8], 0.1],
  \dec, [\kr, [0.001, 20, 8, 0.0, 0.5], 0.1],
  \sus, [\kr,  \amp.asSpec.copy.default_(1)],
  \rel, [\kr, [0.001, 20, 8], 0.1],
  {
    var loopNode = Select.kr(~type, [-99, -99, 0]);
    var relNode = Select.kr(~type, [2, -99, 2]);
    Env([0, 1, ~sus, 0], [~atk, ~dec, ~rel], -4, relNode, loopNode).kr(0, ~in);
  },
  {
    var loopNode = Select.kr(~type, [-99, -99, 0]);
    var relNode = Select.kr(~type, [2, -99, 2]);
    Env([0, 1, ~sus, 0], [~atk, ~dec, ~rel], -4, relNode, loopNode).ar(0, ~in);
  }
);

ESynthDef.lfo(\Clock,
  [\Impulse, \Dust],
  \bpm, [\kr, [0, 999, 2, 0.0, 110], 1],
  \phase, [\kr, [0, 1, \lin]],
  {
    var freq = ~bpm / 60;
    Select.kr(~type, [Impulse.kr(freq, ~phase), Dust.kr(freq)]);
  },
  {
    var freq = ~bpm / 60;
    Select.ar(~type, [Impulse.ar(freq, ~phase), Dust.ar(freq)]);
  }
);

ESynthDef.lfo(\PDiv,
  \in, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  \div, [\kr, [1, 100, \exp, 0.0, 4]],
  {
    PulseDivider.kr(~in, ~div);
  },
  {
    PulseDivider.ar(~in, ~div);
  }
);

ESynthDef.lfo(\CoinGate,
  \in, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  \weight, [\ar, [0, 1.0, \lin, 0.0, 0.5]],
  {
    CoinGate.kr(~weight, ~in)
  },
  {
    CoinGate.ar(~weight, ~in)
  }
);

ESynthDef.lfo(\Mod,
  [\positive, \bipolar],
  \curve, [\kr, [-24, 24, \lin, 0.0, 0], 0.2, true],
  {
    var sig = In.kr(\modbus.ir).lincurve(0, 1, 0, 1, ~curve);
    Lag.kr(sig * 2 - ~type, 0.05);
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

ESynthDef.lfo(\Comp,
  \in1, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  \in2, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  {
    ~in1 > ~in2
  },
  {
    ~in1 > ~in2
  }
);

ESynthDef.lfo(\Changed,
  \in, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  \thresh, [\ar, [0, 1, \lin, 0.0, 0], 0.01, 10],
  {
    Changed.kr(~in, ~thresh);
  },
  {
    Changed.ar(~in, ~thresh);
  }
);

ESynthDef.lfo(\Trig,
  \in, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  \dur, [\kr, [[0.001, 20, 8], [0.00001, 20, 8]], [0.1, 10]],
  {
    Trig.kr(~in, ~dur);
  },
  {
    Trig.ar(~in, ~dur);
  }
);

ESynthDef.lfo(\Audio,
  \gain, [\ar, [-24, 24, \lin, 0.0, 0], 0.2, true],
  {
    A2K.kr(~chain) * ~gain.dbamp
  },
  {
    ~chain * ~gain.dbamp
  }
);

ESynthDef.lfo(\Slew,
  \in, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  \time, [\ar, [0.001, 10, \exp], 0.1],
  {
    Lag2.kr(~in, ~time)
  },
  {
    Lag2.ar(~in, ~time)
  }
);

ESynthDef.lfo(\Quant,
  \in, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  \sens, [\kr, [-12, 12, \lin, 0.0, 0], 0.1, true],
  \steps, [\kr, [1, 100, 5, 0.0, 12], 0.5],
  \knee, [\kr, [0, 1, \lin]],
  \lag, \kr,
  {
    var input = ~in;
    var sensitivity = ~sens.dbamp;
    var steps = ~steps;
    var knee = ~knee;
    var lag = ~lag;
    var adjustedInput, adjustedInputQuant, adjustedInputDiff, adjustedInputDiffKnee;

    steps = steps / 2;
    adjustedInput = (input * sensitivity * steps);
    adjustedInputQuant = adjustedInput.floor;

    knee = knee.lincurve(0, 1, 0, 1, 2).linexp(0, 1, 100, 1);
    adjustedInputDiff = adjustedInput - adjustedInputQuant - 0.5;
    adjustedInputDiffKnee = RemoveBadValues.kr((adjustedInputDiff * 2 * knee).sinh / knee.sinh, 0) * 0.5;

    input = (adjustedInputQuant + adjustedInputDiffKnee) / steps;

    input = Lag2.kr(input, lag);
  },
  {
    var input = ~in;
    var sensitivity = ~sens.dbamp;
    var steps = ~steps;
    var knee = ~knee;
    var lag = ~lag;
    var adjustedInput, adjustedInputQuant, adjustedInputDiff, adjustedInputDiffKnee;

    steps = steps / 2;
    adjustedInput = (input * sensitivity * steps);
    adjustedInputQuant = adjustedInput.floor;

    knee = knee.lincurve(0, 1, 0, 1, 2).linexp(0, 1, 100, 1);
    adjustedInputDiff = adjustedInput - adjustedInputQuant - 0.5;
    adjustedInputDiffKnee = RemoveBadValues.kr((adjustedInputDiff * 2 * knee).sinh / knee.sinh, 0) * 0.5;

    input = (adjustedInputQuant + adjustedInputDiffKnee) / steps;

    input = K2A.ar(input);
    input = Lag2.ar(input, lag);
  }
);


// REALLY NEED MULTICHAN OUTPUT FOR THIS>>>>
ESynthDef.lfo(\Chen,
  \speed, [\kr, [0, 1.0, \lin, 0.0, 0.5]],
  \aa, [\kr, [0, 1.0, \lin, 0.0, 0.5]],
  \bb, [\kr, [0, 1.0, \lin, 0.0, 0.3]],
  \cc, [\kr, [0, 1.0, \lin, 0.0, 0.28]],
  {
    Chen.kr(~speed, ~aa, ~bb, ~cc);
  },
  {
    Chen.ar(~speed, ~aa, ~bb, ~cc);
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
  \sqr, [\kr, \amp.asSpec.copy.default_(0.1)],
  \key, [\kr, [-1, 1, \lin, 0, 1], 0.01, 10, true],
  {
    ~note = ((~note - 48) * ~key) + 48;
    ~freq = (~note + ~tune + ~fine).midicps;
    EVCO.ar(~freq, ~duty, ~slop, ~saw, ~sqr, ~sin, ~tri, ~chain);
  }
);

ESynthDef.osc(\Noise,
  \white, \kr,
  \pink, [\kr, \amp.asSpec.copy.default_(0.2)],
  {
    WhiteNoise.ar(~white) + PinkNoise.ar(~pink)
  }
);

ESynthDef.osc(\SoundIn,
  \gain, [\ar, [-24, 24, \lin, 0.0, 0], 0.2, true],
  \input, [\kr, [0, 16, \lin, 1, 0]],
  {
    SoundIn.ar(~input) * ~gain.dbamp
  }
);

ESynthDef.osc(\NeoVarSaw,
  \tune, [\ar, [-48, 48, \lin, 0.0, 0], 1, 12, true],
  \fine, [\ar, [-2, 2, \lin, 0.0, 0], 0.01, 10, true],
  \duty, [\kr, [0, 1, \lin, 0.0, 0.5], 0.01, 10, true],
  \shape, [\kr, [0, 1, \lin, 0.0, 1.0]],
  \amp, [\kr, [0, 1, \amp, 0.0, 0.1]],
  \key, [\kr, [-1, 1, \lin, 0, 1], 0.01, 10, true],
  {
    ~note = ((~note - 48) * ~key) + 48;
    ~freq = (~note + ~tune + ~fine).midicps;
    NeoVarSawOsc.ar(~freq, ~duty, ~shape) * ~amp;
  }
);

ESynthDef.osc(\808,
  \trig, \ar,
  \tune, [\ar, [-48, 48, \lin, 0.0, 0], 1, 12, true],
  \infsus, \kr,
  \accent, \kr,
  \tone, \kr,
  \decay, \kr,
  \atfm, \kr,
  \selffm, \kr,
  \key, [\kr, [-1, 1, \lin, 0, 1], 0.01, 10, true],
  \amp, [\kr, [0, 1, \amp, 0.0, 0.1]],
  {
    ~note = ((~note - 48) * ~key) + 48;
    ~freq = (~note + ~tune).midicps;
    ~trig = ~trig + Trig.kr(~gate, 0.01);
    AnalogBassDrum.ar(~trig, ~infsus, ~accent, ~freq, ~tone, ~decay, ~atfm, ~selffm) * 8 * ~amp
  }
);

ESynthDef.osc('808snare',
  \trig, \ar,
  \tune, [\ar, [-48, 48, \lin, 0.0, 0], 1, 12, true],
  \infsus, \kr,
  \accent, \kr,
  \tone, \kr,
  \decay, \kr,
  \snappy, \kr,
  \key, [\kr, [-1, 1, \lin, 0, 1], 0.01, 10, true],
  \amp, [\kr, [0, 1, \amp, 0.0, 0.1]],
  {
    ~note = ((~note - 48) * ~key) + 48;
    ~freq = (~note + ~tune).midicps;
    ~trig = ~trig + Trig.kr(~gate, 0.01);
    AnalogSnareDrum.ar(~trig, ~infsus, ~accent, ~freq, ~tone, ~decay, ~snappy) * ~amp;
  }
);

ESynthDef.osc(\StringVoice,
  \trig, \ar,
  \tune, [\ar, [-48, 48, \lin, 0.0, 0], 1, 12, true],
  \infsus, \kr,
  \accent, \kr,
  \struct, \kr,
  \bright, \kr,
  \damp, \kr,
  \key, [\kr, [-1, 1, \lin, 0, 1], 0.01, 10, true],
  {
    ~note = ((~note - 48) * ~key) + 48;
    ~freq = (~note + ~tune).midicps;
    ~trig = ~trig + Trig.kr(~gate, 0.01);
    StringVoice.ar(~trig, ~infsus, ~freq, ~accent, ~struct, ~bright, ~damp);
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
  \amp, [\kr, [0, 1, \amp, 0.0, 0.1]],
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
    Normalizer.ar((saws * amps).sum, 0.1, 0.01) * ~amp * 10;
  }
);

ESynthDef.osc(\SimplexOsc,
  \tune, [\ar, [-48, 48, \lin, 0.0, 0], 1, 12, true],
  \fine, [\ar, [-2, 2, \lin, 0.0, 0], 0.01, 10, true],
  \freqscale, [\ar, [0, 10, 4, 0.0, 1], 0.1, 1],
  \oct, [\ar, [1, 5], 1],
  \offsetX, [\ar, [-48, 48, \lin, 0.0, 0], 0.01, 12, true],
  \offsetY, [\ar, [-48, 48, \lin, 0.0, 0], 0.01, 12, true],
  \amp, [\kr, \amp.asSpec.copy.default_(0.2)],
  {
    ~freq = (~note + ~tune + ~fine).midicps;
    SimplexSynth.ar(~freq, ~offsetX, ~offsetY, ~freqscale, ~oct) * ~amp;
  }
);

ESynthDef.osc(\SimplexNoise,
  \freq, [\ar, [0.001, 1, \exp, 0.0, 0.1]],
  \radius, [\ar, [1, 10000, \exp, 0.0, 1000], 10],
  \oct, [\ar, [1, 5], 1],
  \amp, [\kr, \amp.asSpec.copy.default_(0.2)],
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

ESynthDef.osc('Benjolis',
  ['RLPF', 'RHPF', 'SVF', 'DFM1'],
  \freq1, [\ar, [20, 14000, \exp, 0.0, 70], 25],
  \freq2, [\ar, [0.1, 14000, \exp, 0.0, 4], 25],
  \scale, [\kr, [0.0, 1.0, \lin, 0, 1]],
  \rung1, [\kr, [0.0, 1.0, \lin, 0, 0.16]],
  \rung2, [\kr, [0.0, 1.0]],
  \rFilt, [\kr, [0.0, 10.0, \lin, 0, 9], 0.05],
  \loop, [\kr, [0.0, 1.0, \lin, 0, 1]],
  \fFreq, [\ar, \freq.asSpec.copy.default_(40), 25],
  \q, [\kr, [0.0, 1.0, \lin, 0, 0.82]],
  \gain, [\kr, [0.0, 3.0, \lin, 0, 1]],
  \output, [\kr, [0.0, 6.0, \lin, 0, 6], 0.02],
  \amp, [\kr, \amp.asSpec.copy.default_(0.3)],
  {
    var freq1 = ~freq1 * (~note - 60).midiratio, freq2 = ~freq2, scale = ~scale,
        rungler1 = ~rung1, rungler2 = ~rung2,
        runglerFilt = ~rFilt, loop = ~loop, filtFreq = ~fFreq, q = ~q, gain = ~gain,
        filterType = ~type, amp = ~amp, outSignal = ~output;
    var osc1, osc2, tri1, tri2, sh0, sh1, sh2, sh3, sh4, sh5, sh6, sh7, sh8=1, rungler, pwm, filt, output;
  	var sr;
  	var osc2freq, buf, bufR;

  	bufR = LocalIn.ar(2,0);
  	rungler = bufR.at(0);
  	buf = bufR.at(1);

  	sr = SampleDur.ir;
  	//sr = ControlDur.ir;
  	tri1 = LFTri.ar((rungler*rungler1)+freq1);
  	tri2 = LFTri.ar((rungler*rungler2)+freq2);
  	osc1 = PulseDPW.ar((rungler*rungler1)+freq1);
  	osc2 = PulseDPW.ar((rungler*rungler2)+freq2);

  	//pwm = tri1 > tri2;
  	pwm = BinaryOpUGen('>', (tri1 + tri2),(0));

  	osc1 = ((buf*loop)+(osc1* (loop* -1 +1)));
  	sh0 = BinaryOpUGen('>', osc1, 0.5);
  	sh0 = BinaryOpUGen('==', (sh8 > sh0), (sh8 < sh0));
  	sh0 = (sh0 * -1) + 1;

  	sh1 = DelayN.ar(Latch.ar(sh0,osc2),0.01,sr);
  	sh2 = DelayN.ar(Latch.ar(sh1,osc2),0.01,sr*2);
  	sh3 = DelayN.ar(Latch.ar(sh2,osc2),0.01,sr*3);
  	sh4 = DelayN.ar(Latch.ar(sh3,osc2),0.01,sr*4);
  	sh5 = DelayN.ar(Latch.ar(sh4,osc2),0.01,sr*5);
  	sh6 = DelayN.ar(Latch.ar(sh5,osc2),0.01,sr*6);
  	sh7 = DelayN.ar(Latch.ar(sh6,osc2),0.01,sr*7);
  	sh8 = DelayN.ar(Latch.ar(sh7,osc2),0.01,sr*8);

  	//rungler = ((sh6/8)+(sh7/4)+(sh8/2)); //original circuit
  	//rungler = ((sh5/16)+(sh6/8)+(sh7/4)+(sh8/2));

  	rungler = ((sh1/2.pow(8))+(sh2/2.pow(7))+(sh3/2.pow(6))+(sh4/2.pow(5))+(sh5/2.pow(4))+(sh6/2.pow(3))+(sh7/2.pow(2))+(sh8/2.pow(1)));

  	buf = rungler;
  	rungler = (rungler * scale.linlin(0,1,0,127));
  	rungler = rungler.midicps;

  	LocalOut.ar([rungler,buf]);

  	filt = [
  		RLPF.ar(pwm,(rungler*runglerFilt)+filtFreq,q* -1 +1,gain),
  		//BMoog.ar(pwm,(rungler*runglerFilt)+filtFreq,q,0,gain),
  		RHPF.ar(pwm,(rungler*runglerFilt)+filtFreq,q* -1 +1,gain),
  		SVF.ar(pwm,(rungler*runglerFilt)+filtFreq,q,1,0,0,0,0,gain),
  		DFM1.ar(pwm,(rungler*runglerFilt)+filtFreq,q,gain,1)
  	][filterType];

  	output = SelectX.ar(outSignal, [
  		tri1, osc1, tri2, osc2, pwm, sh0, filt

  	]);
  	LeakDC.ar(output * amp);
  }
);


/////////////////      FILTs       ////////////////

ESynthDef.filt(\Houvilainen,
  ['LP 24db', 'LP 18db', 'LP 12db', 'LP 6db', 'HP 24db', 'BP 24db', 'N 24db', 'bypass'],
  \cutoff, [\ar, \freq.asSpec.copy.default_(20000), 25],
  \res, \kr,
  {
    ~cutoff = ~cutoff * ((~env + ~vel) * 100 + (~key * 1.05)).midiratio;
    HouvilainenFilter.ar(~in.tanh, ~cutoff, ~res, (~type + 1) % 8);
  }
);

ESynthDef.filt(\Vadim,
  ['LP 2', 'LP 4', 'BP 2', 'BP 4', 'HP 2', 'HP 4'],
  \cutoff, [\ar, \freq.asSpec.copy.default_(20000), 25],
  \res, \kr,
  {
    ~cutoff = ~cutoff * ((~env + ~vel) * 100 + (~key * 1.05)).midiratio;
    ~cutoff = ~cutoff.clip(10, 20000);
    VadimFilter.ar(~in, ~cutoff, ~res, ~type);
  }
);

ESynthDef.filt(\Diode,
  \cutoff, [\ar, \freq.asSpec.copy.default_(20000), 25],
  \res, \kr,
  \drive, [\kr, [0.1, 100, \exp, 0.0, 1], 0.3],
  {
    ~cutoff = ~cutoff * ((~env + ~vel) * 100 + (~key * 1.05)).midiratio;
    ~cutoff = ~cutoff.clip(10, 20000);
    VADiodeFilter.ar(~in, ~cutoff, ~res, ~drive) * 0.2;
  }
);

ESynthDef.filt(\Korg35,
  ['LP', 'HP'],
  \cutoff, [\ar, \freq.asSpec.copy.default_(20000), 25],
  \res, \kr,
  \drive, [\kr, [0.1, 100, \exp, 0.0, 1], 0.3],
  {
    ~cutoff = ~cutoff * ((~env + ~vel) * 100 + (~key * 1.05)).midiratio;
    ~cutoff = ~cutoff.clip(10, 20000);
    VAKorg35.ar(~in, ~cutoff, ~res, ~drive, ~type) * 0.2;
  }
);

ESynthDef.filt(\Ladder,
  ['LP 2', 'LP 4', 'BP 2', 'BP 4', 'HP 2', 'HP 4'],
  \cutoff, [\ar, \freq.asSpec.copy.default_(20000), 25],
  \res, \kr,
  \drive, [\kr, [0.1, 100, \exp, 0.0, 1], 0.3],
  {
    ~cutoff = ~cutoff * ((~env + ~vel) * 100 + (~key * 1.05)).midiratio;
    ~cutoff = ~cutoff.clip(10, 20000);
    VALadder.ar(~in, ~cutoff, ~res, ~drive, ~type) * 0.2;
  }
);

ESynthDef.filt(\VASEM12,
  \cutoff, [\ar, \freq.asSpec.copy.default_(20000), 25],
  \res, \kr,
  \transition, [\kr, [-1.0, 1.0, \lin]],
  {
    ~cutoff = ~cutoff * ((~env + ~vel) * 100 + (~key * 1.05)).midiratio;
    ~cutoff = ~cutoff.clip(10, 20000);
    VASEM12.ar(~in, ~cutoff, ~res, ~transition);
  }
);

ESynthDef.filt(\DFM1,
  ['LP', 'HP'],
  \cutoff, [\ar, \freq.asSpec.copy.default_(20000), 25],
  \res, \kr,
  \ingain, [\kr, [0.0, 10.0, \amp, 0, 0.4]],
  \outgain, [\kr, [0.0, 10.0, \amp, 0, 1.0]],
  {
    ~cutoff = ~cutoff * ((~env + ~vel) * 100 + (~key * 1.05)).midiratio;
    DFM1.ar(~in, ~cutoff, ~res, ~ingain, ~type) * ~outgain;
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

ESynthDef.filt(\AudioMSG,
  \index, [\ar, [0.01, 20, \exp, 0.0, 0.01], 0.2],
  {
    AudioMSG.ar(~in * 10, ~index) * 0.1;
  }
);

ESynthDef.filt(\SoftClipAmp,
  \pregain, [\ar, [0.1, 100, \exp, 0.0, 1], 0.3],
  {
    ~pregain = ~pregain + (~env + ~vel).linexp(0, 1, 0.1, 100);
    AudioMSG.ar(~in * 10, ~pregain) * 0.1;
  }
);

ESynthDef.filt(\Delay,
  \delay, [\kr, [0.1, 10.0, \exp, 0.0, 0.3]],
  \decay, [\kr, [-10, 10, \lin, 0.0, 1.0], 0.1, 10, true],
  {
    AllpassC.ar(~in, 10.0, ~delay.lag(0.01), ~decay);
  }
);

ESynthDef.filt(\AVDistortion,
  \drive, \kr,
  \bias, [\kr, [0, 2.5]],
  \low, [\kr, [0.0001, 0.3, \exp]],
  \hi, [\kr, [0.0001, 0.3, \exp]],
  //\cross, [\kr, [50, 5000, \exp, 0.0, 600]],
  {
    ~cross = 600;
    AnalogVintageDistortion.ar(~in, ~drive, ~bias, ~low, ~hi, ~cross, oversample: 1);
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
