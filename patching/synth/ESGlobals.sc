ESGlobals {
  var <synthgroup, <group, <notesyn;
  var <modgroup, <routegroup, <lfogroup;
  var <lfos;
  var <note = 60, <bend = 0, <bendRange = 2, <portamento = 0, <notebus, <gatebus, <velbus, <modbus;
  var <noteStack, <notePriorityFunc;

  *new { |synthgroup, numlfos = 20|
    synthgroup = synthgroup ?? Server.default.defaultGroup;
    ^super.newCopyArgs(synthgroup).init(numlfos);
  }

  init { |numlfos = 20|
    noteStack = [];
    lfos = nil ! numlfos;
    # notebus, gatebus, velbus, modbus = { Bus.control(synthgroup.server) } ! 4;
    this.gate_(0);
    this.vel_(100);
    this.mod_(0);
    this.prMakeGroups;
    this.lastPriority;
  }

  prMakeGroups {
    group = Group(synthgroup, \addToHead);
    notesyn = ESUnit.note(0, group, notebus);
    modgroup = Group(group, \addToTail);
    routegroup = Group(group, \addToTail);
    lfogroup = Group(group, \addToTail);
  }

  free {
    [velbus, gatebus, notebus, modbus].do(_.free);
    lfos.do(_.free);
    group.free;
  }

  modulate { |fromUnit, toUnit, param, amt = 0.1|
    ^ESUnit.modUnits(fromUnit, toUnit, param, amt, modgroup);
  }

  route { |fromUnit, toUnit, index = 0, amt = 1|
    ^ESUnit.routeUnits(fromUnit, toUnit, index, amt, routegroup);
  }

  putLFO { |index, name, rate = 'control', args = (#[]), type|
    lfos[index].free;
    lfos[index] = nil;
    if(name.notNil) {
      args = args ++ [notebus: notebus, velbus: velbus, gatebus: gatebus, modbus: modbus];
      lfos[index] = ESUnit.lfo(name, args, lfogroup, rate, type: type);
    };
  }

  setLFO { |index ...args|
    lfos[index].set(*args);
  }
  setLFOType { |index, type|
    lfos[index].type_(type);
  }

  bendRange_ { |value|
    bendRange = value;
    notesyn.set(\bendRange, value);
  }

  bend_ { |value|
    bend = value;
    notesyn.set(\bend, value);
  }

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
