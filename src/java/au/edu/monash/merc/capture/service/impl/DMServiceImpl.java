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

import au.edu.monash.merc.capture.adapter.DataCaptureAdapter;
import au.edu.monash.merc.capture.adapter.DataCaptureAdapterFactory;
import au.edu.monash.merc.capture.common.CoverageType;
import au.edu.monash.merc.capture.domain.*;
import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.dto.*;
import au.edu.monash.merc.capture.dto.page.Pagination;
import au.edu.monash.merc.capture.exception.DataCaptureException;
import au.edu.monash.merc.capture.file.FileSystemSerivce;
import au.edu.monash.merc.capture.mail.MailService;
import au.edu.monash.merc.capture.rifcs.PartyActivityWSService;
import au.edu.monash.merc.capture.rifcs.RIFCSGenService;
import au.edu.monash.merc.capture.rifcs.RifcsService;
import au.edu.monash.merc.capture.service.*;
import au.edu.monash.merc.capture.util.CaptureUtil;
import au.edu.monash.merc.capture.util.stage.StageFileTransferThread;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.*;

@Scope("prototype")
@Service
@Transactional
public class DMServiceImpl implements DMService {

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionRequestService permRequestService;

    @Autowired
    private FileSystemSerivce fileService;

    @Autowired
    private DataCaptureAdapterFactory adapterFactory;

    @Autowired
    private AuditEventService auditEventService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private MailService mailService;

    @Autowired
    private PartyService partyService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private RIFCSGenService rifcsGenService;

    @Autowired
    private RifcsService rifcsService;

    @Autowired
    private RightsService rightsService;

