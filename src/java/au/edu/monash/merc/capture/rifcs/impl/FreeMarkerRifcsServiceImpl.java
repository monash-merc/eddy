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

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.exception.DCFileException;
import au.edu.monash.merc.capture.exception.RIFCSException;
import au.edu.monash.merc.capture.rifcs.RifcsService;
import au.edu.monash.merc.capture.util.CaptureUtil;
import au.edu.monash.merc.capture.util.io.DCFileUtils;
import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;
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
public class FreeMarkerRifcsServiceImpl implements RifcsService, ResourceLoaderAware {

    private static String RIFCS_EXT = ".xml";

    public static boolean activityCopied = false;

    @Autowired
    @Qualifier("rifcsFreeMarker")
    private FreeMarkerConfigurer rifcsFreeMarker;

    private ResourceLoader resourceLoader;

    private String rifcsStoreLocation;

    @Autowired
    private ConfigSettings configSettings;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public void setRifcsFreeMarker(FreeMarkerConfigurer rifcsFreeMarker) {
        this.rifcsFreeMarker = rifcsFreeMarker;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setConfigSettings(ConfigSettings configSettings) {
        this.configSettings = configSettings;
    }

    @PostConstruct
    public void init() {
        this.rifcsStoreLocation = this.configSettings.getPropValue(ConfigSettings.ANDS_RIFCS_STORE_LOCATION);
        //copy the activity static rifcs file first if not copy it.
        if (!activityCopied) {
            copyActivityRifcs();
        }
    }

    @Override
    public void createRifcs(String identifier, Map<String, Object> templateValues, String rifcsTemplate) {
        try {
            String rifcsFileName = CaptureUtil.pathEncode(identifier);
            Writer rifcsWriter = new FileWriter(new File(this.rifcsStoreLocation + File.separator + rifcsFileName + RIFCS_EXT));
            Template template = this.rifcsFreeMarker.getConfiguration().getTemplate(rifcsTemplate);
            template.process(templateValues, rifcsWriter);
        } catch (Exception ex) {
            throw new RIFCSException(ex);
        }
    }

    private void copyActivityRifcs() {
        try {
            //get the activity file location
            Resource rifcsTempResource = this.resourceLoader.getResource("classpath:rifcs_template");

            //activity rifcs template directory
            String rifcsDir = rifcsTempResource.getURL().getPath();
            if (StringUtils.isBlank(rifcsDir)) {
                throw new DCFileException("The activity RIF-CS file directory not found");
            }
            String activityFileName = this.configSettings.getPropValue(ConfigSettings.OZFLUX_ACTIVITY_KEY) + RIFCS_EXT;
            String srcActivityFile = rifcsDir + activityFileName;
            String destActivityFile = this.rifcsStoreLocation + File.separator + activityFileName;
            DCFileUtils.copyFile(srcActivityFile, destActivityFile, false);
            activityCopied = true;
            System.out.println("Finished to copy the activity file.");
        } catch (Exception ex) {
            logger.error("copying the activity RIF-CS file error, " + ex.getMessage());
        }
    }

    public String getRifcsStoreLocation() {
        return rifcsStoreLocation;
    }

    public void setRifcsStoreLocation(String rifcsStoreLocation) {
        this.rifcsStoreLocation = rifcsStoreLocation;
    }
}
