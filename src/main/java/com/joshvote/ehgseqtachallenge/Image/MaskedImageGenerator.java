package com.joshvote.ehgseqtachallenge.Image;

import java.awt.image.BufferedImage;

/**
 * Implements the top level logic to build a raw image for a give ImageMask + text
 * @author Josh
 *
 */
public class MaskedImageGenerator {
	ThemeColorProvider colorProvider;
	ImageMask mask;
	
	public MaskedImageGenerator(ThemeColorProvider colorProvider, ImageMask mask) {
		this.colorProvider = colorProvider;
		this.mask = mask;
	}
	
	private class PixelApplicator {
		int maskedSeq = TextImageMask.TotalPixels - 1;
		int unmaskedSeq = 0;
		
		void applyPixel(BufferedImage img, int x, int y, boolean masked) {
			if (!masked) {
    			img.setRGB(x, y, colorProvider.generateColorForSequence(maskedSeq--));
    		} else {
    			img.setRGB(x, y, colorProvider.generateColorForSequence(unmaskedSeq++));
    		}
		}
	}
	
	public BufferedImage generateImage() {
		BufferedImage img = new BufferedImage(mask.getImageConfiguration().getWidth(), mask.getImageConfiguration().getHeight(), BufferedImage.TYPE_INT_RGB);
		PixelApplicator applicator = new PixelApplicator();
        
        // it'd be nice to spatially group "similar" sequence numbers in the image in order to give a more pleasant color variation
        // so we do some fancy striping across the diagonals which will handle things nicely
		int width = mask.getImageConfiguration().getWidth();
		int height = mask.getImageConfiguration().getHeight();
		int totalDiagonals = width + height - 1;
		
        for (int diagonal = 0; diagonal < totalDiagonals; diagonal++) {
        	if (diagonal < height) {
        		// we're starting our diagonals from the leftmost vertical and working down
        		for (int x = 0, y = height - diagonal - 1; y < height && x < width; x++, y++ ) {
        			applicator.applyPixel(img, x, y, mask.isMasked(x, y)); // literally the first time I've had a reason to use a double variable for loop - yay!
        		}
        	} else {
        		// these are the diagonals starting from the bottom left and working across
        		// we're starting our diagonals from the leftmost vertical and working down
        		for (int x = diagonal - height + 1, y = 0; y < height && x < width; x++, y++ ) {
        			applicator.applyPixel(img, x, y, mask.isMasked(x, y)); // literally the first time I've had a reason to use a double variable for loop - yay!
        		}
        	}
        }
        
        return img;
	}
}
