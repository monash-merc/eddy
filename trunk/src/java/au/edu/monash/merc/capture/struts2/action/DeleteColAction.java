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

import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.AuditEvent;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.struts2.action.ActConstants.UserViewType;

@Scope("prototype")
@Controller("data.deleteColAction")
public class DeleteColAction extends DMCoreAction {

	private String requestUrl;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Delete user collection
	 * 
	 * @return a String represents SUCCESS or ERROR.
	 */
	public String deleteCollection() {

		try {

			checkUserPermissions(collection.getId(), collection.getOwner().getId());
			if (!permissionBean.isDeleteAllowed()) {
				addActionError(getText("delete.collection.permission.denied"));
				setNavAfterException();
				return ERROR;
			}

			collection = this.dmService.getCollection(collection.getId(), collection.getOwner().getId());
			if (collection != null) {
				String dataStorePath = configSetting.getPropValue(ConfigSettings.DATA_STORE_LOCATION);

				User owner = collection.getOwner();

				// delete collection include permissions and dataset files
				if (collection.isPublished()) {
					// populate the rifcs registration if enabled
					String mdRegEnabledStr = configSetting.getPropValue(ConfigSettings.ANDS_RIFCS_REG_ENABLED);
					boolean mdRegEnabled = Boolean.valueOf(mdRegEnabledStr).booleanValue();
					if (mdRegEnabled) {
						String rifcsRootPath = configSetting.getPropValue(ConfigSettings.ANDS_RIFCS_STORE_LOCATION);
						this.dmService.deletePublisheCollection(collection, dataStorePath, rifcsRootPath);
					}
				} else {
					this.dmService.deleteCollection(collection, dataStorePath);
				}
				setActionSuccessMsg(getText("delete.collection.success", new String[] { collection.getName() }));

				if (viewType.equals(UserViewType.USER.toString())) {
					requestUrl = ActConstants.USER_LIST_COLLECTION_ACTION;
				}
				if (viewType.equals(UserViewType.ALL.toString())) {
					requestUrl = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
				}
				// record the action audit event
				recordAuditEvent(owner, user);
				setNavAfterSuccess();
			} else {
				addActionError(getText("failed.to.delete.nonexisted.collection"));
				setNavAfterException();
				return ERROR;
			}
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("failed.to.delete.collection"));
			setNavAfterException();
			return ERROR;
		}
		return SUCCESS;
	}

	public void validateDeleteCollection() {
		boolean error = false;
		if (collection == null) {
			addFieldError("collection.id", getText("invalid.collection.id"));
			addFieldError("collection.owner.id", getText("invalid.collection.owner.id"));
			error = true;
		}
		if (collection.getId() <= 0) {
			addFieldError("collection.id", getText("invalid.collection.id"));
			error = true;
		}
		if (collection.getOwner().getId() <= 0) {
			addFieldError("collection.id", getText("invalid.collection.owner.id"));
			error = true;
		}
		if (error) {
			setNavAfterException();
		}
	}

	private void recordAuditEvent(User owner, User operator) {
		AuditEvent ev = new AuditEvent();
		ev.setCreatedTime(GregorianCalendar.getInstance().getTime());
		ev.setEvent(collection.getName() + " has been deleted");
		ev.setEventOwner(owner);
		ev.setOperator(operator);
		recordActionAuditEvent(ev);
	}

	private void setNavAfterException() {

		String startNav = null;
		String startNavLink = null;
		String secondNav = getText("delete.collection.error");

		if (viewType != null) {
			if (viewType.equals(ActConstants.UserViewType.USER.toString())) {
				startNav = getText("mycollection.nav.label.name");
				startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
			}

			if (viewType.equals(ActConstants.UserViewType.ALL.toString())) {
				startNav = getText("allcollection.nav.label.name");
				startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
			}
			setPageTitle(startNav, secondNav);
			navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
		} else {
			setPageTitle(secondNav);
			navigationBar = generateNavLabel(secondNav, null, null, null, null, null);
		}
	}

	private void setNavAfterSuccess() {

		String startNav = null;
		String startNavLink = null;
		String secondNav = collection.getName();
		String thirdNav = getText("delete.collection");
		if (viewType != null) {
			if (viewType.equals(ActConstants.UserViewType.USER.toString())) {
				startNav = getText("mycollection.nav.label.name");
				startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
			}

			if (viewType.equals(ActConstants.UserViewType.ALL.toString())) {
				startNav = getText("allcollection.nav.label.name");
				startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
			}

			// set the new page title after successful creating a new collection.
			setPageTitle(startNav, secondNav);
			navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, thirdNav, null);
		}
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

}
