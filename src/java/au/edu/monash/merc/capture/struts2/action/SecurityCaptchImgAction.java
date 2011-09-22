/*
 * Copyright (c) 2010-2011, Monash e-Research Centre
 * (Monash University, Australia)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of the Monash University nor the names of its
 * 	  contributors may be used to endorse or promote products derived from
 * 	  this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package au.edu.monash.merc.capture.struts2.action;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.util.captcha.GradiatedBackgroundProducer;
import au.edu.monash.merc.capture.util.captcha.ImageUtil;
import au.edu.monash.merc.capture.util.captcha.ImgCaptcha;

@Scope("prototype")
@Controller("security.captchImgAction")
public class SecurityCaptchImgAction extends BaseAction {

	/** The InputStream imageStream. * */
	protected InputStream imageStream;

	public String genSecurityCaptchCode() {

		ByteArrayOutputStream output = null;
		try {
			output = new ByteArrayOutputStream();
			ImgCaptcha captcha = new ImgCaptcha.Builder(200, 45).addText().addBackground(
					new GradiatedBackgroundProducer()).gimp().addNoise().addBorder().build();

			String code = captcha.getCode();
			//Save this code in the session.
			saveInSession(ActConstants.SESSION_SECURITY_CODE, code);	
			BufferedImage img = captcha.getImage();
			ImageUtil.writeImage(output, img);
			this.imageStream = new ByteArrayInputStream(output.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception e) {
					// ignore whatever.
				}
			}
		}

		return SUCCESS;
	}

	public InputStream getImageStream() {
		return imageStream;
	}

	public void setImageStream(InputStream imageStream) {
		this.imageStream = imageStream;
	}
}
