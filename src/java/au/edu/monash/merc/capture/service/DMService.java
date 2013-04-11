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
package au.edu.monash.merc.capture.service;

import au.edu.monash.merc.capture.adapter.DataCaptureAdapter;
import au.edu.monash.merc.capture.domain.*;
import au.edu.monash.merc.capture.dto.*;
import au.edu.monash.merc.capture.dto.page.Pagination;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface DMService {

    boolean checkWritePermission(String path);

    void createCollection(Collection collection, String rootPath);

    void deleteCollection(Collection collection, String rootPath);

    void deletePublisheCollection(Collection collection, String storeRootPath, String rifcsRootPath);

    void updateCollection(Collection collection);

    Pagination<Collection> getCollectionsByUserId(long uid, int startPageNo, int recordsPerPage, OrderBy[] orderBys);

    Pagination<Collection> getAllPublicCollections(int startPageNo, int recordsPerPage, OrderBy[] orderBys);

    Pagination<Collection> getAllCollections(int startPageNo, int recordsPerPage, OrderBy[] orderBys);

    Collection getCollectionById(long id);

    Collection getCollection(long cid, long uid);

    boolean checkCollectionNameExisted(String colName);

    List<Collection> getCollectionsByLocation(String coverageType, String spatialCoverage);

    List<Collection> getCollectionsByLocation(long locationId);

    boolean findAnyReferencedCollectionsByLocationId(long locationId);

    List<Collection> getPublishedCollections();

    Collection getPublishedCoByIdentifier(String identifier);

    /**
     * Save a Location
     *
     * @param location A Location
     */
    void saveLocation(Location location);

    /**
     * Merge a Location
     *
     * @param location A Location
     */
    void mergeLocation(Location location);

    /**
     * Update a Location
     *
     * @param location A Location
     */
    void updateLocation(Location location);

    /**
     * Delete a Location
     *
     * @param location a Location location
     */
    void deleteLocation(Location location);

    /**
     * Get a Location by id
     *
     * @param id A Location id
     * @return A Location object
     */
    Location getLocationById(long id);

    /**
     * Delete a Location by id
     *
     * @param id A Location id
     */
    void deleteLocationById(long id);

    /**
     * get all Locations by coverageType
     *
     * @param coverageType a spatial coverage type
     * @return a list of Locations
     */
    List<Location> getLocations(String coverageType);

    /**
     * get a Location by coverage type
     *
     * @param coverageType    a coverage type
     * @param spatialCoverage a spatial coverage value
     * @return a Location object
     */
    Location getLocationByCoverageType(String coverageType, String spatialCoverage);

    Dataset captureData(String destFileName, File srcFile, boolean extractable, boolean globalAttOnly, Collection collection, String rootPath);

    List<String> discoverFiles(String stagePath, FilenameFilter filter);

    void stageTransfer(StageTransferBean transferBean);

    Dataset captureStageData(TransferFileBean fBean, DataCaptureAdapter adapter, Collection co, String stageDir, String destRootPath, boolean transferExistedFile);

    void deleteDataset(Collection co, Dataset ds, String rootPath);

    byte[] getDatasetContent(long dsId, String rootPath);

    byte[] getDatasetContent(Dataset ds, String rootPath);

    InputStream downloadFile(long dsId, String rootPath);

    InputStream downloadFile(Dataset ds, String rootPath);

    Dataset getDatasetById(long id);

    List<Dataset> getDatasetByCollectionId(long cid);

    List<Dataset> getDatasetByCollectionIdUsrId(long cid, long uid);

    boolean checkDatasetNameExisted(String dsName, long cid);

    Dataset getAllDatasetData(long dsId);


    void savePermission(CPermission permission);

    CPermission getPermissionById(long id);

    void updatePermission(CPermission permission);

    void mergePermission(CPermission permission);

    void deletePermission(CPermission permission);

    CPermission getUserCollectionPermission(long collectionId, long userId);

    CPermission getAllRegUserCollectionPermission(long collectionId);

    CPermission getAnonymousCollectionPermission(long collectionId);

    List<CPermission> getCollectionPermissions(long cid);

    InheritPermissionBean getUserInheritPermission(final long coId, final long userId);

    void deletePermissionByPermId(long permissionId);

    void deletePermissionsByCollectionId(long collectionId);

    List<CPermission> saveCollectionPermissions(AssignedPermissions assignedPerms);


    //TODO: to be removed after changing the permission domain

    List<Permission> getUserCoPerms(long permForUsrId, long coId);

    List<Permission> getCollectionDefaultPerms(long cid);

    Permission getAnonymousPerm(long cid);

    void createUserPermission(Permission permission);

    void deleteUserPermission(Permission permission);

    void deletePermissionByPId(long pId);

    void deleteAllPermissionsByColId(long cId);

    void updateUserPermission(Permission permission);

    void createUserPermissions(List<Permission> permissions);

    void updateUserPermissions(List<Permission> permissions);

    void deleteUserPermissions(List<Permission> permissions);

    void deleteUserPermissionsByIds(List<Long> pids);

    void setCollectionPermissions(AssignedPermissions assignedPerms);

   // List<Permission> getCollectionPermissions(long cid);

    void saveUserRequestedPerm(ManagablePerm<Permission> requestPermission, long permRequestId);

    void savePermissionRequest(PermissionRequest permRequest);

    PermissionRequest getPermissionReqById(long id);

    List<PermissionRequest> getPermissionRequestsByOwner(long ownerId);

    void deletePermissionRequestsByCoId(long coId);

    void deletePermissionRequestById(long pmReqId);

    PermissionRequest getCoPermissionRequestByReqUser(long coid, long reqUserId);

    void updatePermissionRequest(PermissionRequest permRequest);

    Pagination<PermissionRequest> getPermRequestsByPages(long ownerId, int startPageNo, int recordsPerPage, OrderBy[] orderBys);

    //TODO end of Permission




    void saveAuditEvent(AuditEvent event);

    void deleteEventByIdWithUserId(long eId, long userId);

    AuditEvent getAuditEventById(long eid);

    Pagination<AuditEvent> getEventByUserId(long uid, int startPageNo, int recordsPerPage, OrderBy[] orderBys);

    Profile getUserProfile(long userId);

    void updateProfile(Profile profile);

    Avatar getUserAvatar(long userId);

    void updateAvatar(Avatar avatar);

    void sendMail(String emailFrom, String emailTo, String emailSubject, String emailBody, boolean isHtml);

    void sendMail(String emailFrom, String emailTo, String emailSubject, Map<String, String> templateValues, String templateFile, boolean isHtml);

    void publishRifcs(MetadataRegistrationBean metadataRegistrationBean);

    List<Party> getPartiesByCollectionId(long cid);

    void saveLicence(Licence licence);

    void mergeLicence(Licence licence);

    void updateLicence(Licence licence);

    void deleteLicence(Licence licence);

    void deleteLicenceById(long id);

    Licence getLicenceById(long id);

    Licence getLicenceByCollectionId(long cid);

    Party getPartyByEmail(String email);

    List<Party> getPartyByUserName(String firstName, String lastName);

    List<Party> getPartyByUserNameOrEmail(String userNameOrEmail);

    Party getPartyByPartyKey(String partyKey);

    void saveParty(Party party);

    void updateParty(Party party);
}
