package au.edu.monash.merc.capture.util.imgcaptcha;

public class CaptchaException extends RuntimeException {
    public CaptchaException(String message) {
        super(message);
    }

    public CaptchaException(String message, Throwable cause) {
        super(message, cause);
    }
}
