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
package au.edu.monash.merc.capture.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.exception.DCInstantiationException;

/**
 * The DataCaptureAdapterFactory class provides the DataCaptureAdapter creation function
 * 
 * @author Simon Yu - Xiaoming.Yu@monash.edu
 * @version v2.0
 * @since v1.0
 * 
 */
@Scope("prototype")
@Component
public class DataCaptureAdapterFactory {

	@Autowired
	private ConfigSettings configSettings;

	public void setConfigSettings(ConfigSettings configSettings) {
		this.configSettings = configSettings;
	}

	private String captureAdapterClass = NetCDFDataCaptureAdapter.class.getName();

	public DataCaptureAdapter createInstance() {
		String adapterClass = configSettings.getPropValue(ConfigSettings.DATA_CAPTURE_ADAPTER_CLASS);
		if (adapterClass != null && !("".equals(adapterClass))) {
			captureAdapterClass = adapterClass;
		}
		try {
			Class<?> clazz = Class.forName(captureAdapterClass);
			if (clazz.isInterface()) {
				throw new DCInstantiationException(clazz.getName() + ", Specified class is an interface");
			}
			return (DataCaptureAdapter) (clazz.newInstance());
		} catch (Exception e) {
			throw new DCInstantiationException(e);
		}
	}
}
