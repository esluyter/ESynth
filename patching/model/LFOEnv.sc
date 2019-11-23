LFOEnv : LFOModel {
  classvar <displayName, <types;

  *initClass {
    displayName = "LF Env";
    types = ['gate', 'retrig', 'global retrig'];
  }

  init {
    this.prAddEnvParams;
  }
}
