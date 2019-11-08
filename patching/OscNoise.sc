OscNoise : OscModel {
  classvar <displayName, <types;

  *initClass {
    displayName = "Noise";
    types = [];
  }

  init {
    this.prAddParam('white', \amp);
    this.prAddParam('pink', \amp);
  }
}
