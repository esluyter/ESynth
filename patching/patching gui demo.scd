(
~esm = ESM();

Window.closeAll;
~window = Window.new("Lines", Rect(0, 200, 880, 975)).front;
~window.acceptsMouseOver = true;
~esv = ESView.new(~window, model: ~esm);


)

~esm.putLFO(1, 'Sin')
~esm.lfos[1].patchTo(~esm.oscs[0])
~esm.oscs[0].patchAt(0).amt = 0.5


