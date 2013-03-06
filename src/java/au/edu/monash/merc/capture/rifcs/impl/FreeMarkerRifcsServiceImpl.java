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

package au.edu.monash.merc.capture.rifcs.impl;

import au.edu.monash.merc.capture.exception.RIFCSException;
import au.edu.monash.merc.capture.rifcs.RifcsService;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 5/03/13 2:51 PM
 */
@Scope("prototype")
@Service
@Qualifier("rifcsService")
public class FreeMarkerRifcsServiceImpl implements RifcsService {

    private static String RIFCS_EXT = ".xml";

    @Autowired
    @Qualifier("rifcsFreeMarker")
    private FreeMarkerConfigurer rifcsFreeMarker;

    public void setRifcsFreeMarker(FreeMarkerConfigurer rifcsFreeMarker) {
        this.rifcsFreeMarker = rifcsFreeMarker;
    }

    @Override
    public void createRifcs(String rifcsStoreLocaton, String identifier, Map<String, Object> templateValues, String rifcsTemplate) {
        try {
            Writer rifcsWriter = new FileWriter(new File(rifcsStoreLocaton + File.separator + identifier + RIFCS_EXT));
            Template template = this.rifcsFreeMarker.getConfiguration().getTemplate(rifcsTemplate);
            template.process(templateValues, rifcsWriter);
        } catch (Exception ex) {
            throw new RIFCSException(ex);
        }
    }
}
