ESModuleList : List {
  var <kind, connections;

  *new { |kind = \lfo, size = 4|
    ^super.new.init(kind, size);
  }

  init { |argkind, size|
    kind = argkind;
    connections = ConnectionList.make {
      this.setCollection(
        size.collect { |i|
          var module = ESModule.newList(this);
          module.connectTo({ |what ...args|
            this.changed(*(args.add(module)));
          });
          module;
        }
      );
    }
  }

  free {
    connections.free;
    this.do(_.free);
  }

  patchCords {
    ^this.collect(_.patchCords).flat;
  }

  asArray {
    ^this.collect(_.asEvent)
  }
}
