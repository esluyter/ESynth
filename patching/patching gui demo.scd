(
~loadES = { |presetName = 'Default'|
  ////////// LOAD MODEL ////////////////
  ~esm.free;
  ~esm = ESM.fromEvent(ESMPresets.at(presetName));

  ////////// OPEN WINDOW WITH VIEW ////////////////
  if (~window.notNil) { ~window.close };
  ~window = ESWindow("ESynth", model: ~esm);
  ~window.setPresetName(presetName);
  ~window.addDependant { |win, what, val|
    if (what == 'preset') {
      ~loadES.(val)
    };
  };

  ////////// EZMIDI KEYBOARD ////////////////
  ~kwview = View(~window.sv, Rect(1000, 765, 495, 350));
  EZMIDI.keyboard(~kwview);
  ~window.view.keyDownAction = { |view, char|
    ~kwview.keyDownAction.(~kwview, char)
  };
  ~window.view.keyUpAction = { |view, char|
    ~kwview.keyUpAction.(~kwview, char)
  };

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

s.record
s.stopRecording

~esc.es.free
