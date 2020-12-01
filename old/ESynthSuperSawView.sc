ESynthSuperSawView : SCViewHolder {
  var <tune, <fine, <duty, <amp;

  *new { |parent, bounds|
    ^super.new.init(parent, bounds);
  }

  init { |parent, bounds|
    view = UserView(parent, bounds);

    tune = ESynthKnob(view, Rect(0, 0, 34, 67), "Tune", [-48, 48, \lin, 0.0, 0], 1, 12, true);
    fine = ESynthKnob(view, Rect(40, 0, 34, 67), "Fine", [-2, 2, \lin, 0.0, 0], 0.01, 10, true);
    duty = ESynthKnob(view, Rect(80, 0, 34, 67), "Duty", [0, 1, \lin, 0.0, 0]);

    amp = ESynthKnob(view, Rect(180, 0, 34, 67), "Amp", \amp);
  }
}
