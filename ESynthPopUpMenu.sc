ESynthMenu : SCViewHolder {
  var <defaultValue = 0;
  var <name;
  var title, menu;
  var <>action;

  *new { |parent, bounds, name, items|
    ^super.new.init(parent, bounds, name, items);
  }

  init { |parent, bounds, argname, items|
    name = argname;

    view = UserView(parent, bounds);
    title = StaticText(view, Rect(0, 1, 34, bounds.height))
      .string_(name)
      .align_(\right)
      .stringColor_(Color.white)
      .font_(Font(ESynthModule.font, 11, true));
    menu = PopUpMenu(view, Rect(40, 0, bounds.width - 40, bounds.height))
      .background_(Color.grey(0.1))
      .stringColor_(Color.white)
      .items_(items)
      .font_(Font(ESynthModule.monofont, 10))
      .action_({ |v|
        //this.changed;
        action.(v.value);
      });
  }

  item_ { |value|
    menu.items.do { |item, i|
      if (item == value) {
        menu.value_(i);
      };
    };
  }

  doesNotUnderstand { |selector ... args|
    menu.performList(selector, args);
  }
}
