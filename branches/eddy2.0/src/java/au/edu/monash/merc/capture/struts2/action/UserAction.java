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

import java.io.File;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.Avatar;
import au.edu.monash.merc.capture.domain.IPBlock;
import au.edu.monash.merc.capture.domain.Profile;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.domain.UserType;
import au.edu.monash.merc.capture.mail.MailService;
import au.edu.monash.merc.capture.service.BlockIPService;
import au.edu.monash.merc.capture.util.MD5;
import au.edu.monash.merc.capture.util.ldap.LdapUser;

@Scope("prototype")
@Controller("user.userAction")
public class UserAction extends BaseAction {

	@Autowired
	private BlockIPService blockIPService;

	@Autowired
	private MailService mailService;

	private String organization;

	private String securityCode;

	private String loginTryMsg;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private String requestUrl;

	private String applicationName;

	/**
	 * User registration
	 * 
	 * @return a String represents SUCCESS or ERROR.
	 */
	public String register() {
		// check security code first, if error, just return immediately.
		if (isSecurityCodeError(securityCode)) {
			addFieldError("securityCode", getText("security.code.invalid"));
			return INPUT;
		}

		try {
			user.setDisplayName(user.getFirstName() + " " + user.getLastName());
			// if errors existed
			if (validateUserReg()) {
				return INPUT;
			}
			// encrypt the user password
			user.setPassword(MD5.hash(user.getPassword()));
			user.setRegistedDate(GregorianCalendar.getInstance().getTime());
			// set the user email as a unique id
			user.setUniqueId(user.getEmail());
			// set the unique id hash code.
			user.setUidHashCode(generateSecurityHash(user.getEmail()));
			// set the activate hash code
			user.setActivationHashCode(generateSecurityHash(user.getEmail()));
			// set the user active into false
			user.setActivated(false);
			// set user type as a registered user.
			user.setUserType(UserType.REGUSER.code());

			// create a default user profile.
			Profile p = genProfile();
            p.setGender("Male");
			p.setOrganization(organization);
			user.setProfile(p);
			p.setUser(user);

			// create an avatar
			Avatar avatar = genAvatar(p.getGender());
			user.setAvatar(avatar);
			avatar.setUser(user);

			// save user
			this.userService.saveUser(user);
			// site name
			String serverQName = getServerQName();
			// start to send register email to admin for approval
			String activateURL = constructActivationURL(serverQName, user.getId(), user.getActivationHashCode());
			sendRegMailToAdmin(serverQName, user.getDisplayName(), user.getEmail(), organization, activateURL);

		} catch (Exception e) {
			// log the database error
			logger.error(e.getMessage());
			// reponse the action error
			addActionError(getText("user.registration.failed"));
			return INPUT;
		}

		// set action finished messsage
		addActionMessage(getText("user.register.finished.msg", new String[] { user.getDisplayName() }));
		setNavAfterRegSuccess();
		return SUCCESS;
	}

	private void setNavAfterRegSuccess() {
		String startNav = getText("user.register.action.title");
		setPageTitle(startNav);
		navigationBar = generateNavLabel(startNav, null, null, null, null, null);
	}

	private boolean validateUserReg() {

		boolean hasErrors = false;
		try {

			// duplicated email is not allowed
			boolean emailExisted = userService.checkEmailExisted(user.getEmail());
			if (emailExisted) {
				addFieldError("email", getText("user.reg.email.already.registed"));
				hasErrors = true;
			}
			// duplicated user display name is not allowed
			boolean displayNameExisted = userService.checkUserDisplayNameExisted(user.getDisplayName());
			if (displayNameExisted) {
				addFieldError("displayName", getText("user.reg.display.name.already.registed"));
				hasErrors = true;
			}
		} catch (Exception e) {
			addFieldError("userAccount", getText("user.reg.check.failed"));
			hasErrors = true;
		}
		return hasErrors;
	}

