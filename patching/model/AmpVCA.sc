AmpVCA : AmpModel {
  classvar <displayName, <types;

  *initClass {
    displayName = "VCA";
    types = [];
  }

  init {
    this.prAddParam('key', \amp, 0.005);
    this.prAddParam('vel', \amp, 0.005);
    this.prAddParam('env', \amp, 0.005);
    this.prAddParam('pan', [-1, 1, \lin, 0.0, 0], centered: true);
    this.prAddEnvParams;
  }
}
