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

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.domain.Dataset;

@Scope("prototype")
@Controller("data.viewDsDataAction")
public class ViewDatasetAction extends DMCoreAction {

    private Dataset dataset;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public String viewDatasetData() {
        try {
            checkUserPermissions(collection.getId(), collection.getOwner().getId());
        } catch (Exception e) {
            logger.error(e);
            addFieldError("checkPermission", getText("check.permissions.error"));
            return INPUT;
        }

        if (!permissionBean.isViewAllowed()) {
            addFieldError("viewPermission", getText("view.dataset.data.permission.denied"));
            return INPUT;
        }

        try {
            Dataset ds = this.dmService.getAllDatasetData(dataset.getId());
            if (ds == null) {
                addActionError(getText("view.dataset.data.failed.dataset.not.exist"));
                return INPUT;
            }
            dataset = ds;
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("view.dataset.data.failed"));
            return INPUT;
        }

        return SUCCESS;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

}