	/**
	 * ldap user registration.
	 * 
	 * @return a String represents SUCCESS or ERROR.
	 */
	public String registerLdapUser() {
		// If security code error. just return immediately, not go further.
		if (isSecurityCodeError(securityCode)) {
			addFieldError("securityCode", getText("security.code.invalid"));
			return INPUT;
		}
		LdapUser ldapUsr = null;
		try {
			// if validate ldap user failed, just return to the ldap user registration page.
			ldapUsr = validateLdapUserReg();
			if (ldapUsr == null) {
				return INPUT;
			}
			// try to register ldap user in the database
			user.setDisplayName(ldapUsr.getDisplayName());
			user.setFirstName(ldapUsr.getFirstName());
			user.setLastName(ldapUsr.getLastName());
			// set ldap user password as ldap
			user.setPassword("ldap");
			user.setRegistedDate(GregorianCalendar.getInstance().getTime());
			user.setUidHashCode(generateSecurityHash(user.getUniqueId()));
			// set user email which get from ldap server
			user.setEmail(ldapUsr.getMail());
			user.setActivationHashCode(generateSecurityHash(user.getUniqueId()));

			user.setActivated(false);
			user.setUserType(UserType.REGUSER.code());

			// create a default user profile.
			Profile p = genProfile();
			p.setOrganization("Monash University");

			p.setGender(ldapUsr.getGender());
			user.setProfile(p);
			p.setUser(user);
			// create an avatar
			Avatar avatar = genAvatar(p.getGender());
			avatar.setUser(user);
			user.setAvatar(avatar);

			this.userService.saveUser(user);
			// site name
			String serverQName = getServerQName();
			// start to send register email to admin for approval
			String activateURL = constructActivationURL(serverQName, user.getId(), user.getActivationHashCode());
			sendRegMailToAdmin(serverQName, user.getDisplayName(), user.getEmail(), p.getOrganization(), activateURL);

		} catch (Exception e) {
			// log the database error
			logger.error(e.getMessage());
			// reponse the action error
			addActionError(getText("user.registration.failed"));
			return INPUT;
		}

		// set action finished messsage
		addActionMessage(getText("user.register.finished.msg", new String[] { user.getDisplayName() }));
		setNavAfterLdapRegSuccess();
		return SUCCESS;
	}

	private Profile genProfile() {
		// create a default user profile.
		return new Profile();
	}

	private Avatar genAvatar(String maleOrFemale) {
		Avatar avatar = new Avatar();
		String avatarFile = null;
		if (StringUtils.isBlank(maleOrFemale)) {
			avatarFile = "avatar" + File.separator + "male.png";
		} else {
			if (StringUtils.equalsIgnoreCase(maleOrFemale, "male")) {
				avatarFile = "avatar" + File.separator + "male.png";
			} else if (StringUtils.equalsIgnoreCase(maleOrFemale, "female")) {
				avatarFile = "avatar" + File.separator + "female.png";
			} else {
				avatarFile = "avatar" + File.separator + "male.png";
			}
		}
		avatar.setFileName(avatarFile);
		avatar.setFileType("png");
		return avatar;
	}

	private void setNavAfterLdapRegSuccess() {
		String startNav = getText("user.ldap.register.action.title");
		setPageTitle(startNav);
		navigationBar = generateNavLabel(startNav, null, null, null, null, null);
	}

	private LdapUser validateLdapUserReg() {

		LdapUser ldapUsr = null;
		// verify monash authcat user first. if ldap authentication failed, just return
		try {
			ldapUsr = this.userService.checkLdapUser(user.getUniqueId(), user.getPassword());
			if (ldapUsr == null) {
				addFieldError("uniqueId", getText("user.reg.ldap.invalid.authcateId.or.password"));
				return null;
			}
		} catch (Exception e) {
			logger.error(e);
			addFieldError("checkUserLdapError", getText("user.req.ldap.check.user.account.ldap.failed"));
			return null;
		}

		try {
			boolean existed = this.userService.checkUserUniqueIdExisted(user.getUniqueId());
			if (existed) {
				addFieldError("uniqueId", getText("user.reg.ldap.authcate.id.already.registed"));
				// set ldap user to null
				logger.error("user authcate id is already registered in the system.");
				ldapUsr = null;
			}
			boolean emailRegistered = this.userService.checkEmailExisted(ldapUsr.getMail());
			if (emailRegistered) {
				addFieldError("email", getText("user.reg.ldap.authcate.email.already.registed"));
				logger.error("user email is already registered in the system.");
				ldapUsr = null;
			}
		} catch (Exception e) {
			addFieldError("checkUserDbError", getText("user.reg.ldap.check.user.account.db.failed"));
			logger.error(e);
			ldapUsr = null;
		}

		return ldapUsr;
	}

