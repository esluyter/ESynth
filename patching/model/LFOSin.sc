LFOSin : LFOModel {
  classvar <displayName, <types;

  *initClass {
    displayName = "LF Sin";
    types = [\global, \gate, \random];
  }

  init {
    this.prAddParam('delay', [0, 10, 4], 0.03);
    this.prAddParam('freq', [0.01, 200, 6, 0, 2], 0.5);
    this.prAddParam('key', \amp, 0.005);
    this.prAddParam('phase');
  }
}

LFOTri : LFOModel {
  classvar <displayName, <types;

  *initClass {
    displayName = "LF Tri";
    types = [\global, \gate, \random];
  }

  init {
    this.prAddParam('delay', [0, 10, 4], 0.03);
    this.prAddParam('freq', [0.01, 200, 6, 0, 2], 0.5);
    this.prAddParam('key', \amp, 0.005);
    this.prAddParam('phase');
    this.prAddParam('duty', [0, 1, 'lin', 0, 0.5], centered: true);
  }
}

LFOSqr : LFOModel {
  classvar <displayName, <types;

  *initClass {
    displayName = "LF Sqr";
    types = [\global, \gate, \random];
  }

  init {
    this.prAddParam('delay', [0, 10, 4], 0.03);
    this.prAddParam('freq', [0.01, 200, 6, 0, 2], 0.5);
    this.prAddParam('key', \amp, 0.005);
    this.prAddParam('phase');
    this.prAddParam('duty', [0, 1, 'lin', 0, 0.5], centered: true);
  }
}
