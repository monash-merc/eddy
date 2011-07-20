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
package au.edu.monash.merc.capture.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.monash.merc.capture.dao.impl.CollectionDAO;
import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.dto.OrderBy;
import au.edu.monash.merc.capture.dto.page.Pagination;
import au.edu.monash.merc.capture.service.CollectionService;

@Scope("prototype")
@Service
@Transactional
public class CollectionServiceImpl implements CollectionService {

	@Autowired
	private CollectionDAO collectionDAO;

	public CollectionDAO getCollectionDAO() {
		return collectionDAO;
	}

	public void setCollectionDAO(CollectionDAO collectionDAO) {
		this.collectionDAO = collectionDAO;
	}

	@Override
	public void deleteCollection(Collection entity) {
		this.collectionDAO.remove(entity);
	}

	@Override
	public Collection getCollectionById(long id) {
		return this.collectionDAO.get(id);
	}

	@Override
	public Pagination<Collection> getCollectionsByUserId(long uid, int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
		return this.collectionDAO.getCollectionsByUserId(uid, startPageNo, recordsPerPage, orderBys);
	}

	@Override
	public Pagination<Collection> getAllPublicCollections(int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
		return this.collectionDAO.getAllPublicCollections(startPageNo, recordsPerPage, orderBys);
	}

	@Override
	public Pagination<Collection> getAllCollections(int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
		return this.collectionDAO.getAllCollections(startPageNo, recordsPerPage, orderBys);
	}

	@Override
	public List<Collection> getCollectionsByUserId(long uid) {
		return this.collectionDAO.getCollectionsByUserId(uid);
	}

	@Override
	public void saveCollection(Collection entity) {
		this.collectionDAO.add(entity);
	}

	@Override
	public void updateCollection(Collection entity) {
		this.collectionDAO.update(entity);
	}

	@Override
	public Collection getCollection(long cid, long uid) {
		return this.collectionDAO.getCollection(cid, uid);
	}

	@Override
	public boolean checkCollectionNameExisted(String colName) {
		return this.collectionDAO.checkCollectionNameExisted(colName);
	}

	@Override
	public List<Collection> getPublishedCollections() {
		return this.collectionDAO.getPublishedCollections();
	}

	@Override
	public Collection getPublishedCoByIdentifier(String identifier) {
		return this.collectionDAO.getPublishedCoByIdentifier(identifier);
	}

}
