ESVoice {
  var <synthgroup, <group, <notesyn;
  var <modgroup, <routegroup, <busingroup, <busoutgroup, <lfogroup, <oscgroup, <filtgroups;
  var <lfos, <oscs, <filtmods, <filts, <amp;
  var <oscbuses, <ampbuses;
  var <note = 60, <bendRange = 2, <bend = 0, <portamento = 0, <notebus, <gatebus, <velbus, <modbus;
  var <noteStack, <notePriorityFunc;

  // todo: do we still need multiple filtgroups?


  *new { |synthgroup, numlfos = 20, numoscs = 6, numfilts = 4|
    synthgroup = synthgroup ?? Server.default.defaultGroup;
    ^super.newCopyArgs(synthgroup).init(numlfos, numoscs, numfilts);
  }

  init { |numlfos, numoscs, numfilts|
    noteStack = [];
    lfos = nil ! numlfos;
    oscs = nil ! numoscs;
    filts = nil ! numfilts;
    # notebus, gatebus, velbus, modbus = { Bus.control(synthgroup.server) } ! 4;
    this.gate_(0);
    this.vel_(100);
    this.mod_(0);
    oscbuses = { ESBus.audio(synthgroup.server) } ! 4;
    ampbuses = { ESBus.audio(synthgroup.server) } ! 3;
    this.prMakeGroups(numfilts);
    this.lastPriority;
  }

  prMakeGroups { |numfilts|
    group = Group(synthgroup, \addToTail);
    notesyn = ESUnit.note(0, group, notebus);
    modgroup = Group(group, \addToTail);
    routegroup = Group(group, \addToTail);
    busingroup = Group(routegroup, \addToHead);
    busoutgroup = Group(routegroup, \addToTail);
    lfogroup = Group(group, \addToTail);
    oscgroup = Group(group, \addToTail);
    filtgroups = { Group(group, \addToTail) }.dup(numfilts/2);
    this.putAmp(\VCA);
  }

  free {
    ([velbus, gatebus, notebus, modbus] ++ oscbuses ++ ampbuses).do(_.free);
    amp.free;
    filtmods.do(_.free);
    lfos.do(_.free);
    oscs.do(_.free);
    filts.do(_.free);
    group.free;
  }

  modulate { |fromUnit, toUnit, param, amt = 0.1|
    ^ESUnit.modUnits(fromUnit, toUnit, param, amt, modgroup);
  }

  route { |fromUnit, toUnit, index = 0, amt = 1|
    var grp = routegroup;
    if (toUnit.isKindOf(Bus)) { grp = busingroup };
    if (fromUnit.isKindOf(Bus)) { grp = busoutgroup };
    ^ESUnit.routeUnits(fromUnit, toUnit, index, amt, grp);
  }

  putLFO { |index, name, rate = 'control', args = (#[]), type|
    lfos[index].free;
    lfos[index] = nil;
    if(name.notNil) {
      args = args ++ [notebus: notebus, velbus: velbus, gatebus: gatebus, modbus: modbus];
      lfos[index] = ESUnit.lfo(name, args, lfogroup, rate, type: type);
    };
  }

  setNotesyn { |...args|
    notesyn.set(*args);
  }

  setLFO { |index ...args|
    lfos[index].set(*args);
  }
  setLFOType { |index, type|
    lfos[index].type_(type);
  }

  putOsc { |index, name, args = (#[]), type|
    oscs[index].free;
    if (name.notNil) {
      args = args ++ [notebus: notebus, velbus: velbus, gatebus: gatebus];
      oscs[index] = ESUnit.osc(name, args, oscgroup, type: type)
    } {
      oscs[index] = ESUnit.nilfilt(oscgroup);
    };
  }

  setOsc { |index ...args|
    oscs[index].set(*args);
  }
  setOscType { |index, type|
    oscs[index].type_(type);
  }

  putFilt { |index, name, args = (#[]), type|
    filts[index].free;
    if (name.notNil) {
      args = args ++ [notebus: notebus, velbus: velbus, gatebus: gatebus];
      filts[index] = ESUnit.filt(name, args, filtgroups[(index / 2).floor], type: type);
    } {
      filts[index] = ESUnit.nilfilt(filtgroups[(index / 2).floor]); // todo: necessary??
    };
  }

  setFilt { |index ...args|
    filts[index].set(*args);
  }
  setFiltType { |index, type|
    filts[index].type_(type);
  }

  putAmp { |name, args = (#[]), type|
    amp.free;
    amp = nil;
    if (name.notNil) {
      args = args ++ [inleft: ampbuses[0], inmono: ampbuses[1], inright: ampbuses[2], notebus: notebus, velbus: velbus, gatebus: gatebus];
      amp = ESUnit.amp(name, args, [group, \addToTail], 0, type: type);
    };
  }

  setAmp { |...args|
    amp.set(*args);
  }
  setAmpType { |type|
    amp.type_(type);
  }

  amps { ^[amp] }

  bendRange_ { |value|
    bendRange = value;
    notesyn.set(\bendRange, value);
  }

  bend_ { |value|
    bend = value;
    notesyn.set(\bend, value);
  }

  notesyns { ^[notesyn] }

  note_ { |value|
    note = value;
    notesyn.set(\note, value);
  }

  portamento_{ |value|
    portamento = value;
    notesyn.set(\portamento, value);
  }

  vel { ^velbus.getSynchronous }

  vel_ { |value|
    velbus.set(value.linlin(0, 127, 0.0, 1.0));
  }

  gate { ^gatebus.getSynchronous }

  gate_ { |value|
    gatebus.set(value);
  }

  mod { ^modbus.getSynchronous }

  mod_ { |value|
    modbus.set(value);
  }

  monobus { ^ampbuses[1] }
  leftbus { ^ampbuses[0] }
  rightbus { ^ampbuses[2] }

  firstPriority {
    notePriorityFunc = { |stack| stack.first };
  }

  lastPriority {
    notePriorityFunc = { |stack| stack.last };
  }

  highestPriority {
    notePriorityFunc = { |stack| stack.maxItem };
  }

  lowestPriority {
    notePriorityFunc = { |stack| stack.minItem };
  }

  noteOn { |note = 60, vel = 100|
    noteStack = noteStack.add(note);
    this.note_(notePriorityFunc.(noteStack));
    this.vel_(vel);
    this.gate_(1);
  }

  noteOff { |note|
    noteStack.remove(note);
    if (noteStack.isEmpty) {
      this.gate_(0);
    } {
      this.note_(notePriorityFunc.(noteStack));
    };
  }

  inUse { ^noteStack.isEmpty.not }
}
