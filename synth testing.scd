~sv = ESynthVoice()

~sv.arSynths.vcos[0].set(\sawAmt, 0.2, \sinAmt, 0, \triAmt, 0);
~sv.arSynths.vcos[1].set(\sawAmt, 0.2, \triAmt, 0, \tune, 0.2);

~sv.arSynths.vcos[0].set(\sawAmt, 0, \sinAmt, 0, \triAmt, 0.2, \duty, 0);
~sv.arSynths.vcos[1].set(\sawAmt, 0, \triAmt, 0.2, \duty, 0, \tune, 0.2);

~sv.arSynths.vcos[2].set(\sqrAmt, 0.1, \tune, -12.1)
~sv.arSynths.noise.set(\pinkAmt, 0.1)

~sv.krSynths.note.set(\note, 60, \portamento, 1)
~sv.krSynths.note.set(\note, 48, \portamento, 0.2)
~sv.krSynths.note.set(\portamento, 0.05);

~sv.arSynths.vcf.set(\cutoff, 200, \envAmt, 70, \keyAmt, 0.5)
~sv.arSynths.vcf.set(\type, 4)
~sv.arSynths.vcf.set(\res, 0.6, \at, 0.5, \dt, 6, \sl, 0.2, \rt, 1);

~sv.arSynths.vca.set(\at, 0.01, \dt, 6, \sl, 0.1, \rt, 2);


~lfobus = Bus.control(s);
~lfo.free;~lfo = { Out.kr(~lfobus, SinOsc.kr(MouseX.kr(0.1, 10, \exponential)).range(0.1, 0.9)) }.play

~sv.arSynths.vcos[0].set(\duty, ~lfobus.asMap)


~sv.noteOn(12, 60);
~sv.noteOn(24, 60);
~sv.noteOn(36, 60);
~sv.noteOn(48, 60);
~sv.noteOn(60, 60);
~sv.noteOn(72, 60);
~sv.noteOn(84, 60);
~sv.noteOn(90, 60);
~sv.noteOff;


~note = nil;
MIDIdef.noteOn(\noteOn, { |vel, num| ~sv.noteOn(num, vel); ~note = num; });
MIDIdef.noteOff(\noteOff, { |vel, num| if (num == ~note) { ~sv.noteOff; ~note = nil; } });
EZMIDI.keyboard