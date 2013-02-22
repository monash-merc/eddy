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

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.dto.StageTransferBean;
import au.edu.monash.merc.capture.dto.TransferFileBean;
import au.edu.monash.merc.capture.util.CaptureUtil;
import au.edu.monash.merc.capture.util.stage.ScanFileFilter;
import au.edu.monash.merc.capture.util.stage.StageTransferManager;
import org.apache.log4j.Logger;

import java.util.*;

public class StageTransferAction extends DMCoreAction {

	private List<TransferFileBean> stageFiles;

	private boolean extractRequired;

	private boolean ignoreExisted;

	private boolean sendEmailRequired;

	private String extractAllOrGlobal;

	private Map<String, String> extractChoices;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static String ALL_STR = "All";

	private static String GLOBAL_STR = "Global";

	public String discoverFiles() {

		setViewColDetailLink(ActConstants.VIEW_COLLECTION_DETAILS_ACTION);
		// init extract metadata choice
		initExtractChoice();
		// init the default required transfer conditions
		initRequiredCon();
		// check the permissions
		try {
			checkUserPermissions(collection.getId(), collection.getOwner().getId());
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("stage.transfer.check.permissions.error"));
			setNavAfterException();
			return ERROR;
		}

		if (!permissionBean.isImportAllowed()) {
			logger.error(getText("stage.transfer.permission.denied"));
			addActionError(getText("stage.transfer.permission.denied"));
			setNavAfterException();
			return ERROR;
		}

