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
import au.edu.monash.merc.capture.domain.Profile;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.domain.UserType;
import au.edu.monash.merc.capture.dto.page.Pagination;
import au.edu.monash.merc.capture.service.DMService;

@Scope("prototype")
@Controller("admin.adminAction")
public class AdminAction extends BaseAction {

	private long regUid;

	private String activationHashCode;

	private String actionId;

	private String organization;

	@Autowired
	private DMService dmService;

	private User regUser;

	private Pagination<User> userPagination;

	private String manageType;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public String verifyAccount() {

		setNavBar(getText("admin.activate.user.account.action.title"));
		boolean hasErrors = checkErrorsInActivationLink();
		if (hasErrors) {
			addActionError(getText("admin.activate.account.link.invalid"));
			return ERROR;
		}

		try {
			user = retrieveLoggedInUser();
			if ((user.getUserType() != UserType.ADMIN.code()) && (user.getUserType() != UserType.SUPERADMIN.code())) {
				addActionError(getText("admin.activate.account.permission.denied"));
				return ERROR;
			}

			// User regUser = this.userService.getUserById(regUid);
			Profile pro = this.dmService.getUserProfile(regUid);
			if (pro == null) {
				addActionError(getText("admin.activate.account.link.invalid"));
				return ERROR;
			}
			organization = pro.getOrganization();
			regUser = pro.getUser();

			if (regUser.isActivated()) {
				addActionError(getText("admin.activate.account.link.expired"));
				return ERROR;
			}
			if (regUser.isRejected()) {
				addActionError(getText("admin.activate.account.link.expired"));
				return ERROR;
			}
			// System.out.println("user activation hash code: " + user.getActivationHashCode());
			if (regUser.getActivationHashCode() != null && (!regUser.getActivationHashCode().equals(activationHashCode))) {
				addActionError(getText("admin.activate.account.link.invalid"));
				return ERROR;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(getText("admin.validate.activation.link.failed"));
			return ERROR;
		}

		return SUCCESS;
	}

	private void setNavBar(String actionTitle) {
		String startNav = getText("user.all.users.title");
		String startNavLink = ActConstants.LIST_ALL_USERS_ACTION;
		// String startNav = getText("admin.main.nav.title.name");
		String secondNav = actionTitle; // getText("admin.activate.user.account.action.title");
		setPageTitle(startNav, secondNav);
		navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
	}

	public boolean checkErrorsInActivationLink() {
		if ((regUid <= 0) || (actionId == null) || (activationHashCode == null)) {
			return true;
		}
		if (actionId != null && (!actionId.equals(ActConstants.ACTIVATION_ACTION_NAME))) {
			return true;
		}
		return false;
	}

	public String activateUserAccount() {
		// set page title and navigation label
		setNavBar(getText("admin.activate.user.account.action.title"));
		if (checkActivateInfo()) {
			addActionError(getText("admin.activate.account.link.invalid"));
			return ERROR;
		}
		try {
			// check the admin permission
			user = retrieveLoggedInUser();
			if ((user.getUserType() != UserType.ADMIN.code()) && (user.getUserType() != UserType.SUPERADMIN.code())) {
				addActionError(getText("admin.manage.user.account.permission.denied"));
				return ERROR;
			}

			User checkedUser = this.userService.getUserById(regUser.getId());

			if (checkedUser == null) {
				// User doesn't existed
				addActionError(getText("admin.activate.account.link.invalid"));
				return ERROR;
			}
			if (checkedUser.isActivated()) {
				addActionError(getText("admin.activate.account.link.expired"));
				return ERROR;
			}

			if (checkedUser.isRejected()) {
				addActionError(getText("admin.activate.account.link.expired"));
				return ERROR;
			}

			if (checkedUser.getActivationHashCode() != null && (!checkedUser.getActivationHashCode().equals(regUser.getActivationHashCode()))) {
				addActionError(getText("admin.activate.account.link.invalid"));
				return ERROR;
			}
			if (checkedUser.getEmail() != null && (!checkedUser.getEmail().equals(regUser.getEmail()))) {
				addActionError(getText("admin.activate.account.link.invalid"));
				return ERROR;
			}

			checkedUser.setActivated(true);
			this.userService.updateUser(checkedUser);
			// set action finished messsage
			sendApprovalAccountEmail(checkedUser.getDisplayName(), checkedUser.getEmail(), organization);
			addActionMessage(getText("admin.activate.account.success.msg", new String[] { checkedUser.getEmail() }));
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("admin.activate.account.failed"));
			return ERROR;
		}
		return SUCCESS;
	}

