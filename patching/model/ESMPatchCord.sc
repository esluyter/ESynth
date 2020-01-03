ESMPatchCord {
  var <fromLFO, <toModule, <toInlet, <params, patchCords, <color, <kind = \patchCord, <index;

  *new { |fromLFO, toModule, toInlet, amt = 0, color|
    ^super.newCopyArgs(fromLFO, toModule, toInlet).init(amt, color);
  }

  init { |amt, argcolor|
    params = [ESMParam(this, ESynthDef.mod.params[0])];
    color = argcolor ?? Color.rand;
    patchCords = [nil];
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
      var rootmodule = this;
      0.01.wait;
      while { rootmodule.class != ESModule } {
        rootmodule = rootmodule.toModule;
      };
      rootmodule.changed(\patchCords);
    }.fork(AppClock);
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
