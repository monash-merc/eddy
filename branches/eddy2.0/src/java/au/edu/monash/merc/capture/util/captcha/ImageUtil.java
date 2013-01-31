package au.edu.monash.merc.capture.util.captcha;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * ImageUtil class provides the filter and write an image functionalities
 * 
 * @version 2.0, 07/2011
 * @version 1.0
 * 
 */
public class ImageUtil {

	public static void applyFilter(BufferedImage img, ImageFilter filter) {
		FilteredImageSource src = new FilteredImageSource(img.getSource(), filter);
		Image fImg = Toolkit.getDefaultToolkit().createImage(src);
		Graphics2D g = img.createGraphics();
		g.drawImage(fImg, 0, 0, null, null);
		g.dispose();
	}

	public static void writeImage(OutputStream os, BufferedImage bi) {
		try {
			ImageIO.write(bi, "png", os);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
