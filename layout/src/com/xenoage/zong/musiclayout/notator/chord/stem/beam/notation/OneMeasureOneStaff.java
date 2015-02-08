package com.xenoage.zong.musiclayout.notator.chord.stem.beam.notation;

import static com.xenoage.utils.iterators.It.it;
import static com.xenoage.zong.core.music.chord.StemDirection.Up;
import static com.xenoage.zong.core.position.MP.getMP;

import java.util.List;

import com.xenoage.utils.iterators.It;
import com.xenoage.utils.math.MathUtils;
import com.xenoage.zong.core.music.beam.Beam;
import com.xenoage.zong.core.music.beam.BeamWaypoint;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.music.chord.StemDirection;
import com.xenoage.zong.core.position.MP;
import com.xenoage.zong.musiclayout.layouter.beamednotation.design.ChordBeamSpacing;
import com.xenoage.zong.musiclayout.notations.ChordNotation;
import com.xenoage.zong.musiclayout.notations.beam.BeamStemAlignments;
import com.xenoage.zong.musiclayout.notations.chord.AccidentalsNotation;
import com.xenoage.zong.musiclayout.notations.chord.NotesNotation;
import com.xenoage.zong.musiclayout.notations.chord.StemNotation;
import com.xenoage.zong.musiclayout.notator.chord.stem.beam.notation.lines.BeamLines;
import com.xenoage.zong.musiclayout.notator.chord.stem.beam.notation.lines.MultipleLines;
import com.xenoage.zong.musiclayout.notator.chord.stem.beam.notation.lines.OneLine;
import com.xenoage.zong.musiclayout.notator.chord.stem.beam.notation.lines.ThreeLines;
import com.xenoage.zong.musiclayout.notator.chord.stem.beam.notation.lines.TwoLines;
import com.xenoage.zong.musiclayout.spacing.ColumnSpacing;
import com.xenoage.zong.musiclayout.spacing.ElementSpacing;

