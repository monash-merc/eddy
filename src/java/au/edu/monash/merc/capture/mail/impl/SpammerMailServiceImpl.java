/**
 * 	Copyright (c) 2010-2011, Monash e-Research Centre
 *	(Monash University, Australia)
 * 	All rights reserved.
 *
 * 	Redistribution and use in source and binary forms, with or without
 * 	modification, are permitted provided that the following conditions are met:
 *		* Redistributions of source code must retain the above copyright
 *    	  notice, this list of conditions and the following disclaimer.
 *		* Redistributions in binary form must reproduce the above copyright
 *    	  notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *		* Neither the name of the Monash University nor the
 *    	  names of its contributors may be used to endorse or promote products
 *    	  derived from this software without specific prior written permission.
 *
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 *	EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 *	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 *	DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 *	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 *	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 *	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 *	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 *	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package au.edu.monash.merc.capture.mail.impl;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import au.edu.monash.merc.capture.exception.MailException;
import au.edu.monash.merc.capture.mail.MailService;

public class SpammerMailServiceImpl implements MailService {

	/** SMTP. * */
	private static final String SMTP = "smtp";

	/** SMTP_PROPERTY. * */
	private static final String SMTP_PROPERTY = "mail.smtp.host";

	private String smtpServer;

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	@Override
	public void sendMail(String emailFrom, String emailTo, String emailSubject, Map<String, String> templateValues,
			String templateFile, boolean isHtml) {
		throw new MailException("un-implemented");
	}

	public void sendMail(String emailFrom, String emailTo, String emailSubject, String emailBody, boolean isHtml) {

		Properties props = System.getProperties();
		props.put(SMTP_PROPERTY, smtpServer);
		Session session = Session.getDefaultInstance(props, null);
		Message msg = new MimeMessage(session);
		try {
			msg.setFrom(new InternetAddress(emailFrom));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo, false));
			msg.setSubject(emailSubject);
			msg.setText(emailBody);
			msg.setSentDate(new Date());
			Transport transport = session.getTransport(SMTP);
			transport.connect(smtpServer, "", "");
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			throw new MailException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		SpammerMailServiceImpl mail = new SpammerMailServiceImpl();
		mail.setSmtpServer("smtp.monash.edu.au");
		String emailFrom = "ozflux_data_management_system";
		String emailTo = "Xiaoming.Yu@monash.edu";
		String emailSubject = "Reset your password confirmation";

		StringBuilder builder = new StringBuilder();
		builder.append("Hi Nigel.Holdgate, ");
		builder.append("\n");
		builder.append("Someone, probably you, made a password recovery request from http://ands_ozflux.its.monash.edu.au.");
		builder.append("Please use the following URL to complete the password recovery. You will be sent to a page asking your email address and the new password. ");
		builder.append("\n");
		builder.append("URL: http://vera024.its.monash.edu.au:8080/thredds/resetPassword&hashcodevalue=REAAFSDFSADFasfsao322211fasdf");
		builder.append("\n");
		builder.append("\n");
		builder.append("Best regards,");
		builder.append("ANDS_OZFLUX Admininstrator: Peter Issac");

		String emailBody = builder.toString();

		mail.sendMail(emailFrom, emailTo, emailSubject, emailBody, false);
		System.out.println("send mail successfully");

	}
}
