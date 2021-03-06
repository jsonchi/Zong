package com.xenoage.zong.core.music.direction;

import com.xenoage.zong.core.music.MusicElementType;
import com.xenoage.zong.core.position.MP;

import lombok.Data;
import lombok.EqualsAndHashCode;



/**
 * Class for a pedal direction. Start or stop.
 *
 * @author Andreas Wenger
 */
@Data @EqualsAndHashCode(callSuper=false)
public final class Pedal
	extends Direction {

	/** Start and stop marking type. */
	public enum Type {
		/** Depress the pedal. */
		Start,
		/** Release the pedal. */
		Stop;
	}

	/** The start or stop marking type. */
	private final Type type;

	@Override public MusicElementType getMusicElementType() {
		return MusicElementType.Pedal;
	}
	
	@Override public MP getMP() {
		return MP.getMPFromParent(this);
	}
	
}
