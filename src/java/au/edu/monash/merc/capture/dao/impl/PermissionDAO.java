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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import au.edu.monash.merc.capture.dao.HibernateGenericDAO;
import au.edu.monash.merc.capture.domain.PermType;
import au.edu.monash.merc.capture.domain.Permission;
import au.edu.monash.merc.capture.repository.IPermissionRepository;

@Scope("prototype")
@Repository
public class PermissionDAO extends HibernateGenericDAO<Permission> implements IPermissionRepository {
	@SuppressWarnings("unchecked")
	@Override
	public List<Permission> getUserCoPerms(long permForUsrId, long coId) {
		Criteria permCriteria = this.session().createCriteria(this.persistClass);
		// create a alias for criteria
		permCriteria.createAlias("permissionForUser", "permForUser");
		Criterion permtype1 = Restrictions.eq("permType", PermType.ALLREGUSER.code());
		Criterion permtype2 = Restrictions.eq("permType", PermType.ANONYMOUS.code());
		Criterion permuser = Restrictions.eq("permForUser.id", permForUsrId);

		// create a disjunction (or), then add the restrictions
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(permtype1);
		disjunction.add(permtype2);
		disjunction.add(permuser);
		permCriteria.add(disjunction);

		Criteria coCriteria = permCriteria.createCriteria("collection");
		coCriteria.add(Restrictions.eq("id", coId));
		return permCriteria.list();
	}

	@Override
	public Permission getAnonymousPerm(long cid) {
		Criteria permCriteria = this.session().createCriteria(this.persistClass);
		permCriteria.add(Restrictions.eq("permType", PermType.ANONYMOUS.code()));
		Criteria coCriteria = permCriteria.createCriteria("collection");
		coCriteria.add(Restrictions.eq("id", cid));
		return (Permission) permCriteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Permission> getCollectionDefaultPerms(long cid) {
		Criteria permCriteria = this.session().createCriteria(this.persistClass);
		permCriteria.add(Restrictions.disjunction().add(Restrictions.eq("permType", PermType.ALLREGUSER.code()))
				.add(Restrictions.eq("permType", PermType.ANONYMOUS.code())));
		Criteria coCriteria = permCriteria.createCriteria("collection");
		coCriteria.add(Restrictions.eq("id", cid));
		return permCriteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Permission> getCollectionPermissions(long cid) {
		Criteria permCriteria = this.session().createCriteria(this.persistClass);
		Criteria coCriteria = permCriteria.createCriteria("collection");
		coCriteria.add(Restrictions.eq("id", cid));
		return permCriteria.list();
	}

	@Override
	public void deletePermissionByPId(long pId) {
		String del_hql = "DELETE FROM " + this.persistClass.getSimpleName() + " AS perm WHERE perm.id = :id";
		Query query = this.session().createQuery(del_hql);
		query.setLong("id", pId);
		query.executeUpdate();
	}

	@Override
	public void deleteAllPermissionsByColId(long cId) {
		String del_hql = "DELETE FROM " + this.persistClass.getSimpleName() + " AS perm WHERE perm.collection.id = :id";
		Query query = this.session().createQuery(del_hql);
		query.setLong("id", cId);
		query.executeUpdate();
	}

}
