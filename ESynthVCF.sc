ESynthVCF {
  var <synth, <isRunning;
  var <cutoff = 20000, <res = 0, <type = 1, <key = 0, <env = 0, <vel = 0;
  var <at = 0.001, <dt = 0.5, <sl = 0, <rt = 0.001;
  var <params = #[\cutoff, \res, \type, \key, \env, \vel, \at, \dt, \sl, \rt];

  *prMakeSynthDef {
    SynthDef(\ESynthVCF, { |out, in, notebus, velbus, gatebus|
      var sig = In.ar(in);
      var cutoff = \cutoff.kr(20000, 0.1);
      var res = \res.kr(0, 0.1);
      var type = \type.kr(1);
      var note = In.kr(notebus);
      var vel = In.kr(velbus);
      var gate = In.kr(gatebus);
      var keyAmt = \key.kr(0, 0.1);
      var envAmt = \env.kr(0, 0.1) * 100;
      var velAmt = \vel.kr(0, 0.1);
      var at = \at.kr(0.001, 0.1);
      var dt = \dt.kr(0.5, 0.1);
      var sl = \sl.kr(0, 0.1);
      var rt = \rt.kr(0.001, 0.1);

      var env = Env.adsr(at, dt, sl, rt, envAmt).kr(0, gate).midiratio;
      var key = ((note - 48) * keyAmt).midiratio;
      // todo vel amt
      var freq = cutoff * env * key;
      freq = freq.clip(20, SampleRate.ir / 2.1);
      sig = HouvilainenFilter.ar(sig, freq, res, type);
      Out.ar(out, sig);
    }).add;
  }

  guiClass { ^ESynthVCFView }

  *new { |group, outbus, inbus, notebus, velbus, gatebus|
    ^super.new.init(group, outbus, inbus, notebus, velbus, gatebus);
  }

  init { |group, outbus, inbus, notebus, velbus, gatebus|
    synth = Synth(\ESynthVCF, [out: outbus, in: inbus, notebus: notebus, velbus: velbus, gatebus: gatebus], group, \addToTail);
    isRunning = true;
  }

  free { synth.free; isRunning = false }

  cutoff_ { |value|
    cutoff = value;
    synth.set(\cutoff, value);
  }

  res_ { |value|
    res = value;
    synth.set(\res, value);
  }

  type_ { |value|
    type = value;
    synth.set(\type, value);
  }

  key_ { |value|
    key = value;
    synth.set(\key, value);
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