		try {
			if (user == null) {
				retrieveLoggedInUser();
			}
			collection = this.dmService.getCollection(collection.getId(), collection.getOwner().getId());
			// if there is a stage transfer in this collection. just ask user to wait;
			if (StageTransferManager.findTransferProcess(String.valueOf(collection.getId()))) {
				addFieldError("transfernotfinished", getText("stage.transfer.working.in.progress"));
				setNavAfterException();
				return INPUT;
			}
			String stagePath = configSetting.getPropValue(ConfigSettings.DATA_STAGE_LOCATION);

			ScanFileFilter filter = new ScanFileFilter();
			// filter.setFileExt(".nc");

			List<String> fileNames = dmService.discoverFiles(stagePath, filter);

			stageFiles = convertFileBean(fileNames);
			// set the navigation
			setNavAfterSuccess();
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("stage.transfer.list.stage.files.failed"));
			setNavAfterException();
			return ERROR;
		}

		return SUCCESS;
	}

	private void initRequiredCon() {
		extractRequired = true;
		sendEmailRequired = true;
		extractAllOrGlobal = ALL_STR;
	}

	private void initExtractChoice() {
		extractChoices = new HashMap<String, String>();
		extractChoices.put(ALL_STR, "All");
		extractChoices.put(GLOBAL_STR, "Global Attributes Only");
	}

	public String stageTransfer() {

		setViewColDetailLink(ActConstants.VIEW_COLLECTION_DETAILS_ACTION);
		// init extract metadata choice
		initExtractChoice();
		// check the permissions
		try {
			checkUserPermissions(collection.getId(), collection.getOwner().getId());
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("stage.transfer.check.permissions.error"));
			setNavAfterException();
			return ERROR;
		}

		if (!permissionBean.isImportAllowed()) {
			logger.error(getText("stage.transfer.permission.denied"));
			addActionError(getText("stage.transfer.permission.denied"));
			setNavAfterException();
			return ERROR;
		}

		if (user == null) {
			retrieveLoggedInUser();
		}
		try {
			collection = this.dmService.getCollection(collection.getId(), collection.getOwner().getId());
			collection.setModifiedTime(GregorianCalendar.getInstance().getTime());
			collection.setModifiedByUser(user);

			List<TransferFileBean> selectedFileBeans = new ArrayList<TransferFileBean>();

			if (stageFiles != null) {
				for (TransferFileBean fbean : stageFiles) {
					if (fbean.isSelected()) {
						selectedFileBeans.add(fbean);
					}
				}
			} else {
				stageFiles = new ArrayList<TransferFileBean>();
			}
			if (checkTransferPropErrors(selectedFileBeans)) {
				setNavAfterException();
				return INPUT;
			}

			StageTransferBean transferBean = createStageTransferBean(collection, user, selectedFileBeans);

			this.dmService.stageTransfer(transferBean);

			if (sendEmailRequired) {
				addActionMessage(getText("stage.transfer.start.with.email.message"));
			} else {
				addActionMessage(getText("stage.transfer.start.message"));
			}
			setNavAfterSuccess();
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("stage.transfer.failed.to.start.stage.transfer"));
			setNavAfterException();
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * Validate the transfer
	 * 
	 * @param fileList
	 * @return
	 */
	private boolean checkTransferPropErrors(List<TransferFileBean> fileList) {
		if (fileList.size() == 0) {
			addFieldError("fileRequired", getText("stage.transfer.files.required"));
			return true;
		}
		return false;
	}

	private StageTransferBean createStageTransferBean(Collection co, User usr, List<TransferFileBean> stageFileList) {
		StageTransferBean transferBean = new StageTransferBean();

		String serverQName = getServerQName();
		String appName = configSetting.getPropValue(ConfigSettings.APPLICATION_NAME);
		String adminEmail = configSetting.getPropValue(ConfigSettings.SYSTEM_SERVICE_EMAIL);
		String stagePath = configSetting.getPropValue(ConfigSettings.DATA_STAGE_LOCATION);
		String datastorePath = configSetting.getPropValue(ConfigSettings.DATA_STORE_LOCATION);

		transferBean.setAppName(appName);
		transferBean.setServerName(serverQName);
		transferBean.setFromMail(adminEmail);
		transferBean.setToEmail(usr.getEmail());
		transferBean.setToUserName(usr.getDisplayName());
		transferBean.setCollection(co);
		transferBean.setTransferFileList(stageFileList);
		transferBean.setStageDir(CaptureUtil.normalizePath(stagePath));
		transferBean.setDestRootDir(CaptureUtil.normalizePath(datastorePath));
		transferBean.setIgnoreExistedFileTranfer(ignoreExisted);
		transferBean.setMetaExtractRequired(extractRequired);
		transferBean.setSendEmailRequired(sendEmailRequired);
		// System.out.println(" === extract metadata? : " + extractRequired);
		// System.out.println(" === extractAllOrGlobal? : + " + extractAllOrGlobal);
		if (extractRequired) {
			if (extractAllOrGlobal != null && extractAllOrGlobal.equals(GLOBAL_STR)) {
				transferBean.setGlobalAttributeOnly(true);
			}
		}
		return transferBean;
	}

	private List<TransferFileBean> convertFileBean(List<String> files) {
		List<TransferFileBean> fileBeans = new ArrayList<TransferFileBean>();
		for (String filename : files) {
			TransferFileBean fbean = new TransferFileBean();
			fbean.setFileName(filename);
			fileBeans.add(fbean);
		}
		return fileBeans;
	}

	private void setNavAfterSuccess() {

		String startNav = null;
		String startNavLink = null;

		String secondNav = collection.getName();
		String secondNavLink = null;
		String thirdNav = getText("stage.transfer.title");
		if (viewType != null) {
			// System.out.println("===============> view type: " + viewType);
			if (viewType.equals(ActConstants.UserViewType.USER.toString())) {
				startNav = getText("mycollection.nav.label.name");
				startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
				secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
						+ collection.getOwner().getId() + "&viewType=" + viewType;
			}

			if (viewType.equals(ActConstants.UserViewType.ALL.toString())) {
				startNav = getText("allcollection.nav.label.name");
				startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
				secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
						+ collection.getOwner().getId() + "&viewType=" + viewType;
			}
			// set the new page title after successful creating a new collection.
			setPageTitle(startNav, secondNav + " - " + thirdNav);
			navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, thirdNav, null);
		}
	}

	private void setNavAfterException() {
		String startNav = null;
		String startNavLink = null;

		String secondNav = collection.getName();
		String secondNavLink = null;
		String thirdNav = getText("stage.transfer.title");
		if (viewType != null) {
			// System.out.println("===============> view type: " + viewType);
			if (viewType.equals(ActConstants.UserViewType.USER.toString())) {
				startNav = getText("mycollection.nav.label.name");
				startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
				secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
						+ collection.getOwner().getId() + "&viewType=" + viewType;
			}

			if (viewType.equals(ActConstants.UserViewType.ALL.toString())) {
				startNav = getText("allcollection.nav.label.name");
				startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
				secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
						+ collection.getOwner().getId() + "&viewType=" + viewType;
			}
			// set the new page title after successful creating a new collection.
			setPageTitle(startNav, secondNav + " - " + (thirdNav + " Error"));
			navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, thirdNav, null);
		}
	}

	public List<TransferFileBean> getStageFiles() {
		return stageFiles;
	}

	public void setStageFiles(List<TransferFileBean> stageFiles) {
		this.stageFiles = stageFiles;
	}

	public boolean isExtractRequired() {
		return extractRequired;
	}

	public void setExtractRequired(boolean extractRequired) {
		this.extractRequired = extractRequired;
	}

	public boolean isIgnoreExisted() {
		return ignoreExisted;
	}

	public void setIgnoreExisted(boolean ignoreExisted) {
		this.ignoreExisted = ignoreExisted;
	}

	public boolean isSendEmailRequired() {
		return sendEmailRequired;
	}

	public void setSendEmailRequired(boolean sendEmailRequired) {
		this.sendEmailRequired = sendEmailRequired;
	}

	public String getExtractAllOrGlobal() {
		return extractAllOrGlobal;
	}

	public void setExtractAllOrGlobal(String extractAllOrGlobal) {
		this.extractAllOrGlobal = extractAllOrGlobal;
	}

	public Map<String, String> getExtractChoices() {
		return extractChoices;
	}

	public void setExtractChoices(Map<String, String> extractChoices) {
		this.extractChoices = extractChoices;
	}

}
