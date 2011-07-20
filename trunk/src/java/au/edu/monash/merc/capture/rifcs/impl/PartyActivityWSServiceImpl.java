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
package au.edu.monash.merc.capture.rifcs.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.dto.ActivityBean;
import au.edu.monash.merc.capture.dto.PartyBean;
import au.edu.monash.merc.capture.dto.ProjectBean;
import au.edu.monash.merc.capture.rifcs.PartyActivityWSService;
import au.edu.monash.merc.capture.ws.client.rm.AndsRMWSClient;

@Scope("prototype")
@Service
public class PartyActivityWSServiceImpl implements PartyActivityWSService {

	private AndsRMWSClient wsclient;

	private boolean configured;

	@Autowired
	private ConfigSettings configSettings;

	public void setConfigSettings(ConfigSettings configSettings) {
		this.configSettings = configSettings;
	}

	private void init() {
		if (wsclient == null) {
			wsclient = new AndsRMWSClient();
			wsclient.setServiceName(configSettings.getPropValue(ConfigSettings.ANDS_PARTY_ACTIVITY_WS_NAME));
			wsclient.setTargetEndpoint(configSettings.getPropValue(ConfigSettings.ANDS_PARTY_ACTIVITY_WS_ENDPOINT));
			wsclient.setTimeout(Long.valueOf(configSettings.getPropValue(ConfigSettings.ANDS_PARTY_ACTIVITY_WS_TIMEOUT)).longValue());
			wsclient.serviceInit();
			configured = true;
		}
	}

	@Override
	public String getNlaId(String authcateId) {
		if (!configured) {
			init();
		}
		return wsclient.getNlaId(authcateId);
	}

	@Override
	public PartyBean getParty(String nlaId) {
		if (!configured) {
			init();
		}
		return wsclient.getPartyRegistryObject(nlaId);
	}

	@Override
	public List<ProjectBean> getProjects(String nlaId) {
		if (!configured) {
			init();
		}
		return wsclient.getProjects(nlaId);
	}

	@Override
	public ActivityBean getActivity(String projectId) {
		if (!configured) {
			init();
		}
		return wsclient.getActivityRegistryObject(projectId);
	}
}
