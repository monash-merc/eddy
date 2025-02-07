package au.edu.monash.merc.capture.util.imgcaptcha;

import java.awt.image.BufferedImage;

public final class GeneratedImgCaptcha {
    private final BufferedImage image;

    private final String code;

    public GeneratedImgCaptcha(BufferedImage image, String code) {
        this.image = image;
        this.code = code;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getCode() {
        return code;
    }
}