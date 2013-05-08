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

import au.edu.monash.merc.capture.common.*;
import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.*;
import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.util.CaptureUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;

/**
 * @author simonyu
 * @version 1.0
 * @since v1.0
 */
@Scope("prototype")
@Controller("data.createColAction")
public class CreateColAction extends DMCoreAction {

    private boolean mdRegEnabled;

    private boolean globalCoverage;

    private Licence licence;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @PostConstruct
    public void initLicenceOpts() {
        //create a default TERN licence
        licence = new Licence();
        licence.setLicenceType(LicenceType.TERN.type());
        licence.setContents(this.configSetting.getPropValue(ConfigSettings.TERN_DATA_LICENCE));
    }

    /**
     * Show create collection action
     *
     * @return a String represents SUCCESS or ERROR.
     */
    public String showCreateCollection() {
        try {
            user = retrieveLoggedInUser();

        } catch (Exception e) {
            logger.error(e.getMessage());
            addActionError(getText("failed.to.show.create.collection.page"));
            setNavForShowExc();
            return INPUT;
        }
        return SUCCESS;
    }

    private void setNavForShowExc() {
        // set the new page title after successful creating a new collection.
        String startNav = getText("mycollection.nav.label.name");
        String startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
        String secondNav = getText("create.new.collection");
        String secondNavLink = null;
        setPageTitle(startNav, secondNav + " Error");
        navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, null, null);
    }

    /**
     * Create a new collection
     *
     * @return a String represents SUCCESS or ERROR.
     */
    public String createCollection() {
        // any root path error. directly return the error to front input page.
        if (!checkDataStorePath()) {
            return INPUT;
        }

        try {
            // retrieve logged in user from database
            user = retrieveLoggedInUser();
            // check the collection name
            String colName = collection.getName();
            if (this.dmService.checkCollectionNameExisted(colName)) {
                addActionError(getText("collection.name.already.existed"));
                return INPUT;
            }
            String dataStorePath = configSetting.getPropValue(ConfigSettings.DATA_STORE_LOCATION);

            String userPath = ActConstants.DATA_STORE_USER_ROOT_PREFIX + user.getId();
            // generate the collection file identifier for file system
            String coFileId = CaptureUtil.generateIdBasedOnTimeStamp();
            // generate the uuid for this collection
            String uuidKey = pidService.genUUIDWithPrefix();
            collection.setUniqueKey(uuidKey);
            collection.setPersistIdentifier(uuidKey);

            // construct the file store location
            String colRelPath = File.separator + userPath + File.separator + coFileId;
            collection.setDirPathName(colRelPath);
            Date date = GregorianCalendar.getInstance().getTime();
            collection.setCreatedTime(date);
            collection.setModifiedTime(date);
            String briefDesc = genShortDesc(collection.getDescription());
            collection.setBriefDesc(briefDesc);

            // set the todate into 23:59:59;
            Date todate = collection.getDateTo();
            if (todate != null) {
                collection.setDateTo(normalizeDate(todate));
            }

            // set the collection owner
            collection.setOwner(user);
            // set collection modified by some user, in this case is an owner user
            collection.setModifiedByUser(user);
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
            //location
            Location location = this.dmService.getLocationByCoverageType(spatialType, spatialValue);
            if (location == null) {
                location = new Location();
                location.setSpatialType(spatialType);
                location.setSpatialCoverage(spatialValue);
                this.dmService.saveLocation(location);
            }
            //save the location inot collection
            collection.setLocation(location);

            List<Permission> coDefaultPerms = setCollectionDefaultPermissions(collection);
            collection.setPermissions(coDefaultPerms);

            //set the collection for licence
            licence.setCollection(collection);
            //set the Date Licence for collection
            collection.setLicence(licence);

            //save the collection
            this.dmService.createCollection(collection, dataStorePath);

            // create handle if handle service is enabled
            String hdlEnabledStr = configSetting.getPropValue(ConfigSettings.HANDLE_SERVICE_ENABLED);
            if (Boolean.valueOf(hdlEnabledStr)) {
                String handle = null;
                try {
                    handle = createHandle(collection);
                    collection.setPersistIdentifier(handle);
                    this.dmService.updateCollection(collection);
                } catch (Exception e) {
                    logger.error(e);
                    addActionError(getText("create.collection.handle.persistent.identifier.failed"));
                    try {
                        //keep the current location first, then we can check the reference later.
                        // if no references, then we have to delete this location.
                        //as the collection will be deleted
                        Location currentLocation = collection.getLocation();
                        long locationId = 0;
                        if (currentLocation != null) {
                            locationId = currentLocation.getId();
                        }

                        //check the reference
                        boolean collectionReferenced = this.dmService.findAnyReferencedCollectionsByLocationId(locationId);
                        if (!collectionReferenced) {
                            this.dmService.deleteLocationById(locationId);
                        }
                        //then delete the collection
                        this.dmService.deleteCollection(collection, dataStorePath);
                    } catch (Exception ex) {
                        logger.error(ex.getMessage() + ". Failed to roll back the collection");
                    }
                    return ERROR;
                }
            }
            // set view type is user
            viewType = UserViewType.USER.type();

            //record down the event
            recordAuditEvent();

            // convert any newline in the description into a br html tag
            String textAreaDesc = collection.getDescription();
            String htmlDesc = nlToBr(textAreaDesc);
            collection.setDescription(htmlDesc);

            //convert any newline in the user defined licecne into a br html tag
            if (licence.getLicenceType().equalsIgnoreCase(LicenceType.USERDEFINED.type())) {
                String licenceContent = licence.getContents();
                String htmlLicence = nlToBr(licenceContent);
                licence.setContents(htmlLicence);
            }

            // populate the rifcs registration if enabled
            String mdRegEnabledStr = configSetting.getPropValue(ConfigSettings.ANDS_RIFCS_REG_ENABLED);
            mdRegEnabled = Boolean.valueOf(mdRegEnabledStr).booleanValue();

            //set the full permissions for owner
            setupFullPermissions();
            //if metadata registration disabled. then we remove the metadata registration permission
            if (!mdRegEnabled) {
                permissionBean.setMdRegAllowed(false);
            }

            // populate the collection links
            populateLinksInUsrCollection();
            // set action successful message
            setActionSuccessMsg(getText("create.collection.success"));
            // set page title and navigation label
            setNavAfterSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage());
            addActionError(getText("failed.to.create.collection"));
            return INPUT;
        }

        return SUCCESS;
    }

    public void validateCreateCollection() {

        if (StringUtils.isBlank(collection.getName())) {
            addFieldError("collection.name", getText("collection.name.required"));
        }

        Date fromDate = collection.getDateFrom();
        Date toDate = collection.getDateTo();

        if ((fromDate != null && toDate == null) || (fromDate == null && toDate != null)) {
            addFieldError("fromToDate", getText("collection.fromDate.and.toDate.must.be.provided"));
        }

        if (fromDate != null && toDate != null) {
            if (fromDate.compareTo(toDate) > 0) {
                addFieldError("invalidFromToDate", getText("collection.start.date.must.be.earlier.than.to.datetime"));
            }
        }

        if (StringUtils.isBlank(collection.getDescription())) {
            addFieldError("collection.description", getText("collection.description.required"));
        }

        if (StringUtils.isNotBlank(collection.getName())) {
            if (!CaptureUtil.notGTFixedLength(collection.getName(), 80)) {
                addFieldError("collection.name.length", getText("collection.name.max.length"));
            }
        }

        if (StringUtils.isNotBlank(collection.getDescription())) {
            if (!CaptureUtil.notGTFixedLength(collection.getDescription(), 4000)) {
                addFieldError("collection.desc.length", getText("collection.desc.max.length"));
            }
        }

        String licenceType = licence.getLicenceType();
        if (licenceType.equals(LicenceType.USERDEFINED.type())) {
            String licenceContent = licence.getContents();
            if (StringUtils.isBlank(licenceContent)) {
                addFieldError("licence.contents", getText("licence.must.be.provided"));
            } else {
                if (!CaptureUtil.notGTFixedLength(licenceContent, 4000)) {
                    addFieldError("licence.contents.length", getText("licence.characters.too.long"));
                }
            }
        } else {
            //set the tern licence content
            this.licence.setContents(this.configSetting.getPropValue(ConfigSettings.TERN_DATA_LICENCE));
        }

        if (StringUtils.isNotBlank(collection.getSpatialCoverage())) {
            if (!CaptureUtil.notGTFixedLength(collection.getSpatialCoverage(), 255)) {
                addFieldError("collection.coverage.length", getText("collection.coverage.max.length"));
            }
        }
    }

    private List<Permission> setCollectionDefaultPermissions(Collection co) {
        List<Permission> defaultPerms = new ArrayList<Permission>();
        //all registered user group permission
        Permission allRegUserPerm = new Permission();
        allRegUserPerm.setViewAllowed(1);
        allRegUserPerm.setPermType(PermType.ALLREGUSER.code());
        User allRegUser = this.userService.getVirtualUser(UserType.ALLREGUSER.code());
        allRegUserPerm.setCollection(co);
        allRegUserPerm.setPermForUser(allRegUser);
        //permission for anonymous user group
        Permission anonymousPerm = new Permission();
        anonymousPerm.setViewAllowed(1);
        anonymousPerm.setPermType(PermType.ANONYMOUS.code());
        User anonymous = this.userService.getVirtualUser(UserType.ANONYMOUS.code());
        anonymousPerm.setCollection(co);
        anonymousPerm.setPermForUser(anonymous);
        defaultPerms.add(allRegUserPerm);
        defaultPerms.add(anonymousPerm);
        return defaultPerms;
    }

    private void recordAuditEvent() {
        AuditEvent ev = new AuditEvent();
        ev.setCreatedTime(GregorianCalendar.getInstance().getTime());
        ev.setEvent(collection.getName() + " has been created");
        ev.setEventOwner(collection.getOwner());
        ev.setOperator(user);
        recordActionAuditEvent(ev);
    }

    // set the page title and navigation label
    private void setNavAfterSuccess() {
        // set the new page title after successful creating a new collection.
        String startNav = getText("mycollection.nav.label.name");
        String secondNav = collection.getName();
        String startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;

        String secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
                + collection.getOwner().getId() + "&viewType=" + viewType;
        setPageTitle(startNav, secondNav);
        navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, null, null);
    }

    // check the dataset path
    private boolean checkDataStorePath() {
        String dataStorePath = configSetting.getPropValue(ConfigSettings.DATA_STORE_LOCATION);
        if (StringUtils.isBlank(dataStorePath)) {
            addActionError(getText("datastore.path.undefined.error"));
            return false;
        }
        try {
            if (!this.dmService.checkWritePermission(dataStorePath)) {
                addActionError(getText("datastore.path.permission.error"));
                return false;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            addActionError(getText("datastore.path.check.permission.failed"));
            return false;
        }
        return true;
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

    public Licence getLicence() {
        return licence;
    }

    public void setLicence(Licence licence) {
        this.licence = licence;
    }
}
