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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.domain.IPBlock;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.service.BlockIPService;
import au.edu.monash.merc.capture.util.MD5;

@Scope("prototype")
@Controller("user.userResetPwdAction")
public class ResetPasswdAction extends BaseAction {

	// dummy action id for security protection.
	private long actUId = -1;

	// user uid hash code
	private String usrIdCode;

	// dummy action name for security protection.
	private String act;

	// dummy reset password hashcode for security protection
	private String hashCd;

	// re-enter new password
	private String rePassword;

	private String securityCode;

	@Autowired
	private BlockIPService blockIPService;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public String verifyPasswdReset() {
		boolean hasErrors = verifyPasswordResetHasErrors();
		if (hasErrors) {
			addActionError(getText("resetpwdact.invalid.confirmation.link"));
			return ERROR;
		}
		// System.out.println("user id = " + actUId + ", act = " + act + ", idCode=" + usrIdCode + ", hashCd=" +
		// hashCd);
		try {
			user = this.userService.getUserById(actUId);
			if (user == null) {
				// User doesn't existed
				addActionError(getText("resetpwdact.invalid.confirmation.link"));
				return ERROR;
			}

			if (user.getResetPasswdHashCode() == null) {
				// The reset password link has been expired
				addActionError(getText("resetpwdact.expired.confirmation.link"));
				return ERROR;
			}

			if (user.getResetPasswdHashCode() != null && (!user.getResetPasswdHashCode().equals(hashCd))) {
				addActionError(getText("resetpwdact.invalid.confirmation.link"));
				return ERROR;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(getText("resetpwdact.validate.reset.password.confirmation.failed"));
			return ERROR;
		}
		return SUCCESS;
	}

	private boolean verifyPasswordResetHasErrors() {

		if ((actUId <= 0) || (act == null) || (usrIdCode == null) || (hashCd == null)) {
			return true;
		}
		if (act != null && (!act.equals(ActConstants.RESET_PWD_ACTION_NAME))) {
			return true;
		}

		return false;
	}

	public String resetPassword() {

		if (checkResetPasswordErrors()) {
			return INPUT;
		}
		try {
			User usr = this.userService.getUserById(user.getId());
			// can't find user, means the reset password link has been expired
			if (usr == null) {
				addActionError("resetpwdact.invalid.confirmation.link");
				return INPUT;
			}
			// reset password hash code is null, means the reset password link has been expired
			if (usr.getResetPasswdHashCode() == null) {
				// The reset password link has been expired
				addActionError(getText("resetpwdact.expired.confirmation.link"));
				return INPUT;
			}

			if (usr.getResetPasswdHashCode() != null && (!usr.getResetPasswdHashCode().equals(user.getResetPasswdHashCode()))) {
				addActionError(getText("resetpwdact.expired.confirmation.link"));
				return INPUT;
			}
			//
			usr.setPassword(MD5.hash(user.getPassword()));
			usr.setResetPasswdHashCode(null);
			this.userService.updateUser(usr);
			// sign a persistent User
			user = usr;
			// find any previous blocked ip, if find, just remove it.
			String ipAddress = request.getRemoteAddr();
			IPBlock ipBlock = this.blockIPService.getIPBlockByIp(ipAddress);
			if (ipBlock != null) {
				this.blockIPService.deleteIPBlock(ipBlock);
			}

			// set action finished messsage
			addActionMessage(getText("user.reset.password.successfully.msg", new String[] { user.getDisplayName() }));
			// set page title and navigation label
			setNavAfterRegSuccess();
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(getText("resetpwdact.failed.to.reset.password"));
			return INPUT;
		}
		return SUCCESS;
	}

	private void setNavAfterRegSuccess() {
		String startNav = getText("user.reset.passwd.action.title");
		setPageTitle(startNav);
		navigationBar = generateNavLabel(startNav, null, null, null, null, null);
	}

	private boolean checkResetPasswordErrors() {
		boolean hasError = false;
		if (!user.getPassword().equals(rePassword)) {
			addFieldError("password", getText("user.reset.two.passwords.not.same"));
			hasError = true;
		}

		if (isSecurityCodeError(securityCode)) {
			addFieldError("securityCode", getText("security.code.invalid"));
			hasError = true;
		}
		return hasError;
	}

	public void setBlockIPService(BlockIPService blockIPService) {
		this.blockIPService = blockIPService;
	}

	public long getActUId() {
		return actUId;
	}

	public void setActUId(long actUId) {
		this.actUId = actUId;
	}

	public String getUsrIdCode() {
		return usrIdCode;
	}

	public void setUsrIdCode(String usrIdCode) {
		this.usrIdCode = usrIdCode;
	}

	public String getHashCd() {
		return hashCd;
	}

	public void setHashCd(String hashCd) {
		this.hashCd = hashCd;
	}

	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
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
