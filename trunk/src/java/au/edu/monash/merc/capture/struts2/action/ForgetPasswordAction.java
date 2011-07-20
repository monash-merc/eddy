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
package au.edu.monash.merc.capture.struts2.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.mail.MailService;

@Scope("prototype")
@Controller("user.forgetPWDAction")
public class ForgetPasswordAction extends BaseAction {

	@Autowired
	private MailService mailService;

	private String securityCode;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Forgot Password
	 * 
	 * @return a String represents SUCCESS or ERROR.
	 */
	public String forgotPassword() {

		// security code error. just return immediately, not go further.
		if (isSecurityCodeError(securityCode)) {
			addFieldError("securityCode", getText("security.code.invalid"));
			return INPUT;
		}
		try {

			User foundUser = this.userService.getByUserEmail(user.getEmail());
			// can find the user in the database
			if (foundUser == null) {
				addActionError(getText("useract.forgotpassword.name.or.email.invalid"));
				return INPUT;
			}

			// user account is inactive
			if (!foundUser.isActivated()) {
				addActionError(getText("useract.forgotpassword.inactive.account"));
				return INPUT;
			}
			if (foundUser.getPassword().equals("ldap")) {
				addActionError(getText("useract.forgotpassword.cannot.reset.ldap.account"));
				return INPUT;
			}

			String displayName = foundUser.getDisplayName();
			String userFullName = user.getFirstName() + " " + user.getLastName();

			// user first name and last name is not the same first name and last name as the registered
			if (!StringUtils.equals(userFullName, displayName)) {
				addActionError(getText("useract.forgotpassword.name.or.email.invalid"));
				return INPUT;
			}

			String resetPasswdCode = generateSecurityHash(foundUser.getEmail());
			foundUser.setResetPasswdHashCode(resetPasswdCode);

			this.userService.updateUser(foundUser);

			// start to send an email to user

			// construct a reset password url
			String resetPwdUrl = constructResetPwdUrl(foundUser.getId(), foundUser.getUidHashCode(), foundUser.getResetPasswdHashCode());
			// site name
			String serverQName = getServerQName();
			sendResetPasswdEmailToUser(serverQName, foundUser.getEmail(), resetPwdUrl);

			// set action finished messsage
			addActionMessage(getText("useract.forgotpassword.request.finished.msg", new String[] { displayName }));
			// set the page title and nav label
			setNavAfterRegSuccess();
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(getText("useract.forgotpassword.request.reset.password.failed"));
			return INPUT;
		}
		return SUCCESS;
	}

	private void setNavAfterRegSuccess() {
		String startNav = getText("user.reset.passwd.action.title");
		setPageTitle(startNav);
		navigationBar = generateNavLabel(startNav, null, null, null, null, null);
	}

	private void sendResetPasswdEmailToUser(String serverQName, String userEmail, String resetPasswdURL) {
		String resetPasswdMailTemplateFile = "resetPasswordEmailTemplate.ftl";

		// prepare to send email.
		String appName = configSetting.getPropValue(ConfigSettings.APPLICATION_NAME);
		String adminEmail = configSetting.getPropValue(ConfigSettings.SYSTEM_SERVICE_EMAIL);
		String subject = getText("useract.forgotpassword.email.title");

		Map<String, String> templateMap = new HashMap<String, String>();
		templateMap.put("userEmail", userEmail);
		templateMap.put("resetPasswdURL", resetPasswdURL);
		templateMap.put("SiteName", serverQName);
		templateMap.put("AppName", appName);

		// send an email to user
		this.mailService.sendMail(adminEmail, userEmail, subject, templateMap, resetPasswdMailTemplateFile, true);
	}

	private String constructResetPwdUrl(long actUsrId, String uidCode, String hashCode) {

		String serverQName = getServerQName();
		String appcontext = getAppContextPath();

		String pkName = "user";
		String actionName = "verifyPwdReset.jspx?";
		String actNamePair = "act=" + ActConstants.RESET_PWD_ACTION_NAME;
		String actIdPair = "&actUId=" + actUsrId;
		String idCodePair = "&usrIdCode=" + uidCode;
		String hashCodePair = "&hashCd=" + hashCode;

		StringBuffer resetPwdUrl = new StringBuffer();
		// application root url
		resetPwdUrl.append(serverQName).append(appcontext).append(ActConstants.URL_PATH_DEIM);
		// action name
		resetPwdUrl.append(pkName).append(ActConstants.URL_PATH_DEIM).append(actionName);
		// actId, idcode, act name and hash code
		resetPwdUrl.append(actNamePair).append(actIdPair).append(idCodePair).append(hashCodePair);

		return new String(resetPwdUrl).trim();
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	
	

}
