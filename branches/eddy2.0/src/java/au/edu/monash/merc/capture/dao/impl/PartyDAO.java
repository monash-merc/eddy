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
import au.edu.monash.merc.capture.domain.Party;
import au.edu.monash.merc.capture.repository.IPartyRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

@Scope("prototype")
@Repository
public class PartyDAO extends HibernateGenericDAO<Party> implements IPartyRepository {

    @Override
    public Party getPartyByPartyKey(String partyKey) {
        Criteria criteria = this.session().createCriteria(this.persistClass);
        criteria.add(Restrictions.eq("partyKey", partyKey));
        return (Party) criteria.uniqueResult();
    }

    @Override
    public Party getPartyByEmail(String email) {
        Criteria criteria = this.session().createCriteria(this.persistClass);
        criteria.add(Restrictions.eq("email", email).ignoreCase());
        return (Party) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Party> getPartyByUserName(String firstName, String lastName) {
        Criteria criteria = this.session().createCriteria(this.persistClass);
        criteria.add(Restrictions.eq("personGivenName", firstName).ignoreCase());
        if (lastName != null) {
            criteria.add(Restrictions.eq("personFamilyName", lastName).ignoreCase());
        }
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Party> getAllParties() {
        Criteria criteria = this.session().createCriteria(this.persistClass);
        criteria.setComment("PartyDAO.getAllParties()");
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Party> getPartiesByCollectionId(long cid) {
        Criteria criteria = this.session().createCriteria(this.persistClass);
        // create a alias for collections
        Criteria colcrit = criteria.createAlias("collections", "co");
        colcrit.add(Restrictions.eq("co.id", cid));
        return criteria.list();
    }

    @Override
    public void deletePartyById(long id) {
        String del_hql = "DELETE FROM " + this.persistClass.getSimpleName() + " AS py WHERE py.id = :id";
        Query query = this.session().createQuery(del_hql);
        query.setLong("id", id);
        query.executeUpdate();
    }

    @Override
    public void deletePartyByPartyKey(String partyKey) {
        String del_hql = "DELETE FROM " + this.persistClass.getSimpleName() + " AS py WHERE py.partyKey = :partyKey";
        Query query = this.session().createQuery(del_hql);
        query.setString("partyKey", partyKey);
        query.executeUpdate();
    }

}
