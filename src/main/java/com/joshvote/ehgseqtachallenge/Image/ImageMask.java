package com.joshvote.ehgseqtachallenge.Image;

import com.joshvote.ehgseqtachallenge.Configuration.ImageConfiguration;

/**
 * Simple image mask interface that defines a 2D image space where every pixel is either set/unset
 * @author Josh
 *
 */
public interface ImageMask {
	/**
	 * Returns the image configuration for this mask
	 * @return
	 */
	ImageConfiguration getImageConfiguration();
	
	/**
	 * Returns true if the pixel at x,y is masked. false otherwise
	 * @param x Must be in the range [0, width)
	 * @param y Must be in the range [0, height)
	 * @return
	 */
	boolean isMasked(int x, int y);
	
}
