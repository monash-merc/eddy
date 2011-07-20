package au.edu.monash.merc.capture.util.captcha;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * TransparentBackgroundProducer class which implements the BackgroundProducer interface generates a transparent
 * background.
 * 
 * @version 2.0, 07/2011
 * @version 1.0
 * 
 */
public class TransparentBackgroundProducer implements BackgroundProducer {

	@Override
	public BufferedImage addBackground(BufferedImage image) {
		return getBackground(image.getWidth(), image.getHeight());
	}

	@Override
	public BufferedImage getBackground(int width, int height) {
		BufferedImage bg = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		Graphics2D g = bg.createGraphics();

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		g.fillRect(0, 0, width, height);

		return bg;
	}

}
