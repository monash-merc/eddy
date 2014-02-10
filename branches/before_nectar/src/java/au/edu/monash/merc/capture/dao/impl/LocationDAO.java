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

package au.edu.monash.merc.capture.dao.impl;

import au.edu.monash.merc.capture.dao.HibernateGenericDAO;
import au.edu.monash.merc.capture.domain.Location;
import au.edu.monash.merc.capture.repository.ILocationRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 13/02/13 10:18 AM
 */

@Scope("prototype")
@Repository
public class LocationDAO extends HibernateGenericDAO<Location> implements ILocationRepository {

    @Override
    public void deleteLocationById(long id) {
        String del_hql = "DELETE FROM " + this.persistClass.getSimpleName() + " AS location WHERE location.id = :id";
        Query query = this.session().createQuery(del_hql);
        query.setLong("id", id);
        query.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Location> getLocations(String coverageType) {
        Criteria criteria = this.session().createCriteria(this.persistClass);
        criteria.add(Restrictions.eq("spatialType", coverageType));
        return criteria.list();
    }

    @Override
    public Location getLocationByCoverageType(String coverageType, String spatialCoverage) {
        Criteria criteria = this.session().createCriteria(this.persistClass);
        criteria.add(Restrictions.eq("spatialType", coverageType));
        criteria.add(Restrictions.eq("spatialCoverage", spatialCoverage));
        return (Location) criteria.uniqueResult();
    }
}
