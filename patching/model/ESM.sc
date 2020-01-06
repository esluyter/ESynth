ESM {
  var <numVoices;
  var <lfos, <oscs, <filts, <amps;
  var connections;

  *new { |numVoices = 8, numLFOs = 20, numOscs = 6, numFilts = 4|
    ^super.newCopyArgs(numVoices).init(numLFOs, numOscs, numFilts).prDefaultConfig;
  }

  init { |numLFOs, numOscs, numFilts|
    lfos = ESModuleList(\lfo, numLFOs);
    oscs = ESModuleList(\osc, numOscs);
    filts = ESModuleList(\filt, numFilts);
    amps = ESModuleList(\amp, 1);
    connections = ConnectionList.make {
      [lfos, oscs, filts, amps].do(_.connectTo({ |changedList ...args|
        this.changed(*args)
      }));
    };
  }

  prDefaultConfig {
    oscs[0].def_(\VCO);
    filts[0].def_(\Houvilainen);
    amps[0].def_(\VCA);
  }

  free {
    connections.free;
    [lfos, oscs, filts, amps].do(_.free);
  }

  putLFO { |index, def, rate = \control, copyParams = false, copyPatchCords = true, global = true|
    // TODO: fix global put in ESynthDef
    lfos[index].def_(def, rate, copyParams, copyPatchCords, global);
  }

  putOsc { |index, def, copyParams = false, copyPatchCords = true|
    oscs[index].def_(def, \audio, copyParams, copyPatchCords);
  }

  putFilt { |index, def, copyParams = false, copyPatchCords = true|
    filts[index].def_(def, \audio, copyParams, copyPatchCords);
  }

  putAmp { /* TODO */ }
  amp { ^amps[0] }

  numLFOs { ^lfos.size }
  numOscs { ^oscs.size }
  numFilts { ^filts.size }
  patchCords {
    ^(
      lfo: lfos.patchCords,
      osc: oscs.patchCords,
      filt: filts.patchCords,
      amp: amps.patchCords
    )
  }
}
