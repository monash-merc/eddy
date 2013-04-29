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

import au.edu.monash.merc.capture.dto.DatasetVerifyResponse;
import au.edu.monash.merc.capture.util.CaptureUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Date;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 29/04/13 11:07 AM
 */
@Scope("prototype")
@Controller("data.dsVerifyAction")
public class DatasetVerifyAction extends DMCoreAction {

    private DatasetVerifyResponse dsVerifyResponse;

    private String fileName;

    private boolean raEnabled;

    private Date raEndDate;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public String dsVerify() {
        //initialize a response
        dsVerifyResponse = new DatasetVerifyResponse();
        try {

            if (StringUtils.isBlank(fileName)) {
                dsVerifyResponse.setSucceed(false);
                dsVerifyResponse.setMessage(getText("dataset.import.file.must.be.provided"));
                logger.error(getText("dataset.import.file.must.be.provided"));
            } else {
                if (this.dmService.checkDatasetNameExisted(fileName, collection.getId())) {
                    dsVerifyResponse.setSucceed(false);
                    dsVerifyResponse.setMessage(getText("dataset.import.file.already.existed", new String[]{fileName}));
                    logger.error(getText("dataset.import.file.already.existed", new String[]{fileName}));
                }
            }

            if (raEnabled) {
                if (raEndDate == null) {
                    dsVerifyResponse.setSucceed(false);
                    dsVerifyResponse.setMessage(getText("restrict.access.end.date.must.be.provided"));

                } else {
                    Date today = CaptureUtil.getToday();

                    if (isBeforeMinRaEndDate(today, raEndDate)) {
                        dsVerifyResponse.setSucceed(false);
                        dsVerifyResponse.setMessage(getText("restrict.access.end.date.is.before.min.end.date"));
                    }

                    if (isAfterMaxRaEndDate(today, raEndDate)) {
                        dsVerifyResponse.setSucceed(false);
                        dsVerifyResponse.setMessage(getText("restrict.access.end.date.is.after.max.end.date"));
                    }
                }
            }

            if (dsVerifyResponse.isSucceed()) {
                dsVerifyResponse.setMessage(getText("file.import.parameters.valid"));
            }
            return SUCCESS;
        } catch (Exception ex) {
            logger.error(ex);
            dsVerifyResponse.setSucceed(false);
            dsVerifyResponse.setMessage(getText("failed.to.check.file.import.parameters"));
            return SUCCESS;
        }
    }

    public DatasetVerifyResponse getDsVerifyResponse() {
        return dsVerifyResponse;
    }

    public void setDsVerifyResponse(DatasetVerifyResponse dsVerifyResponse) {
        this.dsVerifyResponse = dsVerifyResponse;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isRaEnabled() {
        return raEnabled;
    }

    public void setRaEnabled(boolean raEnabled) {
        this.raEnabled = raEnabled;
    }

    public Date getRaEndDate() {
        return raEndDate;
    }

    public void setRaEndDate(Date raEndDate) {
        this.raEndDate = raEndDate;
    }
}
