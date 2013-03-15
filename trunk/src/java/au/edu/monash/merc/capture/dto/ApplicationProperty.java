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

public class ApplicationProperty implements Serializable {

    private String appName;

    private String storeLocation;

    private String collectionPhysicalLocation;

    private String adminEmail;

    private String adminName;

    private String adminPassword;

    private String systemServiceEmail;

    private int loginTryTimes;

    private int blockWaitingTimes;

    private String securityHashSeq;

    private String googleApiKey;

    private boolean stageEnabled;

    private String stageLocation;

    private boolean mdRegEnabled;

    private String rifcsStoreLocation;

    private String rmWsName;

    private String rmWsEndpointAddress;

    private int rmWsTimeout;

    private String andsRegGroupName;

    private boolean hdlWsEnabled;

    private String hdlWsHostName;

    private int hdlWsHostPort;

    private String hdlWsAppId;

    private String hdlWsPath;

    private String hdlWsMethod;

    private String authDomain;

    private String hdlResolverAddress;

    private String ccLicenseWsAddress;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public boolean isStageEnabled() {
        return stageEnabled;
    }

    public void setStageEnabled(boolean stageEnabled) {
        this.stageEnabled = stageEnabled;
    }

    public String getStageLocation() {
        return stageLocation;
    }

    public void setStageLocation(String stageLocation) {
        this.stageLocation = stageLocation;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getSystemServiceEmail() {
        return systemServiceEmail;
    }

    public void setSystemServiceEmail(String systemServiceEmail) {
        this.systemServiceEmail = systemServiceEmail;
    }

    public int getLoginTryTimes() {
        return loginTryTimes;
    }

    public void setLoginTryTimes(int loginTryTimes) {
        this.loginTryTimes = loginTryTimes;
    }

    public int getBlockWaitingTimes() {
        return blockWaitingTimes;
    }

    public void setBlockWaitingTimes(int blockWaitingTimes) {
        this.blockWaitingTimes = blockWaitingTimes;
    }

    public String getSecurityHashSeq() {
        return securityHashSeq;
    }

    public void setSecurityHashSeq(String securityHashSeq) {
        this.securityHashSeq = securityHashSeq;
    }

    public String getGoogleApiKey() {
        return googleApiKey;
    }

    public void setGoogleApiKey(String googleApiKey) {
        this.googleApiKey = googleApiKey;
    }

    public boolean isMdRegEnabled() {
        return mdRegEnabled;
    }

    public void setMdRegEnabled(boolean mdRegEnabled) {
        this.mdRegEnabled = mdRegEnabled;
    }

    public String getRifcsStoreLocation() {
        return rifcsStoreLocation;
    }

    public void setRifcsStoreLocation(String rifcsStoreLocation) {
        this.rifcsStoreLocation = rifcsStoreLocation;
    }

    public String getRmWsName() {
        return rmWsName;
    }

    public void setRmWsName(String rmWsName) {
        this.rmWsName = rmWsName;
    }

    public String getRmWsEndpointAddress() {
        return rmWsEndpointAddress;
    }

    public void setRmWsEndpointAddress(String rmWsEndpointAddress) {
        this.rmWsEndpointAddress = rmWsEndpointAddress;
    }

    public int getRmWsTimeout() {
        return rmWsTimeout;
    }

    public void setRmWsTimeout(int rmWsTimeout) {
        this.rmWsTimeout = rmWsTimeout;
    }

    public String getCollectionPhysicalLocation() {
        return collectionPhysicalLocation;
    }

    public void setCollectionPhysicalLocation(String collectionPhysicalLocation) {
        this.collectionPhysicalLocation = collectionPhysicalLocation;
    }

    public String getAndsRegGroupName() {
        return andsRegGroupName;
    }

    public void setAndsRegGroupName(String andsRegGroupName) {
        this.andsRegGroupName = andsRegGroupName;
    }

    public boolean isHdlWsEnabled() {
        return hdlWsEnabled;
    }

    public void setHdlWsEnabled(boolean hdlWsEnabled) {
        this.hdlWsEnabled = hdlWsEnabled;
    }

    public String getHdlWsHostName() {
        return hdlWsHostName;
    }

    public void setHdlWsHostName(String hdlWsHostName) {
        this.hdlWsHostName = hdlWsHostName;
    }

    public int getHdlWsHostPort() {
        return hdlWsHostPort;
    }

    public void setHdlWsHostPort(int hdlWsHostPort) {
        this.hdlWsHostPort = hdlWsHostPort;
    }

    public String getHdlWsAppId() {
        return hdlWsAppId;
    }

    public void setHdlWsAppId(String hdlWsAppId) {
        this.hdlWsAppId = hdlWsAppId;
    }

    public String getHdlWsPath() {
        return hdlWsPath;
    }

    public void setHdlWsPath(String hdlWsPath) {
        this.hdlWsPath = hdlWsPath;
    }

    public String getHdlWsMethod() {
        return hdlWsMethod;
    }

    public void setHdlWsMethod(String hdlWsMethod) {
        this.hdlWsMethod = hdlWsMethod;
    }

    public String getAuthDomain() {
        return authDomain;
    }

    public void setAuthDomain(String authDomain) {
        this.authDomain = authDomain;
    }

    public String getHdlResolverAddress() {
        return hdlResolverAddress;
    }

    public void setHdlResolverAddress(String hdlResolverAddress) {
        this.hdlResolverAddress = hdlResolverAddress;
    }

    public String getCcLicenseWsAddress() {
        return ccLicenseWsAddress;
    }

    public void setCcLicenseWsAddress(String ccLicenseWsAddress) {
        this.ccLicenseWsAddress = ccLicenseWsAddress;
    }

}
