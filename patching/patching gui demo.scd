(
~loadES = { |presetName = 'Default'|
  ////////// LOAD MODEL ////////////////
  ~esm.free;
  ~esm = ESM.fromEvent(ESMPresets.at(presetName));

  ////////// OPEN WINDOW WITH VIEW ////////////////
  if (~window.notNil) { ~window.onClose_({}); ~window.close };
  ~window = ESWindow("ESynth", model: ~esm);
  ~window.setPresetName(presetName);
  ~window.addDependant { |win, what, val|
    if (what == 'preset') {
      ~loadES.(val)
    };
  };
  ~window.onClose_({ ~esc.free; });

  ////////// EZMIDI KEYBOARD ////////////////
  ~kwview = View(~window.sv, Rect(1000, 765, 495, 350));
  EZMIDI.keyboard(~kwview);
  ~window.view.keyDownAction = { |view, char|
    ~kwview.keyDownAction.(~kwview, char)
  };
  ~window.view.keyUpAction = { |view, char|
    ~kwview.keyUpAction.(~kwview, char)
  };

  ~smview = ServerMeterView(s, ~window, 1460@880, 0, 2);
  ~scopeview = View(~window, Rect(1540, 865, 250, 250));
  ~scope = Stethoscope(s, view: ~scopeview);

  ~esc.free;
  ~esc = ESynthController(s, ~esm);
};

s.waitForBoot {
  ~loadES.();
};


////////// MIDI STUFF ////////////////
MIDIClient.init;
MIDIIn.connectAll;
MIDIdef.noteOn(\keyboardOn, { |vel, num| ~esc.noteOn(num, vel) });
MIDIdef.noteOff(\keyboardOff, { |vel, num| ~esc.noteOff(num) });
MIDIdef.bend(\bend, { |val| ~esm.bend_(val.linlin(0, 16383, -1, 1)) });
MIDIdef.cc(\mod, { |val| ~esm.mod_(val.linlin(0, 127, 0, 1)) }, 1);
)

s.latency = 0.02



s.record
s.stopRecording


100.midiratio
(
{
  var trig = Impulse.kr(10);
  trig = TIRand.kr(0, 1, trig) * trig;
  PinkNoise.ar(0.1) * Decay.kr(trig, 0.1);
}.play
)


{Chen.kr}.plot


ESParam(\a).modName


(
ESynthDef.lfo(\Chen,
  \speed, [\kr, [0, 1.0, \lin, 0.0, 0.5]],
  \a, [\kr, [0, 1.0, \lin, 0.0, 0.5]],
  \b, [\kr, [0, 1.0, \lin, 0.0, 0.3]],
  //\c, [\kr, [0, 1.0, \lin, 0.0, 0.28]],
  {
    //Chen.kr(~speed, ~a, ~b, ~c);
    DC.kr(0);
  },
  {
    //Chen.ar(~speed, ~a, ~b, ~c);
    DC.ar(0);
  }
);
)
1/(48000)

0.00001

ESynthDef.lfos[\Chen].params[2].modName



SynthDef(\hi, { |a_mod = 50, b_mod = 100| Out.ar(0, SinOsc.ar([a_mod, b_mod])) }).play

x.set(\a_mod, 1200)


x = { (LFNoise1.kr > LFNoise1.kr).poll }.play
x.free




b = Buffer.readChannel(s, "/Users/ericsluyter/Music/Talking heads/somethingtoremember.wav", channels: [0]);

(
{
  var chen = Chen.kr(0.01, 0.5, 0.3, 0.28).poll;
  var freq = chen[1].linexp(0, 1, 100, 200);//MouseY.kr(100, 500, \exponential);
  var trig = Impulse.ar(freq + [0, 1]);
  var overlap = 1;
  var buf = b;
  var rate = 1;
  //var pos = (MouseX.kr + LFDNoise0.kr(LFDNoise0.kr(10).exprange(0.1, 10))).lag2(0.05, 0.2);
  var pos = LFDNoise3.kr(chen[0].linexp(0, 1, 0.0001, 1)).range(0, 1);//LFDNoise3.kr(MouseX.kr(0.0001, 1, \exponential).poll).range(0, 1);
  //GrainBuf.ar(1, trig, freq.reciprocal * overlap, buf, rate, pos)
  //PlayBuf.ar(1, buf, BufRateScale.kr(buf), trig, pos)
  [SuperPlayBufX.ar(1, buf, 1, trig[0], pos * BufDur.kr(buf), fadeTime: SampleDur.ir * 10), SuperPlayBufX.ar(1, buf, 1, trig[1], pos * BufDur.kr(buf), fadeTime: SampleDur.ir * 10)];
  //SuperPlayBuf.ar(1, buf, 1, trig, pos * BufDur.kr(buf));
}.play;
)

s.record


ESynthDef.lfos[\Chen].numChannels

ESynthDef

u = ESUnit.lfo(\Chen)
u.bus