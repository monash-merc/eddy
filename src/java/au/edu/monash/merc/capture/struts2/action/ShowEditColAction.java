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

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.domain.Permission;

@Scope("prototype")
@Controller("data.showColEditAction")
public class ShowEditColAction extends DMCoreAction {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private String colNameBeforeUpdate;

	private boolean privateCo;

	public String showEditCollection() {
		try {
			checkUserPermissions(collection.getId(), collection.getOwner().getId());
		} catch (Exception e) {
			logger.error(e);
			addFieldError("checkPermission", getText("check.permissions.error"));
			setNavAfterException();
			return ERROR;
		}
		try {
			if (!permissionBean.isEditAllowed()) {
				addFieldError("updatePermission", getText("show.collection.update.page.permission.denied"));
				setNavAfterException();
				return ERROR;
			}
			List<Permission> permissions = this.dmService.getCollectionDefaultPerms(collection.getId());
			privateCo = checkIsPrivateCo(permissions);
			collection = this.dmService.getCollection(collection.getId(), collection.getOwner().getId());
			if (collection != null) {
				// populate the user object
				colNameBeforeUpdate = collection.getName();
				// set page title and nav label.
				setNavAfterSuccess();
			} else {
				addActionError(getText("show.collection.update.page.failed.collection.not.exist"));
				setNavAfterException();
				return ERROR;
			}
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("show.collection.update.page.failed") + " " + e.getMessage());
			setNavAfterException();
			return ERROR;
		}
		return SUCCESS;
	}

	private boolean checkIsPrivateCo(List<Permission> permissions) {

		for (Permission perm : permissions) {
			if (perm.isViewAllowed()) {
				return false;
			}
		}
		return true;
	}

	protected void setNavAfterException() {

		String startNav = null;
		String startNavLink = null;
		String secondNav = getText("show.collection.updating");

		if (viewType != null) {
			if (viewType.equals(ActConstants.UserViewType.USER.toString())) {
				startNav = getText("mycollection.nav.label.name");
				startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
			}

			if (viewType.equals(ActConstants.UserViewType.ALL.toString())) {
				startNav = getText("allcollection.nav.label.name");
				startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
			}
			setPageTitle(startNav, secondNav + " Error");
			navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
		}
	}

	private void setNavAfterSuccess() {

		String startNav = null;
		String startNavLink = null;
		String secondNav = collection.getName();
		String secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
				+ collection.getOwner().getId() + "&viewType=" + viewType;

		String thirdNav = getText("update.collection");
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
			setPageTitle(startNav, (secondNav + " - " + thirdNav));

			navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, thirdNav, null);
		}
	}

	public String getColNameBeforeUpdate() {
		return colNameBeforeUpdate;
	}

	public void setColNameBeforeUpdate(String colNameBeforeUpdate) {
		this.colNameBeforeUpdate = colNameBeforeUpdate;
	}

	public boolean isPrivateCo() {
		return privateCo;
	}

	public void setPrivateCo(boolean privateCo) {
		this.privateCo = privateCo;
	}
}
