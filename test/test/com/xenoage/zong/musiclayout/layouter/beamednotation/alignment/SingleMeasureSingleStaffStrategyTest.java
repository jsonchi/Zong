package com.xenoage.zong.musiclayout.layouter.beamednotation.alignment;

import static com.xenoage.utils.math.Delta.DRf;
import static com.xenoage.zong.core.music.chord.StemDirection.Down;
import static com.xenoage.zong.core.music.chord.StemDirection.Up;
import static com.xenoage.zong.musiclayout.notations.chord.NoteDisplacementTest.note;
import static com.xenoage.zong.musiclayout.notator.chord.stem.beam.notation.OneMeasureOneStaff.isBeamOutsideStaff;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.xenoage.zong.core.music.chord.StemDirection;
import com.xenoage.zong.musiclayout.notations.chord.NoteDisplacement;
import com.xenoage.zong.musiclayout.notations.chord.NotesNotation;
import com.xenoage.zong.musiclayout.notations.chord.StemNotation;
import com.xenoage.zong.musiclayout.notator.chord.stem.beam.notation.OneMeasureOneStaff;
import com.xenoage.zong.musiclayout.notator.chord.stem.beam.notation.lines.OneLine;

/**
 * Tests for {@link OneMeasureOneStaff}.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class SingleMeasureSingleStaffStrategyTest {

	OneMeasureOneStaff strategy = new OneMeasureOneStaff();


	/**
	 * Attention! This test will only work correct if the values of Ted Ross
	 * are selected in the {@link OneLine}.
	 */
	@Test public void computeStemAlignments1StaffTest() {
		int linesCount = 5;
		float[] positionX;
		float[] positionX6 = { 2, 3, 4, 5, 6, 8 };
		float[] positionX4 = { 2, 4, 6, 8 };
		StemDirection dir;
		StemNotation[] alignment;
		NotesNotation[] cna;
		int singlebeam = 1;
		int doublebeam = 2;

		positionX = new float[2];
		positionX[0] = 2;
		positionX[1] = 8;
		dir = Down;

		int[][] notes1 = { { 6 }, { 7 } };
		cna = generateChordNotesAlignment(notes1);

		alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir)
			.getStemAlignments();

		assertEquals(-0.5, alignment[0].endLp, DRf);
		assertEquals(0, alignment[1].endLp, DRf);

		dir = Up;
		int[][] notes2 = { { -1, 1 }, { -1, 1 } };
		cna = generateChordNotesAlignment(notes2);
		alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir)
			.getStemAlignments();
		assertEquals(8, alignment[0].endLp, DRf);
		assertEquals(8, alignment[1].endLp, DRf);

		dir = Down;
		int[][] notes3 = { { 6 }, { 2 }, { 3 }, { 4 }, { 5 }, { 7 } };
		cna = generateChordNotesAlignment(notes3);
		alignment = strategy.computeStemAlignments(cna, positionX6, linesCount, singlebeam, dir)
			.getStemAlignments();
		assertEquals(1F, alignment[5].endLp - alignment[0].endLp, DRf);
		assertEquals(-4.5, alignment[0].endLp, DRf);
		assertEquals(-3.5, alignment[5].endLp, DRf);

		//Attention! In this example, Ted Ross breaks his own rules!
		dir = Down;
		int[][] notes4 = { { 7 }, { 8 }, { 5 }, { 5 } };
		cna = generateChordNotesAlignment(notes4);
		alignment = strategy.computeStemAlignments(cna, positionX4, linesCount, singlebeam, dir)
			.getStemAlignments();
		assertEquals(-1.0, alignment[0].endLp, DRf);
		assertEquals(-2.0, alignment[3].endLp, DRf);

		dir = Up;
		int[][] notes5 = { { 1 }, { -1 } };
		cna = generateChordNotesAlignment(notes5);
		alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir)
			.getStemAlignments();
		assertEquals(7, alignment[0].endLp, DRf);
		assertEquals(6, alignment[1].endLp, DRf);

		dir = Up;
		int[][] notes6 = { { 0 }, { -1 }, { 2 }, { 5 } };
		cna = generateChordNotesAlignment(notes6);
		alignment = strategy.computeStemAlignments(cna, positionX4, linesCount, singlebeam, dir)
			.getStemAlignments();
		assertEquals(8.5, alignment[0].endLp, DRf);
		assertEquals(11, alignment[3].endLp, DRf);

		dir = Down;
		int[][] notes7 = { { 11 }, { 15 } };
		cna = generateChordNotesAlignment(notes7);
		alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir)
			.getStemAlignments();
		assertEquals(3, alignment[0].endLp, DRf);
		assertEquals(4, alignment[1].endLp, DRf);

		dir = Down;
		int[][] notes8 = { { 11 }, { 12 } };
		cna = generateChordNotesAlignment(notes8);
		alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir)
			.getStemAlignments();
		assertEquals(3.5, alignment[0].endLp, DRf);
		assertEquals(4, alignment[1].endLp, DRf);

		//closer Spacing
		positionX[1] = 4;
		dir = Down;
		int[][] notes9 = { { 9 }, { 5 } };
		cna = generateChordNotesAlignment(notes9);
		alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir)
			.getStemAlignments();
		assertEquals(0, alignment[0].endLp, DRf);
		assertEquals(-1, alignment[1].endLp, DRf);

		//normal Spacing again
		positionX[1] = 8;
		dir = Down;
		int[][] notes10 = { { 9 }, { 13 } };
		cna = generateChordNotesAlignment(notes10);
		alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir)
			.getStemAlignments();
		assertEquals(3, alignment[0].endLp, DRf);
		assertEquals(4, alignment[1].endLp, DRf);

		dir = Down;
		int[][] notes11 = { { 5 }, { 6 }, { 9 }, { 8 }, { 7 }, { 8 } };
		cna = generateChordNotesAlignment(notes11);
		alignment = strategy.computeStemAlignments(cna, positionX6, linesCount, singlebeam, dir)
			.getStemAlignments();
		assertEquals(-1, alignment[0].endLp, DRf);
		assertEquals(1, alignment[5].endLp, DRf);

		dir = Up;
		cna = generateChordNotesAlignment(notes11);
		alignment = strategy.computeStemAlignments(cna, positionX6, linesCount, singlebeam, dir)
			.getStemAlignments();
		assertEquals(0, alignment[5].endLp - alignment[0].endLp, DRf);

		dir = Up;
		int[][] notes12 = { { -2 }, { -5 }, { -2 }, { -5 } };
		cna = generateChordNotesAlignment(notes12);
		alignment = strategy.computeStemAlignments(cna, positionX4, linesCount, singlebeam, dir)
			.getStemAlignments();
		assertEquals(4, alignment[0].endLp, DRf);
		assertEquals(4, alignment[3].endLp, DRf);

		dir = Up;
		cna = generateChordNotesAlignment(notes12);
		alignment = strategy.computeStemAlignments(cna, positionX4, linesCount, doublebeam, dir)
			.getStemAlignments();
		assertEquals(4.5, alignment[0].endLp, DRf);
		assertEquals(4.5, alignment[3].endLp, DRf);

		dir = Down;
		int[][] notes13 = { { 13 }, { 11 }, { 12 }, { 11 } };
		cna = generateChordNotesAlignment(notes13);
		alignment = strategy.computeStemAlignments(cna, positionX4, linesCount, singlebeam, dir)
			.getStemAlignments();
		assertEquals(4, alignment[0].endLp, DRf);
		assertEquals(4, alignment[3].endLp, DRf);

		dir = Down;
		cna = generateChordNotesAlignment(notes13);
		alignment = strategy.computeStemAlignments(cna, positionX4, linesCount, doublebeam, dir)
			.getStemAlignments();
		assertEquals(4, alignment[0].endLp, DRf);
		assertEquals(4, alignment[3].endLp, DRf);

	}

	private NotesNotation[] generateChordNotesAlignment(int[][] notes) {
		NotesNotation[] cna = new NotesNotation[notes.length];
		int i = 0;
		for (int[] chord : notes) {
			NoteDisplacement na[] = new NoteDisplacement[chord.length];
			int a = 0;
			for (int note : chord) {
				na[a++] = note(note);
			}
			cna[i++] = new NotesNotation(1, 1, na, new float[] {}, new int[] { 1 }, 0, false);
		}
		return cna;
	}

	@Test public void testIsBeamOutsideStaff() {
		//stem up, above staff
		assertTrue(isBeamOutsideStaff(Up, 13, 13, 5, 2));
		assertTrue(isBeamOutsideStaff(Up, 12.1f, 13, 5, 2));
		assertTrue(isBeamOutsideStaff(Up, 13f, 12.1f, 5, 2));
		assertFalse(isBeamOutsideStaff(Up, 11.9f, 11.9f, 5, 2));
		assertFalse(isBeamOutsideStaff(Up, 11.9f, 13, 5, 2));
		assertFalse(isBeamOutsideStaff(Up, 13f, 11.9f, 5, 2));
		//stem up, below staff
		assertTrue(isBeamOutsideStaff(Up, -1, -1, 5, 2));
		assertTrue(isBeamOutsideStaff(Up, -0.1f, -1, 5, 2));
		assertTrue(isBeamOutsideStaff(Up, -1, -0.1f, 5, 2));
		assertFalse(isBeamOutsideStaff(Up, 0.1f, 0.1f, 5, 2));
		assertFalse(isBeamOutsideStaff(Up, 0.1f, -1, 5, 2));
		assertFalse(isBeamOutsideStaff(Up, -1, 0.1f, 5, 2));
		//stem down, above staff
		assertTrue(isBeamOutsideStaff(Down, 9, 9, 5, 2));
		assertTrue(isBeamOutsideStaff(Down, 8.1f, 9, 5, 2));
		assertTrue(isBeamOutsideStaff(Down, 9, 8.1f, 5, 2));
		assertFalse(isBeamOutsideStaff(Down, 7.9f, 7.9f, 5, 2));
		assertFalse(isBeamOutsideStaff(Down, 7.9f, 9, 5, 2));
		assertFalse(isBeamOutsideStaff(Down, 9, 7.9f, 5, 2));
		//stem down, below staff
		assertTrue(isBeamOutsideStaff(Down, -5, -5, 5, 2));
		assertTrue(isBeamOutsideStaff(Down, -4.1f, -5, 5, 2));
		assertTrue(isBeamOutsideStaff(Down, -5, -4.1f, 5, 2));
		assertFalse(isBeamOutsideStaff(Down, -3.9f, -3.9f, 5, 2));
		assertFalse(isBeamOutsideStaff(Down, -3.9f, -5, 5, 2));
		assertFalse(isBeamOutsideStaff(Down, -5, -3.9f, 5, 2));
	}

}
