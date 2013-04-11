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

package au.edu.monash.merc.capture.dto;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 26/03/13 4:10 PM
 */
public class InheritPermissionBean {

    private int viewAllowed;

    private int updateAllowed;

    private int importAllowed;

    private int exportAllowed;

    private int deleteAllowed;

    private int mdRegisterAllowed;

    private int racAllowed;

    private int acAllowed;

    private long collectionId;

    private long permUserId;

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

    public long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(long collectionId) {
        this.collectionId = collectionId;
    }

    public long getPermUserId() {
        return permUserId;
    }

    public void setPermUserId(long permUserId) {
        this.permUserId = permUserId;
    }
}
