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

import au.edu.monash.merc.capture.common.UserViewType;
import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.AuditEvent;
import au.edu.monash.merc.capture.domain.Dataset;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Scope("prototype")
@Controller("data.importDSAction")
public class ImportDSAction extends DMCoreAction {

    private boolean extractable;

    // For file uploading
    private File upload;

    private String uploadContentType;

    private String uploadFileName;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public String importDataset() {

        // System.out.println("===> extractbale: " + extractable);
        // start to upload file.
        FileInputStream fis = null;
        try {
            // fis = new FileInputStream(upload);
            collection.setModifiedTime(GregorianCalendar.getInstance().getTime());
            collection.setModifiedByUser(user);
            // persist the dataset.
            String dataStorePath = configSetting.getPropValue(ConfigSettings.DATA_STORE_LOCATION);
            // Dataset dataset = this.dmService.createDataset(uploadFileName, fcontent, extractable, collection,
            // dataStorePath);
            Dataset dataset = this.dmService.captureData(uploadFileName, upload, extractable, false, collection, dataStorePath);
            // re-sort datasets in order
            resortDatasetOrder(dataset);
            recordAuditEvent(dataset);
            // set action successful message
            setActionSuccessMsg(getText("dataset.import.success", new String[]{dataset.getName()}));
            setNavAfterSuccess();

        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("dataset.import.failed"));
            setNavAfterExcInDS();
            return INPUT;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ignore whatever
                }
            }
        }

        return SUCCESS;
    }

    private void recordAuditEvent(Dataset dataset) {
        AuditEvent ev = new AuditEvent();
        ev.setCreatedTime(GregorianCalendar.getInstance().getTime());
        ev.setEvent(dataset.getName() + " has been imported into the " + collection.getName());
        ev.setEventOwner(collection.getOwner());
        ev.setOperator(user);
        recordActionAuditEvent(ev);
    }

    private void resortDatasetOrder(Dataset dataset) {
        List<Dataset> allDs = new ArrayList<Dataset>();
        allDs.add(dataset);
        for (Dataset ds : datasets) {
            allDs.add(ds);
        }
        datasets = allDs;
    }

    public void validateImportDataset() {
        // System.out.println("========= extractable: " + extractable);
        // System.out.println("========= collection id: " + collection.getId());
        //
        // System.out.println("========= owner id: " + collection.getOwner().getId());
        // System.out.println("========= view type: " + viewType);

        boolean hasErrors = false;
        // retrieve the collection and dataset first.
        try {
            retrieveCollection();
            retrieveAllDatasets();
        } catch (Exception e) {
            logger.error(e);
            addFieldError("collectionError", getText("dataset.import.get.collection.details.failed"));
            collectionError = true;
            setNavAfterColExc();
            return;
        }

        try {
            permissionBean = checkPermission(collection.getId(), collection.getOwner().getId());
        } catch (Exception e) {
            addFieldError("checkPermission", getText("check.permissions.error"));
            collectionError = true;
            setNavAfterColExc();
            return;
        }

        if (!permissionBean.isImportAllowed()) {
            addFieldError("importPermission", getText("dataset.import.permission.denied"));
            hasErrors = true;
        }

        if (StringUtils.isBlank(uploadFileName)) {
            addFieldError("uploadFileName", getText("dataset.import.file.must.be.provided"));
            hasErrors = true;
        }

        try {
            if (this.dmService.checkDatasetNameExisted(uploadFileName, collection.getId())) {
                addFieldError("uploadFileName", getText("dataset.import.file.already.existed"));
                hasErrors = true;
            }
        } catch (Exception e) {
            addFieldError("uploadFileName", getText("dataset.import.check.file.name.error"));
            hasErrors = true;
        }

        if (hasErrors) {
            try {
                retrieveCollection();
                retrieveAllDatasets();
                setNavAfterExcInDS();
            } catch (Exception e) {
                logger.error(e);
                // if can't get collection and all datasets, then just use a high level exception handling
                addFieldError("getCollectionError", getText("dataset.import.get.collection.details.failed"));
                collectionError = true;
                setNavAfterColExc();
            }
        }
    }

    // set the nav label if exception is in dataset level.
    private void setNavAfterExcInDS() {
        String startNav = null;
        String startNavLink = null;
        String secondNav = collection.getName();
        String thirdNav = getText("import.dataset.error");

        if (viewType != null) {
            if (viewType.equals(UserViewType.USER.type())) {
                startNav = getText("mycollection.nav.label.name");
                startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
            }

            if (viewType.equals(UserViewType.ALL.type())) {
                startNav = getText("allcollection.nav.label.name");
                startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
            }
            setPageTitle(startNav, (secondNav + " - " + thirdNav));
            String secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
                    + collection.getOwner().getId() + "&viewType=" + viewType;
            navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, thirdNav, null);
        }
    }

    private void setNavAfterColExc() {
        String startNav = null;
        String startNavLink = null;
        String secondNav = getText("import.dataset.error");

        if (viewType != null) {
            if (viewType.equals(UserViewType.USER.type())) {
                startNav = getText("mycollection.nav.label.name");
                startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
            }

            if (viewType.equals(UserViewType.ALL.type())) {
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
            setPageTitle(startNav, secondNav);

            String secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
                    + collection.getOwner().getId() + "&viewType=" + viewType;

            navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, null, null);
        }
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
