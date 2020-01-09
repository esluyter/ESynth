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
    switch (module.kind)
    {\lfo} {
      ~es.setLFO(index, val.name, val.value)
    }
    {\osc} {
      ~es.setOsc(index, val.name, val.value)
    }
    {\filt} {
      ~es.setFilt(index, val.name, val.value)
    }
    {\amp} {
      ~es.setAmp(val.name, val.value)
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
MIDIdef.noteOn(\keyboardOn, { |vel, num| ~es.noteOn(num.postln, vel) });
MIDIdef.noteOff(\keyboardOff, { |vel, num| ~es.noteOff(num) });
MIDIdef.bend(\bend, { |val| ~es.bend_(val.linlin(0, 16383, -2.0, 2.0)) });
)

s.record;
s.stopRecording;

~es.portamento_(0)
~es.globals.noteStack