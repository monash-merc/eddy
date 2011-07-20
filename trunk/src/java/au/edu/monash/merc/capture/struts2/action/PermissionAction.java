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
			if (!coPermForAllUser.isViewAllowed()) {
				coPermForAllUser.setViewAllowed(true);
			}
		}
		if (coPermForAnony.isEditAllowed()) {
			if (!coPermForAllUser.isEditAllowed()) {
				coPermForAllUser.setEditAllowed(true);
			}
		}
		if (coPermForAnony.isImportAllowed()) {
			if (!coPermForAllUser.isImportAllowed()) {
				coPermForAllUser.setImportAllowed(true);
			}
		}
		if (coPermForAnony.isExportAllowed()) {
			if (!coPermForAllUser.isExportAllowed()) {
				coPermForAllUser.setExportAllowed(true);
			}
		}
		if (coPermForAnony.isDeleteAllowed()) {
			if (!coPermForAllUser.isDeleteAllowed()) {
				coPermForAllUser.setDeleteAllowed(true);
			}
		}
		if (coPermForAnony.isChangePermAllowed()) {
			if (!coPermForAllUser.isChangePermAllowed()) {
				coPermForAllUser.setChangePermAllowed(true);
			}
		}

		for (PermissionBean pm : permissionBeans) {

			Permission perm = new Permission();
			perm.setId(pm.getId());

			long uid = pm.getUid();
			User u = this.userService.getUserById(uid);

			perm.setPermissionForUser(u);
			perm.setViewAllowed(pm.isViewAllowed());
			perm.setUpdateAllowed(pm.isEditAllowed());
			perm.setImportAllowed(pm.isImportAllowed());
			perm.setExportAllowed(pm.isExportAllowed());
			perm.setDeleteAllowed(pm.isDeleteAllowed());
			perm.setChangePermAllowed(pm.isChangePermAllowed());
			perm.setPermType(PermType.REGISTERED.code());
			perm.setCollection(col);

			// inherited the permissions from anonymous user
			if (coPermForAnony.isViewAllowed()) {
				if (!pm.isViewAllowed()) {
					perm.setViewAllowed(true);
				}
			}
			if (coPermForAnony.isEditAllowed()) {
				if (!pm.isEditAllowed()) {
					perm.setUpdateAllowed(true);
				}
			}
			if (coPermForAnony.isImportAllowed()) {
				if (!pm.isImportAllowed()) {
					perm.setImportAllowed(true);
				}
			}
			if (coPermForAnony.isExportAllowed()) {
				if (!pm.isExportAllowed()) {
					perm.setExportAllowed(true);
				}
			}
			if (coPermForAnony.isDeleteAllowed()) {
				if (!pm.isDeleteAllowed()) {
					perm.setDeleteAllowed(true);
				}
			}
			if (coPermForAnony.isChangePermAllowed()) {
				if (!pm.isChangePermAllowed()) {
					perm.setChangePermAllowed(true);
				}
			}

			// if id equals -1, and at least one permission is allowed, which means this is a new permission,
			if ((pm.getId() == -1)
					&& (pm.isViewAllowed() || pm.isEditAllowed() || pm.isImportAllowed() || pm.isExportAllowed() || pm.isDeleteAllowed() || pm
							.isChangePermAllowed())) {
				// reset permission id value -1 to 0;
				perm.setId(0);
				// if one of the permissions is differred from the permissions of all-registered-user, which means we
				// will save it.
				if ((coPermForAllUser.isViewAllowed() != perm.isViewAllowed()) || (coPermForAllUser.isEditAllowed() != perm.isUpdateAllowed())
						|| (coPermForAllUser.isImportAllowed() != perm.isImportAllowed())
						|| (coPermForAllUser.isExportAllowed() != perm.isExportAllowed())
						|| (coPermForAllUser.isDeleteAllowed() != perm.isDeleteAllowed())
						|| (coPermForAllUser.isChangePermAllowed() != perm.isChangePermAllowed())) {
					newPermissions.add(perm);
				}

			}
			// if id not equals -1, and at least one permission is allowed which means this is an old permission,
			if ((pm.getId() != -1)
					&& (pm.isViewAllowed() || pm.isEditAllowed() || pm.isImportAllowed() || pm.isExportAllowed() || pm.isDeleteAllowed() || pm
							.isChangePermAllowed())) {

				// if same as all-registered-user permissions, we just remove it.
				if ((coPermForAllUser.isViewAllowed() == perm.isViewAllowed()) && (coPermForAllUser.isEditAllowed() == perm.isUpdateAllowed())
						&& (coPermForAllUser.isImportAllowed() == perm.isImportAllowed())
						&& (coPermForAllUser.isExportAllowed() == perm.isExportAllowed())
						&& (coPermForAllUser.isDeleteAllowed() == perm.isDeleteAllowed())
						&& (coPermForAllUser.isChangePermAllowed() == perm.isChangePermAllowed())) {
					deletePermissionIds.add(perm.getId());
				} else {
					updatePermissions.add(perm);
				}
			}

			// if id not equals -1, and all permissions are not allowed, which means removing this user permission,
			if ((pm.getId() != -1) && !pm.isViewAllowed() && !pm.isEditAllowed() && !pm.isImportAllowed() && !pm.isExportAllowed()
					&& !pm.isDeleteAllowed() && !pm.isChangePermAllowed()) {
				// System.out.println("=========> changed this permission for no previlieges... It will be removed...");
				deletePermissionIds.add(perm.getId());
			}
		}
		// update all registered user permissions
		Permission allRegUserPm = new Permission();
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
