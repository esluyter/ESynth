ESView : SCViewHolder {
  var connections;
  var <model, <patchKnobs, <lfoViews, <oscViews, <filtViews, <ampViews;
  var <portamentoKnob, <portamentoBox, <bendKnob, <bendBox, <voicesKnob, <voicesBox;
  var bendSpec, portamentoSpec, voicesSpec;

  *new { |parent, bounds, model|
    ^super.new.init(parent, bounds).model_(model);
  }

  init { |parent, bounds|
    bounds = bounds ?? Rect(0, 0, parent.bounds.width, parent.bounds.height);
    view = UserView(parent, bounds)
      .background_(Color(0.1, 0, 0.1))
      .onClose_({
        connections.free;
        [lfoViews, oscViews, filtViews, ampViews].do { |a| a.do(_.remove) };
      });
  }

  model_ { |value|
    model = value;

    [lfoViews, oscViews, filtViews, ampViews].do { |a| a.do(_.remove) };

    lfoViews = model.lfos.collect { |lfo, i|
      var row = i.div(5);
      var col = i - (row * 5);
      var indent = row % 2 * 20;
      LFOView(view, Rect(170 * col + indent + 14, 105 * row + 12, 153, 75), lfo);
      // 1px extra for line, 3px extra for dots
    };

    oscViews = model.oscs.collect { |osc, i|
      var row = i.div(3);
      var col = i - (row * 3);
      var indent = row % 2 * 20;
      OscView(view, Rect(270 * col + indent + 14, 105 * row + 442, 253, 75), osc);
    };

    filtViews = model.filts.collect { |filt, i|
      var row = i.div(2);
      var col = i - (row * 2);
      FiltView(view, Rect(370 * col + 14, 105 * row + 667, 353, 75), filt);
    };

    ampViews = [AmpView(view, Rect(14, 887, 293, 75), model.amps[0])];

    portamentoSpec = ESynthDef.note.params[0];
    bendSpec = (spec: ControlSpec(0, 24, 4, 0.0, 2), step: 0.1, shift_scale: 10);
    voicesSpec = (spec: ControlSpec(1, 16, 0, 1, 8), step: 1, shift_scale: 4);

    portamentoKnob = Knob(view, Rect(350, 909, 25, 25))
      .step_(portamentoSpec.step / portamentoSpec.spec.range)
      .shift_scale_(portamentoSpec.shift_scale)
      .value_(portamentoSpec.spec.unmap(model.portamento))
      .mode_(\vert)
      .color_([Color.gray(0.8), Color.white, Color.clear, Color.black]);
    portamentoBox = NumberBox(view, Rect(349, 933, 27, 11))
      .step_(portamentoSpec.step)
      .scroll_step_(portamentoSpec.step)
      .shift_scale_(portamentoSpec.shift_scale)
      .clipLo_(portamentoSpec.spec.minval)
      .clipHi_(portamentoSpec.spec.maxval)
      .value_(model.portamento)
      .font_(Font.monospace.size_(8))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .normalColor_(Color.white)
      .typingColor_(Color.hsv(0, 0.5, 1))
      .align_(\center)
      .maxDecimals_(4);
    StaticText(view, Rect(340, 945, 45, 10))
      .font_(Font.sansSerif.size_(8))
      .align_(\center)
      .stringColor_(Color.white)
      .string_("portamento");

    bendKnob = Knob(view, Rect(400, 909, 25, 25))
      .step_(bendSpec.step / bendSpec.spec.range)
      .shift_scale_(bendSpec.shift_scale)
      .value_(bendSpec.spec.unmap(model.bendRange))
      .mode_(\vert)
      .color_([Color.gray(0.8), Color.white, Color.clear, Color.black]);
    bendBox = NumberBox(view, Rect(399, 933, 27, 11))
      .step_(bendSpec.step)
      .scroll_step_(bendSpec.step)
      .shift_scale_(bendSpec.shift_scale)
      .clipLo_(bendSpec.spec.minval)
      .clipHi_(bendSpec.spec.maxval)
      .value_(model.bendRange)
      .font_(Font.monospace.size_(8))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .normalColor_(Color.white)
      .typingColor_(Color.hsv(0, 0.5, 1))
      .align_(\center)
      .maxDecimals_(4);
    StaticText(view, Rect(390, 945, 45, 10))
      .font_(Font.sansSerif.size_(8))
      .align_(\center)
      .stringColor_(Color.white)
      .string_("bendRange");

    voicesKnob = Knob(view, Rect(450, 909, 25, 25))
      .step_(voicesSpec.step / voicesSpec.spec.range)
      .shift_scale_(voicesSpec.shift_scale)
      .value_(voicesSpec.spec.unmap(model.numVoices))
      .mode_(\vert)
      .color_([Color.gray(0.8), Color.white, Color.clear, Color.black]);
    voicesBox = NumberBox(view, Rect(449, 933, 27, 11))
      .step_(voicesSpec.step)
      .scroll_step_(voicesSpec.step)
      .shift_scale_(voicesSpec.shift_scale)
      .clipLo_(voicesSpec.spec.minval)
      .clipHi_(voicesSpec.spec.maxval)
      .value_(model.numVoices)
      .font_(Font.monospace.size_(8))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .normalColor_(Color.white)
      .typingColor_(Color.hsv(0, 0.5, 1))
      .align_(\center)
      .maxDecimals_(4);
    StaticText(view, Rect(440, 945, 45, 10))
      .font_(Font.sansSerif.size_(8))
      .align_(\center)
      .stringColor_(Color.white)
      .string_("numVoices");

    this.prMakePatches.();

    connections.free;
    connections = ConnectionList.make {
      model.signal(\patchCords).connectTo(this.methodSlot("prMakePatches"));
      model.signal(\portamento).connectTo {
        portamentoKnob.value = portamentoSpec.spec.unmap(model.portamento);
        portamentoBox.value = model.portamento;
      };
      portamentoKnob.signal(\value).connectTo {
        model.portamento = portamentoSpec.spec.map(portamentoKnob.value);
      };
      portamentoBox.signal(\value).connectTo {
        model.portamento = portamentoBox.value;
      };
      model.signal(\bendRange).connectTo {
        bendKnob.value = bendSpec.spec.unmap(model.bendRange);
        bendBox.value = model.bendRange;
      };
      bendKnob.signal(\value).connectTo {
        model.bendRange = bendSpec.spec.map(bendKnob.value);
      };
      bendBox.signal(\value).connectTo {
        model.bendRange = bendBox.value;
      };
      model.signal(\numVoices).connectTo {
        voicesKnob.value = voicesSpec.spec.unmap(model.numVoices);
        voicesBox.value = model.numVoices;
      };
      voicesKnob.signal(\value).connectTo {
        model.numVoices = voicesSpec.spec.map(voicesKnob.value);
      };
      voicesBox.signal(\value).connectTo {
        model.numVoices = voicesBox.value;
      };
    };
  }

  prMakePatches {
    //"ESView::prMakePatches begins".postln;

    patchKnobs.do(_.remove);
    patchKnobs = [];

    view.drawFunc = { |v|
      var plusin = 30@645;
      var plusout = 30@645;

      Pen.width = 2.5;
      model.lfos.patchCords.do { |patchCord|
        this.prDrawPatchCord(
          lfoViews[patchCord.fromIndex].getOutletPoint,
          lfoViews[patchCord.toIndex].getInletPoint(patchCord.toInlet),
          patchCord
        );
      };
      model.oscs.patchCords.do { |patchCord|
        this.prDrawPatchCord(
          lfoViews[patchCord.fromIndex].getOutletPoint,
          oscViews[patchCord.toIndex].getInletPoint(patchCord.toInlet),
          patchCord
        );
      };
      model.filts.patchCords.do { |patchCord|
        this.prDrawPatchCord(
          lfoViews[patchCord.fromIndex].getOutletPoint,
          filtViews[patchCord.toIndex].getInletPoint(patchCord.toInlet),
          patchCord
        );
      };
      model.amps.patchCords.do { |patchCord|
        this.prDrawPatchCord(
          lfoViews[patchCord.fromIndex].getOutletPoint,
          ampViews[patchCord.toIndex].getInletPoint(patchCord.toInlet),
          patchCord
        );
      };

      Pen.width = 2;
      Pen.strokeColor = Color.gray(0.4);
      oscViews.do { |osc|
        this.prDrawPatchCord(osc.getOutletPoint(0), plusin);
      };
      this.prDrawPatchCord(plusout, filtViews[0].getInletPointNoOffset(0));
      this.prDrawPatchCord(plusout, filtViews[1].getInletPointNoOffset(0));
      this.prDrawPatchCord(
        filtViews[0].getOutletPoint(0),
        filtViews[2].getInletPointNoOffset(0)
      );
      this.prDrawPatchCord(
        filtViews[1].getOutletPoint(0),
        filtViews[3].getInletPointNoOffset(0)
      );
      this.prDrawPatchCord(
        filtViews[2].getOutletPoint(0),
        ampViews[0].getInletPointNoOffset(0)
      );
      this.prDrawPatchCord(
        filtViews[3].getOutletPoint(0),
        ampViews[0].getInletPointNoOffset(1)
      );
      Pen.stroke;
    };

    view.refresh;

    //"ESView::prMakePatches ends".postln;
  }

  prDrawPatchCord { |p1, p2, patchCord|
    var offset = Point(0, max(((p2.y - p1.y) / 2), 40));
    //"prDrawPatchCord begins".postln;
    Pen.moveTo(p1);
    Pen.curveTo(p2, p1 + offset, p2 - offset);
    if (patchCord.notNil) {
      Pen.strokeColor = patchCord.color;
    };
    //"path drawn".postln;
    Pen.stroke;
    //"stroked".postln;
    if (patchCord.notNil) {
      //"ready to add knob".postln;
      patchKnobs = patchKnobs.add(
        PatchKnob(view, Rect(p2.x - 8, p2.y - 20, 16, 20), patchCord)
      );
      //"knob added".postln;
      if (patchCord.patchCords.size > 0) {
        patchCord.patchCords.postln;
        this.prDrawPatchCord(
          lfoViews[patchCord.patchCords[0].fromIndex].getOutletPoint,
          Point(p2.x - 5, p2.y - 18),
          patchCord.patchCords[0]
        );
      };
    };
    //"prDrawPatchCord ends".postln;
  }
}
