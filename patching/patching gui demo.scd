(
~loadES = { |presetName = 'Default'|
  presetName.postcs;

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


  ////////// DEFINE ESYNTH - MODEL DEPENDANT STUFF ////////////////

  ~newPatch = { |pc|
    var fromIndex = pc.fromIndex;
    var rootModule = pc.rootModule;
    var toCategory = pc.toCategory;
    var toIndex = pc.rootToIndex;
    var toInlet = pc.rootToInlet;
    var toDepth = pc.toDepth;
    var amt = pc.amt;
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
    if (module.class == ESM) {
      var what = args[1];
      var val = args[2];
      if (what == \numVoices) {
        ~esinit.(~esm);
      } {
        ~es.perform((what ++ "_").asSymbol, val);
      };
    } {
      var index = module.index;
      var esm = args.removeAt(0);
      var what = args.removeAt(0);
      var val = args[0];
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
        {\notesyn} {
          ~es.setNotesyn(name, val.value)
        }
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
        var toCategory = module.category;
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
      {\arPatchCords} {
        var toCategory = module.category;
        var toIndex = module.index;
        var toInlets = [1];
        if (toCategory == \filts) { toInlets = [0, 1] };
        toInlets.do { |i| ~es.freeRoute({ |v| v.perform(toCategory).at(toIndex) }, i) };
        module.arPatchCords.do { |apc|
          var fromCategory = apc.fromCategory;
          var fromIndex = apc.fromIndex;
          var toInlet = apc.toInlet;
          ~es.addRoute({ |v| v.perform(fromCategory)[fromIndex] }, { |v| v.perform(toCategory)[toIndex] }, toInlet);
        };
      }
      {\type} {
        switch (module.kind)
        {\lfo} {
          ~es.setLFOType(index, val)
        }
        {\osc} {
          ~es.setOscType(index, val)
        }
        {\filt} {
          ~es.setFiltType(index, val)
        }
        {\amp} {
          ~es.setAmpType(val)
        }
      }
      {\envType} {
        switch (module.kind)
        {\lfo} {
          ~es.setLFO(index, \envType, val)
        }
        {\osc} {
          ~es.setOsc(index, \envType, val)
        }
        {\filt} {
          ~es.setFilt(index, \envType, val)
        }
        {\amp} {
          ~es.setAmp(\envType, val)
        }
      }
      {\portamento} {
        ~es.portamento_()
      }
      {\def} {
        // shouldn't need this...
      };
    };
  };

  ////////// END DEFINE ESYNTH - MODEL DEPENDENT STUFF ////////////////

  ~esinit = { |model|
    var notes = [];
    if (~es.notNil) {
      notes = ~es.notes;
    };
    ~es.free;
    ~es = ESynth(s, model, model.numVoices);
    // parse model
    ~es.portamento_(~esm.portamento);
    ~es.mod_(~esm.mod);
    ~es.bend_(~esm.bend);
    ~es.bendRange_(~esm.bendRange);
    ~es.priority_(~esm.priority);
    ~es.setNotesyn(\tune, ~esm.notesyn.tune);
    ~es.setNotesyn(\fine, ~esm.notesyn.fine);
    model.lfos.do { |lfo, i|
      ~es.putLFO(i, lfo.def, lfo.rate, lfo.argList, lfo.global, lfo.type)
    };
    model.oscs.do { |osc, i|
      ~es.putOsc(i, osc.def, osc.argList, osc.type)
    };
    model.filts.do { |filt, i|
      ~es.putFilt(i, filt.def, filt.argList, filt.type)
    };
    ~es.putAmp(model.amp.def, model.amp.argList, model.amp.type);
    model.patchCords.values.flat.do { |pc|
      while { pc.notNil } {
        ~newPatch.(pc);
        pc = pc.patchCords[0];
      };
    };
    model.arPatchCords.values.flat.do { |apc|
      ~es.addRoute({ |v| v.perform(apc.fromCategory)[apc.fromIndex] }, { |v| v.perform(apc.toCategory)[apc.toIndex] }, apc.toInlet);
    };
    ~es.notes = notes;
  };
  ~esinit.(~esm);
};

s.waitForBoot {
  ~loadES.();
};


////////// MIDI STUFF ////////////////
MIDIClient.init;
MIDIIn.connectAll;
MIDIdef.noteOn(\keyboardOn, { |vel, num| ~es.noteOn(num, vel) });
MIDIdef.noteOff(\keyboardOff, { |vel, num| ~es.noteOff(num) });
MIDIdef.bend(\bend, { |val| ~esm.bend_(val.linlin(0, 16383, -1, 1)) });
MIDIdef.cc(\mod, { |val| ~esm.mod_(val.linlin(0, 127, 0, 1)) }, 1);
)

s.record
s.stopRecording
