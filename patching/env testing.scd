b = Bus.control(s)

(
x.free;
x = {
  var envType = \env_type.kr(0); // 0 - sustain, 1 - oneshot, 2 - retrig
  var loopNode = Select.kr(envType, [-99, -99, 0]);
  var relNode = Select.kr(envType, [3, -99, 3]);
  var env = EnvGen.kr([ 0, 4, relNode, loopNode, 0, \del.kr(0.3), 5, -4, 1, \atk.kr(0.1), 5, -4, \sus.kr(0.5), \dec.kr(0.5), 5, -4, 0, \rel.kr(1), 5, -4 ], \gatef.kr(1));
  env;
}.play(s, b);
)

b.scope

x.set(\gatef, 1);
x.set(\gatef, 0);
x.set(\env_type, 2);

Env.adsr.asArray
Env.adsr.postcs
Env.dadsr.postcs
Env.dadsr.asArray

Env([0, 1, 0], [1, 1], \lin).releaseNode

{ (\test.kr(1) == 0).postln }.play
{ (\test.kr(1) >= 0).postln }.play


var env = Env(
        [0, 0, 1, e[\sus], 0],
        [e[\del], e[\atk], e[\dec], e[\rel]],
        -4.0,
        3
      ).kr(0, e[\gate]);