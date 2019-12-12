ESModule {
  var <kind, <def, <rate;
  var <list;

  *newList { |list, def, rate = \control|
    ^this.new(list.kind, def, rate).initList(list);
  }

  initList { |arglist|
    list = arglist;
  }

  *new { |kind = \lfo, def, rate = \control|
    ^super.newCopyArgs(kind).def_(def, rate, false, false);
  }

  def_ { |value, rate = \control, copyParams = false, copyPatchCords = true|
    // TODO: copy params and patch cords..
    if (value.isNil or: (value == "- empty -")) {
      def = nil;
      this.rate_(nil);
      ^this;
    };
    if (value.class == String) {
      if (value[0..1] == "AR") {
        rate = \audio;
        value = value[3..];
      } {
        if (value[0..1] == "KR") {
          rate = \control;
          value = value[3..];
        };
      };
      value = value.asSymbol;
    };
    if (value.class == Symbol) {
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
    this.rate_(rate);
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

  defs { ^ESynthDef.perform((kind ++ \s).asSymbol) }
  defNames { ^this.defs.keys.asArray.sort }
  displayNames {
    var ret = ["- empty -"];
    this.defNames.do { |defName|
      var def = this.defs[defName];
      def.rates.do { |rate|
        if (rate == 'audio') {
          ret = ret.add(if (kind == \lfo) { "AR " } { "" } ++ def.name);
        } {
          ret = ret.add("LF " ++ def.name);
        }
      };
    };
    ^ret;
  }
  displayName {
    if (def.isNil) { ^"- empty -" };
    if (rate == \control) { ^("LF " ++ def.name) };
    if (kind == \lfo) { ^("AR" ++ def.name) };
    ^def.name;
  }
}
