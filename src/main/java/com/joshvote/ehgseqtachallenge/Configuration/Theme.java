package com.joshvote.ehgseqtachallenge.Configuration;

/**
 * Represents a combination of RGB masks that collectively define a fully set
 * bitmask for the purposes of defining the balance of RGB in an output image
 * 
 * @author Josh
 *
 */
public class Theme {
    int redMask;
    int greenMask;
    int blueMask;

    public Theme(int redMask, int greenMask, int blueMask) {
        this.redMask = redMask;
        this.greenMask = greenMask;
        this.blueMask = blueMask;
    }

    /**
     * Gets the bitmask for the red channel
     * 
     * @return
     */
    public int getRedMask() {
        return redMask;
    }

    /**
     * Gets the bitmask for the green channel
     * 
     * @return
     */
    public int getGreenMask() {
        return greenMask;
    }

    /**
     * Gets the bitmask for the blue channel
     * 
     * @return
     */
    public int getBlueMask() {
        return blueMask;
    }
}
