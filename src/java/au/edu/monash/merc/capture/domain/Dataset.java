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
package au.edu.monash.merc.capture.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "dataset")
public class Dataset extends Domain {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "pk_generator")
	@TableGenerator(name = "pk_generator", pkColumnName = "pk_column_name", valueColumnName = "pk_column_value", pkColumnValue = "dataset_pk")
	@Column(name = "id", nullable = false)
	private long id;

	@Basic
	@Column(name = "name")
	private String name;

	@Basic
	@Column(name = "extracted")
	private boolean extracted;

	@Basic
	@Column(name = "title")
	private String title;

	@Basic
	@Column(name = "specification", length = 2000)
	private String specification;

	@Basic
	@Column(name = "site_name", length = 100)
	private String siteName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "import_datetime")
	private Date importDateTime;

	@Basic
	@Column(name = "run_datetime_tag", length = 50)
	private String runDateTimeTag;

	@Basic
	@Column(name = "xlmod_datetime_tag", length = 50)
	private String xlModDateTimeTag;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "run_datetime")
	private Date runDateTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "xlmod_datetime")
	private Date xlModDateTime;

	@Basic
	@Column(name = "store_location")
	private String storeLocation;

	@Basic
	@Column(name = "netcdf_level")
	private String netCDFLevel;

	@ManyToOne(targetEntity = Collection.class)
	@JoinColumn(name = "collection_id", referencedColumnName = "id", nullable = false)
	private Collection collection;

	@OneToMany(mappedBy = "dataset", fetch = FetchType.LAZY, targetEntity = GlobalMetadata.class, cascade = CascadeType.ALL)
	private List<GlobalMetadata> globalMetadata;

	@OneToMany(mappedBy = "dataset", fetch = FetchType.LAZY, targetEntity = MetaVariable.class, cascade = CascadeType.ALL)
	private List<MetaVariable> metaVariables;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isExtracted() {
		return extracted;
	}

	public void setExtracted(boolean extracted) {
		this.extracted = extracted;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public Date getImportDateTime() {
		return importDateTime;
	}

	public void setImportDateTime(Date importDateTime) {
		this.importDateTime = importDateTime;
	}

	public String getRunDateTimeTag() {
		return runDateTimeTag;
	}

	public void setRunDateTimeTag(String runDateTimeTag) {
		this.runDateTimeTag = runDateTimeTag;
	}

	public String getXlModDateTimeTag() {
		return xlModDateTimeTag;
	}

	public void setXlModDateTimeTag(String xlModDateTimeTag) {
		this.xlModDateTimeTag = xlModDateTimeTag;
	}

	public Date getRunDateTime() {
		return runDateTime;
	}

	public void setRunDateTime(Date runDateTime) {
		this.runDateTime = runDateTime;
	}

	public Date getXlModDateTime() {
		return xlModDateTime;
	}

	public void setXlModDateTime(Date xlModDateTime) {
		this.xlModDateTime = xlModDateTime;
	}

	public String getStoreLocation() {
		return storeLocation;
	}

	public void setStoreLocation(String storeLocation) {
		this.storeLocation = storeLocation;
	}

	public String getNetCDFLevel() {
		return netCDFLevel;
	}

	public void setNetCDFLevel(String netCDFLevel) {
		this.netCDFLevel = netCDFLevel;
	}

	public List<GlobalMetadata> getGlobalMetadata() {
		return globalMetadata;
	}

	public void setGlobalMetadata(List<GlobalMetadata> globalMetadata) {
		this.globalMetadata = globalMetadata;
	}

	public List<MetaVariable> getMetaVariables() {
		return metaVariables;
	}

	public void setMetaVariables(List<MetaVariable> metaVariables) {
		this.metaVariables = metaVariables;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}
}
