/**
 * 	Copyright (c) 2010-2011, Monash e-Research Centre
 *	(Monash University, Australia)
 * 	All rights reserved.
 *
 * 	Redistribution and use in source and binary forms, with or without
 * 	modification, are permitted provided that the following conditions are met:
 *		* Redistributions of source code must retain the above copyright
 *    	  notice, this list of conditions and the following disclaimer.
 *		* Redistributions in binary form must reproduce the above copyright
 *    	  notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *		* Neither the name of the Monash University nor the
 *    	  names of its contributors may be used to endorse or promote products
 *    	  derived from this software without specific prior written permission.
 *
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 *	EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 *	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 *	DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 *	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 *	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 *	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 *	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 *	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package au.edu.monash.merc.capture.struts2.action.progress;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.struts2.action.BaseAction;
import au.edu.monash.merc.capture.struts2.multipart.ProgressMonitor;


@Scope("prototype")
@Controller("data.uploadProgress")
public class UploadProgress extends BaseAction {

	private static Logger logger = Logger.getLogger(UploadProgress.class.getName());

	// just a random number
	private String rnd;

	private String stringResult;

	public String execute() {

		//logger.info("Executing the UploadProgress action");
		Object mon_obj = findInSession(ProgressMonitor.SESSION_PROGRESS_MONITOR);

		JSONObject json = new JSONObject();
		if (mon_obj != null) {
			ProgressMonitor monitor = (ProgressMonitor) mon_obj;
			json.accumulate("bytesSent", "" + monitor.getBytesRead());
			json.accumulate("bytesTotal", "" + monitor.getBytesLength());
			json.accumulate("percentComplete", "" + monitor.percentComplete());

			if (!monitor.isStillProcessing() || monitor.isAborted()) {
				json.accumulate("aborted", true);
			} else {
				json.accumulate("aborted", false);
			}

		} else {
			json.accumulate("bytesSent", "" + 0);
			json.accumulate("bytesTotal", "" + 0);
			json.accumulate("percentComplete", "" + 0);
			json.accumulate("aborted", false);
		}

		setStringResult(json.toString());
		if (logger.isDebugEnabled()) {
			logger.debug("JSON Result is: " + getStringResult());
		}
		return SUCCESS;
	}

	public void setRnd(String rnd) {
		this.rnd = rnd;
	}

	public String getRnd() {
		return rnd;
	}

	public void setStringResult(String stringResult) {
		this.stringResult = stringResult;
	}

	public String getStringResult() {
		return stringResult;
	}
}
