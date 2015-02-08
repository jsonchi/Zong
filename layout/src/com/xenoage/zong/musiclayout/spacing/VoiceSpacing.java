package com.xenoage.zong.musiclayout.spacing;

import java.util.List;

import lombok.AllArgsConstructor;

import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.VoiceElement;

/**
 * This class contains the spacing of one voice
 * of a measure of a single staff.
 *
 * @author Andreas Wenger
 */
@AllArgsConstructor
public final class VoiceSpacing {

	/** The {@link Voice} this spacing belongs to. */
	public final Voice voice;
	/** The interline space in mm of this voice. */
	public final float interlineSpace;
	/** The {@link ElementSpacing}s of this voice. */
	public final List<ElementSpacing> elements;
	
	
	public ElementSpacing getLast() {
		return elements.get(elements.size() - 1);
	}
	
	/**
	 * Gets the spacing for the given element, or throws an
	 * {@link IllegalArgumentException} if it is not in this voice.
	 */
	public ElementSpacing getElement(VoiceElement element) {
		for (ElementSpacing e : elements) {
			if (e.notation.getElement() == element)
				return e;
		}
		throw new IllegalArgumentException("unknown element");
	}
	
}
