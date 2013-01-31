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

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.AuditEvent;
import au.edu.monash.merc.capture.domain.Dataset;

@Scope("prototype")
@Controller("data.deleteDStAction")
public class DeleteDSAction extends DMCoreAction {

	private Dataset dataset;

	private boolean stageTransferEnabled;

	private boolean mdRegEnabled;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public String deleteDataset() {

		try {
			Dataset ds = this.dmService.getDatasetById(dataset.getId());
			collection = ds.getCollection();

			String dataStorePath = configSetting.getPropValue(ConfigSettings.DATA_STORE_LOCATION);
			// set the collection modified time and modified by an user
			collection.setModifiedTime(GregorianCalendar.getInstance().getTime());
			collection.setModifiedByUser(user);

			this.dmService.deleteDataset(collection, ds, dataStorePath);
			// data.dataset.delete.dataset.file.success.msg
			// set action successful message
			recordAuditEvent(ds);
			retrieveAllDatasets();
			// populate the stage transfer if enabled;
			String stageEnabledStr = configSetting.getPropValue(ConfigSettings.STAGE_TRANSFER_ENABLED);
			stageTransferEnabled = Boolean.valueOf(stageEnabledStr).booleanValue();

			// populate the rifcs registration if enabled
			String mdRegEnabledStr = configSetting.getPropValue(ConfigSettings.ANDS_RIFCS_REG_ENABLED);
			mdRegEnabled = Boolean.valueOf(mdRegEnabledStr).booleanValue();
			// set user type is the owner of collection
			viewType = ActConstants.UserViewType.USER.toString();
			populateLinksInUsrCollection();

			setActionSuccessMsg(getText("delete.dataset.success", new String[] { ds.getName() }));
			setNavAfterSuccess();
		} catch (Exception e) {
			addFieldError("export", getText("failed.to.delete.dataset"));
			logger.error(e);
			try {
				retrieveCollection();
				retrieveAllDatasets();
				setNavAfterExcInDS();
			} catch (Exception ex) {
				addFieldError("getCollectionError", getText("dataset.delete.get.collection.details.failed"));
				collectionError = true;
				setNavAfterColExc();
				return INPUT;
			}
			return INPUT;
		}
		return SUCCESS;
	}

	private void recordAuditEvent(Dataset dataset) {
		AuditEvent ev = new AuditEvent();
		ev.setCreatedTime(GregorianCalendar.getInstance().getTime());
		ev.setEvent(dataset.getName() + " has been deleted from the " + collection.getName());
		ev.setEventOwner(collection.getOwner());
		ev.setOperator(user);
		recordActionAuditEvent(ev);
	}

	public void validateDeleteDataset() {

		boolean hasError = false;
		try {
			retrieveCollection();
			// collection = this.dmService.getCollection(collection.getId(), collection.getOwner().getId());
		} catch (Exception e) {
			addFieldError("collectionerror", getText("dataset.delete.get.collection.details.failed"));
			collectionError = true;
			setNavAfterColExc();
			return;
		}

		if (collection == null) {
			addFieldError("collectionerror", getText("dataset.delete.get.collection.details.failed"));
			collectionError = true;
			setNavAfterColExc();
			return;
		}

		try {
			checkUserPermissions(collection.getId(), collection.getOwner().getId());
		} catch (Exception e) {
			addFieldError("checkPermission", getText("check.permissions.error"));
			collectionError = true;
			setNavAfterColExc();
			return;
		}

		if (!permissionBean.isDeleteAllowed()) {
			addFieldError("exportPermission", getText("dataset.delete.permission.denied"));
			hasError = true;
		}
		try {
			dataset = this.dmService.getDatasetById(dataset.getId());
			if (dataset == null) {
				addFieldError("dataset", getText("dataset.delete.failed.nonexisted.dataset.file"));
				hasError = true;
			}
		} catch (Exception e) {
			addFieldError("dataset", getText("dataset.delete.failed.can.not.get.dataset.file"));
			hasError = true;
		}
		if (hasError) {
			try {
				retrieveCollection();
				retrieveAllDatasets();
				setNavAfterExcInDS();
			} catch (Exception e) {
				addFieldError("getCollectionError", getText("dataset.delete.get.collection.details.failed"));
				collectionError = true;
				setNavAfterColExc();
			}
		}

	}

	private void setNavAfterExcInDS() {
		String startNav = null;
		String startNavLink = null;
		String secondNav = collection.getName();
		String thirdNav = getText("delete.dataset.error");

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
			String secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
					+ collection.getOwner().getId() + "&viewType=" + viewType;
			navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, thirdNav, null);
		}
	}

	private void setNavAfterColExc() {
		String startNav = null;
		String startNavLink = null;
		String secondNav = getText("delete.dataset.error");

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
		}
	}

	private void setNavAfterSuccess() {

		String startNav = null;
		String startNavLink = null;

		String secondNav = collection.getName();
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

			String secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
					+ collection.getOwner().getId() + "&viewType=" + viewType;

			navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, null, null);
		}
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public boolean isStageTransferEnabled() {
		return stageTransferEnabled;
	}

	public void setStageTransferEnabled(boolean stageTransferEnabled) {
		this.stageTransferEnabled = stageTransferEnabled;
	}

	public boolean isMdRegEnabled() {
		return mdRegEnabled;
	}

	public void setMdRegEnabled(boolean mdRegEnabled) {
		this.mdRegEnabled = mdRegEnabled;
	}
}
