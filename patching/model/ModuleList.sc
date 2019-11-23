ModuleList : List {
  *new { |defaultClass = (LFONil), size = 4|
    ^super.new.init(size, defaultClass);
  }

  init { |size, defaultClass|
    this.setCollection(size.collect({ defaultClass.new(this) }));
  }

  patchCords {
    ^this.collect(_.patchCords).flat;
  }
}
