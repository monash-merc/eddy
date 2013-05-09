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

import au.edu.monash.merc.capture.domain.AuditEvent;
import au.edu.monash.merc.capture.domain.Dataset;
import au.edu.monash.merc.capture.domain.RestrictAccess;
import au.edu.monash.merc.capture.dto.RAResponse;
import au.edu.monash.merc.capture.util.CaptureUtil;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 1/05/13 5:00 PM
 */
@Scope("prototype")
@Controller("data.raSetupAction")
public class RestrictAccessAction extends DMCoreAction {

    private Dataset dataset;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private RAResponse raResponse;

    public String raSetup() {

        raResponse = new RAResponse();
        try {
            //check the collection id first
            if (collection == null) {
                raResponse.setSucceed(false);
                raResponse.setMessage(getText("invalid.collection.id"));
                return SUCCESS;
            }
            if (collection.getOwner() == null) {
                raResponse.setSucceed(false);
                raResponse.setMessage(getText("invalid.collection.owner.id"));
                return SUCCESS;
            }
            //check the user permission9
            try {
                permissionBean = checkPermission(collection.getId(), collection.getOwner().getId());
            } catch (Exception e) {
                logger.error(e);
                raResponse.setSucceed(false);
                raResponse.setMessage(getText("check.permissions.error"));
                return SUCCESS;
            }

            //check the ra setting permission
            if (!permissionBean.isRacAllowed()) {
                raResponse.setSucceed(false);
                raResponse.setMessage(getText("restrict.access.setup.permission.denied"));
                return SUCCESS;
            }
            //if the dataset not provided
            if (dataset == null) {
                raResponse.setSucceed(false);
                raResponse.setMessage(getText("restrict.access.dataset.id.not.provided"));
                return SUCCESS;
            }
            //no restrictAccess object
            if (restrictAccess == null) {
                raResponse.setSucceed(false);
                raResponse.setMessage(getText("restrict.access.end.date.must.be.provided"));
                return SUCCESS;
            }
            //start to work with user input ra values
            Date startDate = restrictAccess.getStartDate();
            Date raEndDate = restrictAccess.getEndDate();

            if (raEndDate == null) {
                raResponse.setSucceed(false);
                raResponse.setMessage(getText("restrict.access.end.date.must.be.provided"));
                return SUCCESS;
            }

            //get the dataset
            dataset = this.dmService.getDatasetById(dataset.getId());

            if (dataset != null) {
                //get the collection
                collection = dataset.getCollection();
                RestrictAccess ra = dataset.getRestrictAccess();
                //if previous restricted access already existed
                if (ra != null) {
                    if (isRaExpired(ra)) {
                        raResponse.setSucceed(false);
                        raResponse.setMessage(getText("restrict.access.period.expired"));
                        return SUCCESS;
                    } else {
                        startDate = ra.getStartDate();
                    }
                }
                //if the end date is before today
                if (isEndDateExpired(raEndDate)) {
                    raResponse.setSucceed(false);
                    raResponse.setMessage(getText("restrict.access.end.input.end.date.expired"));
                    return SUCCESS;
                }

                //if the end date is more than 18 months from the start date
                if (isAfterMaxRaEndDate(startDate, raEndDate)) {
                    raResponse.setSucceed(false);
                    raResponse.setMessage(getText("restrict.access.end.date.is.after.max.end.date"));
                    return SUCCESS;
                }

                //if all parameters are valid, then setup the restricted access
                //update the start date, to avoid an user input it manually
                restrictAccess.setStartDate(startDate);
                restrictAccess.setDataset(dataset);
                if (ra != null) {
                    restrictAccess.setId(ra.getId());
                    this.dmService.updateRestrictAccess(restrictAccess);
                    raResponse.setSucceed(true);
                    raResponse.setMessage(getText("restrict.access.updated.success"));
                    //record the event
                    recordAuditEvent(false);
                } else {
                    this.dmService.saveRestrictAccess(restrictAccess);
                    raResponse.setSucceed(true);
                    raResponse.setMessage(getText("restrict.access.create.success"));
                    //record the event
                    recordAuditEvent(true);
                }
                //Format start date and end date into Sting
                String startDateStr = CaptureUtil.formatDateToYYYYMMDD(restrictAccess.getStartDate());
                String endDateStr = CaptureUtil.formatDateToYYYYMMDD(restrictAccess.getEndDate());
                raResponse.setStartDate(startDateStr);
                raResponse.setEndDate(endDateStr);
                //record the event
                return SUCCESS;

            } else {
                raResponse.setSucceed(false);
                raResponse.setMessage(getText("restrict.access.dataset.not.exist"));
                return SUCCESS;
            }
        } catch (Exception ex) {
            logger.error(ex);
            raResponse.setSucceed(false);
            raResponse.setMessage(getText("restrict.access.setup.failed"));
            return SUCCESS;
        }
    }

    private void recordAuditEvent(boolean created) {
        AuditEvent ev = new AuditEvent();
        ev.setCreatedTime(GregorianCalendar.getInstance().getTime());
        if (created) {
            ev.setEvent("The restricted access for " + dataset.getName() + " has been created under the " + collection.getName());
        } else {
            ev.setEvent("The restricted access for " + dataset.getName() + " has been updated under the " + collection.getName());
        }
        ev.setEventOwner(collection.getOwner());
        ev.setOperator(user);
        recordActionAuditEvent(ev);
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public RAResponse getRaResponse() {
        return raResponse;
    }

    public void setRaResponse(RAResponse raResponse) {
        this.raResponse = raResponse;
    }
}
