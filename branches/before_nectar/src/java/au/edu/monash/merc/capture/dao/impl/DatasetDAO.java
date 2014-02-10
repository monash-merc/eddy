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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import au.edu.monash.merc.capture.dao.HibernateGenericDAO;
import au.edu.monash.merc.capture.domain.Dataset;
import au.edu.monash.merc.capture.dto.OrderBy;
import au.edu.monash.merc.capture.dto.page.Pagination;
import au.edu.monash.merc.capture.repository.IDatasetRepository;

@Scope("prototype")
@Repository
public class DatasetDAO extends HibernateGenericDAO<Dataset> implements IDatasetRepository {

    @Override
    public Dataset getDatasetByHandlId(String handleId) {
        return (Dataset) this.session().createCriteria(this.persistClass).add(Restrictions.eq("handleId", handleId)).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Dataset> getDatasetByCollectionId(long cid) {
        Criteria criteria = this.session().createCriteria(this.persistClass);
        Criteria colcrit = criteria.createCriteria("collection");
        colcrit.add(Restrictions.eq("id", cid));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Dataset> getDatasetByCollectionIdUsrId(long cid, long uid) {
        Criteria criteria = this.session().createCriteria(this.persistClass);
        Criteria colcrit = criteria.createCriteria("collection");
        colcrit.add(Restrictions.eq("id", cid));
        Criteria usrcrit = colcrit.createCriteria("owner");
        usrcrit.add(Restrictions.eq("id", uid));
        criteria.addOrder(Order.desc("importDateTime"));
        return criteria.list();
    }

    @Override
    public boolean checkDatasetNameExisted(String dsName, long cid) {
        Criteria criteria = this.session().createCriteria(this.persistClass);
        Criteria colcrit = criteria.createCriteria("collection");
        colcrit.add(Restrictions.eq("id", cid));
        long num = (Long) criteria.setProjection(Projections.rowCount()).add(Restrictions.eq("name", dsName)).uniqueResult();
        if (num == 1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteDatasetByCollectionId(long cid) {
        String del_hql = "DELETE FROM " + this.persistClass.getSimpleName() + " AS ds WHERE ds.collection.id = :id";
        Query query = this.session().createQuery(del_hql);
        query.setLong("id", cid);
        query.executeUpdate();
    }

    @Override
    public void deleteDatasetById(long id) {
        String del_hql = "DELETE FROM " + this.persistClass.getSimpleName() + " AS ds WHERE ds.id = :id";
        Query query = this.session().createQuery(del_hql);
        query.setLong("id", id);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pagination<Dataset> getDatasetByCollectionId(long cid, int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
        Criteria criteria = this.session().createCriteria(this.persistClass);
        Criteria cocriteria = criteria.createCriteria("collection");
        cocriteria.add(Restrictions.eq("id", cid));
        criteria.setProjection(Projections.rowCount());
        int total = ((Long) criteria.uniqueResult()).intValue();
        Pagination<Dataset> dsPage = new Pagination<Dataset>(startPageNo, recordsPerPage, total);

        // query dataset by per-page
        Criteria qcriteria = this.session().createCriteria(this.persistClass);
        Criteria qcoCrit = qcriteria.createCriteria("collection");
        qcoCrit.add(Restrictions.eq("id", cid));

        // add orders
        if (orderBys != null && orderBys.length > 0) {
            for (int i = 0; i < orderBys.length; i++) {
                Order order = orderBys[i].getOrder();
                if (order != null) {
                    qcriteria.addOrder(order);
                }
            }
        } else {
            qcriteria.addOrder(Order.desc("importDateTime"));
        }
        qcriteria.setFirstResult(dsPage.getFirstResult());
        qcriteria.setMaxResults(dsPage.getSizePerPage());
        List<Dataset> dsList = qcriteria.list();
        dsPage.setPageResults(dsList);
        return dsPage;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Dataset> getAllDatasets() {
        Criteria criteria = this.session().createCriteria(this.persistClass);
        return criteria.list();
    }
}
