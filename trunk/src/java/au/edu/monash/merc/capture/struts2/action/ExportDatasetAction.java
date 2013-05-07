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

package au.edu.monash.merc.capture.struts2.action;

import au.edu.monash.merc.capture.common.LicenceType;
import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.Dataset;
import au.edu.monash.merc.capture.domain.Licence;
import au.edu.monash.merc.capture.domain.RestrictAccess;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.InputStream;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 3/05/13 10:43 AM
 */
@Scope("prototype")
@Controller("data.exportDatasetAction")
public class ExportDatasetAction extends DMCoreAction {

    private Dataset dataset;

    private Licence licence;

    private boolean acceptedLicence;

    // For file downloading
    private String contentType;

    private InputStream dsInputStream;

    private String contentDisposition;

    private int bufferSize;

    //logger
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public String preDsExport() {
        try {
            if (exportErrors()) {
                return ERROR;
            }
            //get the licence
            licence = this.dmService.getLicenceByCollectionId(collection.getId());

            if (this.licence == null) {
                this.licence = new Licence();
                this.licence.setLicenceType(LicenceType.TERN.type());
                this.licence.setContents(this.configSetting.getPropValue(ConfigSettings.TERN_DATA_LICENCE));
            }
            return SUCCESS;
        } catch (Exception ex) {
            logger.error(ex);
            addActionError(getText("dataset.export.show.dataset.export.page.failed"));
            return ERROR;
        }
    }

    public String exportDataset() {

        try {
            //get the collection error or dataset error, then just return a error page
            if (exportErrors()) {
                return ERROR;
            }
            //get the licence
            licence = this.dmService.getLicenceByCollectionId(collection.getId());
            if (this.licence == null) {
                this.licence = new Licence();
                this.licence.setLicenceType(LicenceType.TERN.type());
                this.licence.setContents(this.configSetting.getPropValue(ConfigSettings.TERN_DATA_LICENCE));
            }
            //if not accept the data licence, just return the pre export page.
            if (!acceptedLicence) {
                addFieldError("acceptedLicence", getText("dataset.export.must.accept.data.licence"));
                return INPUT;
            }
            String dataStorePath = configSetting.getPropValue(ConfigSettings.DATA_STORE_LOCATION);
            this.dsInputStream = this.dmService.downloadFile(dataset, dataStorePath);
            this.contentDisposition = "attachment;filename=\"" + dataset.getName() + "\"";
            this.bufferSize = 20480;
            this.contentType = "application/octet-stream";
        } catch (Exception e) {
            addFieldError("export", getText("dataset.export.failed"));
            logger.error(e);
            return ERROR;
        }
        return SUCCESS;
    }

    public boolean exportErrors() {
        try {
            collection = this.dmService.getCollection(collection.getId(), collection.getOwner().getId());
        } catch (Exception e) {
            addFieldError("collectionerror", getText("dataset.export.get.collection.details.failed"));
            return true;
        }

        if (collection == null) {
            addFieldError("collectionerror", getText("dataset.export.collection.not.exist"));
            return true;
        }

        try {
            permissionBean = checkPermission(collection.getId(), collection.getOwner().getId());
        } catch (Exception e) {
            addFieldError("checkPermission", getText("check.permissions.error"));
            return true;
        }

        try {
            dataset = this.dmService.getDatasetById(dataset.getId());
            if (dataset == null) {
                addFieldError("dataset", getText("dataset.export.failed.nonexisted.dataset.file"));
                return true;
            }
        } catch (Exception e) {
            addFieldError("dataset", getText("dataset.export.failed.check.dataset.error"));
            return true;
        }
        RestrictAccess ra = this.dmService.getRAByDatasetId(dataset.getId());
        //only check the export permission if restricted access is not expired
        if (ra != null && !raExpired(ra)) {
            if (!permissionBean.isExportAllowed()) {
                addFieldError("exportPermission", getText("dataset.export.permission.denied"));
                return true;
            }
        }
        return false;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public Licence getLicence() {
        return licence;
    }

    public void setLicence(Licence licence) {
        this.licence = licence;
    }

    public boolean isAcceptedLicence() {
        return acceptedLicence;
    }

    public void setAcceptedLicence(boolean acceptedLicence) {
        this.acceptedLicence = acceptedLicence;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public InputStream getDsInputStream() {
        return dsInputStream;
    }

    public void setDsInputStream(InputStream dsInputStream) {
        this.dsInputStream = dsInputStream;
    }

    public String getContentDisposition() {
        return contentDisposition;
    }

    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
}
