FiltModel : ModuleModel {
  classvar <classes, <envTypes;
  var <envType = 0;
  //classvar <displayName, <types; // define these in subclasses

  *initClass {
    classes = [FiltNil, FiltHouvilainen];
    envTypes = [\sustain, \oneshot, \retrig];
    //["Houvilainen", "RLPF", "SVF", "DFM1", "BLowPass"]
  }

  envTypes {
    ^this.class.envTypes;
  }
}
