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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.dto.LdapProperty;
import au.edu.monash.merc.capture.service.ldap.LdapService;
import au.edu.monash.merc.capture.util.ldap.LDAPUtil;
import au.edu.monash.merc.capture.util.ldap.LdapUser;

@Scope("prototype")
@Service
public class LdapServiceImpl implements LdapService {

    @Autowired
    private LDAPUtil ldapUtil;

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

    @Override
    public LdapUser searchLdapUser(String cnOrEmail) {
        return this.ldapUtil.findUserInfo(cnOrEmail);
    }
}
