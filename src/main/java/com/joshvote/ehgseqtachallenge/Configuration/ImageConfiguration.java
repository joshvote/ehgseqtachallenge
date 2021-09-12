package com.joshvote.ehgseqtachallenge.Configuration;

/**
 * Represents the high level configuration about the image that will be
 * generated by this app
 * 
 * @author Josh
 *
 */
public class ImageConfiguration {
    int width;
    int height;

    public ImageConfiguration(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * @return the image width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the image height
     */
    public int getHeight() {
        return height;
    }

}
