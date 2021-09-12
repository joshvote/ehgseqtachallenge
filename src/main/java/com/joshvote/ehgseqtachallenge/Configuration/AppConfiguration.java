package com.joshvote.ehgseqtachallenge.Configuration;

import java.awt.Font;

/**
 * Encapsulates the configuration for the app (generally sourced from CLI
 * arguments)
 * 
 * @author Josh
 *
 */
public class AppConfiguration {
    String text;
    String path;
    Theme theme;
    Font font;

    public AppConfiguration(Font font, String text, String path, Theme theme) {
        this.font = font;
        this.text = text;
        this.path = path;
        this.theme = theme;
    }

    /**
     * The text that will be rendered
     * 
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * The output file path
     * 
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * The font being used to generate text
     * 
     * @return
     */
    public Font getFont() {
        return font;
    }

    /**
     * @return the theme
     */
    public Theme getTheme() {
        return theme;
    }
}
