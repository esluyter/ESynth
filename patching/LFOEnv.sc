LFOEnv : LFOModel {
  classvar <displayName, <types;

  *initClass {
    displayName = "LF Env";
    types = [\sustain, \oneshot, \retrig];
  }

  init {
    this.prAddEnvParams;
  }
}
