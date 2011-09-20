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
package au.edu.monash.merc.capture.struts2.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.Rights;
import au.edu.monash.merc.capture.dto.CCLicense;
import au.edu.monash.merc.capture.dto.CCWSField;
import au.edu.monash.merc.capture.dto.LicenseField;
import au.edu.monash.merc.capture.service.CreativeLicenseService;
import au.edu.monash.merc.capture.util.CaptureUtil;

@Scope("prototype")
@Controller("data.rightsAction")
public class RightsAction extends DMCoreAction {

	private Rights rights;

	private CCWSField commercialField;

	private CCWSField derivativesField;

	private CCWSField jurisdictionField;

	private Map<String, String> jurisMap = new HashMap<String, String>();

	private Map<String, String> rightsMap = new HashMap<String, String>();

	private boolean confirmed;

	private boolean understood;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private CreativeLicenseService cclService;

	public void setCclService(CreativeLicenseService cclService) {
		this.cclService = cclService;
	}

	/**
	 * Show the rights options.
	 * 
	 * @return a String represents SUCCESS or ERROR.                                        lic
	 */
	public String showRightsOptions() {
		try {
			initRightsMap();

			if (rights == null) {
				Rights existedRights = this.dmService.getRightsByCollectionId(collection.getId());
				if (existedRights == null) {
					rights = new Rights();
					rights.setRightsType(ActConstants.RIGHTS_CCCL_TYPE);
				} else {
					rights = existedRights;
				}
			}
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("license.show.options.failed"));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * initialize the rights type
	 */
	private void initRightsMap() {
		rightsMap.put(ActConstants.RIGHTS_CCCL_TYPE, ActConstants.RIGHTS_CCCL_LABEL);
		// rightsMap.put(ActConstants.RIGHTS_CCPD_TYPE, ActConstants.RIGHTS_CCPD_LABEL);
		rightsMap.put(ActConstants.RIGHTS_USEROWNER_TYPE, ActConstants.RIGHTS_USEROWNER_LABEL);
	}

	/**
	 * Select the rights
	 * 
	 * @return a String represents SUCCESS or ERROR.
	 */
	public String selectRights() {
		try {
			Rights existedRights = this.dmService.getRightsByCollectionId(collection.getId());
			if (rights.getRightsType().equals(ActConstants.RIGHTS_CCCL_TYPE)) {
				populateCCCLicense();
				if (existedRights != null && (existedRights.getRightsType().equals(ActConstants.RIGHTS_CCCL_TYPE))) {

					rights.setCommercial(existedRights.getCommercial());
					rights.setDerivatives(existedRights.getDerivatives());
					rights.setJurisdiction(existedRights.getJurisdiction());
				} else {
					if (StringUtils.isBlank(rights.getCommercial())) {
						rights.setCommercial("y");
					}
					if (StringUtils.isBlank(rights.getDerivatives())) {
						rights.setDerivatives("y");
					}
					if (StringUtils.isBlank(rights.getJurisdiction())) {
						rights.setJurisdiction("au");
					}
				}
				return SUCCESS;
			}

			if (rights.getRightsType().equals(ActConstants.RIGHTS_CCPD_TYPE)) {
				return SUCCESS;
			}
			if (rights.getRightsType().equals(ActConstants.RIGHTS_USEROWNER_TYPE)) {

				if ((existedRights != null) && (existedRights.getRightsType().equals(ActConstants.RIGHTS_USEROWNER_TYPE))) {
					rights = existedRights;
				}
				return SUCCESS;
			}
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("license.show.selected.type.failed"));
			initRightsMap();
			rights.setRightsType(ActConstants.RIGHTS_CCCL_TYPE);
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * Validate the rights selection
	 */
	public void validateSelectRights() {
		if (StringUtils.isBlank(rights.getRightsType())) {
			addFieldError("rightsType", getText("license.type.must.be.provided"));
			initRightsMap();
			rights.setRightsType(ActConstants.RIGHTS_CCCL_TYPE);
		}
	}

	private void populateCCCLicense() {
		List<CCWSField> ccwsField = cclService.genLicenseFields(configSetting.getPropValue(ConfigSettings.CCLICENSE_REST_WS_URL));
		for (CCWSField ccf : ccwsField) {
			String id = ccf.getId();
			if (id.equals(ActConstants.COMMERCIAL_ID)) {
				commercialField = ccf;
			}
			if (id.equals(ActConstants.DERIVATIVEs_ID)) {
				derivativesField = ccf;
			}
			if (id.equals(ActConstants.JURISDICTION_ID)) {
				jurisdictionField = ccf;
				List<LicenseField> jlist = jurisdictionField.getLicenseFields();
				populateJurisMap(jlist);
			}
		}
	}

	private void populateJurisMap(List<LicenseField> jlist) {
		Map<String, String> tmpMap = new HashMap<String, String>();
		for (LicenseField lf : jlist) {
			tmpMap.put(lf.getId(), lf.getLabel());
		}
		jurisMap = CaptureUtil.sortByValue(tmpMap);
	}

	public String cccllicense() {
		try {
			if (rights.getRightsType().equals(ActConstants.RIGHTS_CCCL_TYPE)) {
				String wsurl = configSetting.getPropValue(ConfigSettings.CCLICENSE_REST_WS_URL);
				String licenseParams = "commercial=" + rights.getCommercial() + "&derivatives=" + rights.getDerivatives() + "&jurisdiction="
						+ rights.getJurisdiction();
				CCLicense ccl = this.cclService.getCCLicense(wsurl, licenseParams);
				String tempRights = ccl.getLicenseHtml() + " " + ccl.getLicenseHrefText() + " (" + ccl.getLicenseURI() + ").";
				rights.setRightContents(tempRights);
			}
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("license.failed.to.get.cccl.license"));
			return ERROR;
		}
		return SUCCESS;
	}

	public String ccpdlicense() {
		try {
			if (rights.getRightsType().equals(ActConstants.RIGHTS_CCPD_TYPE)) {
				String tmp = "To the extent possible under law, the person who associated CC0 with this work has waived all copyright and related or neighboring rights to this work.";
				rights.setRightContents(tmp);
				return SUCCESS;
			} else {
				addFieldError("rightsType", getText("license.type.invalid"));
				return INPUT;
			}
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("license.failed.to.show.ccpd.license"));
			return ERROR;
		}
	}

