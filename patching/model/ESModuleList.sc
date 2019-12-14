ESModuleList : List {
  var <kind;

  *new { |kind = \lfo, size = 4|
    ^super.new.init(kind, size);
  }

  init { |argkind, size|
    kind = argkind;
    this.setCollection(
      size.collect { ESModule.newList(this) }
    );
  }
  
  patchCords {
    ^this.collect(_.patchCords).flat;
  }
}
