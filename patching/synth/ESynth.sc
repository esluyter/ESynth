ESynth {
  var <globals, <voices, <model, <numVoices;
  var <server, <group, <treeFunc;
  var <roundRobinIndex = 0;

  *initClass {
    Class.initClassTree(ESynthDef);

    // load ESynthDefs
    File(PathName(ESynth.filenameSymbol.asString).pathOnly +/+ "esynthdefs.scd", "r").readAllString.interpret;
  }

  *new { |server, numVoices = 1|
    ^super.new.init(server, numVoices);
  }

  init { |argserver, nVoices = 1|
    server = argserver ?? Server.default;
    numVoices = nVoices;

    treeFunc = {
      if (voices.notNil) {
        voices.do(_.free);
      };
      group.free;

      roundRobinIndex = 0;
      group = Group(server);
      voices = { ESVoice(group) } ! numVoices;
      globals = ESGlobals(group);
    };
    treeFunc.();
    ServerTree.add(treeFunc, server);
  }

  free {
    voices.do(_.free);
    group.free;
    ServerTree.remove(treeFunc, server);
  }

  addRoute { |fromUnitFunc, toUnitFunc, index = 0, amt = 1| // index: 0->main, 1->sidechain
    var fromUnit, toUnit;
    var globalToUnit = true;
    try {
      globalToUnit = toUnitFunc.(voices[0]).isNil;
    };
    if (globalToUnit) {
      fromUnit = fromUnitFunc.(voices[0]);
      toUnit = toUnitFunc.(globals);
      globals.route(fromUnit, toUnit, index, amt);
    } {
      voices.do { |v|
        fromUnit = fromUnitFunc.(v);
        toUnit = toUnitFunc.(v);
        v.route(fromUnit, toUnit, index, amt);
      }
    };
  }

  freeRoute { |toUnitFunc, index|
    var globalToUnit = true;
    try {
      globalToUnit = toUnitFunc.(voices[0]).isNil;
    };
    if (globalToUnit) {
      var toUnit = toUnitFunc.(globals);
      if (toUnit.notNil) {
        toUnit.putRoute(index, nil); // necessary?
      }
    } {
      voices.do({ |v|
        var toUnit = toUnitFunc.(v);
        if (toUnit.isKindOf(ESUnit)) {
          toUnit.putRoute(index, nil);
        } {
          toUnit.freeInputRoutes;
        }
      });
    };
  }

  setRouteAmt { |toUnitFunc, index, amt|
    var globalToUnit = true;
    try {
      globalToUnit = toUnitFunc.(voices[0]).isNil;
    };
    if (globalToUnit) {
      var toUnit = toUnitFunc.(globals);
      toUnit.routes[index].set(\amt, amt);
    } {
      voices.do({ |v|
        var toUnit = toUnitFunc.(v);
        toUnit.routes[index].set(\amt, amt);
      });
    };
  }

  addMod { |lfoIndex, toUnitFunc, param, amt|
    var fromUnit, toUnit;
    var globalLFO = globals.lfos[lfoIndex].notNil;
    var globalToUnit = true;
    if (globalLFO.not and: (voices[0].lfos[lfoIndex].isNil)) {
      ^false;
    };
    try {
      globalToUnit = toUnitFunc.(voices[0]).isNil;
    };
    //[globalLFO, globalToUnit].postln;
    if (globalLFO and: globalToUnit) {
      fromUnit = globals.lfos[lfoIndex];
      toUnit = toUnitFunc.(globals);
      globals.modulate(fromUnit, toUnit, param, amt);
    };
    if (globalLFO and: globalToUnit.not) {
      fromUnit = globals.lfos[lfoIndex];
      voices.do { |v|
        toUnit = toUnitFunc.(v);
        v.modulate(fromUnit, toUnit, param, amt);
      };
    };
    if (globalLFO.not and: globalToUnit) {
      fromUnit = voices[0].lfos[lfoIndex];
      toUnit = toUnitFunc.(globals);
      globals.modulate(fromUnit, toUnit, param, amt);
    };
    if (globalLFO.not and: globalToUnit.not) {
      voices.do { |v|
        fromUnit = v.lfos[lfoIndex];
        toUnit = toUnitFunc.(v);
        v.modulate(fromUnit, toUnit, param, amt);
      };
    }
  }

  freeMod { |toUnitFunc, param|
    var globalToUnit = true;
    try {
      globalToUnit = toUnitFunc.(voices[0]).isNil;
    };
    if (globalToUnit) {
      var toUnit = toUnitFunc.(globals);
      toUnit.putMod(param, nil);
    } {
      voices.do({ |v|
        var toUnit = toUnitFunc.(v);
        toUnit.putMod(param, nil);
      });
    };
  }

  setModAmt { |toUnitFunc, param, amt|
    var globalToUnit = true;
    try {
      globalToUnit = toUnitFunc.(voices[0]).isNil;
    };
    if (globalToUnit) {
      var toUnit = toUnitFunc.(globals);
      toUnit.modAt(param).set(\amt, amt);
    } {
      voices.do({ |v|
        var toUnit = toUnitFunc.(v);
        toUnit.modAt(param).set(\amt, amt);
      });
    };
  }

  setNotesyn { |...args|
    voices.do(_.setNotesyn(*args));
  }

  putLFO { |index, name, rate = 'control', args = (#[]), global = false, type|
    if (global) {
      globals.putLFO(index, name, rate, args, type);
      voices.do(_.putLFO(index, nil));
    } {
      globals.putLFO(index, nil);
      voices.do(_.putLFO(index, name, rate, args, type));
    };
  }

  setLFO { |index ...args|
    if (globals.lfos[index].notNil) {
      globals.setLFO(index, *args);
    } {
      voices.do(_.setLFO(index, *args));
    };
  }
  setLFOType { |index, type|
    if (globals.lfos[index].notNil) {
      globals.setLFOType(index, type);
    } {
      voices.do(_.setLFOType(index, type));
    };
  }

  putOsc { |index, name, args = (#[]), type|
    voices.do(_.putOsc(index, name, args, type));
  }

  setOsc { |index ...args|
    voices.do(_.setOsc(index, *args));
  }
  setOscType { |index, type|
    voices.do(_.setOscType(index, type));
  }

  putFilt { |index, name, args = (#[]), type|
    voices.do(_.putFilt(index, name, args, type));
  }

  setFilt { |index ...args|
    voices.do(_.setFilt(index, *args));
  }
  setFiltType { |index, type|
    voices.do(_.setFiltType(index, type));
  }

  putAmp { |...args|
    voices.do(_.putAmp(*args));
  }

  setAmp { |...args|
    voices.do(_.setAmp(*args));
  }
  setAmpType { |type|
    voices.do(_.setAmpType(type));
  }

  numVoices_ { |value|
    numVoices = value;
    treeFunc.();
  }

  noteOn { |note = 60, vel = 100|
    var i = 0;
    while { voices[roundRobinIndex].inUse && (i < numVoices) } {
      roundRobinIndex = (roundRobinIndex + 1) % numVoices;
      i = i + 1;
    };
    voices[roundRobinIndex].noteOn(note, vel);
    globals.noteOn(note, vel);
    roundRobinIndex = (roundRobinIndex + 1) % numVoices;
  }

  noteOff { |num|
    globals.noteOff(num);
    voices.do(_.noteOff(num));
  }

  notes {
    var ret = [];
    voices.do { |voice, i|
      if (voice.gate == 1) {
        ret = ret.add([voice.note, voice.vel])
      }
    };
    ^ret;
  }

  notes_ { |notes|
    notes.do { |note|
      this.noteOn(*note);
    }
  }

  bendRange_ { |value|
    globals.bendRange = value;
    voices.do(_.bendRange_(value));
  }

  bend_ { |value|
    globals.bend_(value);
    voices.do(_.bend_(value));
  }

  mod_ { |value|
    globals.mod_(value);
    voices.do(_.mod_(value));
  }

  portamento_ { |value|
    globals.portamento_(value);
    voices.do(_.portamento_(value));
  }

  priority_ { |value|
    var method = [\lastPriority, \firstPriority, \highestPriority, \lowestPriority][value];
    globals.perform(method);
    voices.do(_.perform(method));
  }
}
