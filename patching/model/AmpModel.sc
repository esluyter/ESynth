AmpModel : ModuleModel {
  classvar <classes, <envTypes;
  var <envType = 0;
  //classvar <displayName, <types; // define these in subclasses

  *initClass {
    classes = [AmpVCA];
    envTypes = [\sustain, \oneshot, \retrig];
  }

  envTypes {
    ^this.class.envTypes;
  }
}
