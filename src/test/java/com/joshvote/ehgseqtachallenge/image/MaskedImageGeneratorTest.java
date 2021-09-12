package com.joshvote.ehgseqtachallenge.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import org.junit.Test;

import com.joshvote.ehgseqtachallenge.App;
import com.joshvote.ehgseqtachallenge.Configuration.Theme;
import com.joshvote.ehgseqtachallenge.Image.MaskedImageGenerator;
import com.joshvote.ehgseqtachallenge.Image.TextImageMask;
import com.joshvote.ehgseqtachallenge.Image.ThemeColorProvider;

public class MaskedImageGeneratorTest {

    private class PixelSource {
        int rgb;
        int x;
        int y;

        public PixelSource(int rgb, int x, int y) {
            this.rgb = rgb;
            this.x = x;
            this.y = y;
        }

        /**
         * @return the rgb
         */
        public int getRgb() {
            return rgb;
        }

        /**
         * @return the x
         */
        public int getX() {
            return x;
        }

        /**
         * @return the y
         */
        public int getY() {
            return y;
        }
    }

    private void doAllPixelsUniqueTest(Theme theme) {
        TextImageMask mask = TextImageMask.generateForText("this is a test", App.Font_Default);
        ThemeColorProvider colorProvider = new ThemeColorProvider(theme);

        MaskedImageGenerator gen = new MaskedImageGenerator(colorProvider, mask);
        BufferedImage img = gen.generateImage();

        assertEquals(img.getWidth(), mask.getImageConfiguration().getWidth());
        assertEquals(img.getHeight(), mask.getImageConfiguration().getHeight());
        assertEquals(TextImageMask.TotalPixels, img.getWidth() * img.getHeight()); // if this is failing - check TextImageMask - the bug is there

        HashMap<Integer, PixelSource> existingPixels = new HashMap<Integer, PixelSource>();
        for (int x = 0; x < mask.getImageConfiguration().getWidth(); x++) {
            for (int y = 0; y < mask.getImageConfiguration().getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                PixelSource src = new PixelSource(rgb, x, y);

                PixelSource existing = existingPixels.get(rgb);

                if (existing == null) {
                    existingPixels.put(rgb, src);
                } else {
                    assertNull(String.format("We have a duplicate pixel value of (%d, %d, %d) at [%d, %d] and [%d, %d]", (rgb & 0x00ff0000) >> 16, (rgb & 0x0000ff00) >> 8, (rgb & 0x000000ff), existing.getX(), existing.getY(), src.getX(),src.getY()), existing);
                }
            }
        }
    }

    /**
     * Simple validation to ensure that the generated image is nothing but unique
     * pixels
     * 
     * (ok - this is more of an integration test and should probably live in a
     * seperate package for integration tests but w/e)
     */
    @Test
    public void themeInterleavedPixelsUnique() {
        doAllPixelsUniqueTest(App.Theme_Interleaved);
    }

    /**
     * Simple validation to ensure that the generated image is nothing but unique
     * pixels
     * 
     * (ok - this is more of an integration test and should probably live in a
     * seperate package for integration tests but w/e)
     */
    @Test
    public void themeFlatPixelsUnique() {
        doAllPixelsUniqueTest(App.Theme_Flat);
    }

    /**
     * Simple validation to ensure that the generated image is nothing but unique
     * pixels
     * 
     * (ok - this is more of an integration test and should probably live in a
     * seperate package for integration tests but w/e)
     */
    @Test
    public void themeWatPixelsUnique() {
        doAllPixelsUniqueTest(App.Theme_Wat);
    }
}
