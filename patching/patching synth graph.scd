(
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

SynthDef(\lfosinar, {
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
  var pinkamt = \pink.kr(0.2);

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
)

( // alt
SynthDef(\oscnoise, {
  var whiteamt = \white.kr;
  var pinkamt = \pink.kr(0.2);

  var out = \out.ir;
  var freq = LFDNoise0.kr(LFDNoise1.kr(1).exprange(0.1, 20)).exprange(50, 200);
  //var sig = WhiteNoise.ar(whiteamt) + PinkNoise.ar(pinkamt);
  var sig = (Saw.ar(freq * LFNoise1.kr(0.1).midiratio) + Pulse.ar(freq / 2)) * pinkamt;
  Out.ar(out, sig);
}).add;
)

(
// cleanup
~voicegroup.free;
~lfobuses.do(_.free);
~modbuses.do(_.free);
~oscbus.free;
~monobus.free;
~stbus.free;

// groups and buses
~voicegroup = Group(s);
~modgroup = Group(~voicegroup, \addToTail);
~modbuses = [Bus.audio(s), Bus.audio(s), Bus.control(s), Bus.control(s), Bus.control(s), Bus.control(s)];
~lfogroup = Group(~voicegroup, \addToTail);
~lfobuses = { Bus.control(s) } ! 20;
~oscgroup = Group(~voicegroup, \addToTail);
~oscbus = Bus.audio(s);
~filtampgroup = Group(~voicegroup, \addToTail);
~monobus = Bus.audio(s, 1);
~stbus = Bus.audio(s, 2);

// lfos
~lfobuses[0].free;
~lfobuses[0] = Bus.audio(s);
~lfosyns = [
  Synth(\lfosinar, [
    out: ~lfobuses[0],
    freq: 300,
    freqmod: ~modbuses[2].asMap
  ], ~lfogroup),
  Synth(\lfosin, [
    out: ~lfobuses[1],
    freq: 0.5,
    freqmod: ~modbuses[0].asMap
  ], ~lfogroup),
  Synth(\lfonoise, [
    out: ~lfobuses[2],
    freq: 0.2,
    interp: 3
  ], ~lfogroup)
];
//~lfobuses[0].scope;

// mods
~modsyns = [
  Synth(\modulatear, [
    in: ~lfobuses[0],
    out: ~modbuses[0],
    amt: 1
  ], ~modgroup),
  Synth(\modulatear, [
    in: ~lfobuses[0],
    out: ~modbuses[1],
    amt: 1
  ], ~modgroup),
  Synth(\modulatekr, [
    in: ~lfobuses[1],
    out: ~modbuses[2],
    amt: 1,
    amtmod: ~modbuses[3].asMap
  ], ~modgroup),
  Synth(\modulatekr, [
    in: ~lfobuses[2],
    out: ~modbuses[3],
    amt: 0.7
  ], ~modgroup),
  Synth(\modulatekr, [
    in: ~lfobuses[1],
    out: ~modbuses[4],
    amt: 0.5,
    amtmod: ~modbuses[5].asMap
  ], ~modgroup),
  Synth(\modulatekr, [
    in: ~lfobuses[2],
    out: ~modbuses[5],
    amt: -0.5
  ], ~modgroup)
];
~modbuses[3].scope;

// osc
~oscsyns = [
  Synth(\oscnoise, [out: ~oscbus], ~oscgroup)
];

// filt + amp
~filts = [
  Synth(\filter, [
    out: ~monobus,
    in: ~oscbus,
    cutoff: 3000,
    cutoffmod: ~modbuses[1].asMap,
    res: 0.85
  ], ~filtampgroup)
];
~amp = Synth(\vca, [
  out: 0,
  inmono: ~monobus,
  instereo: ~instereo,
  pan: ~modbuses[4].asMap
], ~filtampgroup, \addToTail);
)



SynthDescLib.default[\lfosin]

s.record