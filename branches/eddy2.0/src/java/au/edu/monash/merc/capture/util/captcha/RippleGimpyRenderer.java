package au.edu.monash.merc.capture.util.captcha;

import java.awt.image.BufferedImage;

import com.jhlabs.image.RippleFilter;
import com.jhlabs.image.TransformFilter;

/**
 * RippleGimpyRenderer class
 * 
 * @version 2.0, 07/2011
 * @version 1.0
 * 
 */
public class RippleGimpyRenderer implements GimpyRenderer {

	/**
	 * Apply a RippleFilter to the image.
	 * 
	 * @param image
	 *            The image to be distorted
	 */
	@Override
	public void gimp(BufferedImage image) {
		RippleFilter filter = new RippleFilter();
		filter.setWaveType(RippleFilter.SINGLEFRAME);
		filter.setXAmplitude(2.6f);
		filter.setYAmplitude(1.7f);
		filter.setXWavelength(15);
		filter.setYWavelength(5);
		filter.setEdgeAction(TransformFilter.RANDOMPIXELORDER);
		ImageUtil.applyFilter(image, filter);
	}
}