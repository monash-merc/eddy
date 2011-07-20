/**
 * 	Copyright (c) 2010-2011, Monash e-Research Centre
 *	(Monash University, Australia)
 * 	All rights reserved.
 *
 * 	Redistribution and use in source and binary forms, with or without
 * 	modification, are permitted provided that the following conditions are met:
 *		* Redistributions of source code must retain the above copyright
 *    	  notice, this list of conditions and the following disclaimer.
 *		* Redistributions in binary form must reproduce the above copyright
 *    	  notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *		* Neither the name of the Monash University nor the
 *    	  names of its contributors may be used to endorse or promote products
 *    	  derived from this software without specific prior written permission.
 *
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 *	EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 *	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 *	DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 *	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 *	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 *	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 *	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 *	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
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
import au.edu.monash.merc.capture.domain.PermissionRequest;
import au.edu.monash.merc.capture.dto.OrderBy;
import au.edu.monash.merc.capture.dto.page.Pagination;
import au.edu.monash.merc.capture.repository.IPermissionReqRepository;

@Scope("prototype")
@Repository
public class PermissionRequestDAO extends HibernateGenericDAO<PermissionRequest> implements IPermissionReqRepository {

	@SuppressWarnings("unchecked")
	@Override
	public List<PermissionRequest> getPermissionRequestsByOwner(long ownerId) {
		Criteria criteria = this.session().createCriteria(this.persistClass);
		Criteria coCriteria = criteria.createCriteria("owner");
		coCriteria.add(Restrictions.eq("id", ownerId));
		return criteria.list();
	}

	@Override
	public void deletePermissionRequestsByCoId(long coId) {
		String del_hql = "DELETE FROM " + this.persistClass.getSimpleName()
				+ " AS pmreq WHERE pmreq.collection.id = :id";
		Query query = this.session().createQuery(del_hql);
		query.setLong("id", coId);
		query.executeUpdate();
	}

	@Override
	public void deletePermissionRequestById(long pmReqId) {
		String del_hql = "DELETE FROM " + this.persistClass.getSimpleName() + " AS pmReq WHERE pmReq.id = :id";
		Query query = this.session().createQuery(del_hql);
		query.setLong("id", pmReqId);
		query.executeUpdate();
	}

	@Override
	public PermissionRequest getCoPermissionRequestByReqUser(long coid, long reqUserId) {
		Criteria criteria = this.session().createCriteria(this.persistClass);
		Criteria coCriteria = criteria.createCriteria("collection");
		coCriteria.add(Restrictions.eq("id", coid));
		Criteria reqUCriteria = criteria.createCriteria("requestUser");
		reqUCriteria.add(Restrictions.eq("id", reqUserId));
		return (PermissionRequest) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pagination<PermissionRequest> getPermRequestsByPages(long ownerId, int startPageNo, int recordsPerPage,
			OrderBy[] orderBys) {
		Criteria criteria = this.session().createCriteria(this.persistClass);
		Criteria coCriteria = criteria.createCriteria("owner");
		coCriteria.add(Restrictions.eq("id", ownerId));
		criteria.setProjection(Projections.rowCount());

		int total = ((Integer) criteria.uniqueResult()).intValue();
		Pagination<PermissionRequest> preqpage = new Pagination<PermissionRequest>(startPageNo, recordsPerPage, total);
		Criteria queryCriteria = this.session().createCriteria(this.persistClass);
		Criteria qownerCrit = queryCriteria.createCriteria("owner");
		qownerCrit.add(Restrictions.eq("id", ownerId));

		if (orderBys != null && orderBys.length > 0) {
			for (int i = 0; i < orderBys.length; i++) {
				Order order = orderBys[i].getOrder();
				if (order != null) {
					queryCriteria.addOrder(order);
				}
			}
		} else {
			queryCriteria.addOrder(Order.desc("requestTime"));
		}
		queryCriteria.setFirstResult(preqpage.getFirstResult());
		// set the max results (size-per-page)
		queryCriteria.setMaxResults(preqpage.getSizePerPage());
		List<PermissionRequest> evlist = queryCriteria.list();
		preqpage.setPageResults(evlist);
		return preqpage;
	}

}
