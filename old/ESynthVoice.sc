ESynthVoice {
  var <server, <out, <amp;
  var <noteSynth, <outSynth;
  var <oscs, <vcf, <vca;
  var <krBuses, <arBuses, <group, <oscGroup, <vcfGroup, <vcaGroup;

  *new { |out = 0, amp = 1, server|
    server = server ?? { Server.default };
    ^super.new.init(out, amp, server);
  }

  init { |argout, argamp, argserver|
    out = argout;
    amp = argamp;
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
    ESynthVCO.prMakeSynthDef;
    ESynthNoise.prMakeSynthDef;
    ESynthVCF.prMakeSynthDef;
    ESynthVCA.prMakeSynthDef;
    SynthDef(\ESynthOut, { |out, in|
      var amp = \amp.kr(1, 0.1);
      var sig = In.ar(in, 2);
      Out.ar(out, sig * amp);
    }).add;
  }

  prMakeBuses {
    arBuses = (
      oscs: Bus.audio(server),
      vcf: Bus.audio(server),
      vca: Bus.audio(server, 2)
    );
    krBuses = (
      note: Bus.control(server),
      gate: Bus.control(server),
      vel: Bus.control(server)
    );
  }

  prMakeSynths {
    group = Group(server);

    noteSynth = Synth(\ESynthNote, [notebus: krBuses.note, velbus: krBuses.vel, gatebus: krBuses.gate], group);

    oscGroup = Group(group, \addToTail);
    oscs = (3.collect { ESynthVCO(oscGroup, arBuses.oscs, krBuses.note) }) ++ [ESynthNoise(oscGroup, arBuses.oscs, krBuses.note)];

    vcfGroup = Group(group, \addToTail);
    vcf = ESynthVCF(vcfGroup, arBuses.vcf, arBuses.oscs, krBuses.note, krBuses.vel, krBuses.gate);

    vcaGroup = Group(group, \addToTail);
    vca = ESynthVCA(vcaGroup, arBuses.vca, arBuses.vcf, krBuses.vel, krBuses.gate);

    outSynth = Synth(\ESynthOut, [out: out, in: arBuses.vca, amp: amp], group, \addToTail);
  }

  noteOn { |note, vel|
    noteSynth.set(\note, note, \vel, vel, \gate, 1);
  }

  noteOff {
    noteSynth.set(\gate, 0);
  }
}
