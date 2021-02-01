ESUnit {
  classvar <routeIndices = #[\in, \chain];

  var <def, <args, <group,
      <rate, // \audio or \control
      <bus, <type, <addAction, <freebus = false, <synth,
      <mods, <routes, <outputMods, <outputRoutes, <fromUnit, <toUnit, <paramindex,
      <isMod = false, <isRoute = false;

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
    ^this.new(ESynthDef.mod, [in: fromUnit.bus, amt: amt], group, fromUnit.rate, nil).initMod(fromUnit, toUnit, paramindex);
  }

  *routeUnits { |fromUnit, toUnit, index = 0, amt = 1, group|
    // note: this accepts buses as well as units both to and from
    ^this.new(ESynthDef.mod, [in: fromUnit.asBus, amt: amt], group, \audio, if (toUnit.isKindOf(Bus)) { toUnit } { nil }).initRoute(fromUnit, toUnit, index);
  }

  *lfo { |name, args, group, rate = \control, bus, type|
    var def;
    if (name.class == ESynthDef) {
      def = name;
    } {
      def = ESynthDef.lfos.at(name);
    }
    ^this.new(def, args, group, rate, bus, type);
  }

  *osc { |name, args, group, bus, type|
    var def;
    if (name.class == ESynthDef) {
      def = name;
    } {
      def = ESynthDef.oscs.at(name);
    }
    ^this.new(def, args, group, \audio, bus, type);
  }

  *filt { |name, args, group, bus, type|
    var def;
    if (name.class == ESynthDef) {
      def = name;
    } {
      def = ESynthDef.filts.at(name);
    }
    ^this.new(def, args, group, \audio, bus, type);
  }

  *nilfilt { |group, bus|
    ^this.new(ESynthDef.nilfilt, nil, group, \audio, bus);
  }

  *amp { |name, args, group, bus, type|
    var def;
    if (name.class == ESynthDef) {
      def = name;
    } {
      def = ESynthDef.amps.at(name);
    }
    ^this.new(def, args, group, \audio, bus, type);
  }

  *new { |def, args, group, rate = \audio, bus, type|
    var addAction = \addToHead;
    type = type ? 0;
    if (group.isArray) {
      addAction = group[1];
      group = group[0];
    };
    group = group ?? Server.default.defaultGroup;
    if (bus.notNil) { bus = bus.asBus(rate) };
    ^super.newCopyArgs(def, args.asArray, group, rate, bus, type, addAction).init;
  }

  init {
    mods = nil ! def.maxMods;
    routes = nil ! def.maxRoutes;
    outputMods = [];
    outputRoutes = [];
    if (bus.isNil) {
      freebus = true;
      bus = Bus.alloc(rate, group.server, def.numChannels(rate));
    };
    this.prMakeSynth;
  }

  prMakeSynth {
    synth.free;
    synth = Synth(this.defName, args ++ [out: bus], group, addAction);
    mods.do { |mod, i|
      if (mod.notNil) {
        this.prSetMod(i, mod);
      };
    };
    routes.do { |mod, i|
      if (mod.notNil) {
        this.prSetRoute(i, mod);
      };
    };
  }

  initMod { |from, to, index|
    fromUnit = from; // member variables
    toUnit = to;
    paramindex = index;
    isMod = true;
    toUnit.putMod(index, this);
    fromUnit.addOutputMod(this);
  }

  initRoute { |from, to, index|
    fromUnit = from; // member variables
    toUnit = to;
    paramindex = index;
    isRoute = true;
    if (toUnit.isKindOf(Bus).not) {
      toUnit.putRoute(index, this);
    } {
      toUnit.addInputRoute(this);
    };
    fromUnit.addOutputRoute(this);
  }

  defName {
    ^if (rate == 'audio') { def.arDefName(type) } { def.krDefName(type) };
  }

  free {
    if (isMod and: (toUnit.notNil)) {
      toUnit.removeMod(paramindex);
      fromUnit.removeOutputMod(this);
    };
    if (isRoute) {
      if (toUnit.isKindOf(ESUnit)) {
        toUnit.removeRoute(paramindex);
      } {
        toUnit.removeInputRoute(this);
      };
      fromUnit.removeOutputRoute(this);
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

  asBus { ^bus }

  type_ { |value|
    type = value;
    //synth.set(\type, value);
    this.prMakeSynth;
  }

  set { |... newArgs|
    synth.set(*newArgs);
    args = merge(args.asDict, newArgs.asDict, { |old, new| new }).asPairs;
  }

  getParamModIndex { |param|
    if (param.class == Symbol) {
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
      this.prSetMod(paramindex, mod);
    } {
      this.removeMod(paramindex);
    };
  }
  prSetMod { |paramindex, mod|
    synth.set(def.params[paramindex + def.modOffset].modName, mod.bus.asMap);
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

  putRoute { |index, mod|
    routes[index].free;
    if (mod.notNil) {
      routes[index] = mod;
      this.prSetRoute(index, mod);
    } {
      this.removeRoute(index);
    };
  }
  prSetRoute { |index, mod|
    synth.set(ESUnit.routeIndices[index], mod.bus.asMap);
  }

  removeRoute { |index|
    // does not free mod
    routes[index] = nil;
    synth.set(ESUnit.routeIndices[index], 0);
  }

  addOutputRoute { |mod|
    outputRoutes = outputRoutes.add(mod);
  }

  removeOutputRoute { |mod|
    outputRoutes.removeAt(outputRoutes.indexOf(mod));
  }
}
