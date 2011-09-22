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
package au.edu.monash.merc.capture.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import au.edu.monash.merc.capture.domain.Collection;

public class StageTransferBean implements Serializable {

	private String serverName;

	private String fromMail;

	private String appName;

	private String toUserName;

	private String toEmail;

	private String stageDir;

	private String destRootDir;

	private boolean metaExtractRequired;

	private boolean ignoreExistedFileTranfer;

	private boolean globalAttributeOnly;

	private Collection collection;

	private List<TransferFileBean> transferFileList = new ArrayList<TransferFileBean>();

	private boolean sendEmailRequired;

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getFromMail() {
		return fromMail;
	}

	public void setFromMail(String fromMail) {
		this.fromMail = fromMail;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String getStageDir() {
		return stageDir;
	}

	public void setStageDir(String stageDir) {
		this.stageDir = stageDir;
	}

	public String getDestRootDir() {
		return destRootDir;
	}

	public void setDestRootDir(String destRootDir) {
		this.destRootDir = destRootDir;
	}

	public boolean ignoreExistedFileTranfer() {
		return ignoreExistedFileTranfer;
	}

	public void setIgnoreExistedFileTranfer(boolean ignoreExistedFileTranfer) {
		this.ignoreExistedFileTranfer = ignoreExistedFileTranfer;
	}

	public boolean isMetaExtractRequired() {
		return metaExtractRequired;
	}

	public void setMetaExtractRequired(boolean metaExtractRequired) {
		this.metaExtractRequired = metaExtractRequired;
	}

	public boolean isGlobalAttributeOnly() {
		return globalAttributeOnly;
	}

	public void setGlobalAttributeOnly(boolean globalAttributeOnly) {
		this.globalAttributeOnly = globalAttributeOnly;
	}

	public List<TransferFileBean> getTransferFileList() {
		return transferFileList;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public void setTransferFileList(List<TransferFileBean> transferFileList) {
		this.transferFileList = transferFileList;
	}

	public boolean sendEmailRequired() {
		return sendEmailRequired;
	}

	public void setSendEmailRequired(boolean sendEmailRequired) {
		this.sendEmailRequired = sendEmailRequired;
	}

}
