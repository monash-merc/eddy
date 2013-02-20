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
import java.util.List;

import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.domain.Rights;

public class PublishBean implements Serializable {

	private List<PartyBean> partyList;

	private List<ProjectBean> activityList;

	private Collection collection;

	private Rights rights;

	private String accessRights;

	private String appName;

	private String anzsrcCode;

	private List<String> relatedInfos;

	private String rifcsStoreLocation;

	private String electronicURL;

	private String physicalAddress;

	private String rifcsGroupName;

	public List<PartyBean> getPartyList() {
		return partyList;
	}

	public void setPartyList(List<PartyBean> partyList) {
		this.partyList = partyList;
	}

	public List<ProjectBean> getActivityList() {
		return activityList;
	}

	public void setActivityList(List<ProjectBean> activityList) {
		this.activityList = activityList;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public Rights getRights() {
		return rights;
	}

	public void setRights(Rights rights) {
		this.rights = rights;
	}

	public String getAccessRights() {
		return accessRights;
	}

	public void setAccessRights(String accessRights) {
		this.accessRights = accessRights;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAnzsrcCode() {
		return anzsrcCode;
	}

	public void setAnzsrcCode(String anzsrcCode) {
		this.anzsrcCode = anzsrcCode;
	}

	public List<String> getRelatedInfos() {
		return relatedInfos;
	}

	public void setRelatedInfos(List<String> relatedInfos) {
		this.relatedInfos = relatedInfos;
	}

	public String getRifcsStoreLocation() {
		return rifcsStoreLocation;
	}

	public void setRifcsStoreLocation(String rifcsStoreLocation) {
		this.rifcsStoreLocation = rifcsStoreLocation;
	}

	public String getElectronicURL() {
		return electronicURL;
	}

	public void setElectronicURL(String electronicURL) {
		this.electronicURL = electronicURL;
	}

	public String getPhysicalAddress() {
		return physicalAddress;
	}

	public void setPhysicalAddress(String physicalAddress) {
		this.physicalAddress = physicalAddress;
	}

	public String getRifcsGroupName() {
		return rifcsGroupName;
	}

	public void setRifcsGroupName(String rifcsGroupName) {
		this.rifcsGroupName = rifcsGroupName;
	}
}