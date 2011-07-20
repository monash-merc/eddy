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
package au.edu.monash.merc.capture.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import au.edu.monash.merc.capture.repository.IRepository;

@Scope("prototpe")
@Repository
public class HibernateGenericDAO<T> implements IRepository<T> {

	protected Class<T> persistClass;

	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public HibernateGenericDAO(SessionFactory sessionFactory) {
		this.persistClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public HibernateGenericDAO() {
		this.persistClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session session() {
		return this.sessionFactory.getCurrentSession();
	}

	@Override
	public void add(T entity) {
		this.session().save(entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(long id) {
		return (T) this.session().get(this.persistClass, id);
	}

	@Override
	public void remove(T entity) {
		this.session().delete(entity);
	}

	@Override
	public void update(T entity) {
		this.session().update(entity);
	}

	@Override
	public int saveAll(List<T> entities) {
		int insertedCount = 0;
		for (int i = 0; i < entities.size(); i++) {
			add(entities.get(i));
			if (++insertedCount % 20 == 0) {
				flushAndClear();
			}
		}
		flushAndClear();
		return insertedCount;
	}

	protected void flushAndClear() {
		if (this.session().isDirty()) {
			this.session().flush();
			this.session().clear();
		}
	}
}
