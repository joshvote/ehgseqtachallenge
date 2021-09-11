package com.joshvote.ehgseqtachallenge.Image;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import com.joshvote.ehgseqtachallenge.Configuration.ImageConfiguration;

/**
 * A ImageMask that is generated from some input text
 * @author Josh
 *
 */
public class TextImageMask implements ImageMask {

	ImageConfiguration imageConfiguration;
	boolean[][] mask;
	
	/**
	 * Builds a text image mask from an existing buffered image. Any pixel with a non black color will be considered as being mask = true
	 * @param src
	 * @param imageConfig
	 */
	public TextImageMask(BufferedImage src, ImageConfiguration imageConfig) {
		this.imageConfiguration = imageConfig;
		this.mask = new boolean[imageConfig.getWidth()][imageConfig.getHeight()];
		
		for (int x = 0; x < imageConfig.getWidth(); x++) {
			for (int y = 0; y < imageConfig.getHeight(); y++) {
				int color = src.getRGB(x, y) & 0xffffff;
				mask[x][y] = color > 0;
			}
		}
	}
	
	@Override
	public ImageConfiguration getImageConfiguration() {
		return imageConfiguration;
	}

	@Override
	public boolean isMasked(int x, int y) {
		return mask[x][y];
	}

	public static final int TotalPixels = 32768; 
	/**
	 * Every single factor for TotalPixels - must be sorted
	 */
	public static final int[] FactorsForTotalPixels = new int[] { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768 };
	
	/**
	 * Calculates the optimal image size for a given aspectRatio and the constant TotalPixels
	 * @param aspectRatio defined as width / height
	 * @return
	 */
	public static ImageConfiguration calculateOptimalImageSize(double aspectRatio) {
		/*
		 * gogo gadget algebra
		 * 
		 * width * height = totalPixels
		 * width / height = aspectRatio
		 * 
		 * height = totalPixels / width
		 * width / (totalPixels / width) = aspectRatio
		 * =>
		 * width = sqrt(totalPixels * aspectRatio)
		 * height = totalPixels / width
		 * 
		 * */
		
		// this will need to be rounded to the nearest whole factor of total pixels 
		double perfectWidth = Math.sqrt((double)TotalPixels * aspectRatio);
		int actualWidth;
		
		//
		int factorIndex = Arrays.binarySearch(FactorsForTotalPixels, (int) perfectWidth);
		if (factorIndex >= 0) {
			actualWidth = FactorsForTotalPixels[factorIndex];
		} else {
			factorIndex = ~factorIndex;
			if (factorIndex >= FactorsForTotalPixels.length)
				factorIndex = FactorsForTotalPixels.length - 1;
			actualWidth = FactorsForTotalPixels[factorIndex];
		}
		
		return new ImageConfiguration(actualWidth, TotalPixels / actualWidth);
	}
	
	/**
	 * Generates a new TextImageMask for the specified text string and pixel count
	 * 
	 * In a "real" app this would probably be offloaded to a dedicated factory to better seperate concerns and ease testing
	 * @param text
	 * @param font
	 * @param totalPixels
	 * @return
	 */
	public static TextImageMask generateForText(String text, Font font) {
		// start by calculating the width/height in a vacuum in order to figure out an aspect ratio
		BufferedImage singlePixelImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
		FontMetrics fontMetrics = singlePixelImage.createGraphics().getFontMetrics(font);
		int textHeight = fontMetrics.getHeight();
		int textWidth = fontMetrics.stringWidth(text);
		double targetAspectRatio = (double)textWidth / (double)textHeight;
		
		// now we figure out how big our image will be and render the font into it 
		ImageConfiguration imageCfg = calculateOptimalImageSize(targetAspectRatio);
		
		BufferedImage maskImage = new BufferedImage(imageCfg.getWidth(), imageCfg.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D graphics = maskImage.createGraphics();
		graphics.setColor(new Color(0,0,0));
		graphics.fillRect(0,  0,  imageCfg.getWidth(), imageCfg.getHeight());
		graphics.setColor(new Color(255,255,255));
		
		// we downscale the font slightly so it fits nicely (if we have enough height to anyway)
		float scale = 0.8f;
		graphics.setFont(font.deriveFont((float) Math.ceil(((float)imageCfg.getHeight()) * scale))); 
		graphics.drawString(text, 0, (int)Math.ceil((float)imageCfg.getHeight() * scale));
		
		
		return new TextImageMask(maskImage, imageCfg);
	}
}
