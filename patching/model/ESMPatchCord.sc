ESMPatchCord {
  var <fromLFO, <toModule, <toInlet, <params, <color;

  *new { |fromLFO, toModule, toInlet, amt = 0, color|
    ^super.newCopyArgs(fromLFO, toModule, toInlet).init(amt, color);
  }

  init { |amt, argcolor|
    params = [ESMParam(this, ESynthDef.mod.params[0])];
    color = argcolor ?? Color.rand;
  }

  amt { ^params[0].value }
  amt_ { |value|
    params[0].value_(value);
    ^this;
  }

  fromList { ^fromLFO.list }
  fromIndex { ^fromLFO.index }
  toList { ^toModule.list } // this needs work
  toIndex { ^toModule.index } // also needs work

  list { "Work on ESMPatchCord toList functionality".warn; ^nil }
  index { "Work on ESMPatchCord toIndex functionality".warn; ^nil }
  def { ^ESynthDef.mod }
}
