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

import au.edu.monash.merc.capture.dao.impl.LicenceDAO;
import au.edu.monash.merc.capture.domain.Licence;
import au.edu.monash.merc.capture.service.LicenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 7/03/13 11:38 AM
 */
@Scope("prototype")
@Service
@Transactional
public class LicenceServiceImpl implements LicenceService {

    @Autowired
    private LicenceDAO licenceDao;

    public void setLicenceDao(LicenceDAO licenceDao) {
        this.licenceDao = licenceDao;
    }

    @Override
    public void saveLicence(Licence licence) {
        this.licenceDao.add(licence);
    }

    @Override
    public void updateLicence(Licence licence) {
        this.licenceDao.update(licence);
    }

    @Override
    public void deleteLicence(Licence licence) {
        this.licenceDao.remove(licence);
    }

    @Override
    public void deleteLicenceById(long id) {
        this.licenceDao.deleteLicenceById(id);
    }

    @Override
    public Licence getLicenceById(long id) {
        return this.licenceDao.get(id);
    }

    @Override
    public Licence getLicenceByCollectionId(long cid) {
        return this.getLicenceByCollectionId(cid);
    }
}
