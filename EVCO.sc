EVCO : UGen {
  *ar { |note = 60, tune = 0, pw = 0.5, slop = 0.01, sawAmt = 0, sqrAmt = 0, sinAmt = 0, triAmt = 0|
    var freq = (note + tune).midicps;
    var rate = freq + LFNoise1.ar(LFNoise1.kr(1).range(0.2, 0.3), mul: slop * (freq / 1000));
    var phase = Phasor.ar(0, rate, 0, SampleRate.ir) * 2pi / SampleRate.ir;
    var duty = (pw - 0.5).sign * (pw - 0.5).abs.lincurve(0, 0.5, 0, 0.5, 5);
    var dutyPhase = phase.lincurve(0, 2pi, 0, 2pi, duty.linlin(-0.5, 0.5, -85, 85));
    var k = 12000 * (SampleRate.ir/44100) / (freq * log10(freq));
    var sinSig = SinOsc.ar(0, phase);
    var dutySinSig = SinOsc.ar(0, dutyPhase);
    var cosSig = SinOsc.ar(0, phase + (pi/2));
    var sqSig = tanh(sinSig * k);
    var dutySqSig = tanh(dutySinSig * k);
    //var sawSig = dutySqSig * (cosSig + 1) * 0.5;
    var sawSig = sqSig * (cosSig + 1) * 0.5;
    //var sawPw = (pw - 0.5).abs.lincurve(0, 0.5, 0, 0.5, 2);
    //var triSig = VarSaw.ar(rate, 0, sawPw);
    var sawAlias = Saw.ar(rate) * 2;
    var varSaw = VarSaw.ar(rate, 0, pw);
    var triSig = LinSelectX.ar(pw.linlin(0, 1, -10, 10).sinh / 11013 + 1, [sawAlias, varSaw, sawAlias * -1]);
    //var dutySawSig = LinSelectX.ar(sawPw.lincurve(0, 0.5, 0, 1, -7), [sawSig + Saw.ar(rate), triSig * 1.5]);
    var sigs = [sawSig, dutySqSig * 1.5, dutySinSig, triSig];
    ^((sigs[0] * sawAmt) + (sigs[1] * sqrAmt) + (sigs[2] * sinAmt) + (sigs[3] * triAmt));
  }
}

ENoise : UGen {
  *ar { |pinkAmt, whiteAmt|
    var pink = PinkNoise.ar;
    var white = WhiteNoise.ar;
    ^((pink * pinkAmt) + (white * whiteAmt))
  }
}
