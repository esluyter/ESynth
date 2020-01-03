(
~esm = ESM();

Window.closeAll;
~window = Window.new("Lines", Rect(0, 200, 880, 975)).front;
~window.acceptsMouseOver = true;
~sv = ScrollView(~window, ~window.bounds.origin_(0@0));
~esv = ESView.new(~sv, model: ~esm);

~esm.addDependant { |...args|
  var module = args.pop;
  var esm = args.removeAt(0);
  "% % - %".format(module.kind, module.index, args).postln
};
)


~esm.putLFO(1, 'Sin')
~esm.lfos[1].patchTo(~esm.oscs[0])
~esm.oscs[0].patchAt(0).amt = 0.5