	private void sendRegMailToAdmin(String serverQName, String userName, String userEmail, String organization, String activationURL) {

		String activateEmailTemplateFile = "activateAccountEmailTemplate.ftl";
		String appName = configSetting.getPropValue(ConfigSettings.APPLICATION_NAME);
		// prepare to send email.
		String adminEmail = configSetting.getPropValue(ConfigSettings.SYSTEM_SERVICE_EMAIL);
		String subject = getText("user.register.account.activation.mail.title");

		Map<String, String> templateMap = new HashMap<String, String>();
		templateMap.put("RegisteredUser", userName);
		templateMap.put("UserEmail", userEmail);
		templateMap.put("Organization", organization);
		templateMap.put("ActivationURL", activationURL);
		templateMap.put("SiteName", serverQName);
		templateMap.put("AppName", appName);

		this.mailService.sendMail(adminEmail, adminEmail, subject, templateMap, activateEmailTemplateFile, true);
	}

	private String constructActivationURL(String serverQName, long userId, String activationCode) {

		String appcontext = getAppContextPath();

		String pkName = "admin";
		String actionName = "verifyAccount.jspx?";
		String actNamePair = "actionId=" + ActConstants.ACTIVATION_ACTION_NAME;
		String regUidPair = "&regUid=" + userId;

		String hashCodePair = "&activationHashCode=" + activationCode;

		StringBuffer activationURL = new StringBuffer();
		// application root url
		activationURL.append(serverQName).append(appcontext).append(ActConstants.URL_PATH_DEIM);
		// action name
		activationURL.append(pkName).append(ActConstants.URL_PATH_DEIM).append(actionName);
		// actId, idcode, act name and hash code
		activationURL.append(actNamePair).append(regUidPair).append(hashCodePair);

		return new String(activationURL).trim();
	}

	public String showLogin() {
		// try to remove any previous session values.
		removeFromSession(ActConstants.SESSION_AUTHENTICATION_FLAG);
		removeFromSession(ActConstants.SESSION_AUTHEN_USER_ID);
		removeFromSession(ActConstants.SESSION_AUTHEN_USER_NAME);

		initIPBlockInfoForLoginPage();
		setNavForLogin();
		return SUCCESS;
	}

	private void setNavForLogin() {
		String startNav = getText("user.login.action.title");
		setPageTitle(getText("user.login.action.title"));
		String startNavLink = "user/showLogin.jspx";
		navigationBar = generateNavLabel(startNav, startNavLink, null, null, null, null);
	}

