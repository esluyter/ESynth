ESView : SCViewHolder {
  var connections;
  var <model, <patchKnobs, <lfos, <oscs, <oscbuses, <filts, <ampbuses, <amps, <notesyns;
  var <extraControlView, <portamentoKnob, <portamentoBox, <bendKnob, <bendBox, <voicesKnob, <voicesBox, <priorityMenu, <modeMenu, <portamentoMenu;
  var bendSpec, portamentoSpec, voicesSpec;

  *new { |parent, bounds, model|
    ^super.new.init(parent, bounds).model_(model);
  }

  init { |parent, bounds|
    bounds = bounds ?? Rect(0, 0, parent.bounds.width, parent.bounds.height);
    view = UserView(parent, bounds)
      .background_(Color(0.1, 0, 0.1, 0.7))
      .onClose_({
        connections.free;
        [lfos, oscs, filts, amps].do { |a| a.do(_.remove) };
      });
  }

  model_ { |value|
    model = value;

    [lfos, oscs, filts, amps].do { |a| a.do(_.remove) };

    lfos = model.lfos.collect { |lfo, i|
      var row = i.div(5);
      var col = i - (row * 5);
      var indent = row % 2 * 20;
      LFOView(view, Rect(180 * col + indent + 14, 105 * row + 12, 163, 75), lfo);
      // 1px extra for line, 3px extra for dots
    };

    oscs = model.oscs.collect { |osc, i|
      var row = i.div(2);
      var col = i - (row * 2);
      var indent = row % 2 * 20;
      OscView(view, Rect(440 * col + indent + 14, 105 * row + 442, 423, 75), osc);
    };

    filts = model.filts.collect { |filt, i|
      var row = i.div(2);
      var col = i - (row * 2);
      var indent = row % 2 * 20;
      FiltView(view, Rect(440 * col + indent + 14, 105 * row + 802, 423, 75), filt);
    };

    amps = [AmpView(view, Rect(14, 1032, 423, 75), model.amps[0])];

    oscbuses = [20, 205, 390, 575].collect { |x , i|
      BusView(view, Rect(x, 753, 20, 28), model.oscbuses[i]).name_((i + 65).asAscii)
    };
    ampbuses = [20, 62, 105].collect { |x, i|
      BusView(view, Rect(x, 1003, 20, 28), model.ampbuses[i]).name_(['L', 'C', 'R'][i]).hasOutlet_(false)
    };

    portamentoSpec = ESynthDef.note.params[0];
    bendSpec = (spec: ControlSpec(0, 24, 4, 0.0, 2), step: 0.1, shift_scale: 10);
    voicesSpec = (spec: ControlSpec(1, 16, 0, 1, 8), step: 1, shift_scale: 4);

    extraControlView = View(view, Rect(460, 985, 300, 120));
    portamentoKnob = Knob(extraControlView, Rect(220, 69, 25, 25))
      .step_(portamentoSpec.step / portamentoSpec.spec.range)
      .shift_scale_(portamentoSpec.shift_scale)
      .value_(portamentoSpec.spec.unmap(model.portamento))
      .mode_(\vert)
      .color_([Color.gray(0.8), Color.white, Color.clear, Color.black]);
    portamentoBox = NumberBox(extraControlView, Rect(219, 93, 27, 11))
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
    StaticText(extraControlView, Rect(210, 105, 45, 10))
      .font_(Font.sansSerif.size_(8))
      .align_(\center)
      .stringColor_(Color.white)
      .string_("portamento");

    bendKnob = Knob(extraControlView, Rect(270, 69, 25, 25))
      .step_(bendSpec.step / bendSpec.spec.range)
      .shift_scale_(bendSpec.shift_scale)
      .value_(bendSpec.spec.unmap(model.bendRange))
      .mode_(\vert)
      .color_([Color.gray(0.8), Color.white, Color.clear, Color.black]);
    bendBox = NumberBox(extraControlView, Rect(269, 93, 27, 11))
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
    StaticText(extraControlView, Rect(260, 105, 45, 10))
      .font_(Font.sansSerif.size_(8))
      .align_(\center)
      .stringColor_(Color.white)
      .string_("bendRange");

    voicesKnob = Knob(extraControlView, Rect(30, 69, 25, 25))
      .step_(voicesSpec.step / voicesSpec.spec.range)
      .shift_scale_(voicesSpec.shift_scale)
      .value_(voicesSpec.spec.unmap(model.numVoices))
      .mode_(\vert)
      .color_([Color.gray(0.8), Color.white, Color.clear, Color.black]);
    voicesBox = NumberBox(extraControlView, Rect(29, 93, 27, 11))
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
    StaticText(extraControlView, Rect(20, 105, 45, 10))
      .font_(Font.sansSerif.size_(8))
      .align_(\center)
      .stringColor_(Color.white)
      .string_("numVoices");

    portamentoMenu = PopUpMenu(extraControlView, Rect(85, 55, 100, 12))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .font_(Font.monospace.size_(8))
      .items_(["Portamento On", "Portamento Auto", "Portamento Off"])
      .action_({ "not yet implemented".warn });

    modeMenu = PopUpMenu(extraControlView, Rect(85, 79, 100, 12))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .font_(Font.monospace.size_(8))
      .items_(["Polyphonic", "Unison Poly", "Unison Mono", "Paraphonic"])
      .action_({ "not yet implemented".warn });

    priorityMenu = PopUpMenu(extraControlView, Rect(85, 103, 100, 12))
      .background_(Color.grey(0.04))
      .stringColor_(Color.white)
      .font_(Font.monospace.size_(8))
      .items_(["Latest", "First", "Highest", "Lowest"])
      .value_(model.priority);

    notesyns = [GlobalModuleView(view, Rect(770, 1032, 103, 75), model.notesyn)];

    this.prMakePatches.();

    connections.free;
    connections = ConnectionList.make {
      priorityMenu.signal(\value).connectTo(model.methodSlot("priority_(value)"));
      model.signal(\priority).connectTo {
        priorityMenu.value = model.priority;
      };

      model.signal(\patchCords).connectTo(this.methodSlot("prMakePatches"));
      model.signal(\arPatchCords).connectTo(this.methodSlot("prMakePatches"));

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
      var arPatchCords = model.arPatchCords.values.flat;

      Pen.width = 3.5;
      arPatchCords.do { |apc|
        var outletPoint = this.perform(apc.fromCategory)[apc.fromIndex].getOutletPoint;
        var inletPoint = this.perform(apc.toCategory)[apc.toIndex].getArInletPoint(apc.toInlet);
        Pen.strokeColor = apc.color;
        this.prDrawPatchCord(outletPoint, inletPoint, nil, apc.toInlet == 1);
      };
      Pen.width = 2.5;
      Pen.strokeColor = Color(0.1, 0, 0.1);
      arPatchCords.do { |apc|
        var outletPoint = this.perform(apc.fromCategory)[apc.fromIndex].getOutletPoint;
        var inletPoint = this.perform(apc.toCategory)[apc.toIndex].getArInletPoint(apc.toInlet);
        this.prDrawPatchCord(outletPoint, inletPoint, nil, apc.toInlet == 1);
      };

      Pen.width = 2.5;
      model.notesyns.patchCords.do { |patchCord|
        this.prDrawPatchCord(
          lfos[patchCord.fromIndex].getOutletPoint,
          notesyns[0].getInletPoint(patchCord.toInlet),
          patchCord
        );
      };
      model.lfos.patchCords.do { |patchCord|
        this.prDrawPatchCord(
          lfos[patchCord.fromIndex].getOutletPoint,
          lfos[patchCord.toIndex].getInletPoint(patchCord.toInlet),
          patchCord
        );
      };
      model.oscs.patchCords.do { |patchCord|
        this.prDrawPatchCord(
          lfos[patchCord.fromIndex].getOutletPoint,
          oscs[patchCord.toIndex].getInletPoint(patchCord.toInlet),
          patchCord
        );
      };
      model.filts.patchCords.do { |patchCord|
        this.prDrawPatchCord(
          lfos[patchCord.fromIndex].getOutletPoint,
          filts[patchCord.toIndex].getInletPoint(patchCord.toInlet),
          patchCord
        );
      };
      model.amps.patchCords.do { |patchCord|
        this.prDrawPatchCord(
          lfos[patchCord.fromIndex].getOutletPoint,
          amps[patchCord.toIndex].getInletPoint(patchCord.toInlet),
          patchCord
        );
      };
    };

    view.refresh;

    //"ESView::prMakePatches ends".postln;
  }

  prDrawPatchCord { |p1, p2, patchCord, sidechain = false|
    var offset = Point(0, max(((p2.y - p1.y) / 2), max((p1.y - p2.y) / 3, if (p2.y < p1.y) { 80 } { 40 })));
    var sideoffset = Point(max((p2.x - p1.x) / 2, max((p1.x - p2.x) / 4, 40)), 0);
    //"prDrawPatchCord begins".postln;
    Pen.moveTo(p1);
    Pen.curveTo(p2, p1 + offset, if (sidechain) { p2 - sideoffset } { p2 - offset });
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
        //patchCord.patchCords.postln;
        this.prDrawPatchCord(
          lfos[patchCord.patchCords[0].fromIndex].getOutletPoint,
          Point(p2.x - 5, p2.y - 18),
          patchCord.patchCords[0]
        );
      };
    };
    //"prDrawPatchCord ends".postln;
  }
}
