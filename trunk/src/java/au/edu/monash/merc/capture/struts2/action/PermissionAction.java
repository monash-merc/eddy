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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.monash.merc.capture.dto.ManagablePerm;
import au.edu.monash.merc.capture.dto.ManagablePermType;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.domain.PermType;
import au.edu.monash.merc.capture.domain.Permission;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.domain.UserType;
import au.edu.monash.merc.capture.dto.AssignedPermissions;
import au.edu.monash.merc.capture.dto.PermissionBean;

@Scope("prototype")
@Controller("perm.permAction")
public class PermissionAction extends DMCoreAction {

    private Map<Long, String> activeUsers = new HashMap<Long, String>();

    private List<PermissionBean> permissionBeans = new ArrayList<PermissionBean>();

    private PermissionBean coPermForAllUser;

    private PermissionBean coPermForAnony;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * show set the collection permissions action
     *
     * @return a String represents SUCCESS or ERROR.
     */
    public String showSetColPermissions() {
        try {
            checkUserPermissions(collection.getId(), collection.getOwner().getId());
            if (!permissionBean.isChangePermAllowed()) {
                addActionError(getText("collection.change.permissions.denied"));
                // set page title and action navigation label.
                setNavAfterException();
                return INPUT;
            }
            long ownerId = collection.getOwner().getId();
            collection = this.dmService.getCollection(collection.getId(), ownerId);
            if (collection != null) {
                // populate all active users
                populateFilteredUserNames(ownerId);

                // get all permission from the database
                List<Permission> permissions = this.dmService.getCollectionPermissions(collection.getId());

                // populate the collection permissions
                copyCoPermsToPermissionBean(permissions);

                // set view collection details link
                setViewColDetailsLink();

                // set page title and action navigation label.
                setNavAfterSuccess();
            } else {
                addActionError(getText("failed.to.change.nonexisted.collection.permissions"));
                // set page title and action navigation label.
                setNavAfterException();
                return INPUT;
            }
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("failed.to.change.collection.permissions"));
            // set page title and action navigation label.
            setNavAfterException();
            return INPUT;
        }
        return SUCCESS;
    }

    private void setNavAfterException() {
        String startNav = null;
        String startNavLink = null;
        String secondNav = getText("change.collection.permission.error");
        if (viewType != null) {
            if (viewType.equals(ActConstants.UserViewType.USER.toString())) {
                startNav = getText("mycollection.nav.label.name");
                startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
            }
            if (viewType.equals(ActConstants.UserViewType.ALL.toString())) {
                startNav = getText("allcollection.nav.label.name");
                startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
            }
            setPageTitle(startNav, (secondNav + " Error"));
            navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
        }
    }

    private void setNavAfterSuccess() {
        String pageTitle = null;
        String startNav = null;
        String startNavLink = null;
        String secondNav = collection.getName();
        String secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
                + collection.getOwner().getId() + "&viewType=" + viewType;

        String thirdNav = getText("change.collection.permission.nav.label.name");
        if (viewType != null) {
            if (viewType.equals(ActConstants.UserViewType.USER.toString())) {
                startNav = getText("mycollection.nav.label.name");
                startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
            }
            if (viewType.equals(ActConstants.UserViewType.ALL.toString())) {
                startNav = getText("allcollection.nav.label.name");
                startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
            }

            pageTitle = startNav + " - " + secondNav + " - " + thirdNav;
            setPageTitle(pageTitle);
            navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, thirdNav, null);
        }
    }

    // Copy the Permissions to PermissionBean for editing in the web gui
    private void copyCoPermsToPermissionBean(List<Permission> permissions) {
        coPermForAllUser = new PermissionBean();
        coPermForAnony = new PermissionBean();
        permissionBeans.clear();
        for (Permission perm : permissions) {
            // get default permissions for all-registered user
            if (perm.getPermType().equals(PermType.ALLREGUSER.code())) {
                coPermForAllUser.setId(perm.getId());
                coPermForAllUser.setUserName(perm.getPermissionForUser().getDisplayName());
                coPermForAllUser.setUid(perm.getPermissionForUser().getId());
                coPermForAllUser.setViewAllowed(perm.isViewAllowed());
                coPermForAllUser.setEditAllowed(perm.isUpdateAllowed());
                coPermForAllUser.setImportAllowed(perm.isImportAllowed());
                coPermForAllUser.setExportAllowed(perm.isExportAllowed());
                coPermForAllUser.setDeleteAllowed(perm.isDeleteAllowed());
                coPermForAllUser.setChangePermAllowed(perm.isChangePermAllowed());
                // get default permission for anonymous user
            } else if (perm.getPermType().equals(PermType.ANONYMOUS.code())) {
                coPermForAnony.setId(perm.getId());
                coPermForAnony.setUserName(perm.getPermissionForUser().getDisplayName());
                coPermForAnony.setUid(perm.getPermissionForUser().getId());
                coPermForAnony.setViewAllowed(perm.isViewAllowed());
                coPermForAnony.setEditAllowed(perm.isUpdateAllowed());
                coPermForAnony.setImportAllowed(perm.isImportAllowed());
                coPermForAnony.setExportAllowed(perm.isExportAllowed());
                coPermForAnony.setDeleteAllowed(perm.isDeleteAllowed());
                coPermForAnony.setChangePermAllowed(perm.isChangePermAllowed());
            } else {// get all permission for individual users
                PermissionBean mp = new PermissionBean();
                mp.setId(perm.getId());
                mp.setUid(perm.getPermissionForUser().getId());
                mp.setUserName(perm.getPermissionForUser().getDisplayName());
                mp.setViewAllowed(perm.isViewAllowed());
                mp.setEditAllowed(perm.isUpdateAllowed());
                mp.setImportAllowed(perm.isImportAllowed());
                mp.setExportAllowed(perm.isExportAllowed());
                mp.setDeleteAllowed(perm.isDeleteAllowed());
                mp.setChangePermAllowed(perm.isChangePermAllowed());
                permissionBeans.add(mp);
            }
        }
    }

    /**
     * Sign the permission for a collection action
     *
     * @return a String represents SUCCESS or ERROR.
     */
    public String setColPermissions() {
        try {
            checkUserPermissions(collection.getId(), collection.getOwner().getId());
            if (!permissionBean.isChangePermAllowed()) {
                addActionError(getText("collection.change.permissions.denied"));
                // set page title and action navigation label.
                setNavAfterException();
                return INPUT;
            }
            long ownerId = collection.getOwner().getId();
            collection = this.dmService.getCollection(collection.getId(), ownerId);
            if (collection != null) {
                populateFilteredUserNames(ownerId);

                AssignedPermissions assignedPerms = manageAssignedPerms(collection);

                this.dmService.setCollectionPermissions(assignedPerms);

                List<Permission> updatedPerms = this.dmService.getCollectionPermissions(collection.getId());
                copyCoPermsToPermissionBean(updatedPerms);

                setViewColDetailsLink();
                // set action successful message
                setActionSuccessMsg(getText("change.collection.permission.success"));
                // set page title and action navigation label.
                setNavAfterSuccess();
            } else {
                addActionError(getText("failed.to.change.nonexisted.collection.permissions"));
                // set page title and action navigation label.
                setNavAfterException();
                return INPUT;
            }
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("failed.to.change.collection.permissions"));
            // set page title and action navigation label.
            setNavAfterException();
            return INPUT;
        }
        return SUCCESS;
    }

    // manage the owner assigned permissions, and prepare to persist
    private AssignedPermissions manageAssignedPerms(Collection col) {
        List<Permission> newPermissions = new ArrayList<Permission>();
        List<Permission> updatePermissions = new ArrayList<Permission>();
        List<Long> deletePermissionIds = new ArrayList<Long>();

        AssignedPermissions assignPms = new AssignedPermissions();

        // the permissions for all-registered-user will inherited the permissions from the anonymous
        if (coPermForAnony.isViewAllowed()) {
            coPermForAllUser.setViewAllowed(true);
        }
        if (coPermForAnony.isEditAllowed()) {
            coPermForAllUser.setEditAllowed(true);
        }
        if (coPermForAnony.isImportAllowed()) {
            coPermForAllUser.setImportAllowed(true);
        }
        if (coPermForAnony.isExportAllowed()) {
            coPermForAllUser.setExportAllowed(true);
        }
        if (coPermForAnony.isDeleteAllowed()) {
            coPermForAllUser.setDeleteAllowed(true);
        }
        if (coPermForAnony.isChangePermAllowed()) {
            coPermForAllUser.setChangePermAllowed(true);
        }

        for (PermissionBean pm : permissionBeans) {
            // inherited the permissions from anonymous user
            if (coPermForAnony.isViewAllowed()) {
                pm.setViewAllowed(true);
            }
            if (coPermForAnony.isEditAllowed()) {
                pm.setEditAllowed(true);
            }
            if (coPermForAnony.isImportAllowed()) {
                pm.setImportAllowed(true);
            }
            if (coPermForAnony.isExportAllowed()) {
                pm.setExportAllowed(true);
            }
            if (coPermForAnony.isDeleteAllowed()) {
                pm.setDeleteAllowed(true);
            }
            if (coPermForAnony.isChangePermAllowed()) {
                pm.setChangePermAllowed(true);
            }

            ManagablePerm<PermissionBean> managablePerm = new ManagablePerm<PermissionBean>();
            managablePerm.setPerm(pm);

            ManagablePerm mPerm = sortAssignedPermBean(managablePerm, coPermForAllUser, coPermForAnony);

            if (mPerm != null) {
                Permission perm = new Permission();
                PermissionBean pmb = (PermissionBean) mPerm.getPerm();
                perm.setId(pmb.getId());
                long uid = pmb.getUid();
                User u = this.userService.getUserById(uid);
                perm.setPermissionForUser(u);
                perm.setViewAllowed(pmb.isViewAllowed());
                perm.setUpdateAllowed(pmb.isEditAllowed());
                perm.setImportAllowed(pmb.isImportAllowed());
                perm.setExportAllowed(pmb.isExportAllowed());
                perm.setDeleteAllowed(pmb.isDeleteAllowed());
                perm.setChangePermAllowed(pmb.isChangePermAllowed());
                perm.setPermType(PermType.REGISTERED.code());
                perm.setCollection(col);

                if (mPerm.getManagablePermType().equals(ManagablePermType.DELETE)) {
                    deletePermissionIds.add(perm.getId());
                }
                if (mPerm.getManagablePermType().equals(ManagablePermType.NEW)) {
                    perm.setId(0);
                    newPermissions.add(perm);
                }
                if (mPerm.getManagablePermType().equals(ManagablePermType.UPDATE)) {
                    updatePermissions.add(perm);
                }
            }

        }
        // update all registered user permissions
        Permission allRegUserPm = new Permission();
        //set the existed permission id
        allRegUserPm.setId(coPermForAllUser.getId());

        if (allRegUser == null) {
            long usrid = coPermForAllUser.getUid();
            allRegUser = this.userService.getUserById(usrid);
        }
        allRegUserPm.setPermissionForUser(allRegUser);
        allRegUserPm.setViewAllowed(coPermForAllUser.isViewAllowed());
        allRegUserPm.setUpdateAllowed(coPermForAllUser.isEditAllowed());
        allRegUserPm.setImportAllowed(coPermForAllUser.isImportAllowed());
        allRegUserPm.setExportAllowed(coPermForAllUser.isExportAllowed());
        allRegUserPm.setDeleteAllowed(coPermForAllUser.isDeleteAllowed());
        allRegUserPm.setChangePermAllowed(coPermForAllUser.isChangePermAllowed());
        allRegUserPm.setPermType(PermType.ALLREGUSER.code());
        allRegUserPm.setCollection(col);
        updatePermissions.add(allRegUserPm);

        // update the anonymous permissions
        Permission anonyUserPm = new Permission();
        anonyUserPm.setId(coPermForAnony.getId());
        if (anonymous == null) {
            long usrid = coPermForAnony.getUid();
            anonymous = this.userService.getUserById(usrid);
        }
        anonyUserPm.setPermissionForUser(anonymous);
        anonyUserPm.setViewAllowed(coPermForAnony.isViewAllowed());
        anonyUserPm.setUpdateAllowed(coPermForAnony.isEditAllowed());
        anonyUserPm.setImportAllowed(coPermForAnony.isImportAllowed());
        anonyUserPm.setExportAllowed(coPermForAnony.isExportAllowed());
        anonyUserPm.setDeleteAllowed(coPermForAnony.isDeleteAllowed());
        anonyUserPm.setChangePermAllowed(coPermForAnony.isChangePermAllowed());
        anonyUserPm.setPermType(PermType.ANONYMOUS.code());
        anonyUserPm.setCollection(col);
        updatePermissions.add(anonyUserPm);

        assignPms.setPermissionsNew(newPermissions);
        assignPms.setPermissionsUpdate(updatePermissions);
        assignPms.setDeletePermsIds(deletePermissionIds);

        return assignPms;
    }

    private ManagablePerm<PermissionBean> sortAssignedPermBean(ManagablePerm<PermissionBean> managablePerm, PermissionBean allUserPerm, PermissionBean anonyPerm) {
        //get the individual permission bean.
        PermissionBean perm = managablePerm.getPerm();
        // If none permission is allowed for the all-registered-user, the anonymous user and this individual user:
        // a). If this individual user permission is new, we just ignore it. as it's the same as the permissions
        // for the all-registered-user and the anonymous user.
        // b). If this individual user permission already existed, we have to remove it.
        if (anonyPerm.isNonePerm() && allUserPerm.isNonePerm() && perm.isNonePerm()) {
            if (perm.getId() == 0) {
                managablePerm.setManagablePermType(ManagablePermType.IGNORE);
            } else {
                managablePerm.setManagablePermType(ManagablePermType.DELETE);
            }
            return managablePerm;
        }
        // if none permission is assigned for the anonymous user and the all-registered-user, but assigned for the
        // individual user:
        // a). If this individual user permission is new, just create it.
        // b). If this individual user permission already existed, we just update it.
        if (anonyPerm.isNonePerm() && allUserPerm.isNonePerm() && !perm.isNonePerm()) {
            if (perm.getId() == 0) {
                managablePerm.setManagablePermType(ManagablePermType.NEW);
            } else {
                managablePerm.setManagablePermType(ManagablePermType.UPDATE);
            }
            return managablePerm;
        }

        // if none permission is assigned for the anonymous user and the individual user. but assigned to the
        // all-registered-user, which mean the owner would not give any permissions to this registered user:
        // a). If this individual user permission is new, just create it.
        // b). If this individual user permission already existed, we just update it.
        if (anonyPerm.isNonePerm() && !allUserPerm.isNonePerm() && perm.isNonePerm()) {
            if (perm.getId() == 0) {
                managablePerm.setManagablePermType(ManagablePermType.NEW);
            } else {
                managablePerm.setManagablePermType(ManagablePermType.UPDATE);
            }
            return managablePerm;
        }

        // if none permission is assigned for the anonymous user, but assigned to the
        // all-registered-user and the individual user:
        // 1. If this individual user permission is new, and the all-registered-user permissions are not the same as the
        // individual user permissions: we create an new permission, if the permissions are the same, just ignore.
        // 2). If this individual user permission already existed, and the all-registered-user permissions are not the
        // same as the individual user permissions: we just update it, if the permissions are the same, we remove it
        if (anonyPerm.isNonePerm() && !allUserPerm.isNonePerm() && !perm.isNonePerm()) {
            if (perm.getId() == 0) {
                if (!eqaulsPerms(allUserPerm, perm)) {
                    // create a new permission
                    managablePerm.setManagablePermType(ManagablePermType.NEW);
                } else {
                    managablePerm.setManagablePermType(ManagablePermType.IGNORE);
                }
            } else {
                if (!eqaulsPerms(allUserPerm, perm)) {
                    // create a new permission
                    managablePerm.setManagablePermType(ManagablePermType.UPDATE);
                } else {
                    managablePerm.setManagablePermType(ManagablePermType.DELETE);
                }
            }
            return managablePerm;
        }

        // if the permission is assigned for the anonymous user, the all-registered-user and the individual user:
        // 1. If this individual user permission is new:
        // a): if the individual permissions are the same as the all-registered-user permissions or the individual
        // permissions are the same as the anonymous permissions, just ignore
        // b): otherwise we create a new permission for the individual user.
        //
        // 2). If this individual user permission already existed:
        // a): if the individual permissions are the same as the all-registered-user permissions or the individual
        // permissions are the same as the anonymous permissions, just remove it.
        // b): otherwise we update permission for the individual user.
        if (!anonyPerm.isNonePerm() && !allUserPerm.isNonePerm() && !perm.isNonePerm()) {
            if (perm.getId() == 0) {
                if ((eqaulsPerms(allUserPerm, perm)) || (eqaulsPerms(anonyPerm, perm))) {
                    managablePerm.setManagablePermType(ManagablePermType.IGNORE);
                } else {
                    // create a new permission
                    managablePerm.setManagablePermType(ManagablePermType.NEW);
                }
            } else {
                if ((eqaulsPerms(allUserPerm, perm)) || (eqaulsPerms(anonyPerm, perm))) {
                    managablePerm.setManagablePermType(ManagablePermType.DELETE);
                } else {
                    managablePerm.setManagablePermType(ManagablePermType.UPDATE);
                }
            }
            return managablePerm;
        }
        return null;
    }

    private boolean eqaulsPerms(PermissionBean aPerm, PermissionBean bPerm) {
        if ((aPerm.isViewAllowed() != bPerm.isViewAllowed()) || (aPerm.isEditAllowed() != bPerm.isEditAllowed())
                || (aPerm.isImportAllowed() != bPerm.isImportAllowed()) || (aPerm.isExportAllowed() != bPerm.isExportAllowed())
                || (aPerm.isDeleteAllowed() != bPerm.isDeleteAllowed()) || (aPerm.isChangePermAllowed() != bPerm.isChangePermAllowed())) {
            return false;
        } else {
            return true;
        }
    }


    // populate all active user names
    private void populateFilteredUserNames(long ownerId) {
        activeUsers.clear();
        List<User> users = this.userService.getAllActiveUsers();
        for (User u : users) {
            if ((u.getId() != ownerId) && (u.getId() != getLoginUsrIdFromSession()) && (u.getUserType() != UserType.ADMIN.code())
                    && (u.getUserType() != UserType.SUPERADMIN.code())) {
                activeUsers.put(u.getId(), u.getDisplayName());
            }
        }
    }

    private void setViewColDetailsLink() {
        setViewColDetailLink(ActConstants.VIEW_COLLECTION_DETAILS_ACTION);
    }

    public Map<Long, String> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Map<Long, String> activeUsers) {
        this.activeUsers = activeUsers;
    }

    public List<PermissionBean> getPermissionBeans() {
        return permissionBeans;
    }

    public void setPermissionBeans(List<PermissionBean> permissionBeans) {
        this.permissionBeans = permissionBeans;
    }

    public PermissionBean getCoPermForAllUser() {
        return coPermForAllUser;
    }

    public void setCoPermForAllUser(PermissionBean coPermForAllUser) {
        this.coPermForAllUser = coPermForAllUser;
    }

    public PermissionBean getCoPermForAnony() {
        return coPermForAnony;
    }

    public void setCoPermForAnony(PermissionBean coPermForAnony) {
        this.coPermForAnony = coPermForAnony;
    }
}