	/**
	 * init the IPBlock info. if any blocked IP time out, just unblock it.
	 */
	private void initIPBlockInfoForLoginPage() {
		String loginTryValue = configSetting.getPropValue(ConfigSettings.ALLOW_LOGIN_TRY_TIMES);
		String blockWaitingTimeValue = configSetting.getPropValue(ConfigSettings.LOGIN_IP_BLOCK_WAITING_TIMES);

		try {

			int defaultAllowedTryTimes = Integer.valueOf(loginTryValue).intValue();
			int defaultWaitingTimes = Integer.valueOf(blockWaitingTimeValue).intValue();
			long currentRequestTime = System.currentTimeMillis();
			// first of all, get the request ip address
			String ipAddress = request.getRemoteAddr();
			// then check the block ip info if any
			IPBlock ipblock = checkIPBlockInfo(ipAddress, currentRequestTime, defaultAllowedTryTimes, defaultWaitingTimes);

			// no ip block for this request. just initilize the login try time info.
			if (ipblock == null) {
				loginTryMsg = getText("user.login.try.times", new String[] { String.valueOf(defaultAllowedTryTimes) });
			} else {
				int triedTimes = ipblock.getTryTimes();
				if (triedTimes == defaultAllowedTryTimes) {
					loginTryMsg = getText("user.login.try.too.manay.times", new String[] { String.valueOf(defaultWaitingTimes) });
				} else {// blocked within the waiting time
					loginTryMsg = getText("user.login.try.times", new String[] { String.valueOf(defaultAllowedTryTimes - triedTimes) });
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			loginTryMsg = getText("user.login.try.times", new String[] { String.valueOf(loginTryValue) });
		}
	}

	/**
	 * User login
	 * 
	 * @return a String represents SUCCESS or ERROR.
	 */
	public String login() {
		// set action title
		setNavForLogin();
		// first of all, get the request ip address
		String ipAddress = request.getRemoteAddr();

		long requestTime = System.currentTimeMillis();
		String loginTryValue = configSetting.getPropValue(ConfigSettings.ALLOW_LOGIN_TRY_TIMES);
		String blockWaitingTimeValue = configSetting.getPropValue(ConfigSettings.LOGIN_IP_BLOCK_WAITING_TIMES);

		int defaultAllowedTryTimes = Integer.valueOf(loginTryValue).intValue();
		int defaultWaitingTimes = Integer.valueOf(blockWaitingTimeValue).intValue();

		try {
			// Check the block ip info,
			IPBlock ipBlock = checkIPBlockInfo(ipAddress, requestTime, defaultAllowedTryTimes, defaultWaitingTimes);

			// if user already overdo a login, just block it.
			if (ipBlock != null && (ipBlock.getTryTimes() == defaultAllowedTryTimes)) {
				loginTryMsg = getText("user.login.try.too.manay.times", new String[] { String.valueOf(defaultWaitingTimes) });
				return INPUT;
			}

			// found some errors, then update the ip block
			if (validateLoginInputs()) {
				updateIPBlockInfo(ipAddress, requestTime, defaultAllowedTryTimes, defaultWaitingTimes);
				return INPUT;
			}

			String ldapStr = configSetting.getPropValue(ConfigSettings.LDAP_AUTH_SUPPORTED);

			boolean ldapsupported = Boolean.valueOf(ldapStr);

			User verifiedUser = userService.validateLogin(user.getUniqueId(), user.getPassword(), ldapsupported);
			if (verifiedUser == null) {
				// can't validate login because usr is null
				updateIPBlockInfo(ipAddress, requestTime, defaultAllowedTryTimes, defaultWaitingTimes);
				addActionError(getText("user.login.failed"));
				return INPUT;
			} else {
				if (!verifiedUser.isActivated()) {
					updateIPBlockInfo(ipAddress, requestTime, defaultAllowedTryTimes, defaultWaitingTimes);
					addActionError(getText("user.login.account.inactive.error"));
					return INPUT;
				} else {
					// remove any ipblock
					if (ipBlock != null) {
						this.blockIPService.deleteIPBlock(ipBlock);
					}
					user = verifiedUser;
					saveInSession(ActConstants.SESSION_AUTHENTICATION_FLAG, ActConstants.SESSION_LOGIN);
					saveInSession(ActConstants.SESSION_AUTHEN_USER_ID, user.getId());
					saveInSession(ActConstants.SESSION_AUTHEN_USER_NAME, user.getDisplayName());
				}
			}
			requestUrl = (String) findInSession(ActConstants.REQUEST_URL);
			// remove the requested url.
			removeFromSession(ActConstants.REQUEST_URL);

			// populate the application name in success logged in page to display the application name
			applicationName = configSetting.getPropValue(ConfigSettings.APPLICATION_NAME);
			if (StringUtils.isBlank(requestUrl)) {
				requestUrl = ActConstants.DISPLAY_USER_HOME_ACTION;
			}

			return SUCCESS;

		} catch (Exception e) {
			logger.error(e.getMessage());

			// got some errors, then update the ip block
			updateIPBlockInfo(ipAddress, requestTime, defaultAllowedTryTimes, defaultWaitingTimes);
			addActionError(getText("user.login.failed"));
			return INPUT;
		}
	}

	/**
	 * validate the user inputs for login action
	 * 
	 * @return a boolean value represents wheather there is an error or not.
	 */
	private boolean validateLoginInputs() {
		boolean errors = false;
		if (user.getUniqueId() == null || (user.getUniqueId().trim().equals(""))) {
			addFieldError("uniqueId", getText("user.login.uniqueId.required"));
			errors = true;
		}
		if (user.getPassword() == null || (user.getPassword().trim().equals(""))) {
			addFieldError("password", getText("user.login.password.required"));
			errors = true;
		}
		if (securityCode == null || (securityCode.trim().equals(""))) {
			addFieldError("securityCode", getText("security.code.required"));
			errors = true;
		} else {
			if (isSecurityCodeError(securityCode)) {
				addFieldError("securityCode", getText("security.code.invalid"));
				errors = true;
			}
		}
		return errors;
	}

	// check ip block info, if any ip already timeout. just remove (unblock) it
	private IPBlock checkIPBlockInfo(String ipAddress, long requestTime, int defaultAllowTryTimes, int defaultWaitingTimes) {
		IPBlock ipBlock = this.blockIPService.getIPBlockByIp(ipAddress);
		if (ipBlock != null) {
			long blockedTime = ipBlock.getBlockTimes();
			if (requestTime >= (blockedTime + defaultWaitingTimes * 60 * 1000)) {
				this.blockIPService.deleteIPBlock(ipBlock);
				return null;
			}
		}
		return ipBlock;
	}

	// update the ip block info.
	private void updateIPBlockInfo(String ipAddress, long requestTime, int defaultAllowTryTimes, int defaultWaitingTimes) {
		try {
			IPBlock ipBlock = this.blockIPService.getIPBlockByIp(ipAddress);
			// no ip block for request for current ip, create new one
			if (ipBlock == null) {
				ipBlock = new IPBlock();
				ipBlock.setBlockTimes(System.currentTimeMillis());
				ipBlock.setIp(ipAddress);
				ipBlock.setTryTimes(1);
				this.blockIPService.saveIPBlock(ipBlock);
				loginTryMsg = getText("user.login.try.times", new String[] { String.valueOf(defaultAllowTryTimes - 1) });
			} else {
				// already has a ip block, just update it.
				int triedTimes = ipBlock.getTryTimes();
				int currentTriedTimes = triedTimes + 1;

				if (currentTriedTimes >= defaultAllowTryTimes) {
					ipBlock.setTryTimes(defaultAllowTryTimes);
					loginTryMsg = getText("user.login.try.too.manay.times", new String[] { String.valueOf(defaultWaitingTimes) });
				} else {
					ipBlock.setTryTimes(currentTriedTimes);
					loginTryMsg = getText("user.login.try.times", new String[] { String.valueOf(defaultAllowTryTimes - currentTriedTimes) });
				}
				this.blockIPService.updateIPBlock(ipBlock);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public String logout() {
		cleanSession();
		return SUCCESS;
	}

	private void cleanSession() {
		removeFromSession(ActConstants.SESSION_AUTHENTICATION_FLAG);
		removeFromSession(ActConstants.SESSION_AUTHEN_USER_ID);
		removeFromSession(ActConstants.SESSION_AUTHEN_USER_NAME);

		// Clean login user view collection page parameters
		removeFromSession(ActConstants.SESSION_VIEW_COLLECTION_PAGE_SIZE);
		removeFromSession(ActConstants.SESSION_VIEW_COLLECTION_ORDERBY);
		removeFromSession(ActConstants.SESSION_VIEW_COLLECTION_ORDERBY_TYPE);

		// Clean search page parameters
		removeFromSession(ActConstants.SESSION_SEARCH_PAGE_SIZE);
		removeFromSession(ActConstants.SESSION_SEARCH_ORDERBY);
		removeFromSession(ActConstants.SESSION_SEARCH_ORDERBY_TYPE);
		removeFromSession(ActConstants.SEARCH_CONDITION_KEY);

		// Clean user page parameters
		removeFromSession(ActConstants.SESSION_USER_PAGE_SIZE);
		removeFromSession(ActConstants.SESSION_USER_ORDERBY);
		removeFromSession(ActConstants.SESSION_USER_ORDERBY_TYPE);

		// Clean Events page parameters
		removeFromSession(ActConstants.SESSION_EVENTS_PAGE_SIZE);
		removeFromSession(ActConstants.SESSION_EVENTS_ORDERBY);
		removeFromSession(ActConstants.SESSION_EVENTS_ORDERBY_TYPE);

		// just in case;
		removeFromSession(ActConstants.REQUEST_URL);
	}

	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public BlockIPService getBlockIPService() {
		return blockIPService;
	}

	public void setBlockIPService(BlockIPService blockIPService) {
		this.blockIPService = blockIPService;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getLoginTryMsg() {
		return loginTryMsg;
	}

	public void setLoginTryMsg(String loginTryMsg) {
		this.loginTryMsg = loginTryMsg;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

}
