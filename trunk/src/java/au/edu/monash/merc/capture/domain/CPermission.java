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

package au.edu.monash.merc.capture.domain;


import javax.persistence.*;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 26/03/13 1:29 PM
 */
@Entity
@Table(name = "collection_permission")
public class CPermission extends Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "pk_generator")
    @TableGenerator(name = "pk_generator", pkColumnName = "pk_column_name", valueColumnName = "pk_column_value", pkColumnValue = "collection_perm_pk")
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Column(name = "name", length = 200)
    private String name;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "view_allowed", length = 1)
    private int viewAllowed = 1;

    @Basic
    @Column(name = "update_allowed", length = 1)
    private int updateAllowed;

    @Basic
    @Column(name = "import_allowed", length = 1)
    private int importAllowed;

    @Basic
    @Column(name = "export_allowed", length = 1)
    private int exportAllowed;

    @Basic
    @Column(name = "delete_allowed", length = 1)
    private int deleteAllowed;

    @Basic
    @Column(name = "md_register_allowed", length = 1)
    private int mdRegisterAllowed;

    @Basic
    @Column(name = "ra_control_allowed", length = 1)
    private int racAllowed;

    @Basic
    @Column(name = "access_control_allowed", length = 1)
    private int acAllowed;

    @Basic
    @Column(name = "perm_type", nullable = false)
    private String permType;

    @ManyToOne(targetEntity = Collection.class)
    @JoinColumn(referencedColumnName = "id", nullable = false, name = "collection_id")
    private Collection collection;

    @ManyToOne(targetEntity = User.class, cascade = CascadeType.PERSIST)
    @JoinColumn(referencedColumnName = "id", nullable = false, name = "perm_for_user")
    private User permForUser;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getViewAllowed() {
        return viewAllowed;
    }

    public void setViewAllowed(int viewAllowed) {
        this.viewAllowed = viewAllowed;
    }

    public int getUpdateAllowed() {
        return updateAllowed;
    }

    public void setUpdateAllowed(int updateAllowed) {
        this.updateAllowed = updateAllowed;
    }

    public int getImportAllowed() {
        return importAllowed;
    }

    public void setImportAllowed(int importAllowed) {
        this.importAllowed = importAllowed;
    }

    public int getExportAllowed() {
        return exportAllowed;
    }

    public void setExportAllowed(int exportAllowed) {
        this.exportAllowed = exportAllowed;
    }

    public int getDeleteAllowed() {
        return deleteAllowed;
    }

    public void setDeleteAllowed(int deleteAllowed) {
        this.deleteAllowed = deleteAllowed;
    }

    public int getMdRegisterAllowed() {
        return mdRegisterAllowed;
    }

    public void setMdRegisterAllowed(int mdRegisterAllowed) {
        this.mdRegisterAllowed = mdRegisterAllowed;
    }

    public int getRacAllowed() {
        return racAllowed;
    }

    public void setRacAllowed(int racAllowed) {
        this.racAllowed = racAllowed;
    }

    public int getAcAllowed() {
        return acAllowed;
    }

    public void setAcAllowed(int acAllowed) {
        this.acAllowed = acAllowed;
    }

    public String getPermType() {
        return permType;
    }

    public void setPermType(String permType) {
        this.permType = permType;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public User getPermForUser() {
        return permForUser;
    }

    public void setPermForUser(User permForUser) {
        this.permForUser = permForUser;
    }
}
