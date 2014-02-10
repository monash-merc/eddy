/*
 * Copyright (c) 2010-2013, Monash e-Research Centre
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

import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.domain.Licence;

import java.io.Serializable;
import java.util.List;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 14/03/13 3:07 PM
 */
public class MetadataRegistrationBean implements Serializable {

    private List<PartyBean> partyList;

    private Collection collection;

    private Licence licence;

    private String accessRights;

    private String appName;

    private String rifcsStoreLocation;

    private String collectionUrl;

    private String physicalAddress;

    private String rifcsGroupName;

    private String rifcsCollectionTemplate;

    private String rifcsRMPartyTemplate;

    private String rifcsPartyTemplate;

    public List<PartyBean> getPartyList() {
        return partyList;
    }

    public void setPartyList(List<PartyBean> partyList) {
        this.partyList = partyList;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public Licence getLicence() {
        return licence;
    }

    public void setLicence(Licence licence) {
        this.licence = licence;
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

    public String getRifcsStoreLocation() {
        return rifcsStoreLocation;
    }

    public void setRifcsStoreLocation(String rifcsStoreLocation) {
        this.rifcsStoreLocation = rifcsStoreLocation;
    }

    public String getCollectionUrl() {
        return collectionUrl;
    }

    public void setCollectionUrl(String collectionUrl) {
        this.collectionUrl = collectionUrl;
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

    public String getRifcsCollectionTemplate() {
        return rifcsCollectionTemplate;
    }

    public void setRifcsCollectionTemplate(String rifcsCollectionTemplate) {
        this.rifcsCollectionTemplate = rifcsCollectionTemplate;
    }

    public String getRifcsRMPartyTemplate() {
        return rifcsRMPartyTemplate;
    }

    public void setRifcsRMPartyTemplate(String rifcsRMPartyTemplate) {
        this.rifcsRMPartyTemplate = rifcsRMPartyTemplate;
    }

    public String getRifcsPartyTemplate() {
        return rifcsPartyTemplate;
    }

    public void setRifcsPartyTemplate(String rifcsPartyTemplate) {
        this.rifcsPartyTemplate = rifcsPartyTemplate;
    }
}
