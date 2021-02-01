ESBus : Bus {
  var <outputRoutes, <inputRoutes;

  *new { arg rate=\audio, index=0, numChannels=2, server;
		^super.newCopyArgs(rate, index, numChannels, server ? Server.default).init
	}

  init {
    outputRoutes = [];
    inputRoutes = [];
  }

  addInputRoute { |mod|
    inputRoutes = inputRoutes.add(mod);
  }

  addOutputRoute { |mod|
    outputRoutes = outputRoutes.add(mod);
  }

  removeInputRoute { |mod|
    inputRoutes.removeAt(inputRoutes.indexOf(mod));
  }

  removeOutputRoute { |mod|
    outputRoutes.removeAt(outputRoutes.indexOf(mod));
  }

  freeInputRoutes {
    inputRoutes.copy.do(_.free); // automatically removes from array
  }
}
