ESMParam {
  var <parent, <esparam, <cv;

  *new { |parent, esparam|
    ^super.newCopyArgs(parent, esparam).init;
  }

  init {
    cv = NumericControlValue(spec: this.spec);
  }

  doesNotUnderstand { |selector ... args|
    ^esparam.performList(selector, args);
  }

  value { ^cv.value; }
  value_ { |value| cv.value_(value); ^this; }
  input { ^cv.input; }
  input_ { |value| cv.input_(value); ^this; }

  spec {
    ^esparam.spec(parent.rate)
  }

  step {
    ^esparam.step(parent.rate)
  }

  index { ^parent.params.indexOf(this); }
  modIndex { ^(this.index - parent.def.modOffset) }

  printOn { | stream |
    stream << "ESMParam<" << this.name << ", " << this.value << ">";
  }
}
