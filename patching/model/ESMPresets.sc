ESMPresets {
  *path {
    ^PathName(this.filenameSymbol.asString).pathOnly;
  }

  *all {
    ^File(this.path +/+ "presets.txt", "r").readAllString.interpret;
  }

  *write { |event|
    var f = File(this.path +/+ "presets.txt", "w");
    f.write(event.asCompileString);
    f.close;
  }

  *put { |name, event|
    this.write(this.all[name] = event);
  }

  *at { |name|
    ^this.all[name] 
  }
}
