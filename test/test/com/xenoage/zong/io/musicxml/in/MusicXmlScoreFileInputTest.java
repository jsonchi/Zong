package com.xenoage.zong.io.musicxml.in;

import static com.xenoage.utils.collections.CollectionUtils.alist;
import static com.xenoage.utils.io.FileFilters.xmlFilter;
import static com.xenoage.utils.jse.JsePlatformUtils.io;
import static com.xenoage.utils.jse.JsePlatformUtils.jsePlatformUtils;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.util.InconsistentScoreException;

/**
 * Tests for the {@link MusicXmlScoreFileInput} class.
 * 
 * @author Andreas Wenger
 */
public class MusicXmlScoreFileInputTest {

	public static final String dir11 = "data/test/scores/musicxml11/";
	public static final String dir20 = "data/test/scores/musicxml20/";


	/**
	 * It's too hard to check the contents of a MusicXML file
	 * automatically. We just try to load the score data and
	 * check if they could be loaded without problems.
	 * 
	 * We check all official MusicXML 1.1 and 2.0 sample files.
	 */
	@Test @SuppressWarnings("unused") public void testSampleFiles() {
		long startTime = System.currentTimeMillis();
		for (String file : getSampleFiles()) {
			try {
				Score score = new MusicXmlScoreFileInput().read(
					jsePlatformUtils().openFile(file), file);
				System.out.println("Loaded: " + file);
			} catch (InconsistentScoreException ex) {
				ex.printStackTrace();
				fail("Score inconsistent after loading: " + file);
			} catch (Exception ex) {
				ex.printStackTrace();
				fail("Failed to load file: " + file);
			}
		}
		long totalTime = System.currentTimeMillis() - startTime;
		//TEST
		//System.out.println(getClass() + ": " + totalTime);
	}

	/**
	 * It's too hard to check the contents of a MusicXML file
	 * automatically. We just try to load the score data and
	 * check if they could be loaded without problems.
	 * 
	 * We check all official MusicXML 1.1 sample files.
	 */
	public static List<String> getSampleFiles() {
		List<String> ret = alist();
		List<String> files = io().listFiles(dir11, xmlFilter);
		for (String file : files) {
			ret.add(dir11 + file);
		}
		files = io().listFiles(dir20, xmlFilter);
		for (String file : files) {
			ret.add(dir20 + file);
		}
		return ret;
	}

	/**
	 * Loads the given file from "data/test/scores/" as MusicXML 2.0
	 * and returns the score.
	 */
	public static Score loadXMLTestScore(String filename) {
		try {
			String filepath = "data/test/scores/test/" + filename;
			return new MusicXmlScoreFileInput().read(jsePlatformUtils().openFile(filepath), filepath);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Could not load file: " + filename);
			return null;
		}
	}

	/**
	 * Tests a single file.
	 */
	@Test public void testSingleFile() {
		String file = dir20 + "BeetAnGeSample.xml";
		try {
			new MusicXmlScoreFileInput().read(jsePlatformUtils().openFile(file), file);
			//TEST
			//ScoreTest.printScore(score);
		} catch (FileNotFoundException ex) {
			//file not there. ignore, since copyrighted file. - TODO: ask Michael Good for file license
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Could not load file: " + file);
		}
	}

}
