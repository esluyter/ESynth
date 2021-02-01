ESMArPatchCord {
  var <from, <to, <toInlet, <color, <kind = \arPatchCord;

  *new { |from, to, toInlet, color|
    ^super.newCopyArgs(from, to, toInlet).init(color);
  }

  init { |argcolor|
    color = argcolor ?? Color.rand;
  }

  fromIndex { ^from.index }
  toIndex { ^to.index; }

  fromCategory {
    ^from.category
  }
  toCategory {
    ^to.category
  }

  list { ^to.list }

  printOn { | stream |
    stream << "ESMArPatchCord<" << from << ", " << to << ", " << toInlet << ">";
  }

  asEvent {
    ^(
      fromCategory: this.fromCategory,
      fromIndex: this.fromIndex,
      toCategory: this.toCategory,
      toIndex: this.toIndex,
      toInlet: toInlet
    )
  }
}
