PatchCord {
  var <fromList, <fromIndex, <toList, <toIndex, <toInlet, <amtParam, <color;

  *new { |fromList, fromIndex, toList, toIndex, toInlet, amt = 0|
    ^super.newCopyArgs(fromList, fromIndex, toList, toIndex, toInlet).init(amt);
  }

  init { |amt|
    amtParam = Param(0, this).value_(amt);
    color = Color.rand;
  }

  amt {
    ^amtParam.value;
  }

  amt_ { |val|
    amtParam.value_(val);
    ^this;
  }

  toLFO {
    ^toList[toIndex];
  }

  fromLFO {
    ^fromList[fromIndex];
  }
}
