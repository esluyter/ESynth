ESMBus {
  var <name, <hasOutlet = true;
  var <list;
  var <arPatchCords;
  var <kind;

  *new { |name, hasOutlet = true, list|
    ^super.newCopyArgs(name, hasOutlet, list).init;
  }

  init {
    arPatchCords = [];
    kind = list.kind;
  }

  index {
    ^list.indexOf(this);
  }

  printOn { | stream |
    stream << "ESMBus<" << kind << name << ">";
  }

  arPatchTo { |to, toInlet = 0|
    to.arPatchFrom(this, toInlet);
  }

  arPatchFrom { |from, toInlet = 0|
    var index = arPatchCords.collect(_.from).indexOf(from);
    if (index.notNil) {
      arPatchCords.removeAt(index);
      ^this.changed(\arPatchCords);
    };
    if (from.kind == \lfo or: (from.kind == \amp)) { ^false };
    arPatchCords = arPatchCords.add(ESMArPatchCord(from, this, 0));
    this.changed(\arPatchCords);
  }

  clearArInputs {
    arPatchCords = [];
    this.changed(\arPatchCords);
  }

  category {
    var plural = if (kind.asString.last == $s) {
      \es
    } {
      \s
    };
    ^(kind ++ plural).asSymbol
  }
}
