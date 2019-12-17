ESM {
  var <lfos, <oscs, <filts, <amps;
  var connections;

  *new { |numVoices = 8, numLFOs = 20, numOscs = 6, numFilts = 4|
    ^super.new.init(numVoices, numLFOs, numOscs, numFilts);
  }

  init { |numVoices, numLFOs, numOscs, numFilts|
    lfos = ESModuleList(\lfo, numLFOs);
    oscs = ESModuleList(\osc, numOscs);
    filts = ESModuleList(\filt, numFilts);
    amps = ESModuleList(\amp, 1);
    connections = ConnectionList.make {
      [lfos, oscs, filts, amps].do(_.connectTo({ |...args| args.postln }));
    };
    this.prDefaultConfig;
  }

  free {
    connections.free;
  }

  prDefaultConfig {
    oscs[0].def_(\VCO);
    filts[0].def_(\Houvilainen);
    amps[0].def_(\VCA);
  }

  numLFOs { ^lfos.size }
  numOscs { ^oscs.size }
  numFilts { ^filts.size }
  //numVoices { ^synth.numVoices }
}
