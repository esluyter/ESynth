ESModule {
  var <kind, <def, <rate;
  var <list, <params;
  var <type, <envType, <global;
  var patchCords, connections;

  *newList { |list, def, rate = \control|
    ^this.new(list.kind, def, rate).initList(list);
  }

  initList { |arglist|
    list = arglist;
  }

  *new { |kind = \lfo, def, rate = \control|
    ^super.newCopyArgs(kind).def_(def, rate, false, false);
  }

  defInput_ { |value|
    this.def_(this.displayNames[value]);
  }

  def_ { |value, rate = \control, copyParams = false, copyPatchCords = true, global = true|
    if (value.isNil or: (value == '-empty-')) {
      def = nil;
      rate = nil;
    } {
      if (value.class == Symbol) {
        value = value.asString;
        if (value[0..1] == "AR") {
          rate = \audio;
          value = value[3..];
        } {
          if (value[0..1] == "LF") {
            rate = \control;
            value = value[3..];
          };
        };
        value = value.asSymbol;
        value = ESynthDef.perform((kind ++ \s).asSymbol).at(value);
      };
      if (value.class != ESynthDef) {
        "def must be nil, Symbol or instance of ESynthDef".warn;
        ^false;
      };
      if (value.kind != kind) {
        "def must be same kind as me!".warn;
        ^false;
      };
      def = value;
    };
    this.rate_(rate);
    this.global_(global, false);
    this.changed(\defName, if (def.notNil) { def.name } { nil });
    this.prMakeParams(copyParams);
    this.prInitPatchCords(copyPatchCords);
    this.type_(0);
    this.envType_(0);
    this.changed(\def, this);
    ^this;
  }

  rate_ { |value|
    // TODO: figure out changed notification here...
    if (def.isNil) {
      rate = nil;
      ^this;
    };
    if (def.rates.indexOf(value).notNil) {
      rate = value;
    } {
      rate = def.rates[0];
    };
    ^this;
  }

  prMakeParams { |copyParams = false|
    connections.free;
    if (def.isNil) {
      params = [];
      ^this;
    };
    connections = ConnectionList.make {
      params = def.params.collect { |esparam, i|
        var param = ESMParam(this, esparam);
        param.cv.signal(\value).connectTo({
          this.changed(\param, param)
        });
        if (copyParams) {
          param.value_(params[i].value)
        };
        param;
      };
    };
  }
  prInitPatchCords { |copyPatchCords = false|
    var oldPatchCords = patchCords;
    var numPatchCords = min(this.maxMods, (params.size - this.modOffset));
    patchCords = nil.dup(numPatchCords);
    if (copyPatchCords) {
      min(oldPatchCords.size, numPatchCords).do { |i|
        patchCords[i] = oldPatchCords[i];
      };
    };
    this.changed(\patchCords);
  }

  rootModule { ^this }
  depth { ^0 }

  defs { ^ESynthDef.perform((kind ++ \s).asSymbol) }
  defNames { ^this.defs.keys.asArray.sort }
  displayNames {
    var ret = if (kind == \amp) { [] } { ['-empty-'] };
    this.defNames.do { |defName|
      var def = this.defs[defName];
      def.rates.do { |rate|
        if (rate == 'audio') {
          ret = ret.add((if (kind == \lfo) { "AR " } { "" } ++ def.name).asSymbol);
        } {
          ret = ret.add(("LF " ++ def.name).asSymbol);
        }
      };
    };
    ^ret;
  }
  displayName {
    if (def.isNil) { ^'-empty-' };
    if (rate == \control) { ^("LF " ++ def.name).asSymbol };
    if (kind == \lfo) { ^("AR " ++ def.name).asSymbol };
    ^def.name.asSymbol;
  }

  types {
    if (def.isNil) { ^[] };
    ^def.typelist;
  }

  type_ { |value|
    if (this.types.size == 0) {
      type = nil;
    } {
      type = value.clip(0, this.types.size - 1);
      this.changed(\type, type);
    };
  }

  envTypes {
    if (def.isNil or: ((kind != \amp) and: (kind != \filt))) { ^[] };
    ^[\sustain, \oneshot, \retrig];
  }

  envType_ { |value|
    if (this.envTypes.size == 0) {
      envType = nil;
    } {
      envType = value.clip(0, this.envTypes.size - 1);
      this.changed(\envType, envType);
    };
  }

  global_ { |value, notify = true|
    // TODO: figure out changed notification here...
    if (def.isNil) {
      global = false;
    } {
      if (kind == \lfo) {
        global = value;
        if (notify) {
          this.changed(\global, global);
        };
      } {
        global = false;
      };
    };
  }

  index { ^list.indexOf(this); }

  patchTo { |module, toInlet = 0, amt = 0|
    if (kind != \lfo) { ^false };
    module.patchFrom(this, toInlet, amt);
  }

  patchFrom { |fromLFO, toInlet = 0, amt = 0|
    toInlet = this.prGetModIndex(toInlet);
    if (fromLFO.isNil) {
      patchCords[toInlet] = nil;
    } {
      if (toInlet < params.size) {
        patchCords[toInlet] = ESMPatchCord(fromLFO, this, toInlet, amt);
      }
    };
    this.changed(\patchCords);
  }

  patchCords {
    ^patchCords.select(_.notNil);
  }
  patchAt { |inlet|
    ^patchCords[this.prGetModIndex(inlet)]
  }

  prGetModIndex { |param|
    if (param.class == Symbol) {
      ^params.collect(_.name).indexOf(param) - this.modOffset;
    } {
      ^param;
    };
  }

  modOffset { if (def.notNil) { ^def.modOffset } { ^0 } }
  maxMods { if (def.notNil) { ^def.maxMods } { ^0 } }

  argList {
    var pairs = params.collect { |param| [param.name, param.value] };
    if (envType.notNil) { pairs = pairs.add([\envType, envType]) };
    if (type.notNil) { pairs = pairs.add([\type, type]) };
    ^pairs.flat;
  }

  // to forward getters and setters of param values
  doesNotUnderstand { |selector ... args|
    params.do { |param|
      if (param.name == selector) {
        ^param.value;
      };
      if ((param.name ++ '_').asSymbol == selector) {
        param.value_(args[0]);
        ^this;
      };
    };
    DoesNotUnderstandError(this, selector, args).throw;
  }

  params_ { |paramArr|
    paramArr.do { |val, i|
      params[i].value_(val);
    }
  }

  printOn { | stream |
    stream << "ESModule<" << if (def.notNil) { def.name } { "nil" } << ">";
  }

  asEvent {
    if (def.isNil) { ^nil };
    ^(
      def: def.name,
      rate: rate,
      params: params.collect(_.value),
      type: type,
      envType: envType,
      global: global
    )
  }
}
