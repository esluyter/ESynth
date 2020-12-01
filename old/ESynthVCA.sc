ESynthVCA {
  var <synth, <isRunning;
  var <amp = 0, <pan = 0, <env = 1, <vel = 0;
  var <at = 0.001, <dt = 0.5, <sl = 1, <rt = 0.001;
  var <params = #[\amp, \pan, \env, \vel, \at, \dt, \sl, \rt];

  *prMakeSynthDef {
    SynthDef(\ESynthVCA, { |out, in, velbus, gatebus|
      var sig = In.ar(in);
      var vel = In.kr(velbus);
      var gate = In.kr(gatebus);
      var envAmt = \env.kr(1, 0.1);
      var velAmt = \vel.kr(0, 0.1);
      var amp = \amp.kr(0, 0.1);
      var pan = \pan.kr(0, 0.1);
      var at = \at.kr(0.001, 0.1);
      var dt = \dt.kr(0.5, 0.1);
      var sl = \sl.kr(1, 0.1);
      var rt = \rt.kr(0.001, 0.1);

      var env = Env.adsr(at, dt, sl, rt, envAmt).kr(0, gate);
      // todo vel amt
      amp = amp + env;
      Out.ar(out, Pan2.ar(sig, pan, amp));
    }).add;
  }

  guiClass { ^ESynthVCAView }

  *new { |group, outbus, inbus, velbus, gatebus|
    ^super.new.init(group, outbus, inbus, velbus, gatebus);
  }

  init { |group, outbus, inbus, velbus, gatebus|
    synth = Synth(\ESynthVCA, [out: outbus, in: inbus, velbus: velbus, gatebus: gatebus], group, \addToTail);
    isRunning = true;
  }

  free { synth.free; isRunning = false }


  amp_ { |value|
    amp = value;
    synth.set(\amp, value);
  }

  pan_ { |value|
    pan = value;
    synth.set(\pan, value);
  }

  env_ { |value|
    env = value;
    synth.set(\env, value);
  }

  vel_ { |value|
    vel = value;
    synth.set(\vel, value);
  }

  at_ { |value|
    at = value;
    synth.set(\at, value);
  }

  dt_ { |value|
    dt = value;
    synth.set(\dt, value);
  }

  sl_ { |value|
    sl = value;
    synth.set(\sl, value);
  }

  rt_ { |value|
    rt = value;
    synth.set(\rt, value);
  }
}
