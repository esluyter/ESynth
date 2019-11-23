Param {
  var <index, <parent, <name, <spec, <step, <shift_scale, <centered;
  var <cv;

  *new { |index, parent, name, spec = (#[0, 1]), step = 0.01, shift_scale = 10, centered = false|
    ^super.newCopyArgs(index, parent, name.asSymbol, spec.asSpec, step, shift_scale, centered).init;
  }

  init {
    cv = NumericControlValue(spec: spec);
  }

  value {
    ^cv.value;
  }

  value_ { |val|
    cv.value_(val);
    ^this;
  }

  input {
    ^cv.input;
  }

  input_ { |val|
    cv.input_(val);
    ^this;
  }
}
