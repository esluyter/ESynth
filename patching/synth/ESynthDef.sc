ESynthDef {
  classvar <mod, <note, <lfos, <oscs, <filts, <amps;
  var <type, <name, <krfunc, <arfunc, <typelist, <params, <envirFunc, <autoEnv, <maxMods, <modOffset;

  *initClass {
    Class.initClassTree(ServerBoot);
    Class.initClassTree(SynthDescLib);
    Class.initClassTree(ControlSpec);
    mod = this.new(\mod, \mod, { In.kr(\in.ir) * ~amt }, { InFeedback.ar(\in.ir) * ~amt }, nil, [ESParam(\amt, \control, [-1, 1, \lin, 0.0, 0], centered: true)], maxMods: 1);
    note = this.new(\note, \note, { ~note.lag2(~portamento) + ~bend }, nil, nil, [ESParam(\portamento), ESParam(\note, \control, [0, 127]), ESParam(\bend, \control, [-12, 12])], maxMods: 0);
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
    var velamt, velin, envamt;
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

    velamt = e[\vel] ?? 1;
    velin = In.kr(\velbus.ir);
    envamt = e[\env] ?? 0;
    if (autoEnv) {
      var envType = \env_type.kr(0); // 0 - sustain, 1 - oneshot, 2 - retrig
      var loopNode = Select.kr(envType, [-99, -99, 0]);
      var relNode = Select.kr(envType, [2, -99, 2]);
      var gateDel = Env([0, 0, 0, 1, 0], [0, e[\del], 0, 0], \lin, 3).kr(0, e[\gate]);
      var env = Env([0, 1, e[\sus], 0], [e[\atk], e[\dec], e[\rel]], -4, relNode, loopNode).ar(0, gateDel).poll;
      e[\env] = LinSelectX.kr(velamt, [1, velin * 2]) * envamt * env;
    };
    e[\vel] = LinSelectX.kr(envamt, [In.kr(\velbus.ir) * velamt, 0]);

    if (e[\note].isNil && (type != \mod)) {
      var keyamt = e[\key] ?? 1;
      var note = In.kr(\notebus.ir);
      e[\note] = note;
      e[\key] = ((note - 48) * keyamt);
    };
    ^e;
  }

  *new { |type, name, krfunc, arfunc, typelist, params, envirFunc, autoEnv = false, maxMods = 8, modOffset = 0|
    envirFunc = envirFunc ? {};
    ^super.newCopyArgs(type, name, krfunc, arfunc, typelist, params, envirFunc, autoEnv, maxMods, modOffset).init;
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
    }, false, 5);
    ^lfos[name];
  }

  *osc { |name ...args|
    var typelist, params, arfunc;
    # typelist, params, arfunc = this.prParseConstructorArgs(args);
    oscs[name] = this.new(\osc, name, nil, arfunc, typelist, params, {
      ~note = In.kr(\notebus.ir);
      ~vel = In.kr(\velbus.ir);
    }, false, 8);
    ^oscs[name];
  }

  *filt { |name ...args|
    var typelist, params, arfunc;
    # typelist, params, arfunc = this.prParseConstructorArgs(args, true);
    filts[name] = this.new(\filt, name, nil, arfunc, typelist, params, {
      ~in = In.ar(\out.kr);
      ~gate = In.kr(\gatebus.ir);
    }, true, 8, 3);
    ^filts[name];
  }

  *amp { |name ...args|
    var typelist, params, arfunc;
    # typelist, params, arfunc = this.prParseConstructorArgs(args, true);
    amps[name] = this.new(\amp, name, nil, arfunc, typelist, params, {
      ~inmono = In.ar(\inmono.ir);
      ~instereo = In.ar(\instereo.ir, 2);
      ~gate = In.kr(\gatebus.ir);
    }, true, 6, 3);
    ^amps[name];
  }

  *prParseConstructorArgs { |args, addEnvParams = false|
    var typelist = [], params = [], func1, func2;

    if (args[0].isCollection) { typelist = args[0]; args = args[1..] };

    while { (args[0].class == Symbol) or: args[0].isString } {
      params = params.add(this.prParseParam(args[0], args[1]));
      args = args[2..];
    };

    # func1, func2 = args;

    if (addEnvParams) {
      params = [
        ESParam(\key, \control, [-1, 1, \lin, 0, 0]), ESParam(\vel),
        ESParam(\env),
        ESParam('del', \control, [0, 10, 4], 0.03),
        ESParam('atk', \control, [0.001, 20, 8], 0.1),
        ESParam('dec', \control, [0.001, 20, 8, 0.0, 0.5], 0.1),
        ESParam('sus', \control, \amp.asSpec.copy.default_(1)),
        ESParam('rel', \control, [0.001, 20, 8], 0.1)
      ] ++ params;
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

  kind { ^type } // ugh fix this

  rates {
    var ret = [];
    if (krfunc.notNil) { ret = ret.add(\control) };
    if (arfunc.notNil) { ret = ret.add(\audio) };
    ^ret;
  }
}
