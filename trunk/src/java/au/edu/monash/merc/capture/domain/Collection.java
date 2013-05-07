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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "collection")
public class Collection extends Domain {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "pk_generator")
    @TableGenerator(name = "pk_generator", pkColumnName = "pk_column_name", valueColumnName = "pk_column_value", pkColumnValue = "collection_pk")
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Column(name = "site_name", length = 100)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time")
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_time")
    private Date modifiedTime;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(referencedColumnName = "id", nullable = false, name = "owner_id")
    private User owner;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(referencedColumnName = "id", nullable = false, name = "modified_by_user")
    private User modifiedByUser;

    @Basic
    @Column(name = "directory_name")
    private String dirPathName;

    @Basic
    @Column(name = "brief_desc", length = 255)
    private String briefDesc;

    @Basic
    @Column(name = "description", length = 4000)
    private String description;

    @Basic
    @Column(name = "persist_identifier")
    private String persistIdentifier;

    @Basic
    @Column(name = "unique_key")
    private String uniqueKey;

    @Basic
    @Column(name = "published")
    private boolean published;

    @Basic
    @Column(name = "funded")
    private boolean funded;

    @Basic
    @Column(name = "spatial_type", length = 100)
    private String spatialType;

    @Basic
    @Column(name = "spatial_coverage", length = 255)
    private String spatialCoverage;

    @ManyToOne(targetEntity = Location.class)
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = true)
    private Location location;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_from")
    private Date dateFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_to")
    private Date dateTo;

    @OneToMany(mappedBy = "collection", fetch = FetchType.LAZY, targetEntity = Permission.class)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
    private List<Permission> permissions = new ArrayList<Permission>();

    @OneToMany(mappedBy = "collection", targetEntity = Dataset.class, fetch = FetchType.LAZY)
    @Cascade({CascadeType.DELETE})
    private List<Dataset> datasets = new ArrayList<Dataset>();

    @ManyToMany(targetEntity = Activity.class)
    @JoinTable(name = "collection_activity", joinColumns = {@JoinColumn(name = "collection_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "activity_id", referencedColumnName = "id")}, uniqueConstraints = {@UniqueConstraint(columnNames = {
            "collection_id", "activity_id"})})
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Activity> activities = new ArrayList<Activity>();

    @ManyToMany(targetEntity = Party.class)
    @JoinTable(name = "collection_party", joinColumns = {@JoinColumn(name = "collection_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "party_id", referencedColumnName = "id")}, uniqueConstraints = {@UniqueConstraint(columnNames = {
            "collection_id", "party_id"})})
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Party> parties = new ArrayList<Party>();

    @OneToOne(mappedBy = "collection", targetEntity = Licence.class, fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    private Licence licence;

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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getModifiedByUser() {
        return modifiedByUser;
    }

    public void setModifiedByUser(User modifiedByUser) {
        this.modifiedByUser = modifiedByUser;
    }

    public String getDirPathName() {
        return dirPathName;
    }

    public void setDirPathName(String dirPathName) {
        this.dirPathName = dirPathName;
    }

    public String getBriefDesc() {
        return briefDesc;
    }

    public void setBriefDesc(String briefDesc) {
        this.briefDesc = briefDesc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPersistIdentifier() {
        return persistIdentifier;
    }

    public void setPersistIdentifier(String persistIdentifier) {
        this.persistIdentifier = persistIdentifier;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isFunded() {
        return funded;
    }

    public void setFunded(boolean funded) {
        this.funded = funded;
    }

    public String getSpatialType() {
        return spatialType;
    }

    public void setSpatialType(String spatialType) {
        this.spatialType = spatialType;
    }

    public String getSpatialCoverage() {
        return spatialCoverage;
    }

    public void setSpatialCoverage(String spatialCoverage) {
        this.spatialCoverage = spatialCoverage;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(List<Party> parties) {
        this.parties = parties;
    }

    public Licence getLicence() {
        return licence;
    }

    public void setLicence(Licence licence) {
        this.licence = licence;
    }
}
