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

import javax.persistence.*;

@Entity
@Table(name = "user_permission")
public class Permission extends Domain {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "pk_generator")
    @TableGenerator(name = "pk_generator", pkColumnName = "pk_column_name", valueColumnName = "pk_column_value", pkColumnValue = "user_perm_pk")
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Column(name = "name", length = 200)
    private String name;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "view_allowed")
    private boolean viewAllowed;

    @Basic
    @Column(name = "update_allowed")
    private boolean updateAllowed;

    @Basic
    @Column(name = "import_allowed")
    private boolean importAllowed;

    @Basic
    @Column(name = "export_allowed")
    private boolean exportAllowed;

    @Basic
    @Column(name = "delete_allowed")
    private boolean deleteAllowed;

    @Basic
    @Column(name = "change_perm_allowed")
    private boolean changePermAllowed;

    @Basic
    @Column(name = "mdregister_allowed")
    private boolean mdregisterAllowed;

    @Basic
    @Column(name = "perm_type", nullable = false)
    private String permType;

    @ManyToOne(targetEntity = Collection.class)
    @JoinColumn(referencedColumnName = "id", nullable = false, name = "collection_id")
    private Collection collection;

    @ManyToOne(targetEntity = User.class, cascade = CascadeType.PERSIST)
    @JoinColumn(referencedColumnName = "id", nullable = false, name = "perm_user_id")
    private User permissionForUser;

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

    public boolean isViewAllowed() {
        return viewAllowed;
    }

    public void setViewAllowed(boolean viewAllowed) {
        this.viewAllowed = viewAllowed;
    }

    public boolean isUpdateAllowed() {
        return updateAllowed;
    }

    public void setUpdateAllowed(boolean updateAllowed) {
        this.updateAllowed = updateAllowed;
    }

    public boolean isImportAllowed() {
        return importAllowed;
    }

    public void setImportAllowed(boolean importAllowed) {
        this.importAllowed = importAllowed;
    }

    public boolean isExportAllowed() {
        return exportAllowed;
    }

    public void setExportAllowed(boolean exportAllowed) {
        this.exportAllowed = exportAllowed;
    }

    public boolean isDeleteAllowed() {
        return deleteAllowed;
    }

    public void setDeleteAllowed(boolean deleteAllowed) {
        this.deleteAllowed = deleteAllowed;
    }

    public boolean isChangePermAllowed() {
        return changePermAllowed;
    }

    public void setChangePermAllowed(boolean changePermAllowed) {
        this.changePermAllowed = changePermAllowed;
    }

    public boolean isMdregisterAllowed() {
        return mdregisterAllowed;
    }

    public void setMdregisterAllowed(boolean mdregisterAllowed) {
        this.mdregisterAllowed = mdregisterAllowed;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public User getPermissionForUser() {
        return permissionForUser;
    }

    public void setPermissionForUser(User permissionForUser) {
        this.permissionForUser = permissionForUser;
    }

    public String getPermType() {
        return permType;
    }

    public void setPermType(String permType) {
        this.permType = permType;
    }

    public void setAllPermissions() {
        viewAllowed = true;
        updateAllowed = true;
        importAllowed = true;
        exportAllowed = true;
        deleteAllowed = true;
        changePermAllowed = true;
    }

    @Transient
    public boolean isNonePerm() {
        if (!this.viewAllowed && !this.updateAllowed && !this.importAllowed && !this.exportAllowed && !this.deleteAllowed && !this.changePermAllowed
                && !this.mdregisterAllowed) {
            return true;
        } else {
            return false;
        }
    }
}
