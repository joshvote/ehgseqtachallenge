package com.joshvote.ehgseqtachallenge;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.joshvote.ehgseqtachallenge.Configuration.AppConfiguration;
import com.joshvote.ehgseqtachallenge.Configuration.Theme;
import com.joshvote.ehgseqtachallenge.Image.MaskedImageGenerator;
import com.joshvote.ehgseqtachallenge.Image.TextImageMask;
import com.joshvote.ehgseqtachallenge.Image.ThemeColorProvider;

/**
 * Hello world!
 *
 */
public class App 
{
	final static String DefaultText = "Hello :)";
	final static String DefaultPath = "image.bmp";
	final static String DefaultTheme = "interleaved";
	
	public static final Theme Theme_Interleaved = new Theme((int) 0b100100100100100, (int) 0b010010010010010, (int) 0b001001001001001);
	public static final Theme Theme_Flat = new Theme((int) 0b111110000000000, (int) 0b000001111100000, (int) 0b000000000011111);
	public static final Theme Theme_Wat = new Theme((int)  0b110000110000100, (int) 0b001100001100010, (int) 0b000011000011001);
	
	// these themes dont conform to the challenge - they dont cover the full range of values - they just look nifty
	public static final Theme Theme_Pineapple = new Theme((int)  0b000011111100100, (int) 0b111111000001000, (int) 0);
	public static final Theme Theme_Green = new Theme((int)  0, (int) 0b111111000000100, (int) 0);
	public static final Theme Theme_Blue = new Theme((int)  0, (int) 0, (int) 0b001111111000100);
	public static final Theme Theme_Red = new Theme((int)  0b11111111, (int) 0, (int) 0);
	
	public static final Font Font_Default = new Font("Serif", Font.PLAIN, 100);
	
	
	/**
	 * generates the config from the command line args. Returns null if args are invalid
	 * @param args
	 * @return
	 */
	@Nullable
	private static AppConfiguration parseCli(String[] args) {
		Options options = new Options();
		
		options.addOption(Option.builder("text")
				.desc(String.format("The text to display in the image. Defaults to '%s'", DefaultText))
				.required(false)
				.hasArg()
				.argName("text")
				.build());
		
		options.addOption(Option.builder("path")
				.desc(String.format("The path to a file where the output image bytes will be written (will override existing files). Defaults to '%s'", DefaultPath))
				.required(false)
				.hasArg()
				.argName("path")
				.build());
		
		options.addOption(Option.builder("theme")
				.desc(String.format("The name of the theme. Not all themes implement the challenge correctly. Available themes 'interleaved', 'flat', 'pineapple', 'wat', 'green', 'blue', 'red'. Defaults to '%s'", DefaultTheme))
				.required(false)
				.hasArg()
				.argName("theme")
				.build());
    	
    	CommandLineParser parser = new DefaultParser();
    	
    	
    	CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "app", options );
			return null;
		}
    	
    	String text;
    	if (cmd.hasOption("text"))
    		text = cmd.getOptionValue("text");
    	else
    		text = DefaultText;
    	
    	String path;
    	if (cmd.hasOption("path"))
    		path = cmd.getOptionValue("path");
    	else
    		path = DefaultPath;
    	
    	
    	Font font = Font_Default;
    	
    	String themeName;
    	if (cmd.hasOption("theme"))
    		themeName = cmd.getOptionValue("theme");
    	else
    		themeName = DefaultTheme;
    	
    	
    	Theme theme;
    	switch(themeName) {
	    	case "interleaved":
	    		theme = Theme_Interleaved;
	    		break;
	    	case "flat":
	    		theme = Theme_Flat;
	    		break;	
	    	case "wat":
	    		theme = Theme_Wat;
	    		break;	
	    	case "pineapple":
	    		theme = Theme_Pineapple;
	    		break;	
	    	case "green":
	    		theme = Theme_Green;
	    		break;
	    	case "blue":
	    		theme = Theme_Blue;
	    		break;	
	    	case "red":
	    		theme = Theme_Red;
	    		break;	
    		default:
    			HelpFormatter formatter = new HelpFormatter();
    			formatter.printHelp( "app", options );
    			return null;
    	}
    	
    	
    	return new AppConfiguration(font, text, path, theme);
	}
	
    public static void main( String[] args )
    {
    	AppConfiguration cfg = parseCli(args);
    	if (cfg == null)
    		return;
    	

    	TextImageMask mask = TextImageMask.generateForText(cfg.getText(), cfg.getFont());
    	ThemeColorProvider colorProvider = new ThemeColorProvider(cfg.getTheme());
    	
        System.out.println(String.format("Generating a %d x %d image at %s", mask.getImageConfiguration().getWidth(), mask.getImageConfiguration().getHeight(), cfg.getPath()));
        
        MaskedImageGenerator generator = new MaskedImageGenerator(colorProvider, mask);  
        BufferedImage img = generator.generateImage();
        
        File f = new File(cfg.getPath());
        try {
			ImageIO.write(img, "bmp", f);
			System.out.println("success");
		} catch (IOException e) {
			e.printStackTrace();
		}
        

        return;
    }
}
