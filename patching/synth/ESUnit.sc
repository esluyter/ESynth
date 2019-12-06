ESUnit {
  var <def, <args, <group,
      <rate, // \audio or \control
      <bus, <freebus = false, <synth;

  *mod { |inbus, amt, group|
    if (inbus.class != Bus) { "inbus must be a Bus".warn; ^false };
    ^this.new(ESynthDef.mod, [in: inbus, amt: amt], group, inbus.rate);
  }

  *lfo { |name, args, group, rate = \control|
    ^this.new(ESynthDef.lfos.at(name), args, group, rate);
  }

  *osc { |name, args, group, bus|
    ^this.new(ESynthDef.oscs.at(name), args, group, \audio, bus);
  }

  *filt { |name, args, group, bus|
    ^this.new(ESynthDef.filts.at(name), args, group, \audio, bus);
  }

  *amp { |name, args, group, bus|
    ^this.new(ESynthDef.amps.at(name), args, group, \audio, bus);
  }

  *new { |def, args, group, rate = \audio, bus|
    group = group ?? Server.default.defaultGroup;
    if (bus.notNil) { bus = bus.asBus(rate) };
    ^super.newCopyArgs(def, args.asArray, group, rate, bus).init;
  }

  init {
    if (bus.isNil) {
      freebus = true;
      bus = Bus.alloc(rate, group.server, def.numChannels(rate));
    };
    synth = Synth(this.defName, args ++ [out: bus], group);
  }

  defName {
    ^if (rate == 'audio') { def.arDefName } { def.krDefName };
  }

  free {
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
}
