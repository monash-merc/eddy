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
package au.edu.monash.merc.capture.service.impl;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import au.edu.monash.merc.capture.dto.CCLicense;
import au.edu.monash.merc.capture.dto.CCWSField;
import au.edu.monash.merc.capture.service.CreativeLicenseService;
import au.edu.monash.merc.capture.util.rights.CCSLicenseUtil;

@Scope("prototype")
@Service
public class CreativeLicenseServiceImpl implements CreativeLicenseService {

	protected static List<CCWSField> licenseFields;

	@Override
	public List<CCWSField> genLicenseFields(String ServiceUrl) {

		if (licenseFields == null) {
			CCSLicenseUtil cct = new CCSLicenseUtil();
			cct.setServiceURL(ServiceUrl);
			licenseFields = cct.generateLicenseFields();
		}
		return licenseFields;
	}

	@Override
	public CCLicense getCCLicense(String ServiceUrl, String licenseParams) {
		CCSLicenseUtil cct = new CCSLicenseUtil();
		cct.setServiceURL(ServiceUrl);
		return cct.getCCLicense(licenseParams);
	}
}
