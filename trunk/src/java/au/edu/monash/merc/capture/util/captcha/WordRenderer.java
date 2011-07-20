package au.edu.monash.merc.capture.util.captcha;

import java.awt.image.BufferedImage;

/**
 * Interface WordRenderer class
 * 
 * @version 2.0, 07/2011
 * @version 1.0
 * 
 */
public interface WordRenderer {

	/**
	 * Render a word to a BufferedImage.
	 * 
	 * @param word
	 * @param image
	 */
	public void render(String word, BufferedImage image);

}
