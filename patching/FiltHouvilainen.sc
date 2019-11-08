FiltHouvilainen : FiltModel {
  classvar <displayName, <types;

  *initClass {
    displayName = "Houvilainen";
    types = ['bypass', 'LP 24db', 'LP 18db', 'LP 12db', 'LP 6db', 'HP 24db', 'BP 24db', 'N 24db'];
  }

  init {
    this.prAddParam('key', \amp, 0.005);
    this.prAddParam('vel', \amp, 0.005);
    this.prAddParam('env', \amp, 0.005);
    this.prAddParam('cutoff', \freq.asSpec.copy.default_(20000), 25);
    this.prAddParam('res', \amp, 0.005);
    this.prAddParam('mod', \amp, 0.005);
    this.prAddEnvParams;
  }
}
