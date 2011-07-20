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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import au.edu.monash.merc.capture.dao.HibernateGenericDAO;
import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.domain.PermType;
import au.edu.monash.merc.capture.dto.OrderBy;
import au.edu.monash.merc.capture.dto.page.Pagination;
import au.edu.monash.merc.capture.repository.ICollectionRepository;

@Scope("prototype")
@Repository
public class CollectionDAO extends HibernateGenericDAO<Collection> implements ICollectionRepository {

	@SuppressWarnings("unchecked")
	@Override
	public List<Collection> getCollectionsByUserId(long uid) {
		Criteria criteria = this.session().createCriteria(this.persistClass);
		Criteria userCrit = criteria.createCriteria("owner");
		userCrit.add(Restrictions.eq("id", uid));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pagination<Collection> getCollectionsByUserId(long uid, int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
		// count total
		Criteria criteria = this.session().createCriteria(this.persistClass);
		Criteria userCriteria = criteria.createCriteria("owner");
		userCriteria.add(Restrictions.eq("id", uid));
		criteria.setProjection(Projections.rowCount());

		// int total = ((Integer) criteria.list().get(0)).intValue();
		int total = ((Integer) criteria.uniqueResult()).intValue();

		Pagination<Collection> p = new Pagination<Collection>(startPageNo, recordsPerPage, total);

		// query collections by size-per-page
		Criteria queryCriteria = this.session().createCriteria(this.persistClass);
		Criteria qownerCrit = queryCriteria.createCriteria("owner");
		qownerCrit.add(Restrictions.eq("id", uid));
		// add orders
		if (orderBys != null && orderBys.length > 0) {
			for (int i = 0; i < orderBys.length; i++) {
				Order order = orderBys[i].getOrder();
				if (order != null) {
					queryCriteria.addOrder(order);
				}
			}
		} else {
			queryCriteria.addOrder(Order.desc("name"));
		}
		// calculate the first result from the pagination and set this value into the start search index
		queryCriteria.setFirstResult(p.getFirstResult());
		// set the max results (size-per-page)
		queryCriteria.setMaxResults(p.getSizePerPage());
		List<Collection> collist = queryCriteria.list();
		p.setPageResults(collist);
		return p;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pagination<Collection> getAllPublicCollections(int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
		// count total
		Criteria criteria = this.session().createCriteria(this.persistClass);
		Criteria permCriteria = criteria.createCriteria("permissions");
		permCriteria.add(Restrictions.eq("viewAllowed", true)).add(Restrictions.eq("permType", PermType.ANONYMOUS.code()));

		criteria.setProjection(Projections.rowCount());
		int total = ((Integer) criteria.uniqueResult()).intValue();

		Pagination<Collection> p = new Pagination<Collection>(startPageNo, recordsPerPage, total);

		// start to query the sharing collections
		Criteria findCriteria = this.session().createCriteria(this.persistClass);
		Criteria findPermCriteria = findCriteria.createCriteria("permissions");
		findPermCriteria.add(Restrictions.eq("viewAllowed", true)).add(Restrictions.eq("permType", PermType.ANONYMOUS.code()));

		// add orders
		if (orderBys != null && orderBys.length > 0) {
			for (int i = 0; i < orderBys.length; i++) {
				Order order = orderBys[i].getOrder();
				if (order != null) {
					findCriteria.addOrder(order);
				}
			}
		} else {
			findCriteria.addOrder(Order.desc("name"));
		}

		// calculate the first result from the pagination and set this value into the start search index
		findCriteria.setFirstResult(p.getFirstResult());
		// set the max results (size-per-page)
		findCriteria.setMaxResults(p.getSizePerPage());

		List<Collection> collist = findCriteria.list();

		p.setPageResults(collist);
		return p;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pagination<Collection> getAllCollections(int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
		// count total
		Criteria criteria = this.session().createCriteria(this.persistClass);
		criteria.setProjection(Projections.rowCount());
		int total = ((Integer) criteria.uniqueResult()).intValue();
		Pagination<Collection> p = new Pagination<Collection>(startPageNo, recordsPerPage, total);

		// query collections by size-per-page
		Criteria queryCriteria = this.session().createCriteria(this.persistClass);
		// add orders
		if (orderBys != null && orderBys.length > 0) {
			for (int i = 0; i < orderBys.length; i++) {
				// if sorted by user name required
				// if (orderBys[i].getFieldName().equalsIgnoreCase("user")) {
				// // sort by user name
				// queryCriteria.createAlias("owner", "user");
				// if (orderBys[i].getOrderTypeCode().equals(OrderType.ASC)) {
				//
				// queryCriteria.addOrder(Order.asc("user.displayName"));
				// } else {
				// queryCriteria.addOrder(Order.desc("user.displayName"));
				// }
				//
				// } else {
				// Order order = orderBys[i].getOrder();
				// if (order != null) {
				// queryCriteria.addOrder(order);
				// }
				// }
				Order order = orderBys[i].getOrder();
				if (order != null) {
					queryCriteria.addOrder(order);
				}
			}
		} else {
			queryCriteria.addOrder(Order.desc("name"));
		}

		// calculate the first result from the pagination and set this value into the start search index
		queryCriteria.setFirstResult(p.getFirstResult());
		// set the max results (size-per-page)
		queryCriteria.setMaxResults(p.getSizePerPage());
		queryCriteria.setComment("listAllCollections");
		List<Collection> collist = queryCriteria.list();
		p.setPageResults(collist);
		return p;
	}

	@Override
	public Collection getCollection(long cid, long uid) {
		Criteria criteria = this.session().createCriteria(this.persistClass);
		Criteria usrCrit = criteria.createCriteria("owner");
		criteria.add(Restrictions.eq("id", cid));
		usrCrit.add(Restrictions.eq("id", uid));
		criteria.setComment("getCollection by collection id and user id");
		return (Collection) criteria.uniqueResult();
	}

	public boolean checkCollectionNameExisted(String colName) {
		Criteria criteria = this.session().createCriteria(this.persistClass);
		int num = (Integer) criteria.setProjection(Projections.rowCount()).add(Restrictions.eq("name", colName)).uniqueResult();
		if (num == 1) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Collection> getPublishedCollections() {
		Criteria criteria = this.session().createCriteria(this.persistClass);
		criteria.add(Restrictions.eq("published", true));
		return criteria.list();
	}

	@Override
	public Collection getPublishedCoByIdentifier(String identifier) {
		Criteria criteria = this.session().createCriteria(this.persistClass);
		criteria.add(Restrictions.eq("published", true)).add(Restrictions.eq("persistIdentifier", identifier));
		return (Collection) criteria.uniqueResult();
	}

}
