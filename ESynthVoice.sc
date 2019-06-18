ESynthVoice {
  var <server, <out;
  var <krSynths, <arSynths;
  var <krBuses, <arBuses, <group;

  *new { |out = 0, server|
    server = server ?? { Server.default };
    ^super.new.init(out, server);
  }

  init { |argout, argserver|
    out = argout;
    server = argserver;
    server.waitForBoot {
      this.prMakeSynthDefs;
      this.prMakeBuses;
      server.sync;
      this.prMakeSynths;
    };
  }

  prMakeSynthDefs {
    SynthDef(\ESynthNote, { |notebus, velbus, gatebus|
      var portamento = \portamento.kr(0.1);
      var note = \note.kr(60, portamento);
      var vel = \vel.kr(60, 0.1);
      var gate = \gate.kr(0);
      Out.kr(notebus, note);
      Out.kr(velbus, vel);
      Out.kr(gatebus, gate);
    }).add;
    SynthDef(\ESynthVCO, { |out, notebus|
      var note = In.kr(notebus);
      var tune = \tune.kr(0, 0.1);
      var duty = \duty.kr(0.5, 0.1);
      var slop = \slop.kr(0.01, 0.1);
      var sawAmt = \sawAmt.kr(0, 0.1);
      var sqrAmt = \sqrAmt.kr(0, 0.1);
      var sinAmt = \sinAmt.kr(0, 0.1);
      var triAmt = \triAmt.kr(0, 0.1);
      var sig = EVCO.ar(note, tune, duty, slop, sawAmt, sqrAmt, sinAmt, triAmt);
      Out.ar(out, sig);
    }).add;
    SynthDef(\ESynthNoise, { |out|
      Out.ar(out, ENoise.ar(\pinkAmt.kr(0, 0.1), \whiteAmt.kr(0, 0.1)));
    }).add;
    SynthDef(\ESynthVCF, { |out, in, notebus, velbus, gatebus|
      var sig = In.ar(in);
      var cutoff = \cutoff.kr(1000, 0.1);
      var res = \res.kr(0, 0.1);
      var type = \type.kr(1);
      var note = In.kr(notebus);
      var vel = In.kr(velbus);
      var gate = In.kr(gatebus);
      var keyAmt = \keyAmt.kr(0, 0.1);
      var envAmt = \envAmt.kr(0, 0.1);
      var velAmt = \velAmt.kr(0, 0.1);
      var at = \at.kr(0.01, 0.1);
      var dt = \dt.kr(0.5, 0.1);
      var sl = \sl.kr(0.5, 0.1);
      var rt = \rt.kr(1, 0.1);

      var env = Env.adsr(at, dt, sl, rt, envAmt).kr(0, gate).midiratio;
      var key = ((note - 48) * keyAmt).midiratio;
      // todo vel amt
      var freq = cutoff * env * key;
      freq = freq.clip(20, 20000);
      sig = HouvilainenFilter.ar(sig, freq, res, type);
      Out.ar(out, sig);
    }).add;
    SynthDef(\ESynthVCA, { |out, in, velbus, gatebus|
      var sig = In.ar(in);
      var vel = In.kr(velbus);
      var gate = In.kr(gatebus);
      var envAmt = \envAmt.kr(1, 0.1);
      var velAmt = \velAmt.kr(0, 0.1);
      var amp = \amp.kr(0, 0.1);
      var pan = \pan.kr(0, 0.1);
      var at = \at.kr(0.01, 0.1);
      var dt = \dt.kr(0.5, 0.1);
      var sl = \sl.kr(0.5, 0.1);
      var rt = \rt.kr(1, 0.1);

      var env = Env.adsr(at, dt, sl, rt, envAmt).kr(0, gate);
      // todo vel amt
      amp = amp + env;
      Out.ar(out, Pan2.ar(sig, pan, amp));
    }).add;
  }

  prMakeBuses {
    arBuses = (
      vcos: Bus.audio(server),
      vcf: Bus.audio(server)
    );
    krBuses = (
      note: Bus.control(server),
      gate: Bus.control(server),
      vel: Bus.control(server)
    );
  }

  prMakeSynths {
    group = Group(server);
    krSynths = ();
    arSynths = ();
    krSynths.note = Synth(\ESynthNote, [notebus: krBuses.note, velbus: krBuses.vel, gatebus: krBuses.gate], group);
    arSynths.vcos = 3.collect { Synth(\ESynthVCO, [out: arBuses.vcos, notebus: krBuses.note], group, \addToTail) };
    arSynths.noise = Synth(\ESynthNoise, [out: arBuses.vcos], group, \addToTail);
    arSynths.vcf = Synth(\ESynthVCF, [out: arBuses.vcf, in: arBuses.vcos, notebus: krBuses.note, velbus: krBuses.vel, gatebus: krBuses.gate], group, \addToTail);
    arSynths.vca = Synth(\ESynthVCA, [out: out, in: arBuses.vcf, velbus: krBuses.vel, gatebus: krBuses.gate], group, \addToTail)
  }

  noteOn { |note, vel|
    krSynths.note.set(\note, note, \vel, vel, \gate, 1);
  }

  noteOff {
    krSynths.note.set(\gate, 0);
  }
}
