OscSuperSaw : OscModel {
  classvar <displayName, <types;

  *initClass {
    displayName = "SuperSaw";
    types = [];
  }

  init {
    this.prAddParam('tune', [-48, 48, \lin, 0.0, 0], 1, 12, true);
    this.prAddParam('fine', [-2, 2, \lin, 0.0, 0], 0.01, 10, true);
    this.prAddParam('duty', [0, 1, \lin, 0.0, 0.5], 0.01, 10, true);
    this.prAddParam('nsaws', [1, 10], 1, 1);
    this.prAddParam('amp', \amp);
  }
}
