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

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.config.SystemPropertiesConfigurer;
import au.edu.monash.merc.capture.domain.AuditEvent;
import au.edu.monash.merc.capture.domain.Avatar;
import au.edu.monash.merc.capture.domain.PermissionRequest;
import au.edu.monash.merc.capture.domain.Profile;
import au.edu.monash.merc.capture.dto.OrderBy;
import au.edu.monash.merc.capture.dto.page.Pagination;
import au.edu.monash.merc.capture.util.CaptureUtil;

@Scope("prototype")
@Controller("admin.userProfileAction")
public class UserProfileAction extends DMCoreAction {

	Pagination<AuditEvent> eventPagination;

	Pagination<PermissionRequest> permReqPagination;

	private Profile profile;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	protected static Map<String, String> countryMap = new LinkedHashMap<String, String>();

	private Map<String, String> genderMap = new LinkedHashMap<String, String>();

	@Autowired
	@Qualifier("countryPropertyConfigurer")
	private SystemPropertiesConfigurer countryPropertyConfigurer;

	public String showProfile() {
		try {
			user = retrieveLoggedInUser();
			// profile = this.dmService.getUserProfile(user.getId());
			profile = user.getProfile();
			setNavForProfileSuccess();
		} catch (Exception e) {
			setNavForShowProfileExc();
			addActionError(getText("get.user.profile.failed"));
			return ERROR;
		}
		return SUCCESS;
	}

	private void setNavForShowProfileExc() {
		String startNav = getText("user.display.home.action.title");
		String startNavLink = ActConstants.DISPLAY_USER_HOME_ACTION;
		String secondNav = getText("user.profile.action.title");
		setPageTitle(secondNav + " Error");
		navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
	}

	private void setNavForProfileSuccess() {
		String startNav = getText("user.display.home.action.title");
		String startNavLink = ActConstants.DISPLAY_USER_HOME_ACTION;
		String secondNav = getText("user.profile.action.title");
		setPageTitle(secondNav);
		navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
	}

	private void reformatNewLines() {
		if (!StringUtils.isBlank(profile.getContactDetails())) {
			String contactDetails = nlToBr(profile.getContactDetails());
			profile.setContactDetails(contactDetails);
		}
		if (!StringUtils.isBlank(profile.getInterests())) {
			String interests = nlToBr(profile.getInterests());
			profile.setInterests(interests);
		}
	}

	private void postProcess() {
		if (StringUtils.isBlank(profile.getGender())) {
			profile.setGender("Male");
		}
		if (StringUtils.isBlank(profile.getCountry())) {
			profile.setCountry("AU");
		}
		setGenderMap();
		setCountries();
	}

	public String showProfileUpdate() {
		try {
			user = retrieveLoggedInUser();
			// profile = this.dmService.getUserProfile(user.getId());
			profile = user.getProfile();
			// post processing for populating the gender and countries
			postProcess();
		} catch (Exception e) {
			addActionError(getText("get.user.profile.failed"));
			return ERROR;
		}
		return SUCCESS;
	}

	public String updateProfile() {
		try {

			user = retrieveLoggedInUser();
			Profile oldProfile = user.getProfile(); // this.dmService.getUserProfile(user.getId());
			Avatar avatar = user.getAvatar();

			// check avatar changes first, if any. just update the avatar, the following updating only applys for an
			// user who never upload his avatar.
			if (StringUtils.isNotBlank(profile.getGender()) && (!oldProfile.getGender().equalsIgnoreCase(profile.getGender()))) {
				if (!avatar.isCustomized()) {
					String avatarFile = null;
					if (profile.getGender().equalsIgnoreCase("male")) {
						avatarFile = "avatar" + File.separator + "male.png";
					} else {
						avatarFile = "avatar" + File.separator + "female.png";
					}
					avatar.setFileName(avatarFile);
					this.dmService.updateAvatar(avatar);
				}
			}

			// start to update the profile.
			oldProfile.setGender(profile.getGender());

			if (!StringUtils.isBlank(profile.getAddress())) {
				oldProfile.setAddress(profile.getAddress());
			}
			if (!StringUtils.isBlank(profile.getCity())) {
				oldProfile.setCity(profile.getCity());
			}
			if (!StringUtils.isBlank(profile.getState())) {
				oldProfile.setState(profile.getState());
			}
			if (!StringUtils.isBlank(profile.getPostcode())) {
				oldProfile.setPostcode(profile.getPostcode());
			}
			if (!StringUtils.isBlank(profile.getContactDetails())) {
				oldProfile.setContactDetails(profile.getContactDetails());
			}
			if (!StringUtils.isBlank(profile.getCountry())) {
				oldProfile.setCountry(profile.getCountry());
			}

			if (!StringUtils.isBlank(profile.getOccupation())) {
				oldProfile.setOccupation(profile.getOccupation());
			}
			if (!StringUtils.isBlank(profile.getIndustryField())) {
				oldProfile.setIndustryField(profile.getIndustryField());
			}
			if (!StringUtils.isBlank(profile.getInterests())) {
				oldProfile.setInterests(profile.getInterests());
			}
			if (!StringUtils.isBlank(profile.getOrganization())) {
				oldProfile.setOrganization(profile.getOrganization());
			}

			// update the profile.
			this.dmService.updateProfile(oldProfile);
			profile = oldProfile;
			// reformat the display format for interests and contact details.
			reformatNewLines();
			// post processing for populating the gender and countries
			postProcess();
			// set navigation bar
			setNavForUpdateProfileSuccess();
		} catch (Exception e) {
			logger.error(e);
			// post processing for populating the gender and countries
			setNavForUpdateProfileExc();
			postProcess();
			return ERROR;
		}
		return SUCCESS;
	}

