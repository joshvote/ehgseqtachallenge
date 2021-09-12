package com.joshvote.ehgseqtachallenge.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Font;
import java.awt.image.BufferedImage;

import org.junit.Test;

import com.joshvote.ehgseqtachallenge.Configuration.ImageConfiguration;
import com.joshvote.ehgseqtachallenge.Image.TextImageMask;

public class TextImageMaskTest {

    /**
     * This will catch any silly mistakes on the prebaked constants
     */
    @Test
    public void ensureFactorsForTotalPixelsSetupCorrectly() {
        assertNotNull(TextImageMask.FactorsForTotalPixels);
        assertNotEquals(0, TextImageMask.FactorsForTotalPixels.length);

        for (int i = 1; i < TextImageMask.FactorsForTotalPixels.length; i++) {
            assertTrue("FactorsForTotalPixels is out of order at index " + i, TextImageMask.FactorsForTotalPixels[i] > TextImageMask.FactorsForTotalPixels[i - 1]);
            assertTrue(String.format("FactorsForTotalPixels[%d] %d is not a factor of TextImageMask.TotalPixels", i, TextImageMask.FactorsForTotalPixels[i]), TextImageMask.TotalPixels % TextImageMask.FactorsForTotalPixels[i] == 0);
        }
    }

    /**
     * Just test some prebaked aspect ratios
     */
    @Test
    public void calculateOptimalImageSizeTest() {
        // test 1-1 - should generate the "squarest" possible value
        var imageCfg = TextImageMask.calculateOptimalImageSize(100.0 / 100.0);
        assertEquals(256, imageCfg.getWidth());
        assertEquals(128, imageCfg.getHeight());

        // 4:3
        imageCfg = TextImageMask.calculateOptimalImageSize(4.0 / 3.0);
        assertEquals(256, imageCfg.getWidth());
        assertEquals(128, imageCfg.getHeight());

        // 16:9
        imageCfg = TextImageMask.calculateOptimalImageSize(16.0 / 9.0);
        assertEquals(256, imageCfg.getWidth());
        assertEquals(128, imageCfg.getHeight());

        // 4:1
        imageCfg = TextImageMask.calculateOptimalImageSize(4.0 / 1.0);
        assertEquals(512, imageCfg.getWidth());
        assertEquals(64, imageCfg.getHeight());

        // 8:1
        imageCfg = TextImageMask.calculateOptimalImageSize(8.0 / 1.0);
        assertEquals(512, imageCfg.getWidth());
        assertEquals(64, imageCfg.getHeight());

        // 20:1
        imageCfg = TextImageMask.calculateOptimalImageSize(20.0 / 1.0);
        assertEquals(1024, imageCfg.getWidth());
        assertEquals(32, imageCfg.getHeight());

        // 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768

        // test a very long image
        imageCfg = TextImageMask.calculateOptimalImageSize(9999999.0 / 1.0);
        assertEquals(32768, imageCfg.getWidth());
        assertEquals(1, imageCfg.getHeight());

        // test a very tall image
        imageCfg = TextImageMask.calculateOptimalImageSize(1.0 / 9999999.0);
        assertEquals(1, imageCfg.getWidth());
        assertEquals(32768, imageCfg.getHeight());

    }

    /**
     * Really basic test to ensure we're not crashing and doing something that looks vaguely correct
     * 
     * Really hard thing to test - in production code I'd probably add some fancy 
     * fixed glyph fonts to really test this if required.
     */
    @Test
    public void generateForTextTest() {
        TextImageMask mask = TextImageMask.generateForText("test @-=#", new Font("Serif", Font.PLAIN, 100));
        assertNotNull(mask);

        int totalMasked = 0;
        int totalUnmasked = 0;
        for (int x = 0; x < mask.getImageConfiguration().getWidth(); x++) {
            for (int y = 0; y < mask.getImageConfiguration().getHeight(); y++) {
                if (mask.isMasked(x, y))
                    totalMasked++;
                else
                    totalUnmasked++;

            }
        }

        assertTrue(String.format("There should be at least a few masked pixels. We only got %d", totalMasked), totalMasked > 200);
        assertTrue(String.format("There should be at least a few unmasked pixels. We only got %d", totalUnmasked), totalUnmasked > 200);
    }

    @Test
    public void maskWorksAsExpected() {
        /***
         * Our mask will look like this:
         * 
         * 0 1 0 
         * 1 1 1 
         * 0 0 1
         */
        BufferedImage maskImage = new BufferedImage(3, 3, BufferedImage.TYPE_BYTE_BINARY);
        maskImage.setRGB(0, 0, 0);
        maskImage.setRGB(1, 0, Integer.MAX_VALUE);
        maskImage.setRGB(2, 0, 0);
        maskImage.setRGB(0, 1, Integer.MAX_VALUE);
        maskImage.setRGB(1, 1, Integer.MAX_VALUE);
        maskImage.setRGB(2, 1, Integer.MAX_VALUE);
        maskImage.setRGB(0, 2, 0);
        maskImage.setRGB(1, 2, 0);
        maskImage.setRGB(2, 2, Integer.MAX_VALUE);

        TextImageMask mask = new TextImageMask(maskImage, new ImageConfiguration(3, 3));
        assertFalse(mask.isMasked(0, 0));
        assertTrue(mask.isMasked(1, 0));
        assertFalse(mask.isMasked(2, 0));
        assertTrue(mask.isMasked(0, 1));
        assertTrue(mask.isMasked(1, 1));
        assertTrue(mask.isMasked(2, 1));
        assertFalse(mask.isMasked(0, 2));
        assertFalse(mask.isMasked(1, 2));
        assertTrue(mask.isMasked(2, 2));
    }
}