	private boolean checkActivateInfo() {
		if (regUser == null) {
			return true;
		}

		if (regUser.getId() <= 0 || regUser.getActivationHashCode() == null || regUser.getEmail() == null || organization == null) {
			return true;
		}

		return false;
	}

	private void sendApprovalAccountEmail(String userFullName, String userEmail, String organization) {

		String approveAccountMailTemplateFile = "approveUserRegistrationEmailTemplate.ftl";
		// site name
		String serverQName = getServerQName();
		// prepare to send email.
		String appName = configSetting.getPropValue(ConfigSettings.APPLICATION_NAME);
		String adminEmail = configSetting.getPropValue(ConfigSettings.SYSTEM_SERVICE_EMAIL);
		String subject = getText("user.account.activation.notification.mail.title");

		Map<String, String> templateMap = new HashMap<String, String>();
		templateMap.put("RegisteredUser", userFullName);
		templateMap.put("UserEmail", userEmail);
		// Organization
		templateMap.put("Organization", organization);
		templateMap.put("SiteName", serverQName);
		templateMap.put("AppName", appName);

		// send an email to user
		this.dmService.sendMail(adminEmail, userEmail, subject, templateMap, approveAccountMailTemplateFile, true);
	}

	public String rejectUserAccount() {
		// set page title and navigation label
		setNavBar(getText("admin.activate.user.account.action.title"));
		if (checkActivateInfo()) {
			addActionError(getText("admin.activate.account.link.invalid"));
			return ERROR;
		}
		try {
			// check the admin permission
			user = retrieveLoggedInUser();
			if ((user.getUserType() != UserType.ADMIN.code()) && (user.getUserType() != UserType.SUPERADMIN.code())) {
				addActionError(getText("admin.manage.user.account.permission.denied"));
				return ERROR;
			}

			User checkedUser = this.userService.getUserById(regUser.getId());

			if (checkedUser == null) {
				// User doesn't existed
				addActionError(getText("admin.activate.account.link.invalid"));
				return ERROR;
			}
			if (checkedUser.isActivated()) {
				addActionError(getText("admin.activate.account.link.expired"));
				return ERROR;
			}

			if (checkedUser.isRejected()) {
				addActionError(getText("admin.activate.account.link.expired"));
				return ERROR;
			}

			if (checkedUser.getActivationHashCode() != null && (!checkedUser.getActivationHashCode().equals(regUser.getActivationHashCode()))) {
				addActionError(getText("admin.activate.account.link.invalid"));
				return ERROR;
			}
			if (checkedUser.getEmail() != null && (!checkedUser.getEmail().equals(regUser.getEmail()))) {
				addActionError(getText("admin.activate.account.link.invalid"));
				return ERROR;
			}
			checkedUser.setRejected(true);
			this.userService.updateUser(checkedUser);
			sendRejectEmailToUser(checkedUser.getDisplayName(), checkedUser.getEmail(), organization);
			addActionMessage(getText("admin.reject.account.success.msg", new String[] { checkedUser.getEmail() }));
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("admin.activate.account.failed"));
			return ERROR;
		}
		return SUCCESS;
	}

	private void sendRejectEmailToUser(String userFullName, String userEmail, String organization) {

		String approveAccountMailTemplateFile = "rejectUserRegistrationEmailTemplate.ftl";
		// site name
		String serverQName = getServerQName();
		// prepare to send email.
		String appName = configSetting.getPropValue(ConfigSettings.APPLICATION_NAME);
		String adminEmail = configSetting.getPropValue(ConfigSettings.SYSTEM_SERVICE_EMAIL);
		String subject = getText("user.account.reject.notification.mail.title");

		Map<String, String> templateMap = new HashMap<String, String>();
		templateMap.put("RegisteredUser", userFullName);
		templateMap.put("UserEmail", userEmail);
		// Organization
		templateMap.put("Organization", organization);
		templateMap.put("SiteName", serverQName);
		templateMap.put("AppName", appName);

		// send an email to user
		this.dmService.sendMail(adminEmail, userEmail, subject, templateMap, approveAccountMailTemplateFile, true);
	}

	public String listUsers() {
		setNavBar(getText("admin.list.all.users.action.title"));
		try {
			user = retrieveLoggedInUser();
			populateUserPageParams();
			persistPageSortParamsInSession(ActConstants.SESSION_USER_PAGE_SIZE, ActConstants.SESSION_USER_ORDERBY,
					ActConstants.SESSION_USER_ORDERBY_TYPE, ActConstants.OrderByActionType.USER.actionType());
			userPagination = this.userService.getAllUsers(pageNo, sizePerPage, populateOrderBy());
			populatePaginationLinks(ActConstants.LIST_ALL_USERS_ACTION, ActConstants.PAGINATION_SUFFUX);

		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("list.all.users.failed"));
			return ERROR;
		}
		return SUCCESS;
	}

	public String showUserStatus() {
		try {
			user = retrieveLoggedInUser();
			regUser = this.userService.getUserById(regUser.getId());
            if(regUser == null){
                addActionError(getText("admin.get.user.not.found"));
			    setNavBarAfterExc();
                return ERROR;
            }
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("admin.get.user.details.error"));
			setNavBarAfterExc();
			return ERROR;
		}
		return SUCCESS;
	}

	public String manageUser() {
		try {
			user = retrieveLoggedInUser();
			if ((user.getUserType() != UserType.ADMIN.code()) && (user.getUserType() != UserType.SUPERADMIN.code())) {
				addActionError(getText("admin.manage.user.account.permission.denied"));
				setNavBarAfterExc();
				return ERROR;
			}

			if (StringUtils.isBlank(manageType)) {
				addActionError(getText("admin.manage.user.account.action.type.must.be.specified"));
				setNavBarAfterExc();
				return ERROR;
			}

			regUser = this.userService.getUserById(regUser.getId());

			if (manageType.equals(ActConstants.ManageType.ACTIVATE.manageType())) {
				if (!regUser.isActivated()) {
					regUser.setActivated(true);
					regUser.setRejected(false);
					this.userService.updateUser(regUser);
					sendApprovalAccountEmail(regUser.getDisplayName(), regUser.getEmail(), regUser.getProfile().getOrganization());
					// set action successful message
					setActionSuccessMsg(getText("admin.manage.user.success",
							new String[] { ("Activated " + regUser.getDisplayName() + " user account") }));
				}
			}
			if (manageType.equals(ActConstants.ManageType.DEACTIVATE.manageType())) {
				if (regUser.isActivated()) {
					regUser.setActivated(false);
					regUser.setRejected(false);
					this.userService.updateUser(regUser);
					sendRejectEmailToUser(regUser.getDisplayName(), regUser.getEmail(), regUser.getProfile().getOrganization());
					// set action successful message
					setActionSuccessMsg(getText("admin.manage.user.success",
							new String[] { ("Deactivated " + regUser.getDisplayName() + " user account") }));
				}
			}

			if (manageType.equals(ActConstants.ManageType.SETASADMIN.manageType())) {
				if (regUser.getUserType() != UserType.ADMIN.code()) {
					regUser.setUserType(UserType.ADMIN.code());
					this.userService.updateUser(regUser);
					// set action successful message
					setActionSuccessMsg(getText("admin.manage.user.success",
							new String[] { ("Set " + regUser.getDisplayName() + " user as an admin") }));
				}

			}
			if (manageType.equals(ActConstants.ManageType.SETASUSER.manageType())) {
				if (regUser.getUserType() == UserType.ADMIN.code()) {
					regUser.setUserType(UserType.REGUSER.code());
					this.userService.updateUser(regUser);
					// set action successful message
					setActionSuccessMsg(getText("admin.manage.user.success",
							new String[] { ("Set " + regUser.getDisplayName() + " user as a normal user") }));
				}
			}

		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("admin.manage.user.account.failed"));
			setNavBarAfterExc();
		}
		return SUCCESS;
	}

	private void setNavBarAfterExc() {
		String startNav = getText("user.all.users.title");
		String startNavLink = ActConstants.LIST_ALL_USERS_ACTION;
		String secondNav = getText("admin.view.user.details");
		setPageTitle(startNav, secondNav);
		navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
	}

	public long getRegUid() {
		return regUid;
	}

	public void setRegUid(long regUid) {
		this.regUid = regUid;
	}

	public String getActivationHashCode() {
		return activationHashCode;
	}

	public void setActivationHashCode(String activationHashCode) {
		this.activationHashCode = activationHashCode;
	}

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public DMService getDmService() {
		return dmService;
	}

	public void setDmService(DMService dmService) {
		this.dmService = dmService;
	}

	public User getRegUser() {
		return regUser;
	}

	public void setRegUser(User regUser) {
		this.regUser = regUser;
	}

	public Pagination<User> getUserPagination() {
		return userPagination;
	}

	public void setUserPagination(Pagination<User> userPagination) {
		this.userPagination = userPagination;
	}

	public String getManageType() {
		return manageType;
	}

	public void setManageType(String manageType) {
		this.manageType = manageType;
	}

}
