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

    public boolean checkWritePermission(String path);

    public void createCollection(Collection collection, String rootPath);

    public void deleteCollection(Collection collection, String rootPath);

    public void deletePublisheCollection(Collection collection, String storeRootPath, String rifcsRootPath);

    public void updateCollection(Collection collection);

    public Pagination<Collection> getCollectionsByUserId(long uid, int startPageNo, int recordsPerPage, OrderBy[] orderBys);

    public Pagination<Collection> getAllPublicCollections(int startPageNo, int recordsPerPage, OrderBy[] orderBys);

    public Pagination<Collection> getAllCollections(int startPageNo, int recordsPerPage, OrderBy[] orderBys);

    public Collection getCollectionById(long id);

    public Collection getCollection(long cid, long uid);

    public boolean checkCollectionNameExisted(String colName);

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
    public List<Location> getLocations(String coverageType);

    /**
     * get a Location by coverage type
     *
     * @param coverageType    a coverage type
     * @param spatialCoverage a spatial coverage value
     * @return a Location object
     */
    public Location getLocationByCoverageType(String coverageType, String spatialCoverage);

    public Dataset captureData(String destFileName, File srcFile, boolean extractable, boolean globalAttOnly, Collection collection, String rootPath);

    public List<String> discoverFiles(String stagePath, FilenameFilter filter);

    public void stageTransfer(StageTransferBean transferBean);

    public Dataset captureStageData(TransferFileBean fBean, DataCaptureAdapter adapter, Collection co, String stageDir, String destRootPath,
                                    boolean transferExistedFile);

    public void deleteDataset(Collection co, Dataset ds, String rootPath);

    public byte[] getDatasetContent(long dsId, String rootPath);

    public byte[] getDatasetContent(Dataset ds, String rootPath);

    public InputStream downloadFile(long dsId, String rootPath);

    public InputStream downloadFile(Dataset ds, String rootPath);

    public Dataset getDatasetById(long id);

    public List<Dataset> getDatasetByCollectionId(long cid);

    public List<Dataset> getDatasetByCollectionIdUsrId(long cid, long uid);

    public boolean checkDatasetNameExisted(String dsName, long cid);

    public Dataset getAllDatasetData(long dsId);

    public List<Permission> getUserCoPerms(long permForUsrId, long coId);

    public List<Permission> getCollectionDefaultPerms(long cid);

    public Permission getAnonymousPerm(long cid);

    public void createUserPermission(Permission permission);

    public void deleteUserPermission(Permission permission);

    public void deletePermissionByPId(long pId);

    public void deleteAllPermissionsByColId(long cId);

    public void updateUserPermission(Permission permission);

    public void createUserPermissions(List<Permission> permissions);

    public void updateUserPermissions(List<Permission> permissions);

    public void deleteUserPermissions(List<Permission> permissions);

    public void deleteUserPermissionsByIds(List<Long> pids);

    public void setCollectionPermissions(AssignedPermissions assignedPerms);

    public List<Permission> getCollectionPermissions(long cid);

    public void saveUserRequestedPerm(ManagablePerm<Permission> requestPermission, long permRequestId);

    public void savePermissionRequest(PermissionRequest permRequest);

    public PermissionRequest getPermissionReqById(long id);

    public List<PermissionRequest> getPermissionRequestsByOwner(long ownerId);

    public void deletePermissionRequestsByCoId(long coId);

    public void deletePermissionRequestById(long pmReqId);

    public PermissionRequest getCoPermissionRequestByReqUser(long coid, long reqUserId);

    public void updatePermissionRequest(PermissionRequest permRequest);

    public Pagination<PermissionRequest> getPermRequestsByPages(long ownerId, int startPageNo, int recordsPerPage, OrderBy[] orderBys);

    public void saveAuditEvent(AuditEvent event);

    public void deleteEventByIdWithUserId(long eId, long userId);

    public AuditEvent getAuditEventById(long eid);

    public Pagination<AuditEvent> getEventByUserId(long uid, int startPageNo, int recordsPerPage, OrderBy[] orderBys);

    public Profile getUserProfile(long userId);

    public void updateProfile(Profile profile);

    public Avatar getUserAvatar(long userId);

    public void updateAvatar(Avatar avatar);

    public void sendMail(String emailFrom, String emailTo, String emailSubject, String emailBody, boolean isHtml);

    public void sendMail(String emailFrom, String emailTo, String emailSubject, Map<String, String> templateValues, String templateFile,
                         boolean isHtml);

    public void savePublishInfo(PublishBean pubBean);

    public List<Activity> getActivitiesByCollectionId(long cid);

    public List<Party> getPartiesByCollectionId(long cid);

    public List<Collection> getPublishedCollections();

    public Collection getPublishedCoByIdentifier(String identifier);

    public void saveRights(Rights rights);

    public void updateRights(Rights rights);

    public Rights getRightsById(long id);

    public Rights getRightsByCollectionId(long cid);

    public Party getPartyByEmail(String email);

    public List<Party> getPartyByUserName(String firstName, String lastName);

    public List<Party> getPartyByUserNameOrEmail(String userNameOrEmail);

    public Party getPartyByPartyKey(String partyKey);

    public void saveParty(Party party);

    public void updateParty(Party party);

    public Activity getActivityByActKey(String activityKey);

    public void saveActivity(Activity activity);
}
