LFOModel : ModuleModel {
  classvar <classes;
  //classvar <displayName, <types; // define these in subclasses
  var <rate = \kr, <polarity = \bi;

  *initClass {
    classes = [LFONil, LFOSin, LFOTri, LFOSqr, LFONoise, LFOEnv, LFOARSin];
    //["LF Sin", "LF Tri", "LF Saw", "LF RevSaw", "LF Sqr", "LF Noise", "LF Env", "AR Sin", "AR Tri", "AR Saw", "AR RevSaw", "AR Sqr", "AR Noise", "AR Env"]
  }

  patchTo { |module, inlet|
    module.patchFrom(this, inlet);
  }
}
