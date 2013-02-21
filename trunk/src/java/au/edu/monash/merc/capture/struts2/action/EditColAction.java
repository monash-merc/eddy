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
import au.edu.monash.merc.capture.common.SpatialValue;
import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.AuditEvent;
import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.domain.Location;
import au.edu.monash.merc.capture.domain.Permission;
import au.edu.monash.merc.capture.domain.UserType;
import au.edu.monash.merc.capture.util.CaptureUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author simonyu
 * @version 1.0
 * @since v1.0
 */
@Scope("prototype")
@Controller("data.editColAction")
public class EditColAction extends DMCoreAction {

    private String colNameBeforeUpdate;

    private boolean stageTransferEnabled;

    private boolean mdRegEnabled;

    private boolean globalCoverage;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public String editCollection() {
        try {

            // fetch the old collection from the database first.
            Collection existedCollection = this.dmService.getCollection(collection.getId(), collection.getOwner().getId());
            if (existedCollection != null) {
                // update the new values for collection
                existedCollection.setName(collection.getName());
                //set it's funded if it's funded
                existedCollection.setFunded(collection.isFunded());
                // set modified date time
                existedCollection.setModifiedTime(GregorianCalendar.getInstance().getTime());
                existedCollection.setDescription(collection.getDescription());
                // fetch the brief description.
                String briefDesc = genShortDesc(collection.getDescription());
                existedCollection.setBriefDesc(briefDesc);
                existedCollection.setDateFrom(collection.getDateFrom());

                // set the todate into 23:59:59;
                Date todate = collection.getDateTo();
                if (todate != null) {
                    existedCollection.setDateTo(normalizeDate(todate));
                }

                //check the location
                String spatialType = null;
                String spatialValue = null;
                if (globalCoverage) {
                    spatialType = CoverageType.GLOBAL.type();
                    spatialValue = SpatialValue.GLOBAL.value();
                } else {
                    Location alocation = collection.getLocation();
                    String spValue = alocation.getSpatialCoverage();
                    // check the spatial coverage and type
                    if (StringUtils.isBlank(spValue)) {
                        spatialType = CoverageType.UNKNOWN.type();
                        spatialValue = SpatialValue.UNKNOWN.value();

                    } else {
                        spatialType = CoverageType.KML.type();
                        spatialValue = spValue;
                    }
                }
                //keep the previous location first, then we can check the reference later. if no references, then we have to delete this location.
                Location previousLocation = existedCollection.getLocation();
                long locationId = 0;
                if (previousLocation != null) {
                    locationId = previousLocation.getId();
                }

                Location location = this.dmService.getLocationByCoverageType(spatialType, spatialValue);
                if (location == null) {
                    location = new Location();
                    location.setSpatialType(spatialType);
                    location.setSpatialCoverage(spatialValue);
                    this.dmService.saveLocation(location);
                }
                //save the location inot collection
                existedCollection.setLocation(location);


                // retrieve the permissions for all registered users and anonymous users
                List<Permission> permissions = this.dmService.getCollectionDefaultPerms(collection.getId());
                existedCollection.setModifiedByUser(user);

                this.dmService.updateCollection(existedCollection);

                //try delete this location if can
                try {
                    boolean collectionReferenced = this.dmService.findAnyReferencedCollectionsByLocationId(locationId);
                    if (!collectionReferenced) {
                        this.dmService.deleteLocationById(locationId);
                    }
                } catch (Exception dex) {
                    //if delete the location failed, we just log it
                    logger.error(getText("delete.location.failed") + ", " + dex.getMessage());
                }

                // sign the updated collection object to collection
                collection = existedCollection;
                // record the audit event.
                recordAuditEvent();
                // convert any newline in the description into a br html tag
                String textAreaDesc = collection.getDescription();
                String htmlDesc = nlToBr(textAreaDesc);
                collection.setDescription(htmlDesc);

                // populate the list dataset in this user collection.
                datasets = this.dmService.getDatasetByCollectionIdUsrId(collection.getId(), collection.getOwner().getId());

                // populate the stage transfer if enabled;
                String stageEnabledStr = configSetting.getPropValue(ConfigSettings.STAGE_TRANSFER_ENABLED);
                stageTransferEnabled = Boolean.valueOf(stageEnabledStr).booleanValue();

                // populate the rifcs registration if enabled
                String mdRegEnabledStr = configSetting.getPropValue(ConfigSettings.ANDS_RIFCS_REG_ENABLED);
                mdRegEnabled = Boolean.valueOf(mdRegEnabledStr).booleanValue();

                String mdRegNonLdapEnabledStr = configSetting.getPropValue(ConfigSettings.ANDS_MD_REGISTER_FOR_NON_LDAP_USER_SUPPORTED);
                boolean mdRegNonLdapEnabled = Boolean.valueOf(mdRegNonLdapEnabledStr).booleanValue();
                if (user != null) {
                    //see if it's a ldap user
                    String passwd = user.getPassword();
                    boolean monUser = true;
                    if (!StringUtils.endsWithIgnoreCase(passwd, "ldap")) {
                        monUser = false;
                    }

                    if (mdRegEnabled) {
                        //if metadata registration is enabled, but non-monash user is not supported
                        //we still need to enable an admin to registration metadata
                        if (!monUser && !mdRegNonLdapEnabled) {
                            if ((user.getUserType() != UserType.ADMIN.code() && (user.getUserType() != UserType.SUPERADMIN.code()))) {
                                mdRegEnabled = false;
                            }
                        }
                    }
                }
                // populate the collectionlinks
                populateLinksInUsrCollection();

                // set action successful message
                setActionSuccessMsg(getText("update.collection.success"));
                // set page title and navigation label
                setNavAfterSuccess();

            } else {
                addActionError(getText("collection.update.failed.collection.not.exist"));
                setNavAfterException();
                return INPUT;
            }
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("collection.update.failed"));
            setNavAfterException();
            return INPUT;
        }
        return SUCCESS;
    }

    private void setPrivateCoPerms(List<Permission> permissions) {
        for (Permission perm : permissions) {
            perm.setViewAllowed(false);
            perm.setUpdateAllowed(false);
            perm.setImportAllowed(false);
            perm.setExportAllowed(false);
            perm.setDeleteAllowed(false);
            perm.setChangePermAllowed(false);
        }
    }

    private void setDefaultCoPerms(List<Permission> permissions) {
        for (Permission perm : permissions) {
            perm.setViewAllowed(true);
            perm.setUpdateAllowed(false);
            perm.setImportAllowed(false);
            perm.setExportAllowed(true);
            perm.setDeleteAllowed(false);
            perm.setChangePermAllowed(false);
        }
    }

    private void recordAuditEvent() {
        AuditEvent ev = new AuditEvent();
        ev.setCreatedTime(GregorianCalendar.getInstance().getTime());
        ev.setEvent(collection.getName() + " has been updated");
        ev.setEventOwner(collection.getOwner());
        ev.setOperator(user);
        recordActionAuditEvent(ev);
    }

    public void validateEditCollection() {
        boolean hasErrors = false;
        try {
            checkUserPermissions(collection.getId(), collection.getOwner().getId());
        } catch (Exception e) {
            logger.error(e);
            addFieldError("checkPermission", getText("check.permissions.error"));
            setNavAfterException();
            return;
        }

        if (!permissionBean.isEditAllowed()) {
            addFieldError("updatePermission", getText("collection.update.permission.denied"));
            setNavAfterException();
            return;
        }

        if (StringUtils.isBlank(collection.getName())) {
            addFieldError("collection.name", getText("collection.name.required"));
            hasErrors = true;
        }

        Date fromDate = collection.getDateFrom();
        Date toDate = collection.getDateTo();

        if ((fromDate != null && toDate == null) || (fromDate == null && toDate != null)) {
            addFieldError("fromToDate", getText("collection.fromDate.and.toDate.must.be.provided"));
            hasErrors = true;
        }

        if (fromDate != null && toDate != null) {
            if (fromDate.compareTo(toDate) > 0) {
                addFieldError("invalidFromToDate", getText("collection.start.date.must.be.earlier.than.to.datetime"));
                hasErrors = true;
            }
        }

        if (StringUtils.isBlank(collection.getDescription())) {
            addFieldError("collection.description", getText("collection.description.required"));
            hasErrors = true;
        }

        if (StringUtils.isNotBlank(collection.getName())) {
            if (!CaptureUtil.notGTFixedLength(collection.getName(), 80)) {
                addFieldError("collection.name.length", getText("collection.name.max.length"));
                hasErrors = true;
            }
        }
        if (StringUtils.isNotBlank(collection.getDescription())) {
            if (!CaptureUtil.notGTFixedLength(collection.getDescription(), 4000)) {
                addFieldError("collection.desc.length", getText("collection.desc.max.length"));
                hasErrors = true;
            }
        }
        if (StringUtils.isNotBlank(collection.getSpatialCoverage())) {
            if (!CaptureUtil.notGTFixedLength(collection.getSpatialCoverage(), 255)) {
                addFieldError("collection.coverage.length", getText("collection.coverage.max.length"));
                hasErrors = true;
            }
        }

        if (hasErrors) {
            doPostProcess();
        }

    }

    private void doPostProcess() {

        String startNav = null;
        String startNavLink = null;
        String secondNav = colNameBeforeUpdate;
        String secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
                + collection.getOwner().getId() + "&viewType=" + viewType;
        String thirdNav = getText("update.collection.error");
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

            navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, thirdNav, null);
        }
    }

    protected void setNavAfterException() {

        String startNav = null;
        String startNavLink = null;
        String secondNav = getText("update.collection.error");

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

        String secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
                + collection.getOwner().getId() + "&viewType=" + viewType;

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

            navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, null, null);
        }
    }

    public String getColNameBeforeUpdate() {
        return colNameBeforeUpdate;
    }

    public void setColNameBeforeUpdate(String colNameBeforeUpdate) {
        this.colNameBeforeUpdate = colNameBeforeUpdate;
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

    public boolean isGlobalCoverage() {
        return globalCoverage;
    }

    public void setGlobalCoverage(boolean globalCoverage) {
        this.globalCoverage = globalCoverage;
    }
}
