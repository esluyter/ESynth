ESynthNoiseView : SCViewHolder {
  var <white, <pink;

  *new { |parent, bounds|
    ^super.new.init(parent, bounds);
  }

  init { |parent, bounds|
    view = UserView(parent, bounds);

    white = ESynthKnob(view, Rect(0, 0, 34, 67), "White", \amp);
    pink = ESynthKnob(view, Rect(40, 0, 34, 67), "Pink", \amp);
  }
}
