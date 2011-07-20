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
package au.edu.monash.merc.capture.dto;

import java.io.Serializable;

public class CoPermissionBean implements Serializable {

	private long id;

	private boolean privateCollection;

	private boolean viewAllowed;

	private boolean editAllowed;

	private boolean importAllowed;

	private boolean exportAllowed;

	private boolean deleteAllowed;

	private boolean changePermAllowed;

	private boolean anonyViewAllowed;

	private boolean anonyEditAllowed;

	private boolean anonyImportAllowed;

	private boolean anonyExportAllowed;

	private boolean anonyDeleteAllowed;

	private boolean anonyChangePermAllowed;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isPrivateCollection() {
		return privateCollection;
	}

	public void setPrivateCollection(boolean privateCollection) {
		this.privateCollection = privateCollection;
	}

	public boolean isViewAllowed() {
		return viewAllowed;
	}

	public void setViewAllowed(boolean viewAllowed) {
		this.viewAllowed = viewAllowed;
	}

	public boolean isEditAllowed() {
		return editAllowed;
	}

	public void setEditAllowed(boolean editAllowed) {
		this.editAllowed = editAllowed;
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

	public boolean isAnonyViewAllowed() {
		return anonyViewAllowed;
	}

	public void setAnonyViewAllowed(boolean anonyViewAllowed) {
		this.anonyViewAllowed = anonyViewAllowed;
	}

	public boolean isAnonyEditAllowed() {
		return anonyEditAllowed;
	}

	public void setAnonyEditAllowed(boolean anonyEditAllowed) {
		this.anonyEditAllowed = anonyEditAllowed;
	}

	public boolean isAnonyImportAllowed() {
		return anonyImportAllowed;
	}

	public void setAnonyImportAllowed(boolean anonyImportAllowed) {
		this.anonyImportAllowed = anonyImportAllowed;
	}

	public boolean isAnonyExportAllowed() {
		return anonyExportAllowed;
	}

	public void setAnonyExportAllowed(boolean anonyExportAllowed) {
		this.anonyExportAllowed = anonyExportAllowed;
	}

	public boolean isAnonyDeleteAllowed() {
		return anonyDeleteAllowed;
	}

	public void setAnonyDeleteAllowed(boolean anonyDeleteAllowed) {
		this.anonyDeleteAllowed = anonyDeleteAllowed;
	}

	public boolean isAnonyChangePermAllowed() {
		return anonyChangePermAllowed;
	}

	public void setAnonyChangePermAllowed(boolean anonyChangePermAllowed) {
		this.anonyChangePermAllowed = anonyChangePermAllowed;
	}
}
