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

package au.edu.monash.merc.capture.service.impl;

import au.edu.monash.merc.capture.dao.impl.CPermissionDAO;
import au.edu.monash.merc.capture.domain.CPermission;
import au.edu.monash.merc.capture.dto.InheritPermissionBean;
import au.edu.monash.merc.capture.service.CPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 26/03/13 4:29 PM
 */

@Scope("prototype")
@Service
@Transactional
public class CPermissionServiceImpl implements CPermissionService {

    @Autowired
    private CPermissionDAO cPermissionDao;

    public void setcPermissionDao(CPermissionDAO cPermissionDao) {
        this.cPermissionDao = cPermissionDao;
    }

    @Override
    public void savePermission(CPermission permission) {
        this.cPermissionDao.add(permission);
    }

    @Override
    public CPermission getPermissionById(long id) {
        return this.cPermissionDao.get(id);
    }

    @Override
    public void updatePermission(CPermission permission) {
        this.cPermissionDao.update(permission);
    }

    @Override
    public void mergePermission(CPermission permission) {
        this.cPermissionDao.merge(permission);
    }

    @Override
    public void deletePermission(CPermission permission) {
        this.cPermissionDao.remove(permission);
    }

    @Override
    public CPermission getUserCollectionPermission(long collectionId, long userId) {
        return this.cPermissionDao.getUserCollectionPermission(collectionId, userId);
    }

    @Override
    public CPermission getAllRegUserCollectionPermission(long collectionId) {
        return this.cPermissionDao.getAllRegUserCollectionPermission(collectionId);
    }

    @Override
    public CPermission getAnonymousCollectionPermission(long collectionId) {
        return this.cPermissionDao.getAnonymousCollectionPermission(collectionId);
    }

    @Override
    public List<CPermission> getCollectionPermissions(long cid) {
        return this.cPermissionDao.getCollectionPermissions(cid);
    }

    @Override
    public InheritPermissionBean getUserInheritPermission(long coId, long userId) {
        return this.cPermissionDao.getUserInheritPermission(coId, userId);
    }

    @Override
    public void deletePermissionByPermId(long permissionId) {
        this.cPermissionDao.deletePermissionByPermId(permissionId);
    }

    @Override
    public void deletePermissionsByCollectionId(long collectionId) {
        this.cPermissionDao.deletePermissionsByCollectionId(collectionId);
    }
}
