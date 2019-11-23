OscVCO : OscModel {
  classvar <displayName, <types;

  *initClass {
    displayName = "VCO";
    types = [];
  }

  init {
    this.prAddParam('tune', [-48, 48, \lin, 0.0, 0], 1, 12, true);
    this.prAddParam('fine', [-2, 2, \lin, 0.0, 0], 0.01, 10, true);
    this.prAddParam('duty', [0, 1, \lin, 0.0, 0.5], 0.01, 10, true);
    this.prAddParam('slop', [0.001, 1, \exp, 0.0, 0.01]);
    this.prAddParam('sin', \amp);
    this.prAddParam('tri', \amp);
    this.prAddParam('saw', \amp);
    this.prAddParam('sqr', \amp);
  }
}
