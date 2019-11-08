ModuleModel {
  /*
    N.B. must define in subclasses:
    classvar classes
    must define in subsubclasses:
    classvar types, displayName
  */

  var <list, <type, <params, patchCords, <rate = \ar;

  *new { |list = 0, type = 0, paramInputs = (#[]), patchCords = (#[])|
    ^super.newCopyArgs(list, type).initModel.init.prInitParams(paramInputs, patchCords);
  }

  init { } // override this in subclasses

  initModel {
    params = []; // called before init
  }

  prAddParam { |name, spec = (#[0, 1]), step = 0.01, shiftScale = 10, centered = false|
    params = params.add(Param(params.size, this, name, spec, step, shiftScale, centered));
  }

  prAddEnvParams {
    this.prAddParam('del', [0, 10, 4], 0.03);
    this.prAddParam('atk', [0.001, 20, 8], 0.1);
    this.prAddParam('dec', [0.001, 20, 8, 0.0, 0.5], 0.1);
    this.prAddParam('sus', \amp.asSpec.copy.default_(1));
    this.prAddParam('rel', [0.001, 20, 8], 0.1);
  }

  // to initialize param values & patch connections (i.e. from previous module)
  prInitParams { |inputs, patches|
    min(inputs.size, params.size).do { |i|
      params[i].input = inputs[i];
    };
    patchCords = nil.dup(params.size);
    min(patches.size, params.size).do { |i|
      patchCords[i] = patches[i];
    };
  }

  // to forward getters and setters of param values
  doesNotUnderstand { |selector ... args|
    params.do { |param|
      if (param.name == selector) {
        ^param.value;
      };
      if ((param.name ++ '_').asSymbol == selector) {
        param.value_(args[0]);
        ^this;
      };
    };
    DoesNotUnderstandError(this, selector, args).throw;
  }

  types {
    ^this.class.types;
  }

  displayName {
    ^this.class.displayName;
  }

  classes {
    ^this.class.classes;
  }

  class_ { |value, copyParams = false, copyPatchCords = true|
    var newSelf;
    if (this.classes.indexOf(value) != nil) {
      newSelf = value.new(
        list,
        if (copyParams) { this.type } { 0 },
        if (copyParams) { this.prInputs } { [] },
        if (copyPatchCords) { this.patchCords } { [] }
      );
      if (list.notNil) {
        list[this.index] = newSelf;
      };
      this.changed(\replaced, newSelf);
      list.changed(\patchCords);
      ^newSelf;
    };
  }

  classInput_ { |index|
    ^this.class_(this.classes[index]);
  }

  index {
    ^list.indexOf(this);
  }

  prInputs {
    ^params.collect(_.input);
  }

  patchFrom { |fromLFO, toInlet|
    if (fromLFO.isNil) {
      patchCords[toInlet] = nil;
    } {
      if (toInlet < params.size) {
        patchCords[toInlet] = PatchCord(fromLFO.list, fromLFO.index, list, this.index, toInlet);
      }
    };
    list.changed(\patchCords);
  }

  patchCords {
    ^patchCords.select(_.notNil);
  }
}
