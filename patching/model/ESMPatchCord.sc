ESMPatchCord {
  var <fromLFO, <toModule, <toInlet, <params, patchCords, <color, <kind = \patchCord, <index;
  var connection;

  *new { |fromLFO, toModule, toInlet, amt = 0, color|
    ^super.newCopyArgs(fromLFO, toModule, toInlet).init(amt, color);
  }

  init { |amt, argcolor|
    params = [ESMParam(this, ESynthDef.mod.params[0])];
    color = argcolor ?? Color.rand;
    patchCords = [nil];

    connection = params[0].cv.connectTo { this.rootModule.changed(\patchAmt, this) };
  }

  free {
    connection.free;
  }

  amt { ^params[0].value }
  amt_ { |value|
    params[0].value_(value);
    ^this;
  }

  fromIndex { ^fromLFO.index }
  toIndex {
    if (toModule.class == ESModule) {
      ^toModule.index;
    } {
      ^nil;
    }
  }

  patchFrom { |fromLFO|
    if (fromLFO.isNil) {
      patchCords[0] = nil;
    } {
      patchCords[0] = ESMPatchCord(fromLFO, this, 0);
    };
    {
      var rootmodule = this.rootModule;
      0.01.wait;
      rootmodule.changed(\patchCords);
    }.fork(AppClock);
  }

  rootModule { ^toModule.rootModule }
  depth { ^(toModule.depth + 1) }
  toDepth { ^toModule.depth }
  rootToInlet {
    var tm = this;
    while { tm.toModule.class != ESModule } {
      tm = tm.toModule;
    };
    ^tm.toInlet;
  }

  list { ^toModule.list }

  patchCords {
    ^patchCords.select(_.notNil);
  }
  patchAt { |inlet|
    if ((inlet == 0) or: (inlet == \amt)) {
      ^patchCords[0];
    } {
      ^nil;
    };
  }

  def { ^ESynthDef.mod }
}