    @Autowired
    private LicenceService licenceService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private PartyActivityWSService paWsService;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public void setCollectionService(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    public void setDatasetService(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setFileService(FileSystemSerivce fileService) {
        this.fileService = fileService;
    }

    public void setAdapterFactory(DataCaptureAdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    public void setAuditEventService(AuditEventService auditEventService) {
        this.auditEventService = auditEventService;
    }

    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    public void setAvatarService(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setActivityService(ActivityService activityService) {
        this.activityService = activityService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setRifcsGenService(RIFCSGenService rifcsGenService) {
        this.rifcsGenService = rifcsGenService;
    }

    public void setRifcsService(RifcsService rifcsService) {
        this.rifcsService = rifcsService;
    }

    public void setRightsService(RightsService rightsService) {
        this.rightsService = rightsService;
    }

    public void setLicenceService(LicenceService licenceService) {
        this.licenceService = licenceService;
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    public void setPaWsService(PartyActivityWSService paWsService) {
        this.paWsService = paWsService;
    }

    public boolean checkWritePermission(String path) {
        return this.fileService.checkWritePermission(path);
    }

    @Override
    public void createCollection(Collection collection, String rootPath) {

        String collectionPath = rootPath + File.separator + collection.getDirPathName();
        // create a directory first, if any exception occurs, it will never create a collection in database
        this.fileService.createDirectory(collectionPath);

        try {
            this.collectionService.saveCollection(collection);
        } catch (Exception e) {
            // if create a collection error. then delete the collection directory in the file system
            try {
                File co = new File(collectionPath);
                if (co.exists()) {
                    this.fileService.deleteDirectory(collectionPath);
                }
            } catch (Exception fe) {
                // failed to delete a collection in file system. we should log this exception
                logger.error("failed to delete a collection in file system, " + fe.getMessage());
                throw new DataCaptureException(fe);
            }
            throw new DataCaptureException(e);
        }
    }

    @Override
    public void deleteCollection(Collection collection, String rootPath) {
        // delete the permissions first
        deleteAllPermissionsByColId(collection.getId());
        //delete the permissions request if any
        deletePermissionRequestsByCoId(collection.getId());
        // delete it from database first
        this.collectionService.deleteCollection(collection);
        // then delete it from the file storage
        this.fileService.deleteDirectory(rootPath + File.separator + collection.getDirPathName());
    }

    @Override
    public void deletePublisheCollection(Collection collection, String storeRootPath, String rifcsRootPath) {
        // delete the permissions first
        deleteAllPermissionsByColId(collection.getId());
        //delete the permissions request if any
        deletePermissionRequestsByCoId(collection.getId());
        // delete it from database first
        // TODO:1. delete handle
        String uuidkey = collection.getUniqueKey();
        this.collectionService.deleteCollection(collection);
        // then delete it from the file storage
        try {
            this.fileService.deleteDirectory(storeRootPath + File.separator + collection.getDirPathName());
            this.fileService.deleteFile(rifcsRootPath + File.separator + CaptureUtil.pathEncode(uuidkey) + ".xml");
        } catch (Exception e) {
            throw new DataCaptureException(e);
        }
    }

    @Override
    public void updateCollection(Collection collection) {
        // for update a collection. we only need to update the properties values in the database.
        // the collection path in the file storage will never be changed since it created
        this.collectionService.updateCollection(collection);
    }

    @Override
    public Pagination<Collection> getCollectionsByUserId(long uid, int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
        return this.collectionService.getCollectionsByUserId(uid, startPageNo, recordsPerPage, orderBys);
    }

    @Override
    public Pagination<Collection> getAllPublicCollections(int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
        return this.collectionService.getAllPublicCollections(startPageNo, recordsPerPage, orderBys);
    }

    @Override
    public Pagination<Collection> getAllCollections(int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
        return this.collectionService.getAllCollections(startPageNo, recordsPerPage, orderBys);
    }

    public Collection getCollectionById(long id) {
        return this.collectionService.getCollectionById(id);
    }

    @Override
    public Collection getCollection(long cid, long uid) {
        return this.collectionService.getCollection(cid, uid);
    }

    @Override
    public boolean checkCollectionNameExisted(String colName) {
        return this.collectionService.checkCollectionNameExisted(colName);
    }

    @Override
    public List<Collection> getCollectionsByLocation(String coverageType, String spatialCoverage) {
        return this.collectionService.getCollectionsByLocation(coverageType, spatialCoverage);
    }

    @Override
    public List<Collection> getCollectionsByLocation(long locationId) {
        return this.collectionService.getCollectionsByLocation(locationId);
    }

    @Override
    public boolean findAnyReferencedCollectionsByLocationId(long locationId) {
        return this.collectionService.findAnyReferencedCollectionsByLocationId(locationId);
    }

    @Override
    public void saveLocation(Location location) {
        this.locationService.saveLocation(location);
    }

    @Override
    public void mergeLocation(Location location) {
        this.locationService.mergeLocation(location);
    }

    @Override
    public void updateLocation(Location location) {
        this.locationService.updateLocation(location);
    }

    @Override
    public void deleteLocation(Location location) {
        this.locationService.deleteLocation(location);
    }

    @Override
    public Location getLocationById(long id) {
        return this.locationService.getLocationById(id);
    }

    @Override
    public void deleteLocationById(long id) {
        this.locationService.deleteLocationById(id);
    }

    @Override
    public List<Location> getLocations(String coverageType) {
        return this.locationService.getLocations(coverageType);
    }

    @Override
    public Location getLocationByCoverageType(String coverageType, String spatialCoverage) {
        return this.locationService.getLocationByCoverageType(coverageType, spatialCoverage);
    }

    @Override
    public Dataset captureData(String simpleFileName, File srcFile, boolean extractRequired, boolean globalAttOnly, Collection collection,
                               String rootPath) {

        String destDatasetFileRelPath = collection.getDirPathName() + File.separator + simpleFileName;
        String destDatasetFileFullPath = rootPath + destDatasetFileRelPath;
        Dataset ds = null;
        try {
            // persist file first
            this.fileService.moveFile(srcFile, destDatasetFileFullPath, true);

            DataCaptureAdapter adapter = adapterFactory.createInstance();
            ds = adapter.caputreData(simpleFileName, destDatasetFileFullPath, extractRequired, globalAttOnly);
            // set dataset store location
            ds.setStoreLocation(destDatasetFileRelPath);
            ds.setCollection(collection);
            ds.setImportDateTime(GregorianCalendar.getInstance().getTime());

            // save the dataset in database first
            this.datasetService.saveDataset(ds);

            // update the collection in database
            this.collectionService.updateCollection(collection);

        } catch (Exception e) {
            // if any exception occurs, just delete data file which created before if any.
            try {
                File f = new File(destDatasetFileFullPath);
                if (f.exists()) {
                    this.fileService.deleteFile(destDatasetFileFullPath);
                }
            } catch (Exception fe) {
                // log this exception when a postprocess error occurs
                logger.error("failed to delete " + destDatasetFileFullPath + " file in the destination directory, " + fe.getMessage());
            }
            throw new DataCaptureException(e);
        }
        return ds;
    }

    @Override
    public List<String> discoverFiles(String stagePath, FilenameFilter filter) {
        return this.fileService.discoverFiles(stagePath, filter);
    }

    @Override
    public void stageTransfer(StageTransferBean transferBean) {
        DataCaptureAdapter adapter = adapterFactory.createInstance();
        StageFileTransferThread transferThread = new StageFileTransferThread(this, adapter, transferBean);
        transferThread.transfer();
    }

    @Override
    public Dataset captureStageData(TransferFileBean fBean, DataCaptureAdapter adapter, Collection co, String stageDir, String destRootPath,
                                    boolean transferExistedFile) {

        String simpleFileName = fBean.getFileName();
        if (transferExistedFile) {
            String suffix = CaptureUtil.genCurrentTimestamp();
            simpleFileName = simpleFileName + "." + suffix + "_copy";
        }

        String srcFileFullPath = stageDir + File.separator + fBean.getFileName();
        String destDatasetFileRelPath = co.getDirPathName() + File.separator + simpleFileName;
        String destDatasetFileFullPath = destRootPath + destDatasetFileRelPath;
        Dataset ds = null;
        try {
            // persist file first
            this.fileService.moveFile(srcFileFullPath, destDatasetFileFullPath, true);
            // once the file move into destination directory, then we start to extract the metadata
            ds = adapter.caputreData(simpleFileName, destDatasetFileFullPath, fBean.extractRequired(), fBean.isGlobalAttOnly());
            // set dataset store location
            ds.setStoreLocation(destDatasetFileRelPath);
            ds.setCollection(co);
            ds.setImportDateTime(GregorianCalendar.getInstance().getTime());
            // save the dataset in database first
            this.datasetService.saveDataset(ds);
            // update the collection in database
            this.collectionService.updateCollection(co);

        } catch (Exception e) {
            // if any exception occurs, just reverts data file back to staging directory.
            try {
                File srcf = new File(srcFileFullPath);
                File destf = new File(destDatasetFileFullPath);

                // reverts data file to the staging directory.
                if (destf.exists() && (!srcf.exists())) {
                    this.fileService.moveFile(destDatasetFileFullPath, srcFileFullPath, true);
                }
                // delete the transferred file in the destination directory.
                if (destf.exists()) {
                    this.fileService.deleteFile(destDatasetFileFullPath);
                }
            } catch (Exception fe) {
                logger.error("failed to revert " + destDatasetFileFullPath + " back to the staging directory, " + fe.getMessage());
            }
            throw new DataCaptureException(e);
        }
        return ds;
    }

    @Override
    public void deleteDataset(Collection co, Dataset ds, String rootPath) {

        String filePath = rootPath + ds.getStoreLocation();
        // delete the dataset from database first
        this.datasetService.deleteDataset(ds);
        // delete the dataset file from the file system
        this.fileService.deleteFile(filePath);
        // update the collection
        this.collectionService.updateCollection(co);
    }

    @Override
    /**
     * The getDatasetContent method supports for small file size
     */
    public byte[] getDatasetContent(long dsId, String rootPath) {
        Dataset ds = this.getDatasetById(dsId);
        String fileLocation = rootPath + ds.getStoreLocation();
        return this.fileService.readFileToByteArray(fileLocation);
    }

    @Override
    /**
     * The getDatasetContent method supports for small file size
     */
    public byte[] getDatasetContent(Dataset ds, String rootPath) {
        String fileLocation = rootPath + ds.getStoreLocation();
        return this.fileService.readFileToByteArray(fileLocation);
    }

    @Override
    /**
     * The downloadFile method supports for large file download
     */
    public InputStream downloadFile(long dsId, String rootPath) {
        Dataset ds = this.getDatasetById(dsId);
        String fileLocation = rootPath + ds.getStoreLocation();
        return this.fileService.downloadFile(fileLocation);
    }

    @Override
    /**
     * The downloadFile method supports for large file download
     */
    public InputStream downloadFile(Dataset ds, String rootPath) {
        String fileLocation = rootPath + ds.getStoreLocation();
        return this.fileService.downloadFile(fileLocation);
    }

    @Override
    public Dataset getDatasetById(long id) {
        return this.datasetService.getDatasetById(id);
    }

    @Override
    public List<Dataset> getDatasetByCollectionId(long cid) {
        return this.datasetService.getDatasetByCollectionId(cid);
    }

    @Override
    public List<Dataset> getDatasetByCollectionIdUsrId(long cid, long uid) {
        return this.datasetService.getDatasetByCollectionIdUsrId(cid, uid);
    }

    @Override
    public boolean checkDatasetNameExisted(String dsName, long cid) {
        return this.datasetService.checkDatasetNameExisted(dsName, cid);
    }

    @Override
    public Dataset getAllDatasetData(long dsId) {
        return this.datasetService.getAllDatasetData(dsId);
    }

    @Override
    public List<Permission> getUserCoPerms(long permForUsrId, long coId) {
        return this.permissionService.getUserCoPerms(permForUsrId, coId);
    }

    @Override
    public List<Permission> getCollectionDefaultPerms(long cid) {
        return this.permissionService.getCollectionDefaultPerms(cid);
    }

    @Override
    public Permission getAnonymousPerm(long cid) {
        return this.permissionService.getAnonymousPerm(cid);
    }

    @Override
    public void createUserPermission(Permission permission) {
        this.permissionService.savePermission(permission);
    }

    @Override
    public void deleteUserPermission(Permission permission) {
        this.permissionService.deletePermission(permission);
    }

    @Override
    public void deletePermissionByPId(long pId) {
        this.permissionService.deletePermissionByPId(pId);
    }

    @Override
    public void deleteAllPermissionsByColId(long cId) {
        this.permissionService.deleteAllPermissionsByColId(cId);
    }

    @Override
    public void updateUserPermission(Permission permission) {
        this.permissionService.updatePermission(permission);
    }

    @Override
    public void createUserPermissions(List<Permission> permissions) {
        for (Permission p : permissions) {
            createUserPermission(p);
        }
    }

    @Override
    public void updateUserPermissions(List<Permission> permissions) {
        for (Permission p : permissions) {
            updateUserPermission(p);
        }
    }

    @Override
    public void deleteUserPermissions(List<Permission> permissions) {
        for (Permission p : permissions) {
            deleteUserPermission(p);
        }
    }

    @Override
    public void deleteUserPermissionsByIds(List<Long> pids) {
        for (Long pid : pids) {
            deletePermissionByPId(pid.longValue());
        }
    }

    @Override
    public void setCollectionPermissions(AssignedPermissions assignedPerms) {
        createUserPermissions(assignedPerms.getPermissionsNew());
        updateUserPermissions(assignedPerms.getPermissionsUpdate());
        deleteUserPermissionsByIds(assignedPerms.getDeletePermsIds());
    }

    @Override
    public List<Permission> getCollectionPermissions(long cid) {
        return this.permissionService.getCollectionPermissions(cid);
    }

    @Override
    public void saveUserRequestedPerm(ManagablePerm<Permission> requestPermission, long permRequestId) {
        Permission finalPerm = requestPermission.getPerm();
        if (requestPermission.getManagablePermType().equals(ManagablePermType.DELETE)) {
            this.deletePermissionByPId(finalPerm.getId());
        } else if (requestPermission.getManagablePermType().equals(ManagablePermType.NEW)) {
            this.createUserPermission(finalPerm);
        } else if (requestPermission.getManagablePermType().equals(ManagablePermType.UPDATE)) {
            this.updateUserPermission(finalPerm);
        } else {
            //ignore
        }
        //finally we have to delete this permission request
        this.deletePermissionRequestById(permRequestId);
    }


    // Permission request section
    @Override
    public void savePermissionRequest(PermissionRequest permRequest) {
        this.permRequestService.savePermissionRequest(permRequest);
    }

    @Override
    public PermissionRequest getPermissionReqById(long id) {
        return this.permRequestService.getPermissionReqById(id);
    }

    @Override
    public List<PermissionRequest> getPermissionRequestsByOwner(long ownerId) {
        return this.permRequestService.getPermissionRequestsByOwner(ownerId);
    }

    @Override
    public void deletePermissionRequestsByCoId(long coId) {
        this.permRequestService.deletePermissionRequestsByCoId(coId);
    }

    @Override
    public void deletePermissionRequestById(long pmReqId) {
        this.permRequestService.deletePermissionRequestById(pmReqId);
    }

    @Override
    public PermissionRequest getCoPermissionRequestByReqUser(long coid, long reqUserId) {
        return this.permRequestService.getCoPermissionRequestByReqUser(coid, reqUserId);
    }

    @Override
    public void updatePermissionRequest(PermissionRequest permRequest) {
        this.permRequestService.updatePermissionRequest(permRequest);
    }

    @Override
    public Pagination<PermissionRequest> getPermRequestsByPages(long ownerId, int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
        return this.permRequestService.getPermRequestsByPages(ownerId, startPageNo, recordsPerPage, orderBys);
    }

    @Override
    public void saveAuditEvent(AuditEvent event) {
        this.auditEventService.saveAuditEvent(event);
    }

    @Override
    public void deleteEventByIdWithUserId(long eId, long userId) {
        this.auditEventService.deleteEventByIdWithUserId(eId, userId);
    }

    @Override
    public AuditEvent getAuditEventById(long eid) {
        return this.auditEventService.getAuditEventById(eid);
    }

    @Override
    public Pagination<AuditEvent> getEventByUserId(long uid, int startPageNo, int recordsPerPage, OrderBy[] orderBys) {
        return this.auditEventService.getEventByUserId(uid, startPageNo, recordsPerPage, orderBys);
    }

    @Override
    public Profile getUserProfile(long userId) {
        return this.profileService.getUserProfile(userId);
    }

    @Override
    public void updateProfile(Profile profile) {
        this.profileService.updateProfile(profile);
    }

    @Override
    public Avatar getUserAvatar(long userId) {
        return this.avatarService.getUserAvatar(userId);
    }

    @Override
    public void updateAvatar(Avatar avatar) {
        this.avatarService.updateAvatar(avatar);
    }

    @Override
    public void sendMail(String emailFrom, String emailTo, String emailSubject, String emailBody, boolean isHtml) {
        this.mailService.sendMail(emailFrom, emailTo, emailSubject, emailBody, isHtml);
    }

    @Override
    public void sendMail(String emailFrom, String emailTo, String emailSubject, Map<String, String> templateValues, String templateFile,
                         boolean isHtml) {
        this.mailService.sendMail(emailFrom, emailTo, emailSubject, templateValues, templateFile, isHtml);
    }


    private Map<String, Object> popolatePartyTemplateMap() {

        return null;
    }

    //TODO: new RIFCS
    @Override
    public void publishRifcs(MetadataRegistrationBean metadataRegistrationBean) {
        List<PartyBean> partyList = metadataRegistrationBean.getPartyList();
        // parties
        List<Party> parties = new ArrayList<Party>();
        for (PartyBean partybean : partyList) {
            //we only deal with the selected parties
            if (partybean.isSelected()) {
                // search the party detail by the party's key
                Party p = getPartyByPartyKey(partybean.getPartyKey());
                // if party not found from the database, we just save it into database;
                if (p == null) {
                    p = copyPartyBeanToParty(partybean);
                    saveParty(p);
                }
                parties.add(p);
            }
        }
        Collection collection = metadataRegistrationBean.getCollection();
        collection.setParties(parties);

        // set the collection true
        collection.setPublished(true);
        // update the collection first
        this.updateCollection(collection);
        //check the licence
        Licence dataLicence = metadataRegistrationBean.getLicence();
        Licence foundLicence = this.getLicenceByCollectionId(collection.getId());
        if (foundLicence == null) {
            dataLicence.setCollection(collection);
            this.saveLicence(dataLicence);
        } else {
            dataLicence.setId(foundLicence.getId());
            dataLicence.setCollection(collection);
            this.mergeLicence(dataLicence);
        }
        //create collection rifcs
        String rifcsStoreLocation = metadataRegistrationBean.getRifcsStoreLocation();
        String uniqueKey = collection.getUniqueKey();
        String collectionRifTemp = metadataRegistrationBean.getRifcsCollectionTemplate();
        Map<String, Object> collectionTempValues = populateCollectionRifcsMap(metadataRegistrationBean, parties);
        this.rifcsService.createRifcs(rifcsStoreLocation, uniqueKey, collectionTempValues, collectionRifTemp);

        String noneRMPartyTemp = metadataRegistrationBean.getRifcsPartyTemplate();
        String rmPartyTemp = metadataRegistrationBean.getRifcsRMPartyTemplate();
        for (Party party : parties) {
            String partyKey = party.getPartyKey();
            if (party.isFromRm()) {
                PartyBean rmPartyBean = this.paWsService.getParty(party.getPartyKey());
                String rifcsContents = rmPartyBean.getRifcsContent();
                Map<String, Object> rmPartyTempValues = populateRMPartyRifcsMap(rifcsContents);
                this.rifcsService.createRifcs(rifcsStoreLocation, partyKey, rmPartyTempValues, rmPartyTemp);
            } else {
                Map<String, Object> partyTempValues = populateNoneRMPartyRifcsMap(party);
                this.rifcsService.createRifcs(rifcsStoreLocation, partyKey, partyTempValues, noneRMPartyTemp);
            }
        }
    }

    private Map<String, Object> populateCollectionRifcsMap(MetadataRegistrationBean mdRegBean, List<Party> selectedParties) {
        Map<String, Object> templateMap = new HashMap<String, Object>();
        Collection collection = mdRegBean.getCollection();
        String serverName = mdRegBean.getAppName();
        String groupName = mdRegBean.getRifcsGroupName();
        String localKey = collection.getUniqueKey();
        String identifier = collection.getPersistIdentifier();
        String collectionName = collection.getName();
        String collectionDesc = collection.getDescription();
        String collectionUrl = mdRegBean.getCollectionUrl();
        String postalAddress = mdRegBean.getPhysicalAddress();
        String dateFrom = CaptureUtil.formateDateToW3CDTF(collection.getDateFrom());
        String dateTo = CaptureUtil.formateDateToW3CDTF(collection.getDateTo());
        Location location = null;
        Location spatialLocation = collection.getLocation();

        if (spatialLocation != null) {
            String spatialType = spatialLocation.getSpatialType();
            if (!CoverageType.fromType(spatialType).equals(CoverageType.UNKNOWN)) {
                location = new Location();
                if (CoverageType.fromType(spatialType).equals(CoverageType.GLOBAL)) {
                    location.setSpatialType("text");
                    location.setSpatialCoverage(spatialLocation.getSpatialCoverage());
                } else {
                    location.setSpatialType(spatialLocation.getSpatialType());
                    location.setSpatialCoverage(spatialLocation.getSpatialCoverage());
                }
            }
        }

        Licence licence = mdRegBean.getLicence();
        String licenceContents = licence.getContents();
        templateMap.put("groupName", groupName);
        //check if it's handle key, then we add the handle server url
        String keyId = identifier;
        if (identifier != null && StringUtils.contains(identifier, "/")) {
            keyId = "http://hdl.handle.net" + "/" + identifier;
        }
        templateMap.put("keyId", keyId);

        templateMap.put("originatingSrc", serverName);
        templateMap.put("localKey", localKey);

        //if handle provided, just put the handle identifier
        if (identifier != null && StringUtils.contains(identifier, "/")) {
            String handleId = "http://hdl.handle.net" + "/" + identifier;
            templateMap.put("handleId", handleId);
        }

        templateMap.put("collectionName", collectionName);
        templateMap.put("collectionUrl", collectionUrl);
        templateMap.put("physicalAddress", postalAddress);
        //if location provided, then set the location
        if (location != null) {
            templateMap.put("location", location);
        }
        templateMap.put("temporalDateFrom", dateFrom);
        templateMap.put("temporalDateTo", dateTo);
        templateMap.put("parties", selectedParties);
        templateMap.put("collectionDesc", collectionDesc);
        templateMap.put("licenceContents", licenceContents);

        //citation metadata
        User owner = collection.getOwner();
        String givenName = owner.getFirstName();
        String familyName = owner.getLastName();
        String creator = givenName + " " + familyName;

        Date date = collection.getCreatedTime();
        String publicationYear = CaptureUtil.dateToYYYY(date);

        String publisher = mdRegBean.getRifcsGroupName();

        String citationIdentifier = "local: " + identifier;
        if (identifier != null && StringUtils.contains(identifier, "/")) {
            citationIdentifier = "hdl: " + identifier;
        }
        templateMap.put("creator", creator);
        templateMap.put("publicationYear", publicationYear);
        templateMap.put("publisher", publisher);
        templateMap.put("citationIdentifier", citationIdentifier);
        return templateMap;
    }

    private Map<String, Object> populateNoneRMPartyRifcsMap(Party party) {
        Map<String, Object> templateMap = new HashMap<String, Object>();
        String groupName = party.getGroupName();
        String localKey = party.getPartyKey();
        String originatingSrc = party.getOriginateSourceValue();
        Date date = GregorianCalendar.getInstance().getTime();
        String dateModified = CaptureUtil.formatDateToUTC(date);
        String personTitle = party.getPersonTitle();
        String givenName = party.getPersonGivenName();
        String familyName = party.getPersonFamilyName();
        String webSite = party.getUrl();
        String emailAddress = party.getEmail();
        String partyDesc = party.getDescription();
        templateMap.put("groupName", groupName);
        templateMap.put("localKey", localKey);
        templateMap.put("originatingSrc", originatingSrc);
        templateMap.put("dateModified", dateModified);
        templateMap.put("identifierKey", localKey);
        templateMap.put("personTitle", personTitle);
        templateMap.put("givenName", givenName);
        templateMap.put("familyName", familyName);
        templateMap.put("webSite", webSite);
        templateMap.put("emailAddress", emailAddress);
        if (StringUtils.isNotBlank(partyDesc)) {
            templateMap.put("partyDesc", partyDesc);
        }
        return templateMap;
    }

    private Map<String, Object> populateRMPartyRifcsMap(String partyContents) {
        Map<String, Object> templateMap = new HashMap<String, Object>();
        templateMap.put("partyContents", partyContents);
        return templateMap;
    }

    @Override
    public void savePublishInfo(PublishBean pubBean) {

        List<PartyBean> partyList = pubBean.getPartyList();
        // parties
        List<Party> parties = new ArrayList<Party>();
        for (PartyBean partybean : partyList) {
            // search the party detail by the party's key
            Party p = getPartyByPartyKey(partybean.getPartyKey());
            // if party not found from the database, we just save it into database;
            if (p == null) {
                p = copyPartyBeanToParty(partybean);
                saveParty(p);
            }
            parties.add(p);
        }


        Collection collection = pubBean.getCollection();
        collection.setParties(parties);
        List<ProjectBean> actList = pubBean.getActivityList();
        // activities
        List<Activity> activities = new ArrayList<Activity>();
        if (actList != null) {
            for (ProjectBean projbean : actList) {
                Activity act = this.getActivityByActKey(projbean.getActivityKey());
                if (act == null) {
                    act = new Activity();
                    act.setActivityKey(projbean.getActivityKey());
                    this.saveActivity(act);
                }
                activities.add(act);
            }
        }
        // this.activityService.saveAll(activities);
        collection.setActivities(activities);
        // update the collection first
        this.updateCollection(collection);

        // save or update the previous rights
        Rights rights = pubBean.getRights();
        rights.setCollection(collection);
        if (rights.getId() == 0) {
            Rights existedRights = this.getRightsByCollectionId(collection.getId());
            if (existedRights != null) {
                long id = existedRights.getId();
                rights.setId(id);
                this.updateRights(rights);
            } else {
                this.saveRights(rights);
            }
        } else {
            this.updateRights(rights);
        }
        this.rifcsGenService.publishCollectionRifcs(pubBean);
    }

    private Party copyPartyBeanToParty(PartyBean pb) {
        Party pa = new Party();
        pa.setPartyKey(pb.getPartyKey());
        pa.setPersonTitle(pb.getPersonTitle());
        pa.setPersonGivenName(pb.getPersonGivenName());
        pa.setPersonFamilyName(pb.getPersonFamilyName());
        pa.setUrl(pb.getUrl());
        pa.setEmail(pb.getEmail());
        pa.setAddress(pb.getAddress());
        pa.setIdentifierType(pb.getIdentifierType());
        pa.setIdentifierValue(pb.getIdentifierValue());
        pa.setOriginateSourceType(pb.getOriginateSourceType());
        pa.setOriginateSourceValue(pb.getOriginateSourceValue());
        pa.setGroupName(pb.getGroupName());
        pa.setFromRm(pb.isFromRm());
        return pa;
    }

    @Override
    public List<Activity> getActivitiesByCollectionId(long cid) {
        return this.activityService.getActivitiesByCollectionId(cid);
    }

    @Override
    public List<Party> getPartiesByCollectionId(long cid) {
        return this.partyService.getPartiesByCollectionId(cid);
    }

    @Override
    public List<Collection> getPublishedCollections() {
        return this.collectionService.getPublishedCollections();
    }

    @Override
    public Collection getPublishedCoByIdentifier(String identifier) {
        return this.collectionService.getPublishedCoByIdentifier(identifier);
    }

    //TODO: to be removed
    @Override
    public void saveRights(Rights rights) {
        this.rightsService.saveRights(rights);
    }

    @Override
    public void updateRights(Rights rights) {
        this.rightsService.updateRights(rights);
    }

    @Override
    public Rights getRightsById(long id) {
        return this.rightsService.getRightsById(id);
    }

    @Override
    public Rights getRightsByCollectionId(long cid) {
        return this.rightsService.getRightsByCollectionId(cid);
    }

    @Override
    public void saveLicence(Licence licence) {
        this.licenceService.saveLicence(licence);
    }

    @Override
    public void mergeLicence(Licence licence) {
        this.licenceService.mergeLicence(licence);
    }

    @Override
    public void updateLicence(Licence licence) {
        this.licenceService.updateLicence(licence);
    }

    @Override
    public void deleteLicence(Licence licence) {
        this.licenceService.deleteLicence(licence);
    }

    @Override
    public void deleteLicenceById(long id) {
        this.licenceService.deleteLicenceById(id);
    }

    @Override
    public Licence getLicenceById(long id) {
        return this.licenceService.getLicenceById(id);
    }

    @Override
    public Licence getLicenceByCollectionId(long cid) {
        return this.licenceService.getLicenceByCollectionId(cid);
    }

    @Override
    public Party getPartyByEmail(String email) {
        return this.partyService.getPartyByEmail(email);
    }

    @Override
    public List<Party> getPartyByUserName(String firstName, String lastName) {
        return this.partyService.getPartyByUserName(firstName, lastName);
    }

    @Override
    public List<Party> getPartyByUserNameOrEmail(String userNameOrEmail) {
        String searchFor = null;
        List<Party> parties = new ArrayList<Party>();

        if (StringUtils.contains(userNameOrEmail, "@")) {
            Party foundParty = this.getPartyByEmail(userNameOrEmail);
            if (foundParty != null) {
                parties.add(foundParty);
            }
            return parties;
        }

        String[] names = StringUtils.split(userNameOrEmail, " ");
        if (names != null && names.length >= 2) {
            String firstName = names[0];
            String lastName = names[1];
            parties = this.getPartyByUserName(firstName, lastName);
        }
        if (names != null && names.length == 1) {
            String firstName = names[0];
            parties = this.getPartyByUserName(firstName, null);
        }
        return parties;
    }

    @Override
    public Party getPartyByPartyKey(String partyKey) {
        return this.partyService.getPartyByPartyKey(partyKey);
    }

    @Override
    public void saveParty(Party party) {
        this.partyService.saveParty(party);
    }

    @Override
    public void updateParty(Party party) {
        this.partyService.updateParty(party);
    }

    @Override
    public Activity getActivityByActKey(String activityKey) {
        return this.activityService.getActivityByActKey(activityKey);
    }

    @Override
    public void saveActivity(Activity activity) {
        this.activityService.saveActivity(activity);
    }

}
