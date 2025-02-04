package au.edu.monash.merc.capture.util.imgcaptcha;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class CaptchaUtil {
    public static void writeImage(OutputStream os, BufferedImage bi) {
        try {
            ImageIO.write(bi, "png", os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}