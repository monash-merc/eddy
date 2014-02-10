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

import au.edu.monash.merc.capture.util.io.DCFileUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.InputStream;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 15/02/13 12:09 PM
 */
@Scope("prototype")
@Controller("site.resourceAction")
public class ResourceAction extends DMCoreAction {

    // For file downloading
    private String fname;

    private String contentType;

    private InputStream fileInputStream;

    private String contentDisposition;

    private int bufferSize;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public String datainfo() {
        return SUCCESS;
    }

    public String licenceinfo() {
        return SUCCESS;
    }

    public String netcdf() {
        return SUCCESS;
    }

    public String utilities() {
        return SUCCESS;
    }

    public String faq() {
        return SUCCESS;
    }

    public String userguide() {
        return SUCCESS;
    }

    public String rainfo() {
        return SUCCESS;
    }


    public String ddoc() {
        String dwFile = "docs" + File.separator + fname;
        String targetFile = getAppRoot() + dwFile;
        try {
            this.fileInputStream = DCFileUtils.readFileToInputStream(targetFile);
            this.contentDisposition = "attachment;filename=\"" + fname + "\"";
            this.bufferSize = 10240;
            this.contentType = "application/octet-stream";
        } catch (Exception ex) {
            logger.error("the document " + fname + " is not found.");
            return FILE_NOT_FOUND;
        }
        return SUCCESS;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public InputStream getFileInputStream() {
        return fileInputStream;
    }

    public void setFileInputStream(InputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
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
