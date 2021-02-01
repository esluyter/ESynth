ESMBusList : List {
  var <kind, connections;

  *new { |kind, names, hasOutlet = true|
    ^super.new.init(kind, names, hasOutlet);
  }

  init { |argkind, names, hasOutlet|
    kind = argkind;
    connections = ConnectionList.make {
      this.setCollection(
        names.collect { |name|
          var bus = ESMBus(name, hasOutlet, this);
          bus.connectTo({ |what ...args|
            this.changed(*(args.add(bus)));
          });
          bus;
        }
      );
    }
  }

  arPatchCords {
    ^this.collect(_.arPatchCords).flat;
  }
}
