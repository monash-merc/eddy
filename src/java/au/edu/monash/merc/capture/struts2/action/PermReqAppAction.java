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

import au.edu.monash.merc.capture.domain.*;
import au.edu.monash.merc.capture.dto.ManagablePerm;
import au.edu.monash.merc.capture.dto.ManagablePermType;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

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
            user = retrieveLoggedInUser();
            if (checkPermsReqError()) {
                postProcess(user.getId());
                return INPUT;
            }

            PermissionRequest pmReq = this.dmService.getPermissionReqById(permRequest.getId());
            Collection co = pmReq.getCollection();
            User requestUser = pmReq.getRequestUser();

            ManagablePerm<Permission> grantPerm = grantPermissions(pmReq, co, requestUser);

            if (grantPerm != null) {
                //save the requested permissions
                this.dmService.saveUserRequestedPerm(grantPerm, pmReq.getId());
            }
            //save the successful message
            setActionSuccessMsg(getText("grant.user.requested.permissions.successfully", new String[]{co.getName()}));
            // TODO:
            // send an approval email to request user.
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("failed.to.grant.user.requested.permissions"));
            return ERROR;
        }
        return SUCCESS;
    }

    private ManagablePerm<Permission> grantPermissions(PermissionRequest pmReq, Collection collection, User requestUser) {

        long permReqId = pmReq.getId();

        Permission requestPerm = new Permission();
        requestPerm.setViewAllowed(pmReq.isViewAllowed());
        requestPerm.setUpdateAllowed(pmReq.isUpdateAllowed());
        requestPerm.setImportAllowed(pmReq.isImportAllowed());
        requestPerm.setExportAllowed(pmReq.isExportAllowed());
        requestPerm.setDeleteAllowed(pmReq.isDeleteAllowed());
        requestPerm.setChangePermAllowed(pmReq.isChangePermAllowed());

        // get the user permission for this collection, return max three permissions or min two permissions
        List<Permission> userAllPerms = this.dmService.getUserCoPerms(requestUser.getId(), collection.getId());

        //set user permissions as null first.
        Permission userPerm = null;
        Permission anonyPerm = new Permission();
        Permission allRegPerm = new Permission();


        if (userAllPerms != null) {
            for (Permission perm : userAllPerms) {
                String permType = perm.getPermType();
                if (permType.equals(PermType.REGISTERED.code())) {
                    userPerm = perm;
                }
                if (permType.equals(PermType.ANONYMOUS.code())) {
                    anonyPerm = perm;
                }
                if (permType.equals(PermType.ALLREGUSER.code())) {
                    allRegPerm = perm;
                }
            }
        } else {
            return null;
        }

        //if previous permissions are already existed, just get the id for this requested permissions
        if (userPerm != null) {
            requestPerm.setId(userPerm.getId());
        }

        // inherited the permissions from anonymous user
        if (anonyPerm.isViewAllowed()) {
            requestPerm.setViewAllowed(true);
        }
        if (anonyPerm.isUpdateAllowed()) {
            requestPerm.setUpdateAllowed(true);
        }
        if (anonyPerm.isImportAllowed()) {
            requestPerm.setImportAllowed(true);
        }
        if (anonyPerm.isExportAllowed()) {
            requestPerm.setExportAllowed(true);
        }
        if (anonyPerm.isDeleteAllowed()) {
            requestPerm.setDeleteAllowed(true);
        }
        if (anonyPerm.isChangePermAllowed()) {
            requestPerm.setChangePermAllowed(true);
        }
        //set the collection for this requested permission
        requestPerm.setCollection(collection);
        //set the requested permission user
        requestPerm.setPermissionForUser(requestUser);
        //set the permission type
        requestPerm.setPermType(PermType.REGISTERED.code());

        ManagablePerm<Permission> mgIndividualPerm = new ManagablePerm<Permission>();
        mgIndividualPerm.setPerm(requestPerm);
        return sortRequestedUserPerm(mgIndividualPerm, allRegPerm, anonyPerm);
    }

    private ManagablePerm<Permission> sortRequestedUserPerm(ManagablePerm<Permission> requestedIndividualPerms, Permission allUserPerm, Permission anonyPerm) {
        Permission perm = requestedIndividualPerms.getPerm();

        // If none permission is allowed for the all-registered-user, the anonymous user and this individual user:
        // a). If this individual user permission is new, we just ignore it. as it's the same as the permissions
        // for the all-registered-user and the anonymous user.
        // b). If this individual user permission already existed, we have to remove it.
        if (anonyPerm.isNonePerm() && allUserPerm.isNonePerm() && perm.isNonePerm()) {
            if (perm.getId() == 0) {
                requestedIndividualPerms.setManagablePermType(ManagablePermType.IGNORE);
            } else {
                requestedIndividualPerms.setManagablePermType(ManagablePermType.DELETE);
            }
            return requestedIndividualPerms;
        }
        // if none permission is assigned for the anonymous user and the all-registered-user, but assigned for the
        // individual user:
        // a). If this individual user permission is new, just create it.
        // b). If this individual user permission already existed, we just update it.
        if (anonyPerm.isNonePerm() && allUserPerm.isNonePerm() && !perm.isNonePerm()) {
            if (perm.getId() == 0) {
                requestedIndividualPerms.setManagablePermType(ManagablePermType.NEW);
            } else {
                requestedIndividualPerms.setManagablePermType(ManagablePermType.UPDATE);
            }
            return requestedIndividualPerms;
        }

        // if none permission is assigned for the anonymous user and the individual user. but assigned to the
        // all-registered-user, which mean the owner would not give any permissions to this registered user:
        // a). If this individual user permission is new, just create it.
        // b). If this individual user permission already existed, we just update it.
        if (anonyPerm.isNonePerm() && !allUserPerm.isNonePerm() && perm.isNonePerm()) {
            if (perm.getId() == 0) {
                requestedIndividualPerms.setManagablePermType(ManagablePermType.NEW);
            } else {
                requestedIndividualPerms.setManagablePermType(ManagablePermType.UPDATE);
            }
            return requestedIndividualPerms;
        }

        // if the permission is assigned for the all-registered-user and the individual user:
        // 1. If this individual user permission is new:
        // a): if the individual permissions are the same as the all-registered-user permissions, just ignore
        // b): otherwise we create a new permission for the individual user.
        //
        // 2). If this individual user permission already existed:
        // a): if the individual permissions are the same as the all-registered-user permissions,just remove it.
        // b): otherwise we update permission for the individual user.
        if (!allUserPerm.isNonePerm() && !perm.isNonePerm()) {
            if (perm.getId() == 0) {
                if ((isSamePerms(allUserPerm, perm))) {
                    requestedIndividualPerms.setManagablePermType(ManagablePermType.IGNORE);
                } else {
                    // create a new permission
                    requestedIndividualPerms.setManagablePermType(ManagablePermType.NEW);
                }
            } else {
                if ((isSamePerms(allUserPerm, perm))) {
                    requestedIndividualPerms.setManagablePermType(ManagablePermType.DELETE);
                } else {
                    requestedIndividualPerms.setManagablePermType(ManagablePermType.UPDATE);
                }
            }
            return requestedIndividualPerms;
        }
        return null;
    }


    private boolean isSamePerms(Permission aPerm, Permission bPerm) {
        if ((aPerm.isViewAllowed() != bPerm.isViewAllowed()) || (aPerm.isUpdateAllowed() != bPerm.isUpdateAllowed())
                || (aPerm.isImportAllowed() != bPerm.isImportAllowed()) || (aPerm.isExportAllowed() != bPerm.isExportAllowed())
                || (aPerm.isDeleteAllowed() != bPerm.isDeleteAllowed()) || (aPerm.isChangePermAllowed() != bPerm.isChangePermAllowed())) {
            return false;
        } else {
            return true;
        }
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
            setActionSuccessMsg(getText("reject.user.requested.permissions.successfully", new String[]{permRequest.getCollection().getName()}));
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
