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
package au.edu.monash.merc.capture.service;

import java.util.List;

import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.dto.OrderBy;
import au.edu.monash.merc.capture.dto.page.Pagination;
import au.edu.monash.merc.capture.dto.ldap.LdapUser;

public interface UserService {

    User getByUserEmail(String email);

    User getByUserUnigueId(String uniqueId);

    void saveUser(User user);

    User getUserById(long id);

    void updateUser(User user);

    void deleteUser(User user);

    boolean checkUserUniqueIdExisted(String uniqueId);

    boolean checkUserDisplayNameExisted(String userName);

    boolean checkEmailExisted(String email);

    User login(String username, String password, boolean ldap);

    List<User> getAllActiveUsers();

    Pagination<User> getAllUsers(int startPageNo, int recordsPerPage, OrderBy[] orderBys);

    Pagination<User> getAllActiveUsers(int startPageNo, int recordsPerPage, OrderBy[] orderBys);

    Pagination<User> getAllInActiveUsers(int startPageNo, int recordsPerPage, OrderBy[] orderBys);

    LdapUser verifyLdapUser(String authcatId, String password);

    LdapUser ldapLookup(String cnOrEmail);

    User getVirtualUser(int userType);
}
