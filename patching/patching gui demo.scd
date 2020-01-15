(
~esm.free;
~esm = ESM();

Window.closeAll;
~window = Window.new("Lines", Rect(0, 200, 880, 975)).front;
~window.acceptsMouseOver = true;
~sv = ScrollView(~window, ~window.bounds.origin_(0@0));
~esv = ESView.new(~sv, model: ~esm);

~newPatch = { |pc|
  var fromIndex = pc.fromIndex;
  var rootModule = pc.rootModule;
  var toCategory = (rootModule.kind ++ \s).asSymbol;
  var toIndex = rootModule.index;
  var toInlet = pc.rootToInlet;
  var toDepth = pc.toDepth;
  var amt = pc.amt;
  [fromIndex, toCategory, toIndex, toInlet, toDepth, amt].postln;
  if (toDepth == 0) {
    ~es.addMod(fromIndex, { |v|
      v.perform(toCategory).at(toIndex)
    }, toInlet, amt);
  } {
    ~es.addMod(fromIndex, { |v|
      var unit = v.perform(toCategory).at(toIndex).modAt(toInlet);
      (toDepth - 1).do {
        unit = unit.modAt(0);
      };
      unit;
    }, 0, amt);
  };
};

~esm.addDependant { |...args|
  var module = args.pop;
  var index = module.index;
  var esm = args.removeAt(0);
  var what = args.removeAt(0);
  var val = args[0];
  ("% % - % changed" ++ if (val.notNil) { " to %" } { "" }).format(module.kind, index, what, val).postln;
  switch (what)
  {\global} {
    ~es.putLFO(index, module.def.name, module.rate, module.argList, module.global);
    ~esm.patchCordsFrom(index).do { |pc|
      while { pc.notNil } {
        ~newPatch.(pc);
        pc = pc.patchCords[0];
      };
    };
  }
  {\defName} {
    switch (module.kind)
    {\lfo} {
      ~es.putLFO(index, val, module.rate, module.argList, module.global);
      ~esm.patchCordsFrom(index).do { |pc|
        while { pc.notNil } {
          ~newPatch.(pc);
          pc = pc.patchCords[0];
        };
      };
    }
    {\osc} {
      ~es.putOsc(index, val, module.argList)
    }
    {\filt} {
      ~es.putFilt(index, val, module.argList)
    }
    {\amp} {
      ~es.putAmp(val, module.argList)
    }
  }
  {\param} {
    // NOT THE BEST SPOT FOR THIS!!
    var name = val.name.asString.replace($ , $_).asSymbol;
    switch (module.kind)
    {\lfo} {
      ~es.setLFO(index, name, val.value)
    }
    {\osc} {
      ~es.setOsc(index, name, val.value)
    }
    {\filt} {
      ~es.setFilt(index, name, val.value)
    }
    {\amp} {
      ~es.setAmp(name, val.value)
    }
  }
  {\patchAmt} {
    var pc = val;
    var rootModule = pc.rootModule;
    var toCategory = (rootModule.kind ++ \s).asSymbol;
    var toIndex = rootModule.index;
    var toInlet = pc.rootToInlet;
    var toDepth = pc.toDepth;
    var amt = pc.amt;
    [toCategory, toIndex, toInlet, toDepth, amt].postln;
    if (toDepth == 0) {
      ~es.setModAmt({ |v|
        v.perform(toCategory).at(toIndex)
      }, toInlet, amt);
    } {
      ~es.setModAmt({ |v|
        var unit = v.perform(toCategory).at(toIndex).modAt(toInlet);
        (toDepth - 1).do {
          unit = unit.modAt(0);
        };
        unit;
      }, 0, amt);
    };
  }
  {\patchCords} {
    var toCategory = (module.kind ++ \s).asSymbol;
    var toIndex = module.index;
    var numInlets = if (module.def.isNil) { 0 } { module.def.params.size - module.def.modOffset };
    numInlets.do { |i| ~es.freeMod({ |v| v.perform(toCategory).at(toIndex) }, i) };
    module.patchCords.do { |pc|
      while { pc.notNil } {
        ~newPatch.(pc);
        pc = pc.patchCords[0];
      };
    };
  }
  {\type} {
    switch (module.kind)
    {\lfo} {
      ~es.setLFO(index, \type, val)
    }
    {\osc} {
      ~es.setOsc(index, \type, val)
    }
    {\filt} {
      ~es.setFilt(index, \type, val)
    }
    {\amp} {
      ~es.setAmp(\type, val)
    }
  }
  {\envType} {
    switch (module.kind)
    {\lfo} {
      ~es.setLFO(index, \env_type, val)
    }
    {\osc} {
      ~es.setOsc(index, \env_type, val)
    }
    {\filt} {
      ~es.setFilt(index, \env_type, val)
    }
    {\amp} {
      ~es.setAmp(\env_type, val)
    }
  }
  {\def} {
    // shouldn't need this...
  };
};

~es.free;
~es = ESynth(s, ~esm, 8);
// parse model
// todo... patch cords
~esm.lfos.do { |lfo, i|
  ~es.putLFO(i, lfo.def, lfo.rate, lfo.argList, lfo.global)
};
~esm.oscs.do { |osc, i|
  ~es.putOsc(i, osc.def, osc.argList)
};
~esm.filts.do { |filt, i|
  ~es.putFilt(i, filt.def, filt.argList)
};
~es.putAmp(~esm.amp.def, ~esm.amp.argList);
s.plotTree;

MIDIClient.init;
MIDIIn.connectAll;
MIDIdef.noteOn(\keyboardOn, { |vel, num| ~es.noteOn(num, vel) });
MIDIdef.noteOff(\keyboardOff, { |vel, num| ~es.noteOff(num) });
MIDIdef.bend(\bend, { |val| ~es.bend_(val.linlin(0, 16383, -2.0, 2.0)) });
)


(
ESynthDef.osc('Operator 4',
  ['ALGO 1', 'ALGO 2', 'ALGO 3', 'ALGO 4', 'ALGO 5', 'ALGO 6', 'ALGO 7', 'ALGO 8'],
  'ratio 1', [\kr, [1, 20, \exp, 0.0, 0], 0.1],
  'amp 1', [\kr, [0, 20, 6, 0.0, 0.1], 0.1],
  'ratio 2', [\kr, [1, 20, \exp, 0.0, 0], 0.1],
  'amp 2', [\kr, [0, 20, 6, 0.0, 0], 0.1],
  'ratio 3', [\kr, [1, 20, \exp, 0.0, 0], 0.1],
  'amp 3', [\kr, [0, 20, 6, 0.0, 0], 0.1],
  'ratio 4', [\kr, [1, 20, \exp, 0.0, 0], 0.1],
  'amp 4', [\kr, [0, 20, 6, 0.0, 0], 0.1],
  {
    var feedback = 1;
    var chans = [0];
    var algo = FM7.modMatrix(
      [0, 1, 1],
      [1, 2, 1],
      [2, 3, 1],
      [3, 3, feedback]
    );
    ~freq = ~note.midicps;
    FM7.ar([
        [~freq * ~ratio_1, 0, ~amp_1],
        [~freq * ~ratio_2, 0, ~amp_2],
        [~freq * ~ratio_3, 0, ~amp_3],
        [~freq * ~ratio_4, 0, ~amp_4],
        [0, 0, 0],
        [0, 0, 0],
      ], algo).slice(chans).asArray.sum
  }
);
)

'amp 4'.asString.replace($ , $_).asSymbol

s.record;
s.stopRecording;

~es.portamento_(0)

SynthDescLib.global.at('ESfiltARHouvilainen').def.draw

