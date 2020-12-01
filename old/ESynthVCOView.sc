ESynthVCOView : SCViewHolder {
  var <tune, <fine, <duty, <slop, <saw, <sqr, <sin, <tri;

  *new { |parent, bounds|
    ^super.new.init(parent, bounds);
  }

  init { |parent, bounds|
    view = UserView(parent, bounds);

    tune = ESynthKnob(view, Rect(0, 0, 34, 67), "Tune", [-48, 48, \lin, 0.0, 0], 1, 12, true);
    fine = ESynthKnob(view, Rect(40, 0, 34, 67), "Fine", [-2, 2, \lin, 0.0, 0], 0.01, 10, true);
    duty = ESynthKnob(view, Rect(80, 0, 34, 67), "Duty", [0, 1, \lin, 0.0, 0.5], 0.01, 10, true);
    slop = ESynthKnob(view, Rect(120, 0, 34, 67), "Slop", [0.001, 1, \exp, 0.0, 0.01]);

    saw = ESynthKnob(view, Rect(180, 0, 34, 67), "Saw", \amp);
    sqr = ESynthKnob(view, Rect(220, 0, 34, 67), "Sqr", \amp);
    sin = ESynthKnob(view, Rect(260, 0, 34, 67), "Sin", \amp);
    tri = ESynthKnob(view, Rect(300, 0, 34, 67), "Tri", \amp);
  }
}
