ESUnit {
  var <def, <args, <group,
      <rate, // \audio or \control
      <bus, <freebus = false, <synth, <mods, <outputMods, <fromUnit, <toUnit, <paramindex;

  *note { |portamento = 0, group, bus|
    ^this.new(ESynthDef.note, [portamento: 0, note: 60, bend: 0], group, \control, bus);
  }

  *mod { |inbus, amt = 0, group, bus|
    if (inbus.class != Bus) { "inbus must be a Bus".warn; ^false };
    ^this.new(ESynthDef.mod, [in: inbus, amt: amt], group, inbus.rate, bus);
  }

  *modUnits { |fromUnit, toUnit, param, amt = 0, group|
    var paramindex = toUnit.getParamModIndex(param);
    if (paramindex.isNil) {
      "Must supply valid parameter (int or symbol)".warn;
      ^nil;
    };
    ^this.new(ESynthDef.mod, [in: fromUnit.bus, amt: amt], group, fromUnit.rate, nil, 1).initMod(fromUnit, toUnit, paramindex);
  }

  *lfo { |name, args, group, rate = \control, bus|
    var def;
    if (name.class == ESynthDef) {
      def = name;
    } {
      def = ESynthDef.lfos.at(name);
    }
    ^this.new(def, args, group, rate, bus);
  }

  *osc { |name, args, group, bus|
    var def;
    if (name.class == ESynthDef) {
      def = name;
    } {
      def = ESynthDef.oscs.at(name);
    }
    ^this.new(def, args, group, \audio, bus);
  }

  *filt { |name, args, group, bus|
    var def;
    if (name.class == ESynthDef) {
      def = name;
    } {
      def = ESynthDef.filts.at(name);
    }
    ^this.new(def, args, group, \audio, bus);
  }

  *amp { |name, args, group, bus|
    var def;
    if (name.class == ESynthDef) {
      def = name;
    } {
      def = ESynthDef.amps.at(name);
    }
    ^this.new(def, args, group, \audio, bus);
  }

  *new { |def, args, group, rate = \audio, bus|
    var addAction = \addToHead;
    if (group.isArray) {
      addAction = group[1];
      group = group[0];
    };
    group = group ?? Server.default.defaultGroup;
    if (bus.notNil) { bus = bus.asBus(rate) };
    ^super.newCopyArgs(def, args.asArray, group, rate, bus).init(addAction);
  }

  init { |addAction, maxmods, offset|
    mods = nil ! def.maxMods;
    outputMods = [];
    if (bus.isNil) {
      freebus = true;
      bus = Bus.alloc(rate, group.server, def.numChannels(rate));
    };
    synth = Synth(this.defName, args ++ [out: bus], group, addAction);
  }

  initMod { |from, to, index|
    fromUnit = from;
    toUnit = to;
    paramindex = index;
    toUnit.putMod(index, this);
    fromUnit.addOutputMod(this);
  }

  defName {
    ^if (rate == 'audio') { def.arDefName } { def.krDefName };
  }

  free {
    if ((def.type == \mod) and: (toUnit.notNil)) {
      toUnit.removeMod(paramindex);
      fromUnit.removeOutputMod(this);
    };
    mods.do(_.free);
    outputMods.do(_.free);
    if (synth.isNil) {
      (this.class.name ++ " has already been freed").warn;
      ^this
    };
    if (freebus) {
      bus.free;
    };
    synth.free;
    synth = nil;
  }

  bus_ { |value|
    if (freebus) {
      bus.free;
      freebus = false;
    };
    bus = value.asBus(rate);
    synth.set(\out, bus);
  }

  set { |... args|
    synth.set(*args);
  }

  getParamModIndex { |param|
    if (param.isSymbol) {
      ^this.params.collect(_.name).indexOf(param) - def.modOffset;
    } {
      ^param;
    };
  }

  params { ^def.params; }

  putMod { |param, mod|
    var paramindex = this.getParamModIndex(param);
    mods[paramindex].free;
    if (mod.notNil) {
      mods[paramindex] = mod;
      synth.set(def.params[paramindex + def.modOffset].modName, mod.bus.asMap);
    } {
      this.removeMod(paramindex);
    };
  }

  removeMod { |param|
    // does not free mod
    var paramindex = this.getParamModIndex(param);
    mods[paramindex] = nil;
    synth.set(def.params[paramindex + def.modOffset].modName, 0);
  }

  modAt { |param|
    ^mods[this.getParamModIndex(param)];
  }

  addOutputMod { |mod|
    outputMods = outputMods.add(mod);
  }

  removeOutputMod { |mod|
    outputMods.removeAt(outputMods.indexOf(mod));
  }
}
