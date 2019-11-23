LFONoise : LFOModel {
  classvar <displayName, <types;

  *initClass {
    displayName = "LF Noise";
    types = [\global, \random];
  }

  init {
    this.prAddParam('delay', [0, 10, 4], 0.03);
    this.prAddParam('freq', [0.01, 200, 6, 0, 2], 0.5);
    this.prAddParam('interp', [0, 3], 1);
  }
}
