ESynthDef {
  classvar <mod, <lfos, <oscs, <filts, <amps;
  var <type, <name, <krfunc, <arfunc, <typelist, <params, <envirFunc, <autoEnv;

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
        var out = \out.kr;
        var sig = this.prMakeParamEnvir.use(arfunc);
        if (type == \filt) { ReplaceOut.ar(out, sig) } { Out.ar(out, sig) };
      }).add;
    };
    if (krfunc.notNil) {
      SynthDef(this.krDefName, {
        var out = \out.kr;
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
    params.do { |param|
      var name = param.name;
      var spec = param.spec;
      var value = name.kr(spec.default);
      var modvalue = param.modName.perform(
        if ((param.rate == 'audio') and: (rate == 'audio')) { \ar } { \kr }
      );
      e[name] = spec.warp.map(spec.unmap(value) + (modvalue * 0.5));
    };
    e[\vel] = SelectX.kr(e[\vel] ?? 1, [1, In.kr(\velbus.ir) * 2]);
    if (autoEnv) {
      e[\env] = (e[\env] ?? 1) * (e[\vel]) * Env([0.0, 0.0, 1.0, e[\sus], 0.0], [e[\del], e[\atk], e[\dec], e[\rel]], -4.0, 3).kr(0, e[\gate]);
    };
    ^e;
  }

  *new { |type, name, krfunc, arfunc, typelist, params, envirFunc, autoEnv = false|
    envirFunc = envirFunc ? {};
    ^super.newCopyArgs(type, name, krfunc, arfunc, typelist, params, envirFunc, autoEnv).init;
  }

  init {
    this.prAddSynthDefs;
  }

  *lfo { |name ...args|
    var typelist, params, krfunc, arfunc;
    # typelist, params, krfunc, arfunc = this.prParseConstructorArgs(args);
    lfos[name] = this.new(\lfo, name, krfunc, arfunc, typelist, params, {
      ~note = In.kr(\notebus.ir);
      ~gate = In.kr(\gatebus.ir);
      ~mod = In.kr(\modbus.ir);
    });
    ^lfos[name];
  }

  *osc { |name ...args|
    var typelist, params, arfunc;
    # typelist, params, arfunc = this.prParseConstructorArgs(args);
    oscs[name] = this.new(\osc, name, nil, arfunc, typelist, params, {
      ~note = In.kr(\notebus.ir);
      ~vel = In.kr(\velbus.ir);
    });
    ^oscs[name];
  }

  *filt { |name ...args|
    var typelist, params, arfunc;
    # typelist, params, arfunc = this.prParseConstructorArgs(args, true);
    filts[name] = this.new(\filt, name, nil, arfunc, typelist, params, {
      ~in = In.ar(\out.kr);
      ~note = In.kr(\notebus.ir);
      ~gate = In.kr(\gatebus.ir);
    }, true);
    ^filts[name];
  }

  *amp { |name ...args|
    var typelist, params, arfunc;
    # typelist, params, arfunc = this.prParseConstructorArgs(args, true);
    amps[name] = this.new(\amp, name, nil, arfunc, typelist, params, {
      ~inmono = In.ar(\inmono.ir);
      ~instereo = In.ar(\instereo.ir, 2);
      ~gate = In.kr(\gatebus.ir);
    }, true);
    ^amps[name];
  }

  *prParseConstructorArgs { |args, addEnvParams = false|
    var typelist, params = [], func1, func2;

    if (args[0].isCollection) { typelist = args[0]; args = args[1..] };

    while { args[0].isSymbol or: args[0].isString } {
      params = params.add(this.prParseParam(args[0], args[1]));
      args = args[2..];
    };

    # func1, func2 = args;

    if (addEnvParams) {
      params = [ESParam(\key), ESParam(\vel), ESParam(\env)] ++ params ++ [
        ESParam('del', \control, [0, 10, 4], 0.03),
        ESParam('atk', \control, [0.001, 20, 8], 0.1),
        ESParam('dec', \control, [0.001, 20, 8, 0.0, 0.5], 0.1),
        ESParam('sus', \control, \amp.asSpec.copy.default_(1)),
        ESParam('rel', \control, [0.001, 20, 8], 0.1)
      ]
    };

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
