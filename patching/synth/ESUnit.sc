ESUnit {
  var <def, <args, <group,
      <rate, // \audio or \control
      <bus, <freebus = false, <synth, <mods, <modOffset, <outputMods, <fromUnit, <toUnit, <paramindex;

  *mod { |inbus, amt = 0, group, bus|
    if (inbus.class != Bus) { "inbus must be a Bus".warn; ^false };
    ^this.new(ESynthDef.mod, [in: inbus, amt: amt], group, inbus.rate, bus, 1);
  }

  *modUnits { |fromUnit, toUnit, param, amt = 0, group|
    var paramindex = toUnit.getParamIndex(param);
    if (paramindex.isNil) {
      "Must supply valid parameter (int or symbol)".warn;
      ^nil;
    };
    ^this.new(ESynthDef.mod, [in: fromUnit.bus, amt: amt], group, fromUnit.rate, nil, 1).initMod(fromUnit, toUnit, paramindex);
  }

  *lfo { |name, args, group, rate = \control, bus|
    ^this.new(ESynthDef.lfos.at(name), args, group, rate, bus, 5);
  }

  *osc { |name, args, group, bus|
    ^this.new(ESynthDef.oscs.at(name), args, group, \audio, bus, 8);
  }

  *filt { |name, args, group, bus|
    ^this.new(ESynthDef.filts.at(name), args, group, \audio, bus, 8, 3);
  }

  *amp { |name, args, group, bus|
    ^this.new(ESynthDef.amps.at(name), args, group, \audio, bus, 6, 3);
  }

  *new { |def, args, group, rate = \audio, bus, maxmods = 8, modOffset = 0|
    var addAction = \addToHead;
    if (group.isArray) {
      addAction = group[1];
      group = group[0];
    };
    group = group ?? Server.default.defaultGroup;
    if (bus.notNil) { bus = bus.asBus(rate) };
    ^super.newCopyArgs(def, args.asArray, group, rate, bus).init(addAction, maxmods, modOffset);
  }

  init { |addAction, maxmods, offset|
    modOffset = offset;
    mods = nil ! maxmods;
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
    if (def.type == \mod) {
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

  getParamIndex { |param|
    if (param.isSymbol) {
      ^this.params.collect(_.name).indexOf(param) - modOffset;
    } {
      ^param;
    };
  }

  params { ^def.params; }

  putMod { |param, mod|
    var paramindex = this.getParamIndex(param);
    mods[paramindex].free;
    if (mod.notNil) {
      mods[paramindex] = mod;
      synth.set(def.params[paramindex + modOffset].modName, mod.bus.asMap);
    } {
      this.removeMod(paramindex);
    };
  }

  removeMod { |param|
    // does not free mod
    var paramindex = this.getParamIndex(param);
    mods[paramindex] = nil;
    synth.set(def.params[paramindex].modName, 0);
  }

  modAt { |param|
    ^mods[this.getParamIndex(param)];
  }

  addOutputMod { |mod|
    outputMods = outputMods.add(mod);
  }

  removeOutputMod { |mod|
    outputMods.removeAt(outputMods.indexOf(mod));
  }
}