	public void validateCcpdlicense() {
		if (!confirmed || !understood) {
			addFieldError("waiveraccept", getText("license.user.must.confirm.cco.waiver"));
		}

	}

	public String showLegalCode() {
		return SUCCESS;
	}

	public Rights getRights() {
		return rights;
	}

	public void setRights(Rights rights) {
		this.rights = rights;
	}

	public CCWSField getCommercialField() {
		return commercialField;
	}

	public void setCommercialField(CCWSField commercialField) {
		this.commercialField = commercialField;
	}

	public CCWSField getDerivativesField() {
		return derivativesField;
	}

	public void setDerivativesField(CCWSField derivativesField) {
		this.derivativesField = derivativesField;
	}

	public CCWSField getJurisdictionField() {
		return jurisdictionField;
	}

	public void setJurisdictionField(CCWSField jurisdictionField) {
		this.jurisdictionField = jurisdictionField;
	}

	public Map<String, String> getJurisMap() {
		return jurisMap;
	}

	public void setJurisMap(Map<String, String> jurisMap) {
		this.jurisMap = jurisMap;
	}

	public Map<String, String> getRightsMap() {
		return rightsMap;
	}

	public void setRightsMap(Map<String, String> rightsMap) {
		this.rightsMap = rightsMap;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public boolean isUnderstood() {
		return understood;
	}

	public void setUnderstood(boolean understood) {
		this.understood = understood;
	}
}
