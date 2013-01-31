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

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import au.edu.monash.merc.capture.dao.HibernateGenericDAO;
import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.dto.OrderBy;
import au.edu.monash.merc.capture.dto.SearchBean;
import au.edu.monash.merc.capture.dto.page.Pagination;
import au.edu.monash.merc.capture.repository.ISearchCoRepository;

@Scope("prototype")
@Repository
public class SearchCoDAO extends HibernateGenericDAO<Collection> implements ISearchCoRepository {

	@SuppressWarnings("unchecked")
	@Override
	public Pagination<Collection> search(SearchBean searchBean, int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
		Criteria criteria = this.session().createCriteria(this.persistClass);
		setCollectionCriterion(criteria, searchBean);
		criteria.setProjection(Projections.rowCount());
		int total = ((Integer) criteria.uniqueResult()).intValue();
		Pagination<Collection> coPages = new Pagination<Collection>(startPageNo, recordsPerPage, total);

		Criteria findCriteria = this.session().createCriteria(this.persistClass);
		// set any query associated to collection
		setCollectionCriterion(findCriteria, searchBean);
		// add orders
		if (orderBys != null && orderBys.length > 0) {
			for (int i = 0; i < orderBys.length; i++) {
				Order order = orderBys[i].getOrder();
				if (order != null) {
					findCriteria.addOrder(order);
				}
			}
		} else {
			findCriteria.addOrder(Order.asc("name"));
		}
		// calculate the first result from the pagination and set this value into the start search index
		findCriteria.setFirstResult(coPages.getFirstResult());
		// set the max results (size-per-page)
		findCriteria.setMaxResults(coPages.getSizePerPage());
		List<Collection> coList = findCriteria.list();
		coPages.setPageResults(coList);
		return coPages;
	}

	private void setCollectionCriterion(Criteria criteria, SearchBean searchBean) {

		String coName = searchBean.getCollectionName();
		Date fromDate = searchBean.getStartDate();
		Date endDate = searchBean.getEndDate();

		if (StringUtils.isNotBlank(coName)) {
			// set the search restriction for data collection
			criteria.add(Restrictions.like("name", (coName.trim() + "%"), MatchMode.ANYWHERE).ignoreCase());
		}

		if (fromDate != null) {
			criteria.add(Restrictions.ge("createdTime", fromDate));
		}

		if (endDate != null) {
			criteria.add(Restrictions.le("createdTime", endDate));
		}

		String researcherName = searchBean.getResearcherName();
		if (StringUtils.isNotBlank(researcherName)) {

			Criteria researcherCrit = criteria.createCriteria("owner");
			researcherCrit.add(Restrictions.like("displayName", (researcherName.trim() + "%"), MatchMode.ANYWHERE).ignoreCase());
		}
	}
}
