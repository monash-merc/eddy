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

import org.springframework.beans.factory.annotation.Autowired;
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

    // ANDS RIF-CS Configuration
    public static String ANDS_RIFCS_REG_ENABLED = "ands.rifcs.register.enabled";

    public static String ANDS_PARTY_ACTIVITY_WS_NAME = "ands.party.activtiy.rm.ws.name";

    public static String ANDS_PARTY_ACTIVITY_WS_ENDPOINT = "ands.party.activtiy.rm.ws.endpoint";

    public static String ANDS_PARTY_ACTIVITY_WS_TIMEOUT = "ands.party.activtiy.rm.ws.timeout";

    public static String ANDS_RIFCS_STORE_LOCATION = "ands.rifcs.files.store.location";

    public static String ANDS_RIFCS_REG_GROUP_NAME = "ands.rifcs.register.group.name";

    public static String HANDLE_SERVICE_ENABLED = "ands.handle.ws.enabled";

    public static String HANDLE_SERVICE_IGNORE_CERT_ERROR = "ands.handle.ws.ignore.cert.error";

    public static String HANDLE_SERVICE_HOST = "ands.handle.ws.host.name";

    public static String HANDLE_SERVICE_HOST_PORT = "ands.handle.ws.host.port";

    public static String HANDLE_SERVICE_PATH = "ands.handle.ws.path";

    public static String HANDLE_SERVICE_MINT_METHOD = "ands.handle.ws.mint.method";

    public static String HANDLE_SERVICE_IDENTIFIER = "ands.handle.ws.app.identifier";

    public static String HANDLE_SERVICE_APPID = "ands.handle.ws.authentication.appid";

    public static String HANDLE_SERVICE_AUTH_DOMAIN = "ands.handle.ws.auth.domain";

    public static String HANDLE_RESOLVER_SERVER = "ands.handle.resolver.url";

    // Mail Server Configuration
    public static String SMTP_MAIL_SERVER = "smtp.mail.server";

    // LDAP Configuration
    public static String LDAP_AUTH_SUPPORTED = "ldap.authentication.supported";

    public static String LDAP_AUTH_WS_ENABLED = "ldap.remote.authen.ws.enabled";

    public static String LDAP_AUTH_WS_HOST = "ldap.remote.ws.host.name";

    public static String LDAP_AUTH_WS_PORT = "ldap.remote.ws.host.port";

    public static String LDAP_AUTH_WS_CERT_ERROR_IGNORE = "ldap.remote.ws.cert.error.ignore";

    public static String LDAP_FACTORY = "ldap.factory";

    public static String LDAP_SERVER_URL = "ldap.server.url";

    public static String LDAP_BASE_DN = "ldap.base.dn";

    public static String LDAP_BIND_BASE_DN_REQUIRED = "ldap.bind.base.dn.required";

    public static String LDAP_SECURITY_PROTOCOL = "ldap.security.protocol";

    public static String LDAP_AUTHENTICATION = "ldap.authentication";

    public static String LDAP_UID_ATTR_NAME = "ldap.uid.attrName";

    public static String LDAP_MAIL_ATTR_NAME = "ldap.mail.attrName";

    public static String LDAP_CN_ATTR_NAME = "ldap.cn.attrName";

    public static String LDAP_GENDER_ATTR_NAME = "ldap.gender.attrName";

    public static String LDAP_SN_ATTR_NAME = "ldap.sn.attrName";

    public static String LDAP_GIVENNAME_ATTR_NAME = "ldap.givenname.attrName";

    public static String LDAP_PERSONAL_TITLE_ATTR_NAME = "ldap.personaltitle.attrName";

    public static String TERN_DATA_LICENCE = "tern.data.licence";

    public static String OZFLUX_ACTIVITY_KEY = "ozflux.activity.key";

    public static String RIFCS_COLLECTION_TEMPLATE = "rifcs.collection.template";

    public static String RIFCS_RM_PARTY_TEMPLATE = "rifcs.rm.party.template";

    public static String RIFCS_NONE_RM_PARTY_TEMPLATE = "rifcs.none.rm.party.template";

    @Autowired
    @Qualifier("sysPropertyConfigurer")
    private SystemPropertiesConfigurer sysPropertyConfigurer;

    public SystemPropertiesConfigurer getSysPropertyConfigurer() {
        return sysPropertyConfigurer;
    }

    public void setSysPropertyConfigurer(SystemPropertiesConfigurer sysPropertyConfigurer) {
        this.sysPropertyConfigurer = sysPropertyConfigurer;
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
