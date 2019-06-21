ESynthModule : SCViewHolder {
  classvar <font = "Helvetica";
  classvar <monofont = "Menlo";
  var title;

  *new { |parent, bounds|
    ^super.new.init(parent, bounds);
  }

  init { |parent, bounds|
    bounds = bounds ?? Rect(0, 0, parent.bounds.width, parent.bounds.height);
    view = UserView(parent, bounds);
  }

  titleString_ { |string|
    title.remove;
    title = StaticText(view, Rect(2, 0, 80, 20))
      .string_(string)
      .stringColor_(Color.white)
      .font_(Font(ESynthModule.monofont, 20));
  }
}
