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
	
	public static final Theme Theme_Interleaved = new Theme((int) 0b100100100100100, (int) 0b010010010010010, (int) 0b001001001001001);
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
    	Theme theme = Theme_Interleaved;
    	
    	
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
