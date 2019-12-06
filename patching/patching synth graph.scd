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
~oscs = ModuleList(OscNil, 6);
~oscs[0].class_(OscVCO);
~oscs[1].class_(OscVCO);
~oscs[3].class_(OscVCO);
~oscs[4].class_(OscNoise);
e.free;
e = ESynth(numVoices: 2);
s.plotTree;
)

e.putOsc(4, ~oscs[4]);

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
  Synth(\lfoarsin, [
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
  Synth(\oscnoise, [out: ~oscbus, pink: 0.2], ~oscgroup)
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