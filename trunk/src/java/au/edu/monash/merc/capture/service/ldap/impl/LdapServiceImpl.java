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
package au.edu.monash.merc.capture.service.ldap.impl;

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.dto.LdapProperty;
import au.edu.monash.merc.capture.dto.ldap.LdapUser;
import au.edu.monash.merc.capture.service.ldap.LdapService;
import au.edu.monash.merc.capture.util.ldap.LDAPUtil;
import au.edu.monash.merc.capture.ws.client.ldapws.LdapWSClient;
import au.edu.monash.merc.capture.ws.client.ldapws.WSConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Scope("prototype")
@Service
public class LdapServiceImpl implements LdapService {

    @Autowired
    private LDAPUtil ldapUtil;


    private LdapWSClient ldapWSClient;

    private boolean ldapWsEnabled = false;

    @Autowired
    private ConfigSettings configSettings;

    public void setLdapUtil(LDAPUtil ldapUtil) {
        this.ldapUtil = ldapUtil;
    }

    public void setConfigSettings(ConfigSettings configSettings) {
        this.configSettings = configSettings;
    }

    @PostConstruct
    public void initLdapEnv() {

        ldapWsEnabled = Boolean.valueOf(configSettings.getPropValue(ConfigSettings.LDAP_AUTH_WS_ENABLED));
        if (ldapWsEnabled) {
            String ldapWsHost = configSettings.getPropValue(ConfigSettings.LDAP_AUTH_WS_HOST);
            int ldapWsPort = Integer.valueOf(configSettings.getPropValue(ConfigSettings.LDAP_AUTH_WS_PORT)).intValue();
            boolean ignoreCertError = Boolean.valueOf(configSettings.getPropValue(ConfigSettings.LDAP_AUTH_WS_CERT_ERROR_IGNORE));
            WSConfig ldapWsConfig = new WSConfig();
            ldapWsConfig.setLdapAuthenServiceHost(ldapWsHost);
            ldapWsConfig.setLdapAuthenServicePort(ldapWsPort);
            ldapWsConfig.setIgnoreCertError(ignoreCertError);
            ldapWSClient = new LdapWSClient(ldapWsConfig);
        } else {
            LdapProperty ldapProp = new LdapProperty();
            ldapProp.setLdapFactory(configSettings.getPropValue(ConfigSettings.LDAP_FACTORY));
            ldapProp.setLdapServer(configSettings.getPropValue(ConfigSettings.LDAP_SERVER_URL));
            ldapProp.setProtocol(configSettings.getPropValue(ConfigSettings.LDAP_SECURITY_PROTOCOL));
            ldapProp.setAuthentication(configSettings.getPropValue(ConfigSettings.LDAP_AUTHENTICATION));
            ldapProp.setBaseDN(configSettings.getPropValue(ConfigSettings.LDAP_BASE_DN));
            ldapProp.setBindBaseDnRequired(Boolean.valueOf(configSettings.getPropValue(ConfigSettings.LDAP_BIND_BASE_DN_REQUIRED)));
            ldapProp.setAttUID(configSettings.getPropValue(ConfigSettings.LDAP_UID_ATTR_NAME));
            ldapProp.setAttMail(configSettings.getPropValue(ConfigSettings.LDAP_MAIL_ATTR_NAME));
            ldapProp.setAttGender(configSettings.getPropValue(ConfigSettings.LDAP_GENDER_ATTR_NAME));
            ldapProp.setAttCN(configSettings.getPropValue(ConfigSettings.LDAP_CN_ATTR_NAME));
            ldapProp.setAttSn(configSettings.getPropValue(ConfigSettings.LDAP_SN_ATTR_NAME));
            ldapProp.setAttGivenname(configSettings.getPropValue(ConfigSettings.LDAP_GIVENNAME_ATTR_NAME));
            ldapProp.setAttPersonalTitle(configSettings.getPropValue(ConfigSettings.LDAP_PERSONAL_TITLE_ATTR_NAME));
            this.ldapUtil.initEnv(ldapProp);
        }
    }

    @Override
    public LdapUser lookup(String cnOrEmail) {
        if (ldapWsEnabled) {
            return this.ldapWSClient.lookup(cnOrEmail);
        } else {
            return this.ldapUtil.findUserInfo(cnOrEmail);
        }
    }

    @Override
    public LdapUser verifyLdapUser(String authcatId, String password) {
        if (ldapWsEnabled) {
            return this.ldapWSClient.verifyLdapUser(authcatId, password);
        } else {
            return this.ldapUtil.validateLdapUser(authcatId, password);
        }
    }

    @Override
    public boolean login(String authcatId, String password) {
        if (ldapWsEnabled) {
            return this.ldapWSClient.login(authcatId, password);
        } else {
            return this.ldapUtil.login(authcatId, password);
        }
    }
}
