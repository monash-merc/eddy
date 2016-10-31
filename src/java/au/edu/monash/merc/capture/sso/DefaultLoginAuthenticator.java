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
package au.edu.monash.merc.capture.sso;

import au.edu.monash.merc.capture.dao.impl.UserDAO;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.dto.ldap.LdapUser;
import au.edu.monash.merc.capture.service.ldap.LdapService;
import au.edu.monash.merc.capture.util.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class DefaultLoginAuthenticator implements LoginAuthenticator {

    @Autowired
    private UserDAO userDao;

    @Autowired
    private LdapService ldapService;

    public void setLdapService(LdapService ldapService) {
        this.ldapService = ldapService;
    }

    public void setUserDAO(UserDAO userDao) {
        this.userDao = userDao;
    }

    @Override
    public User login(String uniqueId, String password, boolean ldapSupported) {
        String pwd = MD5.hash(password);
        User user = this.userDao.checkUserLogin(uniqueId, pwd);

        // if user name and password are matched, just return this user. the action level should check the user account
        // status whether is activated or not
        if (user != null) {
            return user;
        }

        // if user name and password are not matched, then check whether the ldap is supported or not.
        if (ldapSupported) {
            user = this.userDao.getByUserUnigueId(uniqueId);
            if (user != null) {
                boolean logined = this.ldapService.login(uniqueId, password);
                if (logined) {
                    return user;
                }
            }
        }

        return null;
    }

    @Override
    public LdapUser verifyLdapUser(String authcatId, String password) {
        return this.ldapService.verifyLdapUser(authcatId, password);
    }

    @Override
    public LdapUser ldapLookup(String cnOrEmail) {
        return this.ldapService.lookup(cnOrEmail);
    }
}
