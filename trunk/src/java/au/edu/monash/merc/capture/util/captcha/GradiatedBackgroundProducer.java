package au.edu.monash.merc.capture.util.captcha;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * GradiatedBackgroundProducer which implements BackgroundProducer interface provides the background of an image
 * 
 * @version 2.0, 07/2011
 * @version 1.0
 * 
 */

public class GradiatedBackgroundProducer implements BackgroundProducer {

	private Color _fromColor = new Color(179, 179, 179);
	private Color _toColor = Color.WHITE;

	@Override
	public BufferedImage addBackground(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		return getBackground(width, height);
	}

	public void setFromColor(Color fromColor) {
		_fromColor = fromColor;
	}

	public void setToColor(Color toColor) {
		_toColor = toColor;
	}

	@Override
	public BufferedImage getBackground(int width, int height) {
		// create an opaque image
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = img.createGraphics();
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setRenderingHints(hints);

		// create the gradient color
		GradientPaint ytow = new GradientPaint(0, 0, _fromColor, width, height, _toColor);

		g.setPaint(ytow);
		// draw gradient color
		g.fill(new Rectangle2D.Double(0, 0, width, height));

		// draw the transparent image over the background
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return img;
	}

}
