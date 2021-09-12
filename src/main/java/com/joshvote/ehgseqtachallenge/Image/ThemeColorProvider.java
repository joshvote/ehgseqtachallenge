package com.joshvote.ehgseqtachallenge.Image;

import java.util.Arrays;
import java.util.ArrayList;
import com.joshvote.ehgseqtachallenge.Configuration.Theme;

public class ThemeColorProvider {

    Theme theme;

    // these bit indexes are ordered from LSB to MSB. They hold the index of the bits that the color channel is going to use
    int[] redBitIndexes;
    int[] greenBitIndexes;
    int[] blueBitIndexes;

    public ThemeColorProvider(Theme theme) {
        this.theme = theme;

        // precalculate our bit indexes for everything up to 32bits
        ArrayList<Integer> foundRedBitIndexes = new ArrayList<Integer>();
        ArrayList<Integer> foundGreenBitIndexes = new ArrayList<Integer>();
        ArrayList<Integer> foundBlueBitIndexes = new ArrayList<Integer>();

        for (int i = 0; i < 32; i++) {
            int mask = 1 << i;

            if ((theme.getRedMask() & mask) != 0) {
                foundRedBitIndexes.add(i);
            }

            if ((theme.getGreenMask() & mask) != 0) {
                foundGreenBitIndexes.add(i);
            }

            if ((theme.getBlueMask() & mask) != 0) {
                foundBlueBitIndexes.add(i);
            }
        }

        redBitIndexes = Arrays.stream(foundRedBitIndexes.toArray(new Integer[foundRedBitIndexes.size()])).mapToInt(Integer::intValue).toArray();
        greenBitIndexes = Arrays.stream(foundGreenBitIndexes.toArray(new Integer[foundGreenBitIndexes.size()])).mapToInt(Integer::intValue).toArray();
        blueBitIndexes = Arrays.stream(foundBlueBitIndexes.toArray(new Integer[foundBlueBitIndexes.size()])).mapToInt(Integer::intValue).toArray();

        if (redBitIndexes.length > 8 || greenBitIndexes.length > 8 || blueBitIndexes.length > 8)
            throw new IllegalArgumentException("at least one of the color channels has too many bits");

    }

    /**
     * @return the theme
     */
    public Theme getTheme() {
        return theme;
    }

    private int calculateChannelValue(int[] bitIndexes, int sequenceNumber) {

        // calculate the raw channel value
        int channelValue = 0;
        for (int i = 0; i < bitIndexes.length; i++) {
            int mask = 1 << bitIndexes[i];
            if ((sequenceNumber & mask) != 0) {
                channelValue |= 1 << i; // we push by i because we want an N bit number, the position of the bits in the mask is irrelevant
            }
        }

        // now rescale it to 8 bit color space
        // at the moment (say we have 5 bits of color) we'll have a number in the range 0-31
        // we want a channel value in the range 0-255 so we have to double it by the number of bits we're short
        int normalisedValue;
        if (bitIndexes.length < 8) {
            normalisedValue = channelValue << (8 - bitIndexes.length);
            normalisedValue |= ((1 << (8 - bitIndexes.length)) - 1); // also set the low bits that we just added as 0
        } else {
            normalisedValue = channelValue & 0xff;
        }

        return normalisedValue;
    }

    /**
     * Returns a 32 bit integer with the following bits
     * 
     * MSB 
     * 8 bits - Alpha channel - 0x00 
     * 8 bits - red channel 
     * 8 bits - green channel
     * 8 bits - blue channel
     * LSB
     * 
     * It will do this for the sequence number based on the internally defined theme
     * 
     * @param sequenceNumber
     * @return
     */
    public int generateColorForSequence(int sequenceNumber) {
        int redChannel = calculateChannelValue(redBitIndexes, sequenceNumber);
        int greenChannel = calculateChannelValue(greenBitIndexes, sequenceNumber);
        int blueChannel = calculateChannelValue(blueBitIndexes, sequenceNumber);

        return (redChannel << 16) | (greenChannel << 8) | blueChannel;
    }
}
