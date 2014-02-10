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

import au.edu.monash.merc.capture.common.CoverageType;
import au.edu.monash.merc.capture.common.LicenceType;
import au.edu.monash.merc.capture.common.SpatialValue;
import au.edu.monash.merc.capture.common.UserViewType;
import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.Licence;
import au.edu.monash.merc.capture.domain.Location;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Scope("prototype")
@Controller("data.showColEditAction")
public class ShowEditColAction extends DMCoreAction {

    private String colNameBeforeUpdate;

    private boolean globalCoverage;

    private Licence licence;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public String showEditCollection() {
        try {
            permissionBean = checkPermission(collection.getId(), collection.getOwner().getId());
        } catch (Exception e) {
            logger.error(e);
            addFieldError("checkPermission", getText("check.permissions.error"));
            setNavAfterException();
            return ERROR;
        }
        try {
            if (!permissionBean.isUpdateAllowed()) {
                addFieldError("updatePermission", getText("show.collection.update.page.permission.denied"));
                setNavAfterException();
                return ERROR;
            }
            collection = this.dmService.getCollection(collection.getId(), collection.getOwner().getId());
            if (collection != null) {
                // populate the user object
                colNameBeforeUpdate = collection.getName();
                //date licence

                licence = collection.getLicence();
                //if licence is available, we just create a default.
                if (licence == null) {
                    licence = new Licence();
                    licence.setLicenceType(LicenceType.TERN.type());
                    licence.setContents(this.configSetting.getPropValue(ConfigSettings.TERN_DATA_LICENCE));
                }

                Location location = collection.getLocation();
                //if no location, we just create a new location with unknown value
                if (location == null) {
                    location = new Location();
                    location.setSpatialType(CoverageType.UNKNOWN.type());
                    location.setSpatialCoverage(SpatialValue.UNKNOWN.value());
                }
                String spatialType = location.getSpatialType();

                if (CoverageType.fromType(spatialType).equals(CoverageType.GLOBAL)) {
                    globalCoverage = true;
                    location.setSpatialCoverage("");
                }
                if (CoverageType.fromType(spatialType).equals(CoverageType.UNKNOWN)) {
                    globalCoverage = false;
                    location.setSpatialCoverage("");
                }
                //set location back to collection
                collection.setLocation(location);

                // set page title and nav label.
                setNavAfterSuccess();
            } else {
                addActionError(getText("show.collection.update.page.failed.collection.not.exist"));
                setNavAfterException();
                return ERROR;
            }
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("show.collection.update.page.failed") + " " + e.getMessage());
            setNavAfterException();
            return ERROR;
        }
        return SUCCESS;
    }

    protected void setNavAfterException() {

        String startNav = null;
        String startNavLink = null;
        String secondNav = getText("show.collection.updating");

        if (viewType != null) {
            if (viewType.equals(UserViewType.USER.type())) {
                startNav = getText("mycollection.nav.label.name");
                startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
            }

            if (viewType.equals(UserViewType.ALL.type())) {
                startNav = getText("allcollection.nav.label.name");
                startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
            }
            setPageTitle(startNav, secondNav + " Error");
            navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
        }
    }

    private void setNavAfterSuccess() {

        String startNav = null;
        String startNavLink = null;
        String secondNav = collection.getName();
        String secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
                + collection.getOwner().getId() + "&viewType=" + viewType;

        String thirdNav = getText("update.collection");
        if (viewType != null) {
            if (viewType.equals(UserViewType.USER.type())) {
                startNav = getText("mycollection.nav.label.name");
                startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
            }

            if (viewType.equals(UserViewType.ALL.type())) {
                startNav = getText("allcollection.nav.label.name");
                startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
            }

            // set the new page title after successful creating a new collection.
            setPageTitle(startNav, (secondNav + " - " + thirdNav));

            navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, thirdNav, null);
        }
    }

    public String getColNameBeforeUpdate() {
        return colNameBeforeUpdate;
    }

    public void setColNameBeforeUpdate(String colNameBeforeUpdate) {
        this.colNameBeforeUpdate = colNameBeforeUpdate;
    }

    public boolean isGlobalCoverage() {
        return globalCoverage;
    }

    public void setGlobalCoverage(boolean globalCoverage) {
        this.globalCoverage = globalCoverage;
    }

    public Licence getLicence() {
        return licence;
    }

    public void setLicence(Licence licence) {
        this.licence = licence;
    }
}
