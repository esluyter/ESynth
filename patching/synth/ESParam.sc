ESParam {
  // rate = \control or \audio
  var <name, <rate, <spec, <step, <shift_scale, <centered;

  *new { |name, rate = \control, spec = \amp, step = 0.005, shift_scale = 10, centered = false|
    ^super.newCopyArgs(name, rate, spec.asSpec, step, shift_scale, centered);
  }
}
