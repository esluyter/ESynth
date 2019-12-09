ESGlobals {
  var <synthgroup, <group;
  var <modgroup, <lfogroup;
  var <lfos;
  var <note, <bend = 0, <notebus, <gatebus, <velbus, <modbus;
  var <noteStack, <notePriorityFunc;

  *new { |synthgroup, numlfos = 20|
    synthgroup = synthgroup ?? Server.default.defaultGroup;
    ^super.newCopyArgs(synthgroup).init(numlfos);
  }

  init { |numlfos = 20|
    noteStack = [];
    lfos = nil ! numlfos;
    # notebus, gatebus, velbus, modbus = { Bus.control(synthgroup.server) } ! 4;
    this.note_(60);
    this.gate_(0);
    this.vel_(100);
    this.mod_(0);
    this.prMakeGroups;
    this.lastPriority;
  }

  prMakeGroups {
    group = Group(synthgroup, \addToHead);
    modgroup = Group(group, \addToTail);
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

  putLFO { |index, name, rate = 'control', args = (#[])|
    lfos[index].free;
    lfos[index] = nil;
    if(name.notNil) {
      args = args ++ [notebus: notebus, velbus: velbus, gatebus: gatebus, modbus: modbus];
      lfos[index] = ESUnit.lfo(name, args, lfogroup, rate);
    };
  }

  setLFO { |index ...args|
    lfos[index].set(*args);
  }

  bend_ { |value|
    bend = value;
    notebus.set(note + bend);
  }

  note_ { |value|
    note = value;
    notebus.set(note + bend);
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
