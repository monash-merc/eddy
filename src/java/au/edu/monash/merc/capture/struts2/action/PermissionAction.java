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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.monash.merc.capture.domain.*;
import au.edu.monash.merc.capture.dto.ManagablePerm;
import au.edu.monash.merc.capture.dto.ManagablePermType;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.dto.AssignedPermissions;
import au.edu.monash.merc.capture.dto.PermissionBean;

@Scope("prototype")
@Controller("perm.permAction")
public class PermissionAction extends DMCoreAction {

    private Map<Long, String> activeUsers = new HashMap<Long, String>();

    private List<PermissionBean> regUserPerms = new ArrayList<PermissionBean>();

    private PermissionBean allRegUserPerm;

    private PermissionBean anonymousePerm;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * show set the collection permissions action
     *
     * @return a String represents SUCCESS or ERROR.
     */
    public String showSetColPermissions() {
        try {
            permissionBean = checkPermission(collection.getId(), collection.getOwner().getId());
            if (!permissionBean.isAcAllowed()) {
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

                //get all permissions for this collection
                List<CPermission> permissions = this.dmService.getCollectionPermissions(collection.getId());

                //copy the permissions into permission beans
                copyPermissionsToPermissionBeans(permissions);
                System.out.println("=================== start to check existed permissions: ");
                printGrantedPermissions();
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
        String secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id=" + collection.getOwner().getId() + "&viewType=" + viewType;

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

    private void copyPermissionsToPermissionBeans(List<CPermission> permissions) {
        allRegUserPerm = new PermissionBean();
        anonymousePerm = new PermissionBean();
        regUserPerms.clear();
        for (CPermission perm : permissions) {
            // get default permissions for all-registered user
            if (perm.getPermType().equals(PermType.ALLREGUSER.code())) {
                //copy the allregistered user permissions
                allRegUserPerm = copyPermissionToPermissionBean(perm);
            }

            if (perm.getPermType().equals(PermType.ANONYMOUS.code())) {
                //copy anonymous user permissions
                anonymousePerm = copyPermissionToPermissionBean(perm);
            }

            if (perm.getPermType().equals(PermType.REGISTERED.code())) {
                //copy the individual registered user permissions
                PermissionBean individualPerm = copyPermissionToPermissionBean(perm);
                regUserPerms.add(individualPerm);
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
            permissionBean = checkPermission(collection.getId(), collection.getOwner().getId());
            if (!permissionBean.isAcAllowed()) {
                addActionError(getText("collection.change.permissions.denied"));
                // set page title and action navigation label.
                setNavAfterException();
                return INPUT;
            }
            long ownerId = collection.getOwner().getId();
            collection = this.dmService.getCollection(collection.getId(), ownerId);
            if (collection != null) {
                populateFilteredUserNames(ownerId);

                //printGrantedPermissions();
                AssignedPermissions assignedPermissions = grantCollectionPermissions(collection);

                //save the granted permissions
                List<CPermission> grantedPermissions = this.dmService.saveCollectionPermissions(assignedPermissions);

                //copy the permissions into permission beans
                copyPermissionsToPermissionBeans(grantedPermissions);

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


    private CPermission copyPermissionBeanToPermission(Collection col, PermissionBean pmBean, String permType) {
        CPermission permission = new CPermission();

        if (pmBean != null) {
            permission.setPermType(permType);
            permission.setId(pmBean.getId());
            long uid = pmBean.getUid();
            //get an user for this permission based on an user id
            User permForUser = this.userService.getUserById(uid);
            permission.setPermForUser(permForUser);

            //set the permission collection
            permission.setCollection(col);

            boolean viewAllowed = pmBean.isViewAllowed();
            if (viewAllowed) {
                permission.setViewAllowed(1);
            } else {
                permission.setViewAllowed(0);
            }

            boolean importAllowed = pmBean.isImportAllowed();
            if (importAllowed) {
                permission.setImportAllowed(1);
            } else {
                permission.setImportAllowed(0);
            }

            boolean exportAllowed = pmBean.isExportAllowed();
            if (exportAllowed) {
                permission.setExportAllowed(1);
            } else {
                permission.setExportAllowed(0);
            }

            boolean racAllowed = pmBean.isRacAllowed();
            if (racAllowed) {
                permission.setRacAllowed(1);
            } else {
                permission.setRacAllowed(0);
            }

            boolean updateAllowed = pmBean.isUpdateAllowed();
            if (updateAllowed) {
                permission.setUpdateAllowed(1);
            } else {
                permission.setUpdateAllowed(0);
            }

            boolean deleteAllowed = pmBean.isDeleteAllowed();
            if (deleteAllowed) {
                permission.setDeleteAllowed(1);
            } else {
                permission.setDeleteAllowed(0);
            }

            boolean mdRegAllowed = pmBean.isMdRegAllowed();
            if (mdRegAllowed) {
                permission.setMdRegisterAllowed(1);
            } else {
                permission.setMdRegisterAllowed(0);
            }

            boolean acAllowed = pmBean.isAcAllowed();
            if (acAllowed) {
                permission.setAcAllowed(1);
            } else {
                permission.setAcAllowed(0);
            }
        }
        return permission;
    }

    private void printGrantedPermissions() {
        System.out.println("===== anonymous permission id: " + anonymousePerm.getId());
        System.out.println("===== anonymous uid: " + anonymousePerm.getUid());
        System.out.println("===== anonymous view allowed: " + anonymousePerm.isViewAllowed());
        System.out.println("===== anonymous import  allowed: " + anonymousePerm.isImportAllowed());
        System.out.println("===== anonymous export allowed: " + anonymousePerm.isExportAllowed());
        System.out.println("===== anonymous update allowed: " + anonymousePerm.isUpdateAllowed());
        System.out.println("===== anonymous delete allowed: " + anonymousePerm.isDeleteAllowed());
        System.out.println("===== anonymous ra control allowed: " + anonymousePerm.isRacAllowed());
        System.out.println("===== anonymous access control allowed: " + anonymousePerm.isAcAllowed());
        System.out.println("===== anonymous mdreg allowed: " + anonymousePerm.isMdRegAllowed());
        System.out.println("");

        System.out.println("===== allRegisteredUser permission id: " + allRegUserPerm.getId());
        System.out.println("===== allRegisteredUser uid: " + allRegUserPerm.getUid());
        System.out.println("===== allRegisteredUser view allowed: " + allRegUserPerm.isViewAllowed());
        System.out.println("===== allRegisteredUser import  allowed: " + allRegUserPerm.isImportAllowed());
        System.out.println("===== allRegisteredUser export allowed: " + allRegUserPerm.isExportAllowed());
        System.out.println("===== allRegisteredUser update allowed: " + allRegUserPerm.isUpdateAllowed());
        System.out.println("===== allRegisteredUser delete allowed: " + allRegUserPerm.isDeleteAllowed());
        System.out.println("===== allRegisteredUser ra control allowed: " + allRegUserPerm.isRacAllowed());
        System.out.println("===== allRegisteredUser access control allowed: " + allRegUserPerm.isAcAllowed());
        System.out.println("===== allRegisteredUser mdreg allowed: " + allRegUserPerm.isMdRegAllowed());
        System.out.println("");

        for (PermissionBean pm : regUserPerms) {
            System.out.println("===== a registered user permission id: " + pm.getId());
            System.out.println("===== a registered user uid: " + pm.getUid());
            System.out.println("===== a registered user view allowed: " + pm.isViewAllowed());
            System.out.println("===== a registered user import  allowed: " + pm.isImportAllowed());
            System.out.println("===== a registered user export allowed: " + pm.isExportAllowed());
            System.out.println("===== a registered user update allowed: " + pm.isUpdateAllowed());
            System.out.println("===== a registered user delete allowed: " + pm.isDeleteAllowed());
            System.out.println("===== a registered user ra control allowed: " + pm.isRacAllowed());
            System.out.println("===== a registered user access control allowed: " + pm.isAcAllowed());
            System.out.println("===== a registered user mdreg allowed: " + pm.isMdRegAllowed());
            System.out.println("");
        }
    }

    //grant the collection permissions
    private AssignedPermissions grantCollectionPermissions(Collection col) {
        //check the hierarchic permissions for anonymous group
        checkHierarchicPerms(anonymousePerm);
        //check the hierarchic permissions for all registered group
        checkHierarchicPerms(allRegUserPerm);
        //check the hierarchic permissions for each registered user
        for (PermissionBean pm : regUserPerms) {
            checkHierarchicPerms(pm);
        }

        //get the inherited group permissions for all registered user group
        getInheritedGroupPerimssions(anonymousePerm, allRegUserPerm);
        //get the inherited group permissions for each registered user
        for (PermissionBean pm : regUserPerms) {
            getInheritedGroupPerimssions(allRegUserPerm, pm);
        }
        printGrantedPermissions();

        return assignPermissionForCollection(col);
    }

    //check the hierarchic permissions
    private void checkHierarchicPerms(PermissionBean pmBean) {
        if (pmBean != null) {
            if (pmBean.isAcAllowed()) {
                pmBean.setDeleteAllowed(true);
                pmBean.setUpdateAllowed(true);
                pmBean.setRacAllowed(true);
                pmBean.setImportAllowed(true);
                pmBean.setExportAllowed(true);
                pmBean.setViewAllowed(true);
            }
            if (pmBean.isDeleteAllowed()) {
                pmBean.setUpdateAllowed(true);
                pmBean.setRacAllowed(true);
                pmBean.setImportAllowed(true);
                pmBean.setExportAllowed(true);
                pmBean.setViewAllowed(true);
            }
            if (pmBean.isUpdateAllowed()) {
                pmBean.setRacAllowed(true);
                pmBean.setImportAllowed(true);
                pmBean.setExportAllowed(true);
                pmBean.setViewAllowed(true);
            }
            if (pmBean.isRacAllowed()) {
                pmBean.setImportAllowed(true);
                pmBean.setExportAllowed(true);
                pmBean.setViewAllowed(true);
            }
            if (pmBean.isImportAllowed()) {
                pmBean.setViewAllowed(true);
            }
            if (pmBean.isExportAllowed()) {
                pmBean.setViewAllowed(true);
            }
        }
    }

    //get the inherited group permissions
    private void getInheritedGroupPerimssions(PermissionBean groupPermissionBean, PermissionBean toPermissionBean) {

        boolean viewAllowed = groupPermissionBean.isViewAllowed();
        if (viewAllowed) {
            toPermissionBean.setViewAllowed(true);
        }

        boolean updateAllowed = groupPermissionBean.isUpdateAllowed();
        if (updateAllowed) {
            toPermissionBean.setUpdateAllowed(true);
        }

        boolean importAllowed = groupPermissionBean.isImportAllowed();
        if (importAllowed) {
            toPermissionBean.setImportAllowed(true);
        }

        boolean exportAllowed = groupPermissionBean.isExportAllowed();
        if (exportAllowed) {
            toPermissionBean.setExportAllowed(true);
        }

        boolean deleteAllowed = groupPermissionBean.isDeleteAllowed();
        if (deleteAllowed) {
            toPermissionBean.setDeleteAllowed(true);
        }

        boolean mdRegAllowed = groupPermissionBean.isMdRegAllowed();
        if (mdRegAllowed) {
            toPermissionBean.setMdRegAllowed(true);
        }

        boolean racAllowed = groupPermissionBean.isRacAllowed();
        if (racAllowed) {
            toPermissionBean.setRacAllowed(true);
        }

        boolean acAllowed = groupPermissionBean.isAcAllowed();
        if (acAllowed) {
            toPermissionBean.setAcAllowed(true);
        }
    }

    //assign the collection permissions
    private AssignedPermissions assignPermissionForCollection(Collection col) {

        //covert the permission bean for anonymous user
        CPermission permAnonymous = copyPermissionBeanToPermission(col, anonymousePerm, PermType.ANONYMOUS.code());
        //covert the permission bean for all registered user
        CPermission permAllRegUser = copyPermissionBeanToPermission(col, allRegUserPerm, PermType.ALLREGUSER.code());

        //create an Assigned permission bean
        AssignedPermissions assignedPermissions = new AssignedPermissions();
        //set the collection id
        assignedPermissions.setCollectionId(col.getId());

        //set the permission for anonymous into the assigned permission bean
        assignedPermissions.setAnonymousPerm(permAnonymous);
        //set the permission for all registered user into the assigned permission bean
        assignedPermissions.setAllRegisteredPerm(permAllRegUser);

        //check the individual user permission
        for (PermissionBean pm : regUserPerms) {
            //if it's the same as the all registered user group's permission, we have to remove it
            if (!eqaulsPerms(pm, allRegUserPerm)) {
                CPermission permForUser = copyPermissionBeanToPermission(col, pm, PermType.REGISTERED.code());
                assignedPermissions.setRegisteredUserPerm(permForUser);
            }
        }
        return assignedPermissions;
    }

    private boolean eqaulsPerms(PermissionBean aPerm, PermissionBean bPerm) {
        if ((aPerm.isImportAllowed() != bPerm.isImportAllowed()) || (aPerm.isExportAllowed() != bPerm.isExportAllowed())
                || (aPerm.isUpdateAllowed() != bPerm.isUpdateAllowed()) || (aPerm.isRacAllowed() != bPerm.isRacAllowed())
                || (aPerm.isDeleteAllowed() != bPerm.isDeleteAllowed()) || (aPerm.isAcAllowed() != bPerm.isAcAllowed())) {
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

    public List<PermissionBean> getRegUserPerms() {
        return regUserPerms;
    }

    public void setRegUserPerms(List<PermissionBean> regUserPerms) {
        this.regUserPerms = regUserPerms;
    }

    public PermissionBean getAllRegUserPerm() {
        return allRegUserPerm;
    }

    public void setAllRegUserPerm(PermissionBean allRegUserPerm) {
        this.allRegUserPerm = allRegUserPerm;
    }

    public PermissionBean getAnonymousePerm() {
        return anonymousePerm;
    }

    public void setAnonymousePerm(PermissionBean anonymousePerm) {
        this.anonymousePerm = anonymousePerm;
    }
}
