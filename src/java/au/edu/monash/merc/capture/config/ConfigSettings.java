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
package au.edu.monash.merc.capture.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Scope("singleton")
@Component
public class ConfigSettings {

    // Data Capture Properties Configuration
    public static String APPLICATION_NAME = "application.name";

    public static String DATA_CAPTURE_ADAPTER_CLASS = "data.capture.adapter.class";

    public static String DATA_STORE_LOCATION = "data.store.location";

    public static String DATA_COLLECTIONS_PHYSICAL_LOCATION = "data.collections.physical.location";

    public static String DATA_COLLECTION_UUID_PREFIX = "data.collection.uuid.prefix";

    public static String DATASET_LEVEL_SEARCH_ENABLE = "data.search.dataset.level.enable";

    public static String SYSTEM_ADMIN_EMAIL = "admin.user.email";

    public static String SYSTEM_ADMIN_NAME = "admin.user.displayName";

    public static String SYSTEM_ADMIN_PWD = "admin.user.password";

    public static String SYSTEM_SERVICE_EMAIL = "system.service.email";

    public static String ALLOW_LOGIN_TRY_TIMES = "allow.login.try.times";

    public static String LOGIN_IP_BLOCK_WAITING_TIMES = "login.ip.block.waiting.time";

    public static String USER_HASH_SEQUENCE = "user.security.hash.sequence";

    public static String ANDS_RIFCS_STORE_LOCATION = "ands.rifcs.files.store.location";

    public static String ANDS_RIFCS_REG_GROUP_NAME = "ands.rifcs.register.group.name";

    // Mail Server Configuration
    public static String SMTP_MAIL_SERVER = "smtp.mail.server";

    public static String TERN_DATA_LICENCE = "tern.data.licence";

    public static String GOOGLE_MAP_ENABLED = "google.map.enabled";

    private final SystemPropertiesConfigurer sysPropertyConfigurer;

    public ConfigSettings(@Qualifier("sysPropertyConfigurer") SystemPropertiesConfigurer sysPropertyConfigurer) {
        this.sysPropertyConfigurer = sysPropertyConfigurer;
    }

    public SystemPropertiesConfigurer getSysPropertyConfigurer() {
        return sysPropertyConfigurer;
    }

    public String getPropValue(String propKey) {
        String propValue = this.sysPropertyConfigurer.getPropValue(propKey);
        if (propValue != null) {
            propValue = propValue.trim();
        }
        return propValue;
    }

    public Map<String, String> getResolvedProps() {
        return this.sysPropertyConfigurer.getResolvedProps();
    }
}
