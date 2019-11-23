OscModel : ModuleModel {
  classvar <classes;
  //classvar <displayName, <types; // define these in subclasses

  *initClass {
    classes = [OscNil, OscVCO, OscSuperSaw, OscNoise];
    //["VCO", "SuperSaw", "FM", "Noise"]
  }
}
