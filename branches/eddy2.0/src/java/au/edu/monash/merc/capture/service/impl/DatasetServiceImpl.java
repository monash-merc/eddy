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
package au.edu.monash.merc.capture.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.edu.monash.merc.capture.dao.impl.DatasetDAO;
import au.edu.monash.merc.capture.dao.impl.GlobalMetadataDAO;
import au.edu.monash.merc.capture.dao.impl.MetaAttributeDAO;
import au.edu.monash.merc.capture.dao.impl.MetaVariableDAO;
import au.edu.monash.merc.capture.domain.Dataset;
import au.edu.monash.merc.capture.domain.GlobalMetadata;
import au.edu.monash.merc.capture.domain.MetaVariable;
import au.edu.monash.merc.capture.dto.OrderBy;
import au.edu.monash.merc.capture.dto.page.Pagination;
import au.edu.monash.merc.capture.service.DatasetService;

@Scope("prototype")
@Service
@Transactional
public class DatasetServiceImpl implements DatasetService {

	@Autowired
	private DatasetDAO datasetDAO;

	@Autowired
	private GlobalMetadataDAO globalMetadataDAO;

	@Autowired
	private MetaVariableDAO metaVariableDAO;

	@Autowired
	private MetaAttributeDAO metaAttributeDAO;

	public DatasetDAO getDatasetDAO() {
		return datasetDAO;
	}

	public void setDatasetDAO(DatasetDAO datasetDAO) {
		this.datasetDAO = datasetDAO;
	}

	public GlobalMetadataDAO getGlobalMetadataDAO() {
		return globalMetadataDAO;
	}

	public void setGlobalMetadataDAO(GlobalMetadataDAO globalMetadataDAO) {
		this.globalMetadataDAO = globalMetadataDAO;
	}

	public MetaVariableDAO getMetaVariableDAO() {
		return metaVariableDAO;
	}

	public void setMetaVariableDAO(MetaVariableDAO metaVariableDAO) {
		this.metaVariableDAO = metaVariableDAO;
	}

	public MetaAttributeDAO getMetaAttributeDAO() {
		return metaAttributeDAO;
	}

	public void setMetaAttributeDAO(MetaAttributeDAO metaAttributeDAO) {
		this.metaAttributeDAO = metaAttributeDAO;
	}

	@Override
	public void deleteDataset(Dataset entity) {
		this.datasetDAO.remove(entity);
	}

	@Override
	public void deleteDatasetById(long id) {
		this.datasetDAO.deleteDatasetById(id);
	}

	@Override
	public void deleteDatasetsByCollectionId(long cid) {
		this.datasetDAO.deleteDatasetByCollectionId(cid);
	}

	@Override
	public boolean checkDatasetNameExisted(String dsName, long cid) {
		return this.datasetDAO.checkDatasetNameExisted(dsName, cid);
	}

	@Override
	public Pagination<Dataset> getDatasetByCollectionId(long cid, int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
		return this.datasetDAO.getDatasetByCollectionId(cid, startPageNo, recordsPerPage, orderBys);
	}

	@Override
	public List<Dataset> getDatasetByCollectionId(long cid) {
		return this.datasetDAO.getDatasetByCollectionId(cid);
	}

	@Override
	public List<Dataset> getDatasetByCollectionIdUsrId(long cid, long uid) {
		return this.datasetDAO.getDatasetByCollectionIdUsrId(cid, uid);
	}

	@Override
	public Dataset getDatasetByHandlId(String handleId) {
		return this.datasetDAO.getDatasetByHandlId(handleId);
	}

	@Override
	public Dataset getDatasetById(long id) {
		return this.datasetDAO.get(id);
	}

	@Override
	public void saveDataset(Dataset entity) {
		this.datasetDAO.add(entity);
	}

	@Override
	public void updateDataset(Dataset entity) {
		this.datasetDAO.update(entity);
	}

	public Dataset getAllDatasetData(long dsId) {
		Dataset ds = this.getDatasetById(dsId);
		if (ds != null) {
			List<GlobalMetadata> gMetadata = this.globalMetadataDAO.getAllGlobalMetadataByDatasetId(dsId);
			List<MetaVariable> metaVariables = this.metaVariableDAO.getAllMetaVariableByDatasetId(dsId);
			// for (MetaVariable mvar : metaVariables) {
			// List<MetaAttribute> metaAttrs = this.metaAttributeDAO.getAllAttributeByVarId(mvar.getId());
			// mvar.setMetaAttributes(metaAttrs);
			// }
			ds.setGlobalMetadata(gMetadata);
			ds.setMetaVariables(metaVariables);
		}
		return ds;

	}

}
