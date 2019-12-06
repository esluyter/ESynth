ESynthDef {
  classvar <mod, <lfos, <oscs, <filts, <amps;
  var <type, <name, <krfunc, <arfunc, <typelist, <params, <envirFunc;
  var numChannels;

  *initClass {
    Class.initClassTree(ServerBoot);
    Class.initClassTree(SynthDescLib);
    Class.initClassTree(ControlSpec);
    mod = this.new(\mod, \mod, { In.kr(\in.ir) * ~amt }, { In.ar(\in.ir) * ~amt }, nil, [ESParam(\amt, \control, [-1, 1])]);
    lfos = ();
    oscs = ();
    filts = ();
    amps = ();
    ServerBoot.add {
      this.addSynthDefs;
    };
  }

  *addSynthDefs {
    mod.prAddSynthDefs;
    lfos.do(_.prAddSynthDefs);
    oscs.do(_.prAddSynthDefs);
    filts.do(_.prAddSynthDefs);
    amps.do(_.prAddSynthDefs);
  }

  prAddSynthDefs {
    if (arfunc.notNil) {
      SynthDef(this.arDefName, {
        var out = \out.ir;
        var sig = this.prMakeParamEnvir.use(arfunc);
        Out.ar(out, sig);
      }).add;
    };
    if (krfunc.notNil) {
      SynthDef(this.krDefName, {
        var out = \out.ir;
        var sig = this.prMakeParamEnvir('control').use(krfunc);
        Out.kr(out, sig);
      }).add;
    };
  }

  arDefName { ^("ES" ++ type ++ "AR" ++ name).asSymbol }
  krDefName { ^("ES" ++ type ++ "KR" ++ name).asSymbol }

  prMakeParamEnvir { |rate = 'audio'|
    var e = Environment.make(envirFunc);
    e[\type] = \type.kr;
    e[\bend] = \bend.kr;
    e[\note] = \note.kr(60) + e[\bend];
    e[\gate] = \gate.kr(1);
    e[\mod] = \mod.kr;
    params.do { |param|
      var name = param.name;
      var spec = param.spec;
      var value = name.kr(spec.default);
      var modvalue = (name ++ '_mod').asSymbol.perform(
        if ((param.rate == 'audio') and: (rate == 'audio')) { \ar } { \kr });
      e[name] = spec.warp.map(spec.unmap(value) + (modvalue * 0.5));
    };
    ^e;
  }

  *new { |type, name, krfunc, arfunc, typelist, params, envirFunc|
    envirFunc = envirFunc ? {};
    ^super.newCopyArgs(type, name, krfunc, arfunc, typelist, params, envirFunc).init;
  }

  init {
    this.prAddSynthDefs;
  }

  *lfo { |name ...args|
    var typelist, params, krfunc, arfunc;
    # typelist, params, krfunc, arfunc = this.prParseConstructorArgs(args);
    lfos[name] = this.new(\lfo, name, krfunc, arfunc, typelist, params);
    ^lfos[name];
  }

  *osc { |name ...args|
    var typelist, params, arfunc;
    # typelist, params, arfunc = this.prParseConstructorArgs(args);
    oscs[name] = this.new(\osc, name, nil, arfunc, typelist, params);
    ^oscs[name];
  }

  *filt { |name ...args|
    var typelist, params, arfunc;
    # typelist, params, arfunc = this.prParseConstructorArgs(args);
    filts[name] = this.new(\filt, name, nil, arfunc, typelist, params, {
      ~in = In.ar(\in.ir);
    });
    ^filts[name];
  }

  *amp { |name ...args|
    var typelist, params, arfunc;
    # typelist, params, arfunc = this.prParseConstructorArgs(args);
    amps[name] = this.new(\amp, name, nil, arfunc, typelist, params, {
      ~inmono = In.ar(\inmono.ir);
      ~instereo = In.ar(\instereo.ir, 2);
    });
    ^amps[name];
  }

  *prParseConstructorArgs { |args|
    var typelist, params = [], func1, func2;

    if (args[0].isCollection) { typelist = args[0]; args = args[1..] };

    while { args[0].isSymbol or: args[0].isString } {
      params = params.add(this.prParseParam(args[0], args[1]));
      args = args[2..];
    };

    # func1, func2 = args;

    ^[typelist, params, func1, func2];
  }

  *prParseParam { |name, args|
    name = name.asSymbol;
    args = args.asArray;
    if ((args[0] == \audio) or: (args[0] == \ar)) {
      args[0] = \audio;
    } {
      args[0] = \control;
    };
    args = [name] ++ args;
    ^ESParam(*args);
  }

  numChannels { |rate = 'audio'|
    var name = if (rate == 'audio') { this.arDefName } { this.krDefName };
    ^SynthDescLib.global[name].outputs[0].numberOfChannels;
  }
}
