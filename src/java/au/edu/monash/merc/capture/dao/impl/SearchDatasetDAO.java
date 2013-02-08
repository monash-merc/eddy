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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import au.edu.monash.merc.capture.dao.HibernateGenericDAO;
import au.edu.monash.merc.capture.domain.Dataset;
import au.edu.monash.merc.capture.dto.OrderBy;
import au.edu.monash.merc.capture.dto.SearchBean;
import au.edu.monash.merc.capture.dto.page.Pagination;
import au.edu.monash.merc.capture.repository.ISearchDatasetRepository;

@Scope("prototype")
@Repository
public class SearchDatasetDAO extends HibernateGenericDAO<Dataset> implements ISearchDatasetRepository {

	@SuppressWarnings("unchecked")
	@Override
	public Pagination<Dataset> search(SearchBean searchBean, int startPageNo, int recordsPerPage, OrderBy[] orderBys) {

		Criteria criteria = this.session().createCriteria(this.persistClass);

		// set any query associated to collection
		setCollectionCriterion(criteria, searchBean);

		// set the dataset search criterion restrictions
		setDatasetCriterion(criteria, searchBean);

		// set the variable metadata search criterion restrictions
		// setVariableMetaCriterion(criteria, searchBean);

		criteria.setProjection(Projections.rowCount());

		int total = ((Long) criteria.uniqueResult()).intValue();

		Pagination<Dataset> dsPage = new Pagination<Dataset>(startPageNo, recordsPerPage, total);

		Criteria findCriteria = this.session().createCriteria(this.persistClass);
		// set any query associated to collection
		setCollectionCriterion(findCriteria, searchBean);
		// set the dataset search criterion restrictions
		setDatasetCriterion(findCriteria, searchBean);
		// set the variable metadata search criterion restrictions
		// setVariableMetaCriterion(findCriteria, searchBean);

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
		findCriteria.setFirstResult(dsPage.getFirstResult());
		// set the max results (size-per-page)
		findCriteria.setMaxResults(dsPage.getSizePerPage());
		List<Dataset> dsList = findCriteria.list();
		dsPage.setPageResults(dsList);
 
		return dsPage;
	}

	private void setCollectionCriterion(Criteria criteria, SearchBean searchBean) {

		String coName = searchBean.getCollectionName();
		Date fromDate = searchBean.getStartDate();
		Date endDate = searchBean.getEndDate();

		Criteria coCriteria = null;
		if (StringUtils.isNotBlank(coName)) {
			coCriteria = criteria.createCriteria("collection");
			// set the search restriction for data collection
			coCriteria.add(Restrictions.like("name", (coName.trim() + "%"), MatchMode.ANYWHERE).ignoreCase());
		}
		if (fromDate != null) {
			if (coCriteria == null) {
				coCriteria = criteria.createCriteria("collection");
			}
			coCriteria.add(Restrictions.ge("createdTime", fromDate));
		}

		if (endDate != null) {
			if (coCriteria == null) {
				coCriteria = criteria.createCriteria("collection");
			}
			coCriteria.add(Restrictions.le("createdTime", endDate));
		}

		String researcherName = searchBean.getResearcherName();

		if (StringUtils.isNotBlank(researcherName)) {
			if (coCriteria == null) {
				coCriteria = criteria.createCriteria("collection");
			}
			Criteria researcherCrit = coCriteria.createCriteria("owner");
			researcherCrit.add(Restrictions.like("displayName", (researcherName.trim() + "%"), MatchMode.ANYWHERE).ignoreCase());
		}
	}

	private void setDatasetCriterion(Criteria criteria, SearchBean searchBean) {
		String siteName = searchBean.getSiteName();
		String dsName = searchBean.getDatasetName();
		String dsLevel = searchBean.getDatasetLevel();

		Criterion snCr = null;
		if (StringUtils.isNotBlank(siteName)) {
			snCr = Restrictions.like("siteName", (siteName.trim() + "%"), MatchMode.ANYWHERE).ignoreCase();
			criteria.add(snCr);
		}
		Criterion dsNameCr = null;
		if (StringUtils.isNotBlank(dsName)) {
			dsNameCr = Restrictions.like("name", (dsName.trim() + "%"), MatchMode.ANYWHERE).ignoreCase();
			criteria.add(dsNameCr);
		}
		Criterion dsLevelCr = null;
		if (StringUtils.isNotBlank(dsLevel)) {
			dsLevelCr = Restrictions.eq("netCDFLevel", dsLevel);
			criteria.add(dsLevelCr);
		}
	}

	// private void setVariableMetaCriterion(Criteria criteria, SearchBean searchBean){
	//
	// List<VariableBean> listVars = searchBean.getVarBeans();
	// if (listVars.size() > 0) {
	// Criteria varCriteria = criteria.createCriteria("metaVariables");
	// Criteria attCriteria = varCriteria.createCriteria("metaAttributes");
	// for (VariableBean vb : listVars) {
	// varCriteria.add(Restrictions.like("name", (vb.getVarName() + "%"), MatchMode.ANYWHERE).ignoreCase());
	// List<AttributeBean> listAtts = vb.getAttBeans();
	// for (AttributeBean ab : listAtts) {
	// attCriteria.add(Restrictions.eq("name", ab.getAttributeName()));
	// if (ab.getComparison().equals("equals")) {
	// attCriteria.add(Restrictions.eq("value", ab.getValue()));
	// } else {
	// attCriteria.add(Restrictions.like("value", (ab.getValue() + "%"), MatchMode.ANYWHERE)
	// .ignoreCase());
	// }
	// }
	// }
	// }
	// }

}