/**
 * {@link Strategy} for a {@link Beam}, which spans over a single staff and measure.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public class OneMeasureOneStaff
	implements Strategy {

	private static final float closeSpacing = 5;
	private static final float wideSpacing = 8;


	@Override public void compute(Beam beam, List<ColumnSpacing> columnSpacings) {

		//collect needed information
		ColumnSpacing column = columnSpacings.get(getMP(beam.getChord(0)).measure);
		NotesNotation[] chordNa = new NotesNotation[beam.getWaypoints().size()];
		float[] stemX = new float[beam.getWaypoints().size()];
		Chord firstChord = beam.getStart().getChord();
		MP firstChordMP = getMP(firstChord);
		int staffLinesCount = firstChord.getScore().getStaff(firstChordMP).getLinesCount();
		int beamLinesCount = beam.getMaxBeamLinesCount();
		int staffIndex = beam.getUpperStaffIndex();
		int voiceIndex = firstChordMP.voice;
		int i = 0;
		StemDirection dir = Up;
		for (BeamWaypoint waypoint : beam.getWaypoints()) {
			Chord chord = waypoint.getChord();
			ElementSpacing cs = column.getElement(chord);
			ChordNotation cn = (ChordNotation) cs.notation;
			if (i == 0)
				dir = cn.stemDirection;
			chordNa[i] = cn.notes;
			AccidentalsNotation aa = cn.accidentals;
			stemX[i] = cs.offsetIs + aa.widthIs + chordNa[i].stemOffsetIs;
			i++;
		}

		//compute the stem alignments
		BeamStemAlignments bsa = computeStemAlignments(chordNa, stemX, staffLinesCount, beamLinesCount, dir);

		//save stems in notations
		It<BeamWaypoint> waypoints = it(beam.getWaypoints());
		for (BeamWaypoint waypoint : waypoints) {
			Chord chord = waypoint.getChord();
			ChordNotation notation = column.getNotation(chord);
			notation.stem = bsa.stemAlignments[waypoints.getIndex()];
		}
	}

	/**
	 * Computes the vertical positions of the stems of the given
	 * chord layouts grouped by a beam within a single staff and single measure.
	 * @param chordNa          the alignments of all chords of the beam
	 * @param stemX            the horizontal positions of the stems in interline spaces
	 * @param staffLinesCount  the number of lines in this staff
	 * @param beamLinesCount   the number of lines of the beam
	 * @param stemDirection    the direction of the stem
	 * @return  the alignments of all stems of the given chords                        
	 */
	public BeamStemAlignments computeStemAlignments(NotesNotation[] chordNa, float[] stemX,
		int staffLinesCount, int beamLinesCount, StemDirection stemDirection) {
		
		//get appropriate beam design
		BeamLines beamDesign;
		switch (beamLinesCount) {
			case 1:
				beamDesign = new OneLine(stemDirection, staffLinesCount);
				break;
			case 2:
				beamDesign = new TwoLines(stemDirection, staffLinesCount);
				break;
			case 3:
				beamDesign = new ThreeLines(stemDirection, staffLinesCount);
				break;
			default:
				beamDesign = new MultipleLines(stemDirection, staffLinesCount, beamLinesCount);
		}

		//compute beautiful slant
		float slantIs = computeSlant(beamDesign, chordNa, stemX, stemDirection, staffLinesCount);

		//compute stem alignments
		BeamStemAlignments beamstemalignments = computeStemLengths(beamDesign, chordNa, stemX, slantIs,
			beamLinesCount, stemDirection);

		return beamstemalignments;
	}

	/**
	 * Computes the slant that fits best to the given beam.
	 * The slant is defined as the directed vertical distance of
	 * the first stem endpoint and the last stem endpoint in IS.
	 * @param beamDesign     design information about the beam
	 * @param chordNa        the alignments of all chords of the beam
	 * @param stemX          the horizontal positions of the stems in interline spaces   
	 * @param stemDirection  the direction of the stems
	 * @param staffLinesCount  the number of lines in this staff
	 */
	private float computeSlant(BeamLines beamDesign, NotesNotation[] chordNotesAlignment,
		float[] positionX, StemDirection stemDirection, int staffLinesCount) {
		//collect relevant note line positions (positions of the outermost notes)
		int firstRelevantNoteLP;
		int lastRelevantNoteLP;
		int chordsCount = chordNotesAlignment.length;
		int[] relevantNoteLPs = new int[chordsCount];
		if (stemDirection == StemDirection.Down) {
			for (int i = 0; i < chordsCount; i++) {
				relevantNoteLPs[i] = chordNotesAlignment[i].getLps().getBottom();
			}
		}
		else if (stemDirection == StemDirection.Up) {
			for (int i = 0; i < chordsCount; i++) {
				relevantNoteLPs[i] = chordNotesAlignment[i].getLps().getTop();
			}
		}
		firstRelevantNoteLP = relevantNoteLPs[0];
		lastRelevantNoteLP = relevantNoteLPs[chordsCount - 1];

		//if the notes outline the same interval e.g. a e a e => horizontal beam
		if (chordsCount >= 4 && chordsCount % 2 == 0) {
			boolean usehorizontalbeam = true;
			for (int i = 2; i < chordsCount; i++) {
				if (relevantNoteLPs[i % 2] != relevantNoteLPs[i]) {
					usehorizontalbeam = false;
				}
			}
			if (usehorizontalbeam) {
				return 0;
			}
		}

		//if all middlenotes are further away from the beam than a direct line from
		//the first to the last note, a normal slant is used
		boolean useDefaultSlant = true;
		for (int i = 1; i < chordsCount - 2; i++) {
			float lp = firstRelevantNoteLP + 1f * (lastRelevantNoteLP - firstRelevantNoteLP) * i /
				(chordsCount - 1);
			if ((stemDirection == StemDirection.Up && relevantNoteLPs[i] > Math.ceil(lp)) ||
				(stemDirection == StemDirection.Down && relevantNoteLPs[i] < Math.floor(lp))) {
				useDefaultSlant = false;
				break;
			}
		}
		if (useDefaultSlant) {
			//use default rules (Ted Ross page 111)
			float lengthX = positionX[chordsCount - 1] - positionX[0];
			return computeNormalBeamSlant(beamDesign, firstRelevantNoteLP, lastRelevantNoteLP, lengthX,
				staffLinesCount);
		}

		//rule Ted Ross page 97 bottom
		//When the first and last notes are on different staff degrees, and all inside notes
		//descend to the last note, the beam slants one-half space in the direction
		//of the run of the inside notes.
		if (firstRelevantNoteLP != lastRelevantNoteLP) {
			boolean ascend = true;
			for (int i = 2; i < chordsCount; i++) {
				if (relevantNoteLPs[i - 1] >= relevantNoteLPs[i]) {
					ascend = false;
					break;
				}
			}
			if (ascend) {
				return beamDesign.getSlantAscendingMiddleNotesIs();
			}
			boolean descend = true;
			for (int i = 1; i < chordsCount - 1; i++) {
				if (relevantNoteLPs[i - 1] <= relevantNoteLPs[i]) {
					descend = false;
					break;
				}
			}
			if (descend) {
				return beamDesign.getSlantDescendingMiddleNotesIs();
			}
		}

		//otherwise: horizontal beam
		return 0;
	}

	/**
	 * This method calculates the slant of a beam in IS.
	 * The method of the calculations is a mixture between those of Ted Ross (p. 97ff)
	 * and the behaviour of Sibelius.
	 * @param beamDesign       design information about the beam
	 * @param firstNoteLP      the LP of the outermost note of the first chord
	 * @param lastNoteLP       the LP of the outermost note of the last chord
	 * @param lengthX          the horizontal distance between the first and the last note in IS
	 * @param staffLinesCount  the number of lines in this staff
	 */
	private float computeNormalBeamSlant(BeamLines beamDesign, int firstNoteLP, int lastNoteLP,
		float lengthX, int staffLinesCount) {
		ChordBeamSpacing spacing = ChordBeamSpacing.Normal;
		if (lengthX > wideSpacing) {
			spacing = ChordBeamSpacing.Wide;
		}
		else if (lengthX < closeSpacing) {
			spacing = ChordBeamSpacing.Close;
		}
		return computeSlant(beamDesign, firstNoteLP, lastNoteLP, spacing, staffLinesCount);
	}

	/**
	 * This method calculates the slant of a beam in IS.
	 * The method of the calculations is a mixture between those of Ted Ross (p. 97ff)
	 * and the behaviour of Sibelius.
	 * @param beamDesign       design information about the beam
	 * @param firstNoteLP      the LP of the outermost note of the first chord
	 * @param lastNoteLP       the LP of the outermost note of the last chord 
	 * @param spacing          the horizontal spacing of the beam chords
	 * @param staffLinesCount  the number of lines in this staff
	 */
	private float computeSlant(BeamLines beamDesign, int firstNoteLP, int lastNoteLP,
		ChordBeamSpacing spacing, int staffLinesCount) {
		int verticalDistanceLP = lastNoteLP - firstNoteLP;
		float slantIS = 0;
		boolean usesimpleslant = false;

		if (verticalDistanceLP == 0) {
			return 0;
		}
		if (Math.abs(verticalDistanceLP) > 8) {
			//interval greater than an octave -> only a small slant (Sibelius)
			return MathUtils.sign(verticalDistanceLP) * 1f;
		}

		int staffMaxLP = (staffLinesCount - 1) * 2;
		if (firstNoteLP >= staffMaxLP + 2 || lastNoteLP >= staffMaxLP + 2 //above the first leger line
			|| firstNoteLP <= -2 || lastNoteLP <= -2) //below the first leger line
		{
			usesimpleslant = true;
		}

		if (usesimpleslant || spacing == ChordBeamSpacing.Close) {
			slantIS = beamDesign.getCloseSpacingSlantIs(firstNoteLP, lastNoteLP) / 2;
		}
		else {
			if (spacing == ChordBeamSpacing.Wide) {
				slantIS = beamDesign.getWideSpacingSlantIs(firstNoteLP, lastNoteLP) / 2;
			}
			else {
				slantIS = beamDesign.getNormalSpacingSlantIs(firstNoteLP, lastNoteLP) / 2;
			}
		}
		if (verticalDistanceLP < 0) {
			slantIS = -1 * slantIS;
		}
		return slantIS;
	}

	/**
	 * Computes the lengths of all stems of the chord.
	 * @param beamDesign       design information about the beam
	 * @param chordNa          the alignments of all chords of the beam
	 * @param stemX            the horizontal positions of all stems of the beam
	 * @param slantIS          the slant in interline spaces        
	 * @param beamLinesCount   the number of beam lines         
	 * @param stemDirection    the direction of the stems
	 */
	private BeamStemAlignments computeStemLengths(BeamLines beamDesign, NotesNotation[] chordNa,
		float[] stemX, float slantIS, int beamLinesCount, StemDirection stemDirection) {

		int chordsCount = chordNa.length;
		StemNotation[] stemAlignments = new StemNotation[chordsCount];
		float beamStartLPCorrection = stemDirection.getSign() * 0.5f;

		boolean correctStemLength = false;
		float lengthX = stemX[chordsCount - 1] - stemX[0];
		float endline = 0;

		//try to place the beam at LP 4 first. correct step for step, if not ok.
		float beamStartLP = 4;

		//TODO: find algorithm that runs in constant time, not in linear time.
		while (true) {

			//for each chord: check if stem length is ok
			for (int i = 0; i < chordsCount; i++) {
				int highestNote = chordNa[i].getLps().getTop();
				int lowestNote = chordNa[i].getLps().getBottom();

				//compute stem length at current chord
				endline = beamStartLP + slantIS * 2 * (stemX[i] - stemX[0]) / lengthX;
				float startline;
				float currentStemLength = 0;

				if (stemDirection == StemDirection.Up) {
					startline = lowestNote;
					currentStemLength = (endline - highestNote) / 2;
				}
				else {
					startline = highestNote;
					currentStemLength = (lowestNote - endline) / 2;
				}

				//stem length ok?
				if (currentStemLength < beamDesign.getMinimumStemLengthIs()) {
					correctStemLength = false;
					break;
				}

				stemAlignments[i] = new StemNotation(startline, endline);
				correctStemLength = true;
			}

			//beam found, that looks ok for all stems?
			if (correctStemLength && beamDesign.isGoodStemPosition(beamStartLP, slantIS)) {
				//ok. we can break the loop.
				break;
			}
			else {
				//no. start another try.
				beamStartLP += beamStartLPCorrection;
			}

		}

		BeamStemAlignments beamStemAlignments = new BeamStemAlignments(stemAlignments,
			BeamLines.beamLineWidth, beamDesign.getDistanceBetweenBeamLinesIs(), beamLinesCount);
		return beamStemAlignments;
	}

	/**
	 * Returns true, when the lines of the given beam are completely outside the staff
	 * (not even touching a staff line).
	 * @param stemDirection     the direction of the stems
	 * @param firstStemEndLP    the LP of the endpoint of the first stem
	 * @param lastStemEndLP     the LP of the endpoint of the last stem  
	 * @param staffLinesCount   the number of staff lines 
	 * @param totalBeamWidthIS  the total height of the beam lines in IS
	 */
	public static boolean isBeamOutsideStaff(StemDirection stemDirection, float firstStemEndLP,
		float lastStemEndLP, int staffLinesCount, float totalBeamWidthIS) {
		float maxStaffLP = (staffLinesCount - 1) * 2;
		if (stemDirection == StemDirection.Up) {
			//beam lines above the staff?
			if (firstStemEndLP > maxStaffLP + totalBeamWidthIS * 2 &&
				lastStemEndLP > maxStaffLP + totalBeamWidthIS * 2) {
				return true;
			}
			//beam lines below the staff?
			if (firstStemEndLP < 0 && lastStemEndLP < 0) {
				return true;
			}
		}
		else if (stemDirection == StemDirection.Down) {
			//beam lines above the staff?
			if (firstStemEndLP > maxStaffLP && lastStemEndLP > maxStaffLP) {
				return true;
			}
			//beam lines below the staff?
			if (firstStemEndLP < -1 * totalBeamWidthIS * 2 && lastStemEndLP < -1 * totalBeamWidthIS * 2) {
				return true;
			}
		}
		return false;
	}

}
