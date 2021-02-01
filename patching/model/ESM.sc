ESM {
  var <numVoices, <portamento, <bendRange, <mod, <bend, <priority;
  var <notesyns, <lfos, <oscs, <filts, <amps;
  var <oscbuses, <ampbuses;
  var connections;

  *new { |numVoices = 8, numLFOs = 20, numOscs = 6, numFilts = 4, portamento = 0, mod = 0, bend = 0, bendRange = 2, priority = 0|
    ^super.newCopyArgs(numVoices, portamento, bendRange, mod, bend, priority).init(numLFOs, numOscs, numFilts).prDefaultConfig;
  }

  init { |numLFOs, numOscs, numFilts|
    notesyns = ESModuleList(\notesyn, 1);
    notesyns[0].def_(ESynthDef.note);
    lfos = ESModuleList(\lfo, numLFOs);
    oscs = ESModuleList(\osc, numOscs);
    filts = ESModuleList(\filt, numFilts);
    oscbuses = ESMBusList(\oscbus, ['A', 'B', 'C', 'D']);
    ampbuses = ESMBusList(\ampbus, ['L', 'C', 'R'], false);
    amps = ESModuleList(\amp, 1);
    connections = ConnectionList.make {
      [notesyns, lfos, oscs, filts, amps, oscbuses, ampbuses].do(_.connectTo({ |changedList ...args|
        this.changed(*args)
      }));
    };
  }

  prDefaultConfig {
    amps[0].def_(\VCA); // not necessary after fixing .fromEvent below
  }

  free {
    connections.free;
    [lfos, oscs, filts, amps].do(_.free);
  }

  notesyn { ^notesyns[0] }

  putLFO { |index, def, rate = \control, copyParams = false, copyPatchCords = true, global = true|
    // TODO: fix global put in ESynthDef
    lfos[index].def_(def, rate, copyParams, copyPatchCords, global);
    ^lfos[index];
  }

  putOsc { |index, def, copyParams = false, copyPatchCords = true|
    oscs[index].def_(def, \audio, copyParams, copyPatchCords);
    ^oscs[index];
  }

  putFilt { |index, def, copyParams = false, copyPatchCords = true|
    filts[index].def_(def, \audio, copyParams, copyPatchCords);
    ^filts[index];
  }

  putAmp { /* TODO */ }
  amp { ^amps[0] }

  numVoices_ { |value|
    numVoices = value;
    this.changed(\numVoices, numVoices, this);
  }
  portamento_ { |value|
    portamento = value;
    this.changed(\portamento, portamento, this);
  }
  mod_ { |value|
    mod = value;
    this.changed(\mod, mod, this);
  }
  bend_ { |value|
    bend = value;
    this.changed(\bend, bend, this);
  }
  bendRange_ { |value|
    bendRange = value;
    this.changed(\bendRange, bendRange, this);
  }
  priority_ { |value|
    priority = value;
    this.changed(\priority, priority, this);
  }

  numLFOs { ^lfos.size }
  numOscs { ^oscs.size }
  numFilts { ^filts.size }
  patchCords {
    ^(
      lfo: lfos.patchCords,
      osc: oscs.patchCords,
      filt: filts.patchCords,
      amp: amps.patchCords,
      notesyn: notesyns.patchCords
    )
  }
  patchCordsFrom { |index|
    var level0 = this.patchCords.values.flat;
    var patchCords = [];
    level0.do { |pc|
      while { pc.notNil } {
        patchCords = patchCords.add(pc);
        pc = pc.patchCords[0];
      };
    };
    ^patchCords.select({ |pc| pc.fromIndex == index });
  }

  arPatchCords {
    ^(
      lfo: lfos.arPatchCords,
      osc: oscs.arPatchCords,
      oscbus: oscbuses.arPatchCords,
      filt: filts.arPatchCords,
      ampbus: ampbuses.arPatchCords,
      amp: amps.arPatchCords
    )
  }
  arPatchCordsFrom { |fromCategory, fromIndex|
    ^this.arPatchCords.values.flat.select({ |pc| pc.fromCategory == fromCategory and: (pc.fromIndex == fromIndex) });
  }

  asEvent {
    ^(
      numVoices: numVoices,
      portamento: portamento,
      mod: mod,
      bend: bend,
      bendRange: bendRange,
      priority: priority,
      lfos: lfos.asArray,
      oscs: oscs.asArray,
      filts: filts.asArray,
      amps: amps.asArray,
      patchCords: this.patchCords.values.flat.collect(_.asEvent),
      arPatchCords:
      this.arPatchCords.values.flat.collect(_.asEvent)
    )
  }

  *fromEvent { |e|
    var handlePC;
    var numVoices = e.numVoices;
    var numLFOs = e.lfos.size;
    var numOscs = e.oscs.size;
    var numFilts = e.filts.size;
    var portamento = e.portamento;
    var mod = e.mod;
    var bend = e.bend;
    var bendRange = e.bendRange;
    var priority = e.priority ? 0;
    var esm = this.new(numVoices, numLFOs, numOscs, numFilts, portamento, mod, bend, bendRange, priority);

    e.lfos.do { |lfo, i|
      var def = if (lfo.notNil) { lfo.def } { nil };
      var rate = if (lfo.notNil) { lfo[\rate] } { nil };
      var global = if (lfo.notNil) { lfo.global } { nil };
      var newLFO = esm.putLFO(i, def, rate, false, false, global);
      if (lfo.notNil) {
        newLFO.type_(lfo.type);
        newLFO.envType_(lfo.envType);
        newLFO.params_(lfo.params);
      };
    };
    e.oscs.do { |osc, i|
      var def = if (osc.notNil) { osc.def } { nil };
      var newOsc = esm.putOsc(i, def, false, false);
      if (osc.notNil) {
        newOsc.type_(osc.type);
        newOsc.envType_(osc.envType);
        newOsc.params_(osc.params);
      };
    };
    e.filts.do { |filt, i|
      var def = if (filt.notNil) { filt.def } { nil };
      var newFilt = esm.putFilt(i, def, false, false);
      if (filt.notNil) {
        newFilt.type_(filt.type);
        newFilt.envType_(filt.envType);
        newFilt.params_(filt.params);
      };
    };
    e.amps.do { |amp, i|
      // todo put amp
      esm.amps[0].type = amp.type;
      esm.amps[0].envType = amp.envType;
      esm.amps[0].params = amp.params;
    };

    handlePC = { |pc|
      var rootModule = esm.perform(pc.toCategory)[pc.rootToIndex];
      if (pc.toDepth == 0) {
        rootModule.patchFrom(esm.lfos[pc.fromIndex], pc.rootToInlet, pc.amt);
      } {
        var patchModule = rootModule.patchAt(pc.rootToInlet);
        (pc.toDepth - 1).do {
          patchModule = patchModule.patchAt(0);
        };
        patchModule.patchFrom(esm.lfos[pc.fromIndex], pc.amt);
      };
      pc.patchCords.do { |pc| handlePC.(pc) };
    };
    e.patchCords.do { |pc| handlePC.(pc) };

    e.arPatchCords.do { |apc|
      var to = esm.perform(apc.toCategory).at(apc.toIndex);
      var from = esm.perform(apc.fromCategory).at(apc.fromIndex);
      to.arPatchFrom(from, apc.toInlet);
    };

    ^esm;
  }
}
