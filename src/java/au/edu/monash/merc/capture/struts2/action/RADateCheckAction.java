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

import au.edu.monash.merc.capture.dto.RASettingResponse;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
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
 *        Date: 23/04/13 2:33 PM
 */
@Scope("prototype")
@Controller("data.raDateCheckAction")
public class RADateCheckAction extends DMCoreAction {

    private Date raEndDate;

    private RASettingResponse raResponse;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public String checkDate() {
        try {
            this.raResponse = new RASettingResponse();
            if (validateRAEndTime()) {
                this.raResponse.setSucceed(true);
                this.raResponse.setMessage(getText("restrict.access.end.date.valid"));
            }
        } catch (Exception ex) {
            logger.error(ex);
            this.raResponse.setSucceed(false);
            this.raResponse.setMessage(getText("restrict.access.end.date.invalid"));
        }
        return SUCCESS;
    }

    private boolean validateRAEndTime() {
        boolean valid = true;

        if (raEndDate == null) {
            raResponse.setSucceed(false);
            raResponse.setMessage(getText("restrict.access.end.date.must.be.provided"));
            return false;
        }

        Date today = GregorianCalendar.getInstance().getTime();

        DateTime todayDateTime = new DateTime(today);

        DateTime minEndTime = todayDateTime.plusDays(29);
        DateTime maxEndTime = todayDateTime.plusMonths(18);
        DateTime endDateTime = new DateTime(raEndDate);

        System.out.println(" End Date : " + endDateTime);
        System.out.println("endDateTime.isAfter(minEndTime): " + endDateTime.isAfter(minEndTime));
        System.out.println("endDateTime.isAfter(maxEndTime): " + endDateTime.isAfter(maxEndTime));

        if (!endDateTime.isAfter(minEndTime)) {
            raResponse.setSucceed(false);
            raResponse.setMessage(getText("restrict.access.end.date.is.before.min.end.date"));
            return false;
        }
        if (endDateTime.isAfter(maxEndTime)) {
            raResponse.setSucceed(false);
            raResponse.setMessage(getText("restrict.access.end.date.is.after.max.end.date"));
            return false;
        }
        return valid;
    }


    public Date getRaEndDate() {
        return raEndDate;
    }

    public void setRaEndDate(Date raEndDate) {
        this.raEndDate = raEndDate;
    }

    public RASettingResponse getRaResponse() {
        return raResponse;
    }

    public void setRaResponse(RASettingResponse raResponse) {
        this.raResponse = raResponse;
    }
}
