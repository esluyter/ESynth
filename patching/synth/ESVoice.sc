ESVoice {
  var <synthgroup, <group;
  var <modgroup, <lfogroup, <oscgroup, <filtgroup, <ampgroup;
  var <mods, <lfos, <oscs, <filts, <amp;
  var <oscbus, <stbus, <monobus;


  *new { |synthgroup, numlfos = 20, numoscs = 6, numfilts = 4|
    ^super.newCopyArgs(synthgroup).init(numlfos, numoscs, numfilts);
  }

  init { |numlfos, numoscs, numfilts|
    mods = [];
    lfos = nil ! numlfos;
    oscs = nil ! numoscs;
    filts = nil ! numfilts;
    oscbus = Bus.audio(group.server);
    stbus = Bus.audio(group.server, 2);
    monobus = Bus.audio(group.server, 1);
  }

  prMakeGroups {
    group = Group(synthgroup, \addToTail);
    modgroup = Group(group, \addToTail);
    lfogroup = Group(group, \addToTail);
    oscgroup = Group(group, \addToTail);
    filtgroup = Group(group, \addToTail);
    ampgroup = Group(group, \addToTail);
  }

  putOsc { |index, osc|
    oscs[index].free;
    oscs[index] = nil;
    if (osc.notNil) {
      var defname = osc.class.asSymbol.toLower;
      oscs[index] = Synth(defname, [out: oscbus], oscgroup);
    };
  }

  putFilt { |index, filt|

  }
}
