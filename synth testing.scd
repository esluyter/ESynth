~sv = ESynthVoice()

SynthDescLib.at(~sv.arSynths.vcos[0].defName).controlNames

~sv.arSynths.vcos[0].query


~lfobus = Bus.control(s);
~lfo.free;~lfo = { Out.kr(~lfobus, SinOsc.kr(MouseX.kr(0.1, 10, \exponential)).range(0.1, 0.9)) }.play

~sv.arSynths.vcos[0].set(\duty, ~lfobus.asMap)


x = CueList.new("/Users/ericsluyter/Dropbox/sc/cuelists/Synth/"); w = x.makeWindow;

~note = nil;
MIDIdef.noteOn(\noteOn, { |vel, num| ~sv.noteOn(num, vel); ~note = num; });
MIDIdef.noteOff(\noteOff, { |vel, num| if (num == ~note) { ~sv.noteOff; ~note = nil; } });
EZMIDI.keyboard

~sv.arBuses.vca.scope

[-48, 48].asSpec
NumberBox().maxDecimals

Synth()
