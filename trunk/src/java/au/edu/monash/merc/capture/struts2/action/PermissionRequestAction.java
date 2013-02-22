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

import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.domain.PermissionRequest;
import au.edu.monash.merc.capture.domain.User;

@Scope("prototype")
@Controller("perm.permReqAction")
public class PermissionRequestAction extends DMCoreAction {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private PermissionRequest permReq;

    private List<PermissionRequest> permRequests;

    public String applyForPerms() {
        try {

            user = retrieveLoggedInUser();
            collection = this.dmService.getCollectionById(collection.getId());
            User owner = collection.getOwner();
            // check if the logged in user is an owner of this collection or not
            // owner of the collection doesn't need to apply for the access permissions.
            if (user.getId() == owner.getId()) {
                addActionError(getText("collection.owner.does.not.need.apply.perms"));
                setNavAfterExc();
                return INPUT;
            }
            permReq = this.dmService.getCoPermissionRequestByReqUser(collection.getId(), user.getId());
            if (permReq == null) {
                permReq = new PermissionRequest();
            }
            setNavAfterSuccess();
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("failed.to.show.apply.collection.perms.page"));
            setNavAfterExc();
            return INPUT;
        }
        return SUCCESS;
    }

    public String sendPermsReq() {
        try {
            if (checkPermsReqError()) {
                setNavAfterSuccess();
                return INPUT;
            }
            collection = this.dmService.getCollectionById(collection.getId());
            User owner = collection.getOwner();
            user = retrieveLoggedInUser();
            // check if the logged in user is an owner of this collection or not
            // owner of the collection doesn't need to apply for the access permissions.
            if (user.getId() == owner.getId()) {
                addActionError(getText("collection.owner.does.not.need.apply.perms"));
                setNavAfterExc();
                return INPUT;
            }
            //set new permission request
            permReq.setOwner(owner);
            permReq.setRequestUser(user);
            permReq.setCollection(collection);
            permReq.setRequestTime(GregorianCalendar.getInstance().getTime());

            //get old permission request if any
            PermissionRequest previousReq = this.dmService.getCoPermissionRequestByReqUser(collection.getId(), user.getId());
            //if previous request existed, then we just set the id for this new request
            if (previousReq != null) {
                permReq.setId(previousReq.getId());
            }

            if (permReq.getId() == 0) {
                this.dmService.savePermissionRequest(permReq);
            }
            if (permReq.getId() > 0) {
                this.dmService.updatePermissionRequest(permReq);
            }
            setActionSuccessMsg(getText("apply.for.collection.permissions.successfully", new String[]{collection.getName()}));
            setNavAfterSuccess();
        } catch (Exception e) {
            logger.error(e);
            addActionError("failed.to.apply.collection.permissions");
            setNavAfterExc();
            return INPUT;
        }
        return SUCCESS;
    }

    private boolean checkPermsReqError() {
        if (!permReq.isViewAllowed() && !permReq.isUpdateAllowed() && !permReq.isImportAllowed() && !permReq.isExportAllowed()
                && !permReq.isDeleteAllowed() && !permReq.isChangePermAllowed()) {
            addFieldError("perms", getText("at.least.selected.permission.required"));
            return true;
        }
        return false;

    }

    private void setNavAfterExc() {
        String startNav = getText("allcollection.nav.label.name");
        String startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
        String secondNav = getText("apply.collection.permission.nav.label.name");
        setPageTitle(startNav, secondNav + " Error");
        navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
    }

    private void setNavAfterSuccess() {
        String pageTitle = null;
        String startNav = getText("allcollection.nav.label.name");
        String startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
        String secondNav = collection.getName();
        String secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
                + collection.getOwner().getId() + "&viewType=all";
        String thirdNav = getText("apply.collection.permission.nav.label.name");
        pageTitle = startNav + " - " + secondNav + " - " + thirdNav;
        setPageTitle(pageTitle);
        navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, thirdNav, null);
    }

    public PermissionRequest getPermReq() {
        return permReq;
    }

    public void setPermReq(PermissionRequest permReq) {
        this.permReq = permReq;
    }

    public List<PermissionRequest> getPermRequests() {
        return permRequests;
    }

    public void setPermRequests(List<PermissionRequest> permRequests) {
        this.permRequests = permRequests;
    }

}
