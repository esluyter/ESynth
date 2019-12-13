ESMParam {
  var <parent, <esparam, <cv;

  *new { |parent, esparam|
    ^super.newCopyArgs(parent, esparam).init;
  }

  init {
    cv = NumericControlValue(spec: esparam.spec);
  }

  doesNotUnderstand { |selector ... args|
    ^esparam.performList(selector, args);
  }

  value { ^cv.value; }
  input { ^cv.input; }
  input_ { |value| cv.input_(value); ^this; }

  index { ^parent.params.indexOf(this); }
}
