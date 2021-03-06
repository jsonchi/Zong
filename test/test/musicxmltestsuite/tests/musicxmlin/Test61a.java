package musicxmltestsuite.tests.musicxmlin;

import static com.xenoage.utils.kernel.Range.range;
import static musicxmltestsuite.tests.utils.ChordTest.assertEqualsChordLyrics;
import musicxmltestsuite.tests.base.Base61a;
import musicxmltestsuite.tests.utils.ChordTest;

import org.junit.Test;

import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.chord.Chord;
import com.xenoage.zong.core.position.MP;

public class Test61a
	implements Base61a, MusicXmlInTest {

	private Score score = getScore();


	@Test public void test() {
		for (int iMeasure : range(3)) {
			for (int iElement : range(iMeasure == 2 ? 3 : 4)) {
				MP mp = MP.atElement(0, iMeasure, 0, iElement);
				Chord chord = ChordTest.getChordAt(score, mp);
				assertEqualsChordLyrics(expectedLyrics[iMeasure * 4 + iElement], chord, mp);
			}
		}
	}

}
