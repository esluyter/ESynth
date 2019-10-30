~sv = ESynthVoice()

SynthDescLib.at(~sv.oscs[0].synth.defName).controlNames

~sv.arSynths.vcos[0].query
~sv.oscs[0].synth.defName

~lfobus = Bus.control(s);
~lfo.free;~lfo = { Out.kr(~lfobus, SinOsc.kr(MouseX.kr(0.1, 10, \exponential)).range(0.1, 0.9)) }.play

~sv.arSynths.vcos[0].set(\duty, ~lfobus.asMap)

x = CueList.new("/Users/ericsluyter/Dropbox/sc/cuelists/Synth/"); w = x.makeWindow;

~note = nil;
MIDIClient.init
MIDIIn.connectAll
MIDIdef.noteOn(\noteOn, { |vel, num| ~sv.noteOn(num, vel); ~note = num; });
MIDIdef.noteOff(\noteOff, { |vel, num| if (num == ~note) { ~sv.noteOff; ~note = nil; } });
EZMIDI.keyboard

~sv.arBuses.vca.scope

[-48, 48].asSpec
NumberBox().maxDecimals

Synth()

~sv.vca.perform(\at)
~sv.vca.perform((\at ++ "_").asSymbol, 1)
{SinOsc.ar}.play

s.record
s.stopRecording




~sv.oscs.addDependant({ |...args| args.postln })
~sv.oscs.changed(2)