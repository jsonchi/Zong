package com.xenoage.zong.musiclayout.stampings.bitmap;

import com.xenoage.utils.graphics.Units;
import com.xenoage.utils.graphics.color.ColorInfo;
import com.xenoage.utils.math.MathUtils;


/**
 * This class helps drawing nice lines on a bitmap,
 * like the screen or in a bitmap file.
 * 
 * Because a bitmap works with integer coordinates, no lines
 * thinner than 1 px can be drawn.
 * 
 * This class computes the best possible display of lines:
 * It ensures that each line is at least 1 px wide,
 * but is drawn with some transparency to fake the "thinner"-effect.
 *
 * @author Andreas Wenger
 */
public final class BitmapLine
{
  
	/** The width in mm, that fits best to the bitmap */
  public final float widthMm;
  /** The color, that fits best to the bitmap */
  public final ColorInfo color;
  
  
  /**
   * Creates a {@link BitmapLine} with the given width in mm and
   * the given color at the given scaling factor.
   * @param scaling  the current scaling factor. e.g. 1 means 72 dpi, 2 means 144 dpi.
   */
  public BitmapLine(float widthMm, ColorInfo color, float scaling)
  {
  	//width
    float widthPxFloat = Units.mmToPx(widthMm, scaling);
    float widthPx = MathUtils.clampMin(Math.round(widthPxFloat), 1);
    this.widthMm = Units.pxToMm(widthPx, scaling);
    //color
    if (widthPxFloat < 1)
    	this.color = new ColorInfo(color.r, color.g, color.b, (int) (color.a * widthPxFloat));
    else
    	this.color = color;
  }

}
