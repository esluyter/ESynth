/*
//ESynthDef.mod(name, ...params, func); // addEnvParams = false, rate = [audio : control], 1 input
ESynthDef.lfo(name, [typelist,] ...params, krfunc, arfunc); // addEnvParams = false, rate = [audio : control], 0 inputs
ESynthDef.osc(name, [typelist,] ...params, func); // addEnvParams = false, rate = audio, 0 inputs
ESynthDef.filt(name, [typelist,] ...params, func); // addEnvParams = true, rate = audio, 1 input
ESynthDef.amp(name, [typelist,] ...params, func); // addEnvParams = true, rate = audio, 3 inputs
*/

/*
each param has:
- name
- inlet rate / spec / step / shift scale / centered
*/

/*
next up in synth land:
- key control of filter cutoff
- LFOs that do all the things they're meant to (one shot, retrig, global, random, blah blah)
- Fill out LFOs and oscs and filts
- add filter mod?
- handle freeing and cmd-period a little more gracefully?

moving into model incorporation:
- change number of voices on the fly
- init with model, synth is slave to model
- simplify model a great deal! -- use ESynthDefs instead of classes
... probably in reverse order
*/


e.putOsc(0, \VCO, [saw: 0.1, sqr: 0.1 ]);
e.putFilt(0, \Houvilainen, [cutoff: 100, res: 0, type: 4, env: 0.7, atk: 3, dec: 5, sus: 0.5, rel: 0.5]);
e.putFilt(1, \Houvilainen, [cutoff: 200, res: 0, type: 3, env: 0.7, atk: 3, dec: 5, sus: 0.5, rel: 0.5]);
e.setAmp(\rel, 3.5)

e.putOsc(0, \VCO, [saw: 0.1, sqr: 0.1 ]);
e.setAmp(\rel, 3.5, \atk, 0.01, \dec, 0.1, \sus, 0.3)
e.setAmp(\env_type, 2)
e.putLFO(0, \Noise, \control, [interp: 2], true);
e.addMod(0, { |v| v.amp }, \dec, 0.3)


e = ESynth(s, nil, 8);
e.free

(
MIDIClient.init;
MIDIIn.connectAll;
MIDIdef.noteOn(\on, { |vel, num|
  e.noteOn(num, vel);
});
MIDIdef.noteOff(\off, { |vel, num|
  e.noteOff(num);
});
MIDIdef.bend(\bend, { |val|
  e.bend_(val.linlin(0, 16383, -2.0, 2.0));
});
)

e.portamento = 0.2

e.putOsc(0, \VCO, [\saw, 0.1]);
e.putLFO(0, \Sin);
e.putLFO(1, \Sin, args: [freq: 0.1]);
e.putFilt(0, \Houvilainen, [\type, 1, \cutoff, 100, \res, 0.5]);
e.setFilt(0, \rel, 0.1, \atk, 2, \dec, 3, \sus, 0.5, \env, 1, \vel, 0);
e.setAmp(\rel, 1, \atk, 0.1, \vel, 1);
e.addMod(0, { |v| v.filts[0] }, \cutoff, 0.1);
e.setLFO(0, \freq, 6);
e.setModAmt({ |v| v.filts[0] }, \cutoff, 0.03);
e.putFilt(1, \Houvilainen, [\type, 1, \cutoff, 100, \res, 0.5]);
e.setFilt(1, \rel, 0.1, \atk, 1.8, \dec, 3, \sus, 0.5, \env, 0.9);
e.addMod(1, { |v| v.filts[1] }, \cutoff, 0.03);

e.putOsc(1, \VCO, [\sqr, 0.03, \tune, -12]);
e.putLFO(2, \Sin, args: [freq: 0.1]);
e.addMod(2, { |v| v.oscs[1] }, \duty, 0.5);
e.putOsc(2, \VCO, [\saw, 0.1, \fine, -0.1]);
e.setAmp(\atk, 0.01);
e.setFilt(0, \atk, 0.7, \vel, 1, \env, 1)
e.setFilt(1, \atk, 1.2, \vel, 1, \env, 1)

