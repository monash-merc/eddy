package au.edu.monash.merc.capture.util.captcha;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/**
 * DefaultTextProducer class implements the TextProducer interface produces the text string
 * 
 * @version 2.0, 07/2011
 * @version 1.0
 * 
 */
public class DefaultTextProducer implements TextProducer {

	private static final Random _gen = new SecureRandom();
	private static final int DEFAULT_LENGTH = 6;
	private static final char[] DEFAULT_CHARS = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 'w',
			'x', 'y', 'z', '2', '3', '4', '5', '6', '7', '8', '9', };

	private final int _length;
	private final char[] _srcChars;

	public DefaultTextProducer() {
		this(DEFAULT_LENGTH, DEFAULT_CHARS);
	}

	public DefaultTextProducer(int length) {
		this(length, DEFAULT_CHARS);
	}

	public DefaultTextProducer(int length, char[] srcChars) {
		_length = length;
		_srcChars = srcChars != null ? Arrays.copyOf(srcChars, srcChars.length) : DEFAULT_CHARS;
	}

	@Override
	public String getText() {
		int car = _srcChars.length - 1;

		String capText = "";
		for (int i = 0; i < _length; i++) {
			capText += _srcChars[_gen.nextInt(car) + 1];
		}

		return capText;
	}
}
