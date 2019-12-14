ESModule {
  var <kind, <def, <rate;
  var <list, <params;
  var <type, <envType, <global;
  var patchCords;

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

  def_ { |value, rate = \control, copyParams = false, copyPatchCords = true|
    // TODO: copy patch cords..
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
    this.prMakeParams(copyParams);
    this.prInitPatchCords(copyPatchCords);
    this.type_(0);
    this.envType_(0);
    this.global_(true);
    this.changed(\def, this);
    ^this;
  }

  rate_ { |value|
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
    if (def.isNil) {
      params = [];
      ^this;
    };
    params = def.params.collect { |esparam, i|
      var param = ESMParam(this, esparam);
      if (copyParams) { param.value_(params[i].value) };
      param;
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
    list.changed(\patchCords);
  }

  defs { ^ESynthDef.perform((kind ++ \s).asSymbol) }
  defNames { ^this.defs.keys.asArray.sort }
  displayNames {
    var ret = ['-empty-'];
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
    };
    this.changed(\type, type);
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
    };
    this.changed(\envType, envType);
  }

  global_ { |value|
    if (def.isNil) {
      global = false;
    } {
      if (kind == \lfo) {
        global = value;
      } {
        global = false;
      };
    };
    this.changed(\global, global);
  }

  index { ^list.indexOf(this); }

  patchTo { |module, toInlet = 0|
    if (kind != \lfo) { ^false };
    module.patchFrom(this, toInlet);
  }

  patchFrom { |fromLFO, toInlet = 0|
    toInlet = this.prGetModIndex(toInlet);
    if (fromLFO.isNil) {
      patchCords[toInlet] = nil;
    } {
      if (toInlet < params.size) {
        patchCords[toInlet] = ESMPatchCord(fromLFO, this, toInlet);
      }
    };
    list.changed(\patchCords);
  }

  patchCords {
    ^patchCords.select(_.notNil);
  }
  patchAt { |inlet|
    ^patchCords[this.prGetModIndex(inlet)]
  }

  prGetModIndex { |param|
    if (param.isSymbol) {
      ^params.collect(_.name).indexOf(param) - this.modOffset;
    } {
      ^param;
    };
  }

  modOffset { if (def.notNil) { ^def.modOffset } { ^0 } }
  maxMods { if (def.notNil) { ^def.maxMods } { ^0 } }

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
}
