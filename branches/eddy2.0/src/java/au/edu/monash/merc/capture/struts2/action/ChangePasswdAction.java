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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.util.MD5;

@Scope("prototype")
@Controller("admin.changePwdAction")
public class ChangePasswdAction extends BaseAction {

	private String newPassword;

	// re-enter new password
	private String rePassword;

	private String securityCode;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public String showChangePwd() {
		try {
			user = retrieveLoggedInUser();
			if (user.getPassword().equals("ldap")) {
				addActionError(getText("user.change.ldap.password.not.allowed"));
				setNavBar();
				return ERROR;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(getText("user.show.change.password.failed"));
			return ERROR;
		}

		return SUCCESS;
	}

	public String changePassword() {
		try {
			boolean hasError = false;
			User foundUsr = retrieveLoggedInUser();
			String md5pwd = foundUsr.getPassword();
			String md5newpwd = MD5.hash(user.getPassword());

			if (!StringUtils.equals(md5pwd, md5newpwd)) {
				addFieldError("currentPassword", getText("user.change.password.incorrect.current.password"));
				hasError = true;
			}

			if (!StringUtils.equals(newPassword, rePassword)) {
				addFieldError("repassword", getText("user.change.password.two.passwords.not.same"));
				hasError = true;
			}
			if (isSecurityCodeError(securityCode)) {
				addFieldError("securityCode", getText("security.code.invalid"));
				hasError = true;
			}

			if (hasError) {
				return INPUT;
			}

			foundUsr.setPassword(MD5.hash(newPassword));
			this.userService.updateUser(foundUsr);
			user = foundUsr;
			addActionMessage(getText("user.change.password.success.msg", new String[] { user.getDisplayName() }));
			setNavBar();
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(getText("user.change.password.failed"));
			return ERROR;
		}
		return SUCCESS;
	}

	private void setNavBar() {
		String startNav = getText("user.display.home.action.title");
		String startNavLink = ActConstants.DISPLAY_USER_HOME_ACTION;
		String secondNav = getText("user.change.password.action.title");
		setPageTitle(secondNav);
		navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getRePassword() {
		return rePassword;
	}

	public void setRePassword(String rePassword) {
		this.rePassword = rePassword;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

}
