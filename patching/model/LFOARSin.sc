LFOARSin : LFOModel {
  classvar <displayName, <types;
  var <rate = \ar;

  *initClass {
    displayName = "AR Sin";
    types = [\global, \gate, \random];
  }

  init {
    rate.postln;
    this.prAddParam('delay', [0, 10]);
    this.prAddParam('freq', [0.01, 200, 6, 0, 2]);
    this.prAddParam('key');
    this.prAddParam('phase');
  }
}
