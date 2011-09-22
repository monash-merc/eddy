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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.AuditEvent;
import au.edu.monash.merc.capture.domain.Dataset;
import au.edu.monash.merc.capture.util.CaptureUtil;

@Scope("prototype")
@Controller("data.importFileAction")
public class ImportFileAction extends DMCoreAction {

	private Map<String, String> responseData = new HashMap<String, String>();

	private boolean extractable;

	private File upload;

	private String uploadContentType;

	private String uploadFileName;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public String importFile() {

		// check the collection and user
		try {
			user = retrieveLoggedInUser();
			collection = this.dmService.getCollection(collection.getId(), collection.getOwner().getId());
			collection.setModifiedTime(GregorianCalendar.getInstance().getTime());
			collection.setModifiedByUser(user);
		} catch (Exception e) {
			logger.error(e);
			responseData.put("success", String.valueOf(false));
			responseData.put("message", getText("dataset.import.get.collection.details.failed"));
			return SUCCESS;
		}

		// check the permissions
		try {
			checkUserPermissions(collection.getId(), collection.getOwner().getId());
		} catch (Exception e) {
			logger.error(e);
			responseData.put("success", String.valueOf(false));
			responseData.put("message", getText("check.permissions.error"));
			return SUCCESS;
		}

		if (!permissionBean.isImportAllowed()) {
			logger.error(getText("dataset.import.permission.denied"));
			responseData.put("success", String.valueOf(false));
			responseData.put("message", getText("dataset.import.permission.denied"));
			return SUCCESS;
		}

		// check the file exists or not
		try {
			if (this.dmService.checkDatasetNameExisted(uploadFileName, collection.getId())) {
				responseData.put("success", String.valueOf(false));
				responseData.put("message", getText("dataset.import.file.already.existed"));
				logger.error(getText("dataset.import.file.already.existed"));
				return SUCCESS;
			}
		} catch (Exception e) {
			logger.error(e);
			responseData.put("success", String.valueOf(false));
			responseData.put("message", getText("dataset.import.check.file.name.error"));
			return SUCCESS;
		}

		// start to upload the file
		FileInputStream fis = null;
		try {
			// read the uploading inputstream
			// fis = new FileInputStream(upload);
			String dataStorePath = configSetting.getPropValue(ConfigSettings.DATA_STORE_LOCATION);
			dataStorePath = CaptureUtil.normalizePath(dataStorePath);
			// start to capture the data from the file.
			Dataset dataset = this.dmService.captureData(uploadFileName, upload, extractable, false, collection, dataStorePath);
			// log the audit event.
			recordAuditEvent(dataset);
			responseData.put("success", String.valueOf(true));
			responseData.put("message", getText("dataset.import.success", new String[] { dataset.getName() }));
			return SUCCESS;
		} catch (Exception e) {
			logger.error(e);
			responseData.put("success", String.valueOf(false));
			responseData.put("message", getText("dataset.import.failed"));
			return SUCCESS;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// ignore whatever
				}
			}
		}
	}

	private void recordAuditEvent(Dataset dataset) {
		AuditEvent ev = new AuditEvent();
		ev.setCreatedTime(GregorianCalendar.getInstance().getTime());
		ev.setEvent(dataset.getName() + " has been imported into the " + collection.getName());
		ev.setEventOwner(collection.getOwner());
		ev.setOperator(user);
		recordActionAuditEvent(ev);
	}

	public Map<String, String> getResponseData() {
		return responseData;
	}

	public void setResponseData(Map<String, String> responseData) {
		this.responseData = responseData;
	}

	public boolean isExtractable() {
		return extractable;
	}

	public void setExtractable(boolean extractable) {
		this.extractable = extractable;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
}
