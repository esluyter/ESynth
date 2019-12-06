e = ESUnit(\lfosin);
e.bus.scope
e.free

SynthDescLib.global.at(\lfosin).controlNames

/*
Implicit:
in
out
type
*/

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
- inlet rate
- spec / step / shift scale / centered
*/

(
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

ESynthDef.filt(\Houvilainen,
  ['bypass', 'LP 24db', 'LP 18db', 'LP 12db', 'LP 6db', 'HP 24db', 'BP 24db', 'N 24db'],
  \cutoff, [\ar, \freq.asSpec.copy.default_(20000), 25],
  \res, \kr,
  \mod, \kr,
  {
    HouvilainenFilter.ar(~in, ~cutoff, ~res, ~type);
  }
);

ESynthDef.amp(\VCA,
  \pan, [\ar, [-1, 1, \lin, 0.0, 0], 0.01, 10, true],
  {
    Pan2.ar(~inmono, ~pan) + Balance2.ar(~instereo[0], ~instereo[1], ~pan);
  }
);
)


SynthDescLib.global[\ESoscARVCO].inputs
SynthDescLib.global[\ESfiltARHouvilainen].outputs
SynthDescLib.global[\ESampARVCA].outputs[0].numberOfChannels
x.free;x = Synth(\ESoscARVCO, [\tune: 0, \saw, 0.1, \tune_mod, 0])
Synth(\ESmodKRmod)

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