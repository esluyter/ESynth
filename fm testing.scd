(
x.release;
x = {
  arg feedback = 1, gate = 1;
  var type = 0;
  var chans = [[0], [0], [0], [0], [0, 2], [0, 1, 2], [0, 1, 2], [0, 1, 2, 3]][type];
  var freqs = [50, 100];
  var algorithms = [
    FM7.modMatrix(
      [0, 1, 1],
      [1, 2, 1],
      [2, 3, 1],
      [3, 3, feedback]
    ),
    FM7.modMatrix(
      [0, 1, 1],
      [1, 2, 1],
      [1, 3, 1],
      [3, 3, feedback]
    ),
    FM7.modMatrix(
      [0, 1, 1],
      [1, 2, 1],
      [0, 3, 1],
      [3, 3, feedback]
    ),
    FM7.modMatrix(
      [0, 1, 1],
      [0, 2, 1],
      [2, 3, 1],
      [3, 3, feedback]
    ),
    FM7.modMatrix(
      [0, 1, 1],
      [2, 3, 1],
      [3, 3, feedback]
    ),
    FM7.modMatrix(
      [0, 3, 1],
      [1, 3, 1],
      [2, 3, 1],
      [3, 3, feedback]
    ),
    FM7.modMatrix(
      [2, 3, 1],
      [3, 3, feedback]
    ),
    FM7.modMatrix(
      [3, 3, feedback]
    ),
  ];
  var mods = algorithms[type];
  var sig = freqs.collect({ |freq|
    Splay.ar({
      FM7.ar([
        [freq * LFNoise1.kr(0.5).exprange(0.9, 1.1), 0, 1],
        [freq, 0, LFNoise1.kr(0.1).exprange(1, 4)],
        [freq * 5 * LFNoise1.kr(0.1).exprange(0.99, 1.01), 0, LFNoise1.kr(0.1).exprange(0.01, 1.5)],
        [freq, 0, LFNoise1.kr(0.1).exprange(0.6, 1.85)],
        [0, 0, 0],
        [0, 0, 0],
      ], mods).slice(chans).asArray.sum
    }.dup(5));
  }).sum;
  sig * Env.adsr(0.5).kr(2, gate) * 0.1
}.play;
)