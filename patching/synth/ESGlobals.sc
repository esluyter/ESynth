ESGlobals {
  var <synthgroup, <group;
  var <lfogroup, <lfos, <lfobuses, <modgroup, <mods, <modbuses;

  *new { |synthgroup, numlfos = 20|
    ^super.newCopyArgs(synthgroup).init(numlfos);
  }

  init { |numlfos = 20|
    mods = [];
    lfos = nil ! numlfos;
  }

  prMakeGroups {
    group = Group(synthgroup, \addToHead);
    modgroup = Group(group, \addToTail);
    lfogroup = Group(group, \addToTail);
  }
}
