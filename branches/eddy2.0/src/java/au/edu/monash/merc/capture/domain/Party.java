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
package au.edu.monash.merc.capture.domain;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "party")
public class Party extends Domain {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "pk_generator")
	@TableGenerator(name = "pk_generator", pkColumnName = "pk_column_name", valueColumnName = "pk_column_value", pkColumnValue = "party_pk")
	@Column(name = "id", nullable = false)
	private long id;

	@Basic
	@Column(name = "party_key")
	private String partyKey;

	@Basic
	@Column(name = "group_name")
	private String groupName;

	@Basic
	@Column(name = "originate_src_type")
	private String originateSourceType;

	@Basic
	@Column(name = "originate_src_value")
	private String originateSourceValue;

	@Basic
	@Column(name = "identifier_type")
	private String identifierType;

	@Basic
	@Column(name = "identifier_value")
	private String identifierValue;

	@Basic
	@Column(name = "person_title")
	private String personTitle;

	@Basic
	@Column(name = "given_name")
	private String personGivenName;

	@Basic
	@Column(name = "family_name")
	private String personFamilyName;

	@Basic
	@Column(name = "url")
	private String url;

	@Basic
	@Column(name = "email")
	private String email;

	@Basic
	@Column(name = "address")
	private String address;

	@Basic
	@Column(name = "from_rm")
	private boolean fromRm;

	@ManyToMany(mappedBy = "parties")
	private List<Collection> collections;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPartyKey() {
		return partyKey;
	}

	public void setPartyKey(String partyKey) {
		this.partyKey = partyKey;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getOriginateSourceType() {
		return originateSourceType;
	}

	public void setOriginateSourceType(String originateSourceType) {
		this.originateSourceType = originateSourceType;
	}

	public String getOriginateSourceValue() {
		return originateSourceValue;
	}

	public void setOriginateSourceValue(String originateSourceValue) {
		this.originateSourceValue = originateSourceValue;
	}

	public String getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}

	public String getIdentifierValue() {
		return identifierValue;
	}

	public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}

	public String getPersonTitle() {
		return personTitle;
	}

	public void setPersonTitle(String personTitle) {
		this.personTitle = personTitle;
	}

	public String getPersonGivenName() {
		return personGivenName;
	}

	public void setPersonGivenName(String personGivenName) {
		this.personGivenName = personGivenName;
	}

	public String getPersonFamilyName() {
		return personFamilyName;
	}

	public void setPersonFamilyName(String personFamilyName) {
		this.personFamilyName = personFamilyName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isFromRm() {
		return fromRm;
	}

	public void setFromRm(boolean fromRm) {
		this.fromRm = fromRm;
	}

	public List<Collection> getCollections() {
		return collections;
	}

	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}
}