	private void setNavForUpdateProfileExc() {
		String startNav = getText("user.display.home.action.title");
		String startNavLink = ActConstants.DISPLAY_USER_HOME_ACTION;
		String secondNav = getText("user.profile.action.title");
		setPageTitle(secondNav + " Updating Error");
		navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
	}

	private void setNavForUpdateProfileSuccess() {
		String startNav = getText("user.display.home.action.title");
		String startNavLink = ActConstants.DISPLAY_USER_HOME_ACTION;
		String secondNav = getText("user.profile.action.title");
		setPageTitle(secondNav);
		navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
	}

	public void validateUpdateProfile() {
		if (!CaptureUtil.notGTFixedLength(profile.getContactDetails(), 255)) {
			addFieldError("contactDetails", getText("profile.contact.details.length.too.long"));
		}

	}

	public String displayUserHome() {

		try {
			user = retrieveLoggedInUser();
			// profile = this.dmService.getUserProfile(user.getId());
			profile = user.getProfile();

			// get permissions requests
			permReqPagination = this.dmService.getPermRequestsByPages(user.getId(), 1, 10, orderByDescTime("requestTime"));

			eventPagination = this.dmService.getEventByUserId(user.getId(), 1, 10, orderByDescTime("createdTime"));

			// reformat the display format for interests and contact details.
			reformatNewLines();
			// post processing for populating the gender and countries
			setNavAfterSuccess();
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("view.user.home.failed"));
			setNavAfterExc();
			return INPUT;
		}
		return SUCCESS;
	}

	protected OrderBy[] orderByDescTime(String orderName) {
		return new OrderBy[] { OrderBy.desc(orderName) };
	}

	private void setGenderMap() {
		genderMap.put("Male", "Male");
		genderMap.put("Female", "Female");
	}

	private void setCountries() {
		if (countryMap == null || countryMap.size() == 0) {
			Map<String, String> cMp = this.countryPropertyConfigurer.getResolvedProps();
			countryMap = CaptureUtil.sortByValue(cMp);
		}
	}

	private void setNavAfterSuccess() {
		String startNav = getText("user.display.home.action.title");
		String startNavLink = ActConstants.DISPLAY_USER_HOME_ACTION;
		setPageTitle(startNav);
		navigationBar = generateNavLabel(startNav, startNavLink, null, null, null, null);
	}

	private void setNavAfterExc() {
		String startNav = getText("user.display.home.action.title");
		setPageTitle(startNav + " Error");
		navigationBar = generateNavLabel(startNav, null, null, null, null, null);
	}

	public Pagination<AuditEvent> getEventPagination() {
		return eventPagination;
	}

	public void setEventPagination(Pagination<AuditEvent> eventPagination) {
		this.eventPagination = eventPagination;
	}

	public Pagination<PermissionRequest> getPermReqPagination() {
		return permReqPagination;
	}

	public void setPermReqPagination(Pagination<PermissionRequest> permReqPagination) {
		this.permReqPagination = permReqPagination;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public SystemPropertiesConfigurer getCountryPropertyConfigurer() {
		return countryPropertyConfigurer;
	}

	public void setCountryPropertyConfigurer(SystemPropertiesConfigurer countryPropertyConfigurer) {
		this.countryPropertyConfigurer = countryPropertyConfigurer;
	}

	public Map<String, String> getCountryMap() {
		return countryMap;
	}

	public void setCountryMap(Map<String, String> countryMap) {
		UserProfileAction.countryMap = countryMap;
	}

	public Map<String, String> getGenderMap() {
		return genderMap;
	}

	public void setGenderMap(Map<String, String> genderMap) {
		this.genderMap = genderMap;
	}

}