(72 * 2).midiratio
(
var unit;
try { unit = e.voices[0].lfos[0].modAt(\freq) };
unit;
)
e.addMod(1, { |v| v.lfos[0] }, \freq, 0.1);


e.putOsc(0, \VCO);
e.oscs[0].set(\saw, 0.1)
e.putLFO(0, \Sin);
e.putLFO(1, \Sin, args: [freq: 0.1]);
e.modulate(e.lfos[0], e.oscs[0], \tune, 0.1);
e.modulate(e.lfos[1], e.oscs[0].modAt(\tune), \amt, 0.1);
e.oscs[0].putMod(\tune, nil);
e.putFilt(0, \Houvilainen);
e.filts[0].set(\type, 1, \cutoff, 1000);
e.modulate(e.lfos[0], e.filts[0], \cutoff, 1);
e.filts[0].modAt(\cutoff).set(\amt, 0.5);
e.putFilt(1, \Houvilainen);
e.filts[1].set(\type, 4, \cutoff, 3000);
e.modulate(e.lfos[1], e.filts[1], \cutoff, 1);
e.lfos[1].set(\freq, 1.3333);
e.lfos[0].set(\freq, 1.8);
e.putOsc(1, \VCO, [\sqr, 0.03, \tune, -12]);
e.putLFO(2, \Sin, args: [freq: 0.1]);
e.modulate(e.lfos[2], e.oscs[1], \duty);
e.oscs[1].modAt(\duty).set(\amt, 0.5);
e.putLFO(3, \Noise, args: [interp: 3]);
e.modulate(e.lfos[3], e.amp, \pan, 1);
e.putFilt(1, nil)
e.putFilt(0, nil)
e.putOsc(0, nil)
e.putOsc(1, nil)
(0..3).do(e.putLFO(_, nil))









(
~buses = (
  stereo: Bus.audio(s, 2),
  mono: Bus.audio(s),
  oscs: Bus.audio(s)
);
)
a = ESUnit.amp(\VCA, [\instereo, ~buses.stereo, \inmono, ~buses.mono], bus: 0)
f = ESUnit.filt(\Houvilainen, [\in, ~buses.oscs], bus: ~buses.mono)
x = ESUnit.osc(\VCO, [\saw, 0.1], bus: ~buses.oscs)
y = ESUnit.osc(\VCO, [\saw, 0.1, tune: -12], bus: ~buses.oscs)
l = ESUnit.lfo(\Sin)
i = ESUnit.lfo(\Sin)
m = ESUnit.mod(l.bus, 0.1)
n = ESUnit.mod(l.bus, 0.1)
o = ESUnit.mod(i.bus, 0.1)
p = ESUnit.mod(i.bus, 0.5)
f.synth.set(\cutoff, 1000, \type, 2)
x.synth.set(\fine_mod, m.bus.asMap)
l.synth.set(\freq, 0.5)
f.synth.set(\cutoff_mod, n.bus.asMap)
n.synth.set(\amt, -1)
y.synth.set(\tri, 0.1, \saw, 0.1, \duty, 0.5, \duty_mod, o.bus.asMap)
o.synth.set(\amt, 1)
i.synth.set(\freq, 0.333)
a.synth.set(\pan_mod, p.bus.asMap, \pan, 0)
p.synth.set(\amt, 1)
f.synth.set(\res, 0.1)
f.synth.set(\res, 0.2)
f.synth.set(\res, 0.4)
f.synth.set(\res, 0.6)
f.synth.set(\res, 0.7)
f.synth.set(\res, 0.8)
f.synth.set(\res, 0.9)
y.synth.set(\tune, -12)
y.synth.set(\tune, -13)
y.synth.set(\tune, -14)
y.synth.set(\tune, -15)
y.synth.set(\tune, -16)
y.synth.set(\tune, -17)
y.synth.set(\tune, -19)
y.synth.set(\tune, -24)
y.synth.set(\tune, -10)
y.synth.set(\tune, -9)
y.synth.set(\tune, -8)
y.synth.set(\tune, -7)
y.synth.set(\tune, -6)
y.synth.set(\tune, 0)
o.bus.scope
p.bus.scope
n.bus.scope
n.free
x.bus.scope
y.bus.scope
x.free
y.free
b = Bus.audio
b.rate