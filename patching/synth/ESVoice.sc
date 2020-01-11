ESVoice {
  var <synthgroup, <group, <notesyn;
  var <modgroup, <lfogroup, <oscgroup, <filtgroups;
  var <lfos, <oscs, <filtmods, <filts, <amp;
  var <oscbus, <stbus, <monobus;
  var <note = 60, <bend = 0, <portamento = 0, <notebus, <gatebus, <velbus, <modbus;
  var <noteStack, <notePriorityFunc;


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
    oscbus = Bus.audio(synthgroup.server);
    stbus = Bus.audio(synthgroup.server, 2);
    monobus = Bus.audio(synthgroup.server, 1);
    this.prMakeGroups(numfilts);
    this.lastPriority;
  }

  prMakeGroups { |numfilts|
    group = Group(synthgroup, \addToTail);
    notesyn = ESUnit.note(0, group, notebus);
    modgroup = Group(group, \addToTail);
    lfogroup = Group(group, \addToTail);
    oscgroup = Group(group, \addToTail);
    filtmods = [
      ESUnit.mod(oscbus, 1, [group, \addToTail], monobus),
      ESUnit.mod(oscbus, 0, [group, \addToTail], stbus.subBus(0)),
      ESUnit.mod(oscbus, 0, [group, \addToTail], stbus.subBus(1))
    ];
    filtgroups = { Group(group, \addToTail) }.dup(numfilts/2);
    this.putAmp(\VCA);
  }

  free {
    [monobus, stbus, oscbus, velbus, gatebus, notebus, modbus].do(_.free);
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

  putOsc { |index, name, args = (#[])|
    oscs[index].free;
    oscs[index] = nil;
    if (name.notNil) {
      args = args ++ [notebus: notebus, velbus: velbus];
      oscs[index] = ESUnit.osc(name, args, oscgroup, oscbus)
    };
  }

  setOsc { |index ...args|
    oscs[index].set(*args);
  }

  putFilt { |index, name, args = (#[])|
    filts[index].free;
    filts[index] = nil;
    if (name.notNil) {
      args = args ++ [notebus: notebus, velbus: velbus, gatebus: gatebus];
      filts[index] = ESUnit.filt(name, args, filtgroups[(index / 2).floor], monobus);
    };
    this.prCheckFilts;
  }

  prCheckFilts {
    var oddFilts = [], evenFilts = [];
    filts.pairsDo { |a, b|
      if (a.notNil) {
        oddFilts = oddFilts.add(a);
      };
      if (b.notNil) {
        evenFilts = evenFilts.add(b);
      };
    };
    if ((oddFilts.size == 0) or: (evenFilts.size == 0)) {
      filtmods[0].set(\amt, 1);
      filtmods[1].set(\amt, 0);
      filtmods[2].set(\amt, 0);
      filts.do(_.set(\out, monobus));
    } {
      filtmods[0].set(\amt, 0);
      filtmods[1].set(\amt, 1);
      filtmods[2].set(\amt, 1);
      oddFilts.do(_.set(\out, stbus.subBus(0)));
      evenFilts.do(_.set(\out, stbus.subBus(1)));
    };
  }

  setFilt { |index ...args|
    filts[index].set(*args);
  }

  putAmp { |name, args = (#[])|
    amp.free;
    amp = nil;
    if (name.notNil) {
      args = args ++ [inmono: monobus, instereo: stbus, notebus: notebus, velbus: velbus, gatebus: gatebus];
      amp = ESUnit.amp(name, args, [group, \addToTail], 0);
    };
  }

  setAmp { |...args|
    amp.set(*args);
  }

  amps { ^[amp] }

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
