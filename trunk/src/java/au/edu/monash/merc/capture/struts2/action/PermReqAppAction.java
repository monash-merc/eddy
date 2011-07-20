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

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.domain.PermissionRequest;

@Scope("prototype")
@Controller("perm.permReqAppAction")
public class PermReqAppAction extends DMCoreAction {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private PermissionRequest permRequest;

	private List<PermissionRequest> permRequests;

	public String listPermRequests() {
		setNavForPermReq();
		try {
			// long uid = getLoginUsrIdFromSession();
			user = retrieveLoggedInUser();
			permRequests = this.dmService.getPermissionRequestsByOwner(user.getId());

		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("failed.to.get.user.permission.requests"));
			return ERROR;
		}
		return SUCCESS;
	}

	public String approvePermReq() {

		setNavForPermReq();
		try {
			// long uid = getLoginUsrIdFromSession();
			user = retrieveLoggedInUser();
			if (checkPermsReqError()) {
				postProcess(user.getId());
				return INPUT;
			}
			// save the permission
			this.dmService.approveUserPermRequest(permRequest);

			permRequests = this.dmService.getPermissionRequestsByOwner(user.getId());
			setActionSuccessMsg(getText("grant.user.requested.permissions.successfully", new String[] { permRequest.getCollection().getName() }));
			// TODO:
			// send an approval email to request user.
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("failed.to.grant.user.requested.permissions"));
			return ERROR;
		}
		return SUCCESS;
	}

	private void postProcess(long uid) {
		permRequests = this.dmService.getPermissionRequestsByOwner(uid);
	}

	public String rejectPermReq() {
		setNavForPermReq();
		try {
			// long uid = getLoginUsrIdFromSession();
			user = retrieveLoggedInUser();
			this.dmService.deletePermissionRequestById(permRequest.getId());
			permRequests = this.dmService.getPermissionRequestsByOwner(user.getId());
			setActionSuccessMsg(getText("reject.user.requested.permissions.successfully", new String[] { permRequest.getCollection().getName() }));
			// TODO:
			// send a rejected email to user.
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("failed.to.reject.user.requested.permissions"));
			return ERROR;
		}
		return SUCCESS;
	}

	private void setNavForPermReq() {
		String startNav = getText("user.display.home.action.title");
		String startNavLink = ActConstants.DISPLAY_USER_HOME_ACTION;
		String secondNav = getText("view.all.permission.requests.action.title");
		String secondNavLink = ActConstants.VIEW_PERM_REQUESTS_ACTION;
		setPageTitle(secondNav);
		navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, null, null);
	}

	private boolean checkPermsReqError() {
		if (!permRequest.isViewAllowed() && !permRequest.isUpdateAllowed() && !permRequest.isImportAllowed() && !permRequest.isExportAllowed()
				&& !permRequest.isDeleteAllowed() && !permRequest.isChangePermAllowed()) {
			addFieldError("perms", getText("at.least.selected.permission.required"));
			return true;
		}
		return false;
	}

	public PermissionRequest getPermRequest() {
		return permRequest;
	}

	public void setPermRequest(PermissionRequest permRequest) {
		this.permRequest = permRequest;
	}

	public List<PermissionRequest> getPermRequests() {
		return permRequests;
	}

	public void setPermRequests(List<PermissionRequest> permRequests) {
		this.permRequests = permRequests;
	}

}
