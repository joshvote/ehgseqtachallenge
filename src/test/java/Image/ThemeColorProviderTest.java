package Image;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.joshvote.ehgseqtachallenge.Configuration.Theme;
import com.joshvote.ehgseqtachallenge.Image.ThemeColorProvider;

public class ThemeColorProviderTest {
	
	private void assertRgb(int r, int g, int b, int actual) {
		int actualR = (actual & 0x00ff0000) >> 16;
		int actualG = (actual & 0x0000ff00) >> 8;
		int actualB = (actual & 0x000000ff);
		
		assertTrue(String.format("Expected (%d, %d, %d) but got (%d, %d, %d)", r, g, b, actualR, actualG, actualB), r == actualR && g == actualG && b == actualB);
	}
	
	
	@Test
	public void testFlat5BitTheme() {
		ThemeColorProvider provider = new ThemeColorProvider(new Theme((int) 0b111110000000000, (int) 0b000001111100000, (int) 0b000000000011111));
		
		// test our extremes
		assertRgb(7, 7, 7, provider.generateColorForSequence(0));
		assertRgb(255, 255, 255, provider.generateColorForSequence(0xffffffff));
		
		// test our lowest value
		assertRgb(7, 7, 15, provider.generateColorForSequence(1));
		assertRgb(7, 15, 7, provider.generateColorForSequence(1 << 5));
		assertRgb(15, 7, 7, provider.generateColorForSequence(1 << 10));
		assertRgb(15, 15, 15, provider.generateColorForSequence(1 | 1 << 5 | 1 << 10));
		
		
		assertRgb(7, 31, 255, provider.generateColorForSequence(127));

	}
	
	@Test
	public void testInterleaved5BitTheme() {
		ThemeColorProvider provider = new ThemeColorProvider(new Theme((int) 0b100100100100100, (int) 0b010010010010010, (int) 0b001001001001001));
		
		// test our extremes
		assertRgb(7, 7, 7, provider.generateColorForSequence(0));
		assertRgb(255, 255, 255, provider.generateColorForSequence(0xffffffff));
		
		// test our lowest value
		assertRgb(7, 7, 15, provider.generateColorForSequence(1));
		assertRgb(7, 15, 7, provider.generateColorForSequence(2));
		assertRgb(15, 7, 7, provider.generateColorForSequence(4));
		assertRgb(15, 15, 15, provider.generateColorForSequence(7));
		
		
		assertRgb(255, 7, 7, provider.generateColorForSequence(0b100100100100100));
		assertRgb(129, 191, 15, provider.generateColorForSequence(0b111010001100000));

	}
}
