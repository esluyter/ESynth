(
{
  var sig = EVCO.ar(60, pw: MouseY.kr, sawAmt: 1, triAmt: 0, sqrAmt: 0, sinAmt: 0);
  //sig = AudioMSG.ar(sig, MouseY.kr(0, 2pi));

  /*
  var x = sig * MouseY.kr(0.5, 10);
  var a = 1 + exp (sqrt (abs (x)) * -0.75);
  var output = (exp (x) - exp (-1 * x * a)) / (exp (x) + exp (-1 * x));
  sig = output;
  */

  sig * 0.1 ! 2;
}.play
)


i = 2

1 - 2 + 1

(0, 0.01 .. 1).lincurve(0, 1, 0, 0.02, -4, \min).plot

(
{
  var freq = 60.midicps;
  var freqs, amps;
  var duty = MouseY.kr(0.35, 2);
  var max = 30;
  var n = duty.linexp(0, 1, 0.1, max);
  var d = duty.lincurve(0, 1, 0, 0.02, 2, \min);
  n = n.clip(0, max);
  freqs = freq * (max + 1).collect({ |i| LFNoise1.kr(0.001).range(1 - d, 1 + d) });
  amps = (max + 1).collect({ |i| (n - i + 1).clip(0, 1) });
  (Saw.ar(freqs) * amps).sum * (n + 5).reciprocal * 1 ! 2
}.play
)


(
SynthDef(\supersaw_poc, { |out, gate = 1, freq = 440, amp = 0.1, pan = 0, cutoff = 20000|
  var freqs, amps, sig;
  var env = Env.adsr(0.1, 2, 0.4, 0.3).kr(2, gate);
  var duty = MouseY.kr(0, 2);
  var max = 30;
  var n = duty.linexp(0, 1.5, 0.1, max);
  var d = duty.lincurve(0.2, 1.5, 0.005, 0.02, 2, nil);
  n = n.clip(0, max);
  freqs = freq * (max + 1).collect({ |i| LFNoise1.kr(0.001).range(1 - d, 1 + d) });
  amps = (max + 1).collect({ |i| (n - i + 1).clip(0, 1) });
  sig = (Saw.ar(freqs) * amps).sum * n.lincurve(0, 30, 1, 0.25);
  sig = sig * env;
  sig = RLPF.ar(sig, cutoff, MouseX.kr(1, 0.05, \exponential));
  Out.ar(out, Pan2.ar(sig, pan, amp));
}).add;
)

{ ESuperSaw.ar(MouseX.kr(100, 5000,\exponential), MouseY.kr) ! 2 }.play

x = Synth(\supersaw_poc)
x.free


(0..30).lincurve(0, 30, 1, 0.1).plot
(5..35).reciprocal.plot


MIDIClient.init
MIDIIn.connectAll
(
~notes = ();
~cutoff = 20000;
MIDIdef.noteOn(\noteOn, { |vel, num| ~notes[num].free; ~notes[num] = Synth(\supersaw_poc, [freq: num.midicps, amp: vel.linlin(0, 127, -30, -6).dbamp, cutoff: ~cutoff]) });
MIDIdef.noteOff(\noteOff, { |vel, num| ~notes[num].release; ~notes[num] = nil; });
MIDIdef.cc(\mod, { |val| ~cutoff = val.linexp(0, 127, 200, 20000); ~notes.do(_.set(\cutoff, ~cutoff)) }, 1);
EZMIDI.keyboard
)




0.02 / 10

VarSaw.ar()

(0, 0.05 .. 1).linlin(0, 1, -1, 1).collect({ |n| [n.asin, n.tan, (n * 10).sinh] }).flop.plot

10.sinh / 11013



{[VarSaw.ar(440, 0, 0), Saw.ar(440) * 2]}.plot

AudioMSG

Shaper


(
{
var x = SoundIn.ar * 0.686306;
var a = 1 + exp (sqrt (abs (x)) * -0.75);
var output = (exp (x) - exp (-1 * x * a)) / (exp (x) + exp (-1 * x));
output
}.scope
)