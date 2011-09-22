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
package au.edu.monash.merc.capture.util.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import au.edu.monash.merc.capture.adapter.DataCaptureAdapter;
import au.edu.monash.merc.capture.domain.AuditEvent;
import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.domain.Dataset;
import au.edu.monash.merc.capture.dto.StageTransferBean;
import au.edu.monash.merc.capture.dto.StageTransferProcess;
import au.edu.monash.merc.capture.dto.StageTransferResult;
import au.edu.monash.merc.capture.dto.TransferFileBean;
import au.edu.monash.merc.capture.service.impl.DMServiceImpl;

public class StageFileTransferThread implements Runnable {

	private Thread transferThread;

	private DMServiceImpl dmService;

	private DataCaptureAdapter adapter;

	private StageTransferBean transferBean;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static String MAIL_SUBJECT = "Stage Transfer Results";

	private static String MAIL_TEMPLATE_FILE = "stageTransferMailTemplate.ftl";

	private List<StageTransferResult> successList;

	private List<StageTransferResult> failedList;

	public StageFileTransferThread() {
		this.transferThread = new Thread(this);
		successList = new ArrayList<StageTransferResult>();
		failedList = new ArrayList<StageTransferResult>();
	}

	public StageFileTransferThread(DMServiceImpl dmService, DataCaptureAdapter adapter, StageTransferBean transferBean) {
		this.transferThread = new Thread(this);
		successList = new ArrayList<StageTransferResult>();
		failedList = new ArrayList<StageTransferResult>();
		this.dmService = dmService;
		this.adapter = adapter;
		this.transferBean = transferBean;
	}

	public void transfer() {
		if (this.transferThread == null) {
			this.transferThread = new Thread(this);
		}
		this.transferThread.start();
	}

	public void run() {
		Thread runThread = Thread.currentThread();
		if ((transferThread != null) && (transferThread == runThread)) {

			Collection collection = transferBean.getCollection();
			String stageDir = transferBean.getStageDir();
			String destRootDir = transferBean.getDestRootDir();

			if (registerProcess(collection.getId())) {
				// clear all previous results if any
				successList.clear();
				failedList.clear();
				List<TransferFileBean> tFileList = transferBean.getTransferFileList();

				boolean metaExtractRequired = transferBean.isMetaExtractRequired();

				boolean globalAttOnly = transferBean.isGlobalAttributeOnly();

				boolean ignoreExisted = transferBean.ignoreExistedFileTranfer();

				boolean sendEmailRequired = transferBean.sendEmailRequired();

				for (TransferFileBean ftbean : tFileList) {
					try {
						ftbean.setExtractRequired(metaExtractRequired);
						ftbean.setGlobalAttOnly(globalAttOnly);
						// check the file name exists or not.
						boolean fnameExisted = this.dmService.checkDatasetNameExisted(ftbean.getFileName(), collection.getId());

						if (fnameExisted && ignoreExisted) {
							recordFailedTransfer(ftbean.getFileName(), "File already exists and ignored the transferring by the system");
						} else {
							Dataset ds = this.dmService.captureStageData(ftbean, adapter, collection, stageDir, destRootDir, ignoreExisted);
							if (ds != null) {
								recordSuccessTransfer(ftbean.getFileName());
							}
						}
						// just slow down the processing
						try {
							Thread.sleep(500);
						} catch (Exception se) {
							// just set it into sleep for one second to slow down the data capture;
							// ignore whatever
						}
					} catch (Exception e) {
						logger.error("Failed to capture staging dataset - " + ftbean.getFileName() + ", " + e.getMessage());
						recordFailedTransfer(ftbean.getFileName(), e.getMessage());
					}
				}
				// finally to unlock this process.
				unregisterProcess(collection.getId());
				// record transfer event.
				recordTransferEvent(collection, tFileList.size());
				// send the transfer result email when its' required
				if (sendEmailRequired) {
					sendMailToUser();
				}
			}
		}
	}

	private void recordTransferEvent(Collection co, int totalFileSize) {
		AuditEvent ev = new AuditEvent();
		ev.setCreatedTime(co.getModifiedTime());
		String evmsg = successList.size() + " of total " + totalFileSize + " are successful, and " + failedList.size() + " of total " + totalFileSize
				+ " are failed during a staging transfer in collection - " + co.getName();
		ev.setEvent(evmsg);
		ev.setEventOwner(co.getOwner());
		ev.setOperator(co.getModifiedByUser());
		try {
			this.dmService.saveAuditEvent(ev);
		} catch (Exception e) {
			// if can't persist the audit event, just log the exception, and let the other action finish
			logger.error("Failed to persist the audit event of a staging transfer - " + evmsg + ", " + e.getMessage());
		}
	}

	private void recordSuccessTransfer(String fileName) {
		StageTransferResult sins = new StageTransferResult();
		sins.setSucceed(true);
		sins.setFileName(fileName);
		sins.setMessage("The " + fileName + " has been transferred successfully");
		successList.add(sins);
	}

	private void recordFailedTransfer(String fileName, String message) {
		StageTransferResult fins = new StageTransferResult();
		fins.setSucceed(false);
		fins.setFileName(fileName);
		fins.setMessage(message);
		failedList.add(fins);
	}

	private boolean registerProcess(long coId) {
		StageTransferProcess process = new StageTransferProcess();
		process.setCollectionId(coId);
		return StageTransferManager.registerProcess(String.valueOf(coId), process);
	}

	private boolean unregisterProcess(long coId) {
		return StageTransferManager.unregisterProcess(String.valueOf(coId));

	}

	// send the stage transfer to user
	private void sendMailToUser() {

		String appName = transferBean.getAppName();
		String serverName = transferBean.getServerName();
		String fromMail = transferBean.getFromMail();
		String toMail = transferBean.getToEmail();
		String userName = transferBean.getToUserName();
		String coName = transferBean.getCollection().getName();
		int transferFileSize = transferBean.getTransferFileList().size();

		Map<String, String> templateMap = new HashMap<String, String>();
		templateMap.put("RegisteredUser", userName);
		templateMap.put("Collection", coName);
		templateMap.put("SuccessNum", String.valueOf(successList.size()));
		templateMap.put("TotalFiles", String.valueOf(transferFileSize));
		templateMap.put("FailureNum", String.valueOf(failedList.size()));

		StringBuilder errors = new StringBuilder();
		if (failedList.size() > 0) {
			errors.append("<table class='table_data'>");
			errors.append("<tr><td colspan='2' align='center'>File Name</td><td align='center'>Error Message</td></tr>");
			for (int i = 0; i < failedList.size(); i++) {
				StageTransferResult errorResult = failedList.get(i);
				errors.append("<tr>");
				errors.append("<td>" + (i + 1) + ".</td><td>" + errorResult.getFileName() + "</td><td>" + errorResult.getMessage() + "</td>");
				errors.append("</tr>");
			}
			errors.append("</table>");
		}
		templateMap.put("ErrorReports", errors.toString());
		templateMap.put("SiteName", serverName);
		templateMap.put("AppName", appName);
		// send an email to user
		this.dmService.sendMail(fromMail, toMail, MAIL_SUBJECT, templateMap, MAIL_TEMPLATE_FILE, true);
	}

	public DMServiceImpl getDmService() {
		return dmService;
	}

	public void setDmService(DMServiceImpl dmService) {
		this.dmService = dmService;
	}

	public DataCaptureAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(DataCaptureAdapter adapter) {
		this.adapter = adapter;
	}
}
