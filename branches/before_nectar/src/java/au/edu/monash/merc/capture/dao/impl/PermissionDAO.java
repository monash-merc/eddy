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

package au.edu.monash.merc.capture.dao.impl;

import au.edu.monash.merc.capture.dao.HibernateGenericDAO;
import au.edu.monash.merc.capture.domain.Permission;
import au.edu.monash.merc.capture.common.PermType;
import au.edu.monash.merc.capture.dto.InheritPermissionBean;
import au.edu.monash.merc.capture.repository.IPermissionRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
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
 *        Date: 26/03/13 1:55 PM
 */
@Scope("prototype")
@Repository
public class PermissionDAO extends HibernateGenericDAO<Permission> implements IPermissionRepository {

    @Override
    public Permission getUserCollectionPermission(long collectionId, long userId) {
        Criteria permCriteria = this.session().createCriteria(this.persistClass);
        Criteria coCriteria = permCriteria.createCriteria("collection");
        Criteria userCriteria = permCriteria.createCriteria("permForUser");
        coCriteria.add(Restrictions.eq("id", collectionId));
        userCriteria.add(Restrictions.eq("id", userId));
        return (Permission) permCriteria.uniqueResult();
    }

    @Override
    public Permission getAnonymousCollectionPermission(long collectionId) {
        Criteria permCriteria = this.session().createCriteria(this.persistClass);
        permCriteria.add(Restrictions.eq("permType", PermType.ANONYMOUS.code()));
        Criteria coCriteria = permCriteria.createCriteria("collection");
        coCriteria.add(Restrictions.eq("id", collectionId));
        return (Permission) permCriteria.uniqueResult();
    }

    @Override
    public Permission getAllRegUserCollectionPermission(long collectionId) {
        Criteria permCriteria = this.session().createCriteria(this.persistClass);
        permCriteria.add(Restrictions.eq("permType", PermType.ALLREGUSER.code()));
        Criteria coCriteria = permCriteria.createCriteria("collection");
        coCriteria.add(Restrictions.eq("id", collectionId));
        return (Permission) permCriteria.uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public InheritPermissionBean getUserInheritPermission(final long coId, final long permForUsrId) {
        Criteria permCriteria = this.session().createCriteria(this.persistClass);
        // create a alias for criteria
        // permCriteria.createAlias("permForUser", "permUser");
        //Criterion permuser = Restrictions.eq("permUser.id", permForUsrId);
        Criterion permtype1 = Restrictions.eq("permType", PermType.ALLREGUSER.code());
        Criterion permtype2 = Restrictions.eq("permType", PermType.ANONYMOUS.code());

        // create a disjunction (or), then add the restrictions
        Disjunction disjunction = Restrictions.disjunction();
        // disjunction.add(permuser);
        disjunction.add(permtype1);
        disjunction.add(permtype2);

        permCriteria.add(disjunction);

        Criteria coCriteria = permCriteria.createCriteria("collection");
        coCriteria.add(Restrictions.eq("id", coId));
        permCriteria.setProjection(Projections.projectionList()
                .add(Projections.max("viewAllowed").as("viewAllowed"))
                .add(Projections.max("updateAllowed").as("updateAllowed"))
                .add(Projections.max("importAllowed").as("importAllowed"))
                .add(Projections.max("exportAllowed").as("exportAllowed"))
                .add(Projections.max("deleteAllowed").as("deleteAllowed"))
                .add(Projections.max("mdRegisterAllowed").as("mdRegisterAllowed"))
                .add(Projections.max("racAllowed").as("racAllowed"))
                .add(Projections.max("acAllowed").as("acAllowed")));

        permCriteria.setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] results, String[] strings) {
                InheritPermissionBean inheritPermissionBean = new InheritPermissionBean();
                inheritPermissionBean.setViewAllowed(((Integer) results[0]).intValue());
                inheritPermissionBean.setUpdateAllowed(((Integer) results[1]).intValue());
                inheritPermissionBean.setImportAllowed(((Integer) results[2]).intValue());
                inheritPermissionBean.setExportAllowed(((Integer) results[3]).intValue());
                inheritPermissionBean.setDeleteAllowed(((Integer) results[4]).intValue());
                inheritPermissionBean.setMdRegisterAllowed(((Integer) results[5]).intValue());
                inheritPermissionBean.setRacAllowed(((Integer) results[6]).intValue());
                inheritPermissionBean.setAcAllowed(((Integer) results[7]).intValue());
                inheritPermissionBean.setCollectionId(coId);
                inheritPermissionBean.setPermUserId(permForUsrId);
                return inheritPermissionBean;
            }

            @Override
            public List transformList(List list) {
                return list;
            }
        });
        return (InheritPermissionBean) permCriteria.uniqueResult();
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
    public void deletePermissionByPermId(long permissionId) {
        String del_hql = "DELETE FROM " + this.persistClass.getSimpleName() + " AS perm WHERE perm.id = :id";
        Query query = this.session().createQuery(del_hql);
        query.setLong("id", permissionId);
        query.executeUpdate();
    }

    @Override
    public void deletePermissionsByCollectionId(long collectionId) {
        String del_hql = "DELETE FROM " + this.persistClass.getSimpleName() + " AS perm WHERE perm.collection.id = :id";
        Query query = this.session().createQuery(del_hql);
        query.setLong("id", collectionId);
        query.executeUpdate();
    }
}
