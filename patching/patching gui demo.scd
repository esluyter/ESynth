BootWindow.show;

ESMPresets.put('FM Clarinet', ~esm.asEvent)
ESMPresets.put('8bit Sqr fuckedup', ~esm.asEvent)
ESMPresets.put('Robot voice glottal lead B', ~esm.asEvent)
ESMPresets.at('Buzzy Brass Pad').lfos
ESMPresets.at('Ethereal trumpets').postcs
ESMPresets.all.keys.asArray.sort
(
~loadES = { |presetName = 'Default'|
  ~esm.free;
  //~esm = ESM();
  ~esm = ESM.fromEvent(ESMPresets.at(presetName));
  //Window.closeAll;
  if (~window.notNil) { ~window.close };
  ~window = Window.new("Lines", Rect(0, 200, 880, min(975, Window.screenBounds.height - 25))).background_(Color(0.1, 0, 0.1)).front;
  ~window.acceptsMouseOver = true;
  ~sv = ScrollView(~window, ~window.bounds.resizeBy(0, -30).origin_(0@30)).hasBorder_(false);
  ~esv = ESView.new(~sv, Rect(0, 0, 880, 990), model: ~esm);
  ~presetMenu = PopUpMenu(~window, Rect(610, 5, 260, 20))
  .background_(Color.grey(0.04))
  .stringColor_(Color.white)
  .font_(Font.monospace.size_(8))
  .items_(ESMPresets.all.keys.asArray.sort)
  .value_(ESMPresets.all.keys.asArray.sort.indexOf(presetName))
  .action_({ ~loadES.(~presetMenu.item) });
  ~saveButt = Button(~window, Rect(530, 5, 70, 20))
  .states_([["Save As...", Color.white, Color.grey(0.04)]])
  .font_(Font.monospace.size_(8));

  ~newPatch = { |pc|
    var fromIndex = pc.fromIndex;
    var rootModule = pc.rootModule;
    var toCategory = pc.toCategory;
    var toIndex = pc.rootToIndex;
    var toInlet = pc.rootToInlet;
    var toDepth = pc.toDepth;
    var amt = pc.amt;
    //[fromIndex, toCategory, toIndex, toInlet, toDepth, amt].postln;
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
      [what, val].postln;
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
      //("% % - % changed" ++ if (val.notNil) { " to %" } { "" }).format(module.kind, index, what, val).postln;
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
        //[toCategory, toIndex, toInlet, toDepth, amt].postln;
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
    ~es.notes = notes;
  };
  ~esinit.(~esm);
};
~loadES.();
//s.plotTree;

MIDIClient.init;
MIDIIn.connectAll;
MIDIdef.noteOn(\keyboardOn, { |vel, num| ~es.noteOn(num, vel) });
MIDIdef.noteOff(\keyboardOff, { |vel, num| ~es.noteOff(num) });
MIDIdef.bend(\bend, { |val| ~esm.bend_(val.linlin(0, 16383, -1, 1)) });
MIDIdef.cc(\mod, { |val| ~esm.mod_(val.linlin(0, 127, 0, 1)) }, 1);
)

~esm.filts[0].params[7].

s.record;
s.stopRecording;
~es.globals.portamento
~esm.portamento_(0.1)
~esm.numVoices_(8)
~esm.bendRange_(7)
~esm.asEvent.postcs


~es.free
SynthDescLib.global.at('ESfiltARHouvilainen').def.draw

~esm.filts[0].argList
~esm.amp.argList
~es.voices[0].amp.synth.get(\envType, _.postln)
~es.voices[0].filts[0].synth.get(\type, _.postln)
~es.voices[0].filts[0].args


(
ESynthDef.osc(\test,
  (0..10).collect(_.asSymbol),
  \duty, \kr,
  {
    VarSaw.ar(~note.midicps * (~type + 1), width: ~duty);
  }
);
)

ESynthDef.lfos[\Sin].dump
