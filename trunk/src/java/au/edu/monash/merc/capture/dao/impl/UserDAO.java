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
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.domain.UserType;
import au.edu.monash.merc.capture.dto.OrderBy;
import au.edu.monash.merc.capture.dto.page.Pagination;
import au.edu.monash.merc.capture.repository.IUserRepository;

@Scope("prototype")
@Repository
public class UserDAO extends HibernateGenericDAO<User> implements IUserRepository {

	@Override
	public User getByUserEmail(String email) {
		return (User) this.session().createCriteria(this.persistClass).add(Restrictions.eq("email", email)).setComment("UserDAO.getByUserEmail")
				.uniqueResult();
	}

	@Override
	public User getByUserUnigueId(String uniqueId) {
		return (User) this.session().createCriteria(this.persistClass).add(Restrictions.eq("uniqueId", uniqueId))
				.setComment("UserDAO.getByUserUnigueId").uniqueResult();
	}

	@Override
	public boolean checkUserUniqueIdExisted(String uniqueId) {
		int num = (Integer) this.session().createCriteria(this.persistClass).setProjection(Projections.rowCount())
				.add(Restrictions.eq("uniqueId", uniqueId).ignoreCase()).uniqueResult();
		if (num == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean checkUserDisplayNameExisted(String userDisplayName) {
		int num = (Integer) this.session().createCriteria(this.persistClass).setProjection(Projections.rowCount())
				.add(Restrictions.eq("displayName", userDisplayName).ignoreCase()).uniqueResult();
		if (num == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean checkEmailExisted(String email) {
		int num = (Integer) this.session().createCriteria(this.persistClass).setProjection(Projections.rowCount())
				.add(Restrictions.eq("email", email).ignoreCase()).uniqueResult();
		if (num == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public User checkUserLogin(String username, String password) {
		return (User) this.session().createCriteria(this.persistClass).add(Restrictions.eq("uniqueId", username))
				.add(Restrictions.eq("password", password)).setComment("UserDAO.validateLogin").uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pagination<User> getAllUsers(int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
		Criteria criteria = this.session().createCriteria(this.persistClass);
		criteria.add(Restrictions.and(Restrictions.ne("userType", UserType.ALLREGUSER.code()), Restrictions.ne("userType", UserType.ANONYMOUS.code())));
		criteria.setProjection(Projections.rowCount());
		int total = ((Integer) criteria.uniqueResult()).intValue();

		Pagination<User> p = new Pagination<User>(startPageNo, recordsPerPage, total);
		Criteria qcriteria = this.session().createCriteria(this.persistClass);
		qcriteria.add(Restrictions.and(Restrictions.ne("userType", UserType.ALLREGUSER.code()),
				Restrictions.ne("userType", UserType.ANONYMOUS.code())));
		if (orderBys != null && orderBys.length > 0) {
			for (int i = 0; i < orderBys.length; i++) {
				Order order = orderBys[i].getOrder();
				if (order != null) {
					qcriteria.addOrder(order);
				}
			}
		} else {
			qcriteria.addOrder(Order.desc("displayName"));
		}

		qcriteria.setFirstResult(p.getFirstResult());
		// set the max results (size-per-page)
		qcriteria.setMaxResults(p.getSizePerPage());
		List<User> collist = qcriteria.list();
		p.setPageResults(collist);
		return p;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pagination<User> getAllActiveUsers(int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
		Criteria criteria = this.session().createCriteria(this.persistClass);
		criteria.add(Restrictions.eq("isActivated", true)).add(
				Restrictions.and(Restrictions.ne("userType", UserType.ALLREGUSER.code()), Restrictions.ne("userType", UserType.ANONYMOUS.code())));
		criteria.setProjection(Projections.rowCount());
		int total = ((Integer) criteria.uniqueResult()).intValue();

		Pagination<User> p = new Pagination<User>(startPageNo, recordsPerPage, total);
		Criteria qcriteria = this.session().createCriteria(this.persistClass);
		qcriteria.add(Restrictions.eq("isActivated", true)).add(
				Restrictions.and(Restrictions.ne("userType", UserType.ALLREGUSER.code()), Restrictions.ne("userType", UserType.ANONYMOUS.code())));
		if (orderBys != null && orderBys.length > 0) {
			for (int i = 0; i < orderBys.length; i++) {
				Order order = orderBys[i].getOrder();
				if (order != null) {
					qcriteria.addOrder(order);
				}
			}
		} else {
			qcriteria.addOrder(Order.desc("displayName"));
		}

		qcriteria.setFirstResult(p.getFirstResult());
		// set the max results (size-per-page)
		qcriteria.setMaxResults(p.getSizePerPage());
		List<User> collist = qcriteria.list();
		p.setPageResults(collist);
		return p;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pagination<User> getAllInActiveUsers(int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
		Criteria criteria = this.session().createCriteria(this.persistClass);
		criteria.add(Restrictions.eq("isActivated", false));
		criteria.setProjection(Projections.rowCount());
		int total = ((Integer) criteria.uniqueResult()).intValue();

		Pagination<User> p = new Pagination<User>(startPageNo, recordsPerPage, total);
		Criteria qcriteria = this.session().createCriteria(this.persistClass);
		qcriteria.add(Restrictions.eq("isActivated", false));
		if (orderBys != null && orderBys.length > 0) {
			for (int i = 0; i < orderBys.length; i++) {
				Order order = orderBys[i].getOrder();
				if (order != null) {
					qcriteria.addOrder(order);
				}
			}
		} else {
			qcriteria.addOrder(Order.desc("displayName"));
		}

		qcriteria.setFirstResult(p.getFirstResult());
		// set the max results (size-per-page)
		qcriteria.setMaxResults(p.getSizePerPage());
		List<User> collist = qcriteria.list();
		p.setPageResults(collist);
		return p;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllActiveUsers() {
		Criteria criteria = this.session().createCriteria(this.persistClass);
		criteria.setComment("UserDAO.getAllActiveUsers");
		criteria.add(Restrictions.eq("isActivated", true)).add(
				Restrictions.and(Restrictions.ne("userType", UserType.ALLREGUSER.code()), Restrictions.ne("userType", UserType.ANONYMOUS.code())));
		return criteria.addOrder(Order.desc("displayName")).list();
	}

	// this method is only used to retrieve three types of virtual user - all registered user and anonymouse user.
	@Override
	public User getVirtualUser(int userType) {
		return (User) this.session().createCriteria(this.persistClass).setComment("UserDAO.getVirtualUser")
				.add(Restrictions.eq("userType", userType)).uniqueResult();
	}

}
