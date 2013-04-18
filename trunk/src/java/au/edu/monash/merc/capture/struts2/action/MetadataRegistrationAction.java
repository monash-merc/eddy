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

package au.edu.monash.merc.capture.struts2.action;

import au.edu.monash.merc.capture.common.LicenceType;
import au.edu.monash.merc.capture.common.UserType;
import au.edu.monash.merc.capture.common.UserViewType;
import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.AuditEvent;
import au.edu.monash.merc.capture.domain.Licence;
import au.edu.monash.merc.capture.domain.Party;
import au.edu.monash.merc.capture.dto.MetadataRegistrationBean;
import au.edu.monash.merc.capture.dto.PartyBean;
import au.edu.monash.merc.capture.dto.RegisterActivity;
import au.edu.monash.merc.capture.util.CaptureUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 7/03/13 10:38 AM
 */
@Scope("prototype")
@Controller("data.metadataRegAction")
public class MetadataRegistrationAction extends DMCoreAction {

    private List<PartyBean> partyList;

    private RegisterActivity activity;

    private Licence licence;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @PostConstruct
    public void initReg() {
        licence = new Licence();
        licence.setLicenceType(LicenceType.TERN.type());
        activity = new RegisterActivity();
        activity.setKey(this.configSetting.getPropValue(ConfigSettings.OZFLUX_ACTIVITY_KEY));
    }

    public String showMdRegister() {

        setViewColDetailLink(ActConstants.VIEW_COLLECTION_DETAILS_ACTION);
        //check the user is logged in or not
        try {
            user = retrieveLoggedInUser();
            //if user is not logged in
            if (user == null) {
                addActionError(getText("ands.md.registration.none.owner.or.admin.permission.denied"));
                setNavAfterExc();
                return ERROR;
            }
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("ands.md.registration.get.user.failed"));
            setNavAfterExc();
            return ERROR;
        }
        //check the collection
        try {
            collection = this.dmService.getCollection(collection.getId(), collection.getOwner().getId());
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("ands.md.registration.get.collection.failed"));
            setNavAfterExc();
            return ERROR;
        }

        //only the owner and system admin can publish this collection
        if ((user.getId() != collection.getOwner().getId()) && (user.getUserType() != UserType.ADMIN.code() && (user.getUserType() != UserType.SUPERADMIN.code()))) {
            logger.error(getText("ands.md.registration.none.owner.or.admin.permission.denied"));
            addActionError(getText("ands.md.registration.none.owner.or.admin.permission.denied"));
            setNavAfterExc();
            return ERROR;
        }

        // check the existed parties if any
        try {
            List<Party> ps = this.dmService.getPartiesByCollectionId(collection.getId());
            partyList = populatePartyBean(ps);
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("ands.md.registration.check.existed.parties.failed"));
            setNavAfterExc();
            return ERROR;
        }
        //Get Licence

        try {
            if (collection.isFunded()) {
                this.licence.setContents(this.configSetting.getPropValue(ConfigSettings.TERN_DATA_LICENCE));
            } else {
                this.licence.setLicenceType(LicenceType.USERDEFINED.type());
                Licence foundLicence = this.dmService.getLicenceByCollectionId(collection.getId());
                if (foundLicence != null) {
                    this.licence = foundLicence;
                }
            }
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("ands.md.registration.check.license.failed"));
            setNavAfterExc();
            return ERROR;
        }
        setNavAfterSuccess();
        return SUCCESS;
    }

    public String mdRegister() {

        setViewColDetailLink(ActConstants.VIEW_COLLECTION_DETAILS_ACTION);
        try {
            user = retrieveLoggedInUser();
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("ands.md.registration.get.user.failed"));
            setNavAfterExc();
            return ERROR;
        }

        //user not logged in
        if (user == null) {
            logger.error(getText("ands.md.registration.permission.denied"));
            addActionError(getText("ands.md.registration.permission.denied"));
            setNavAfterExc();
            return ERROR;
        }

        try {
            collection = this.dmService.getCollection(collection.getId(), collection.getOwner().getId());
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("ands.md.registration.get.collection.failed"));
            setNavAfterExc();
            return ERROR;
        }
        //check the unique key for this collection
        String existedUniqueKey = collection.getUniqueKey();
        if (StringUtils.isBlank(existedUniqueKey)) {
            try {
                String uuidKey = pidService.genUUIDWithPrefix();
                collection.setUniqueKey(uuidKey);
            } catch (Exception e) {
                logger.error(e);
                addActionError(getText("ands.md.registration.create.unique.key.failed"));
                setNavAfterExc();
                return INPUT;
            }
        }

        //only the owner and system admin can publish this collection
        if ((user.getId() != collection.getOwner().getId()) && (user.getUserType() != UserType.ADMIN.code() && (user.getUserType() != UserType.SUPERADMIN.code()))) {
            logger.error(getText("ands.md.registration.none.owner.or.admin.permission.denied"));
            addActionError(getText("ands.md.registration.none.owner.or.admin.permission.denied"));
            setNavAfterExc();
            return ERROR;
        }
        //if any error, just return
        if (validateMetadataReg()) {
            return INPUT;
        }

        // populate the url of this collection
        String serverQName = getServerQName();
        String appContext = getAppContextPath();
        StringBuffer collectionUrl = new StringBuffer();
        collectionUrl.append(serverQName).append(appContext).append(ActConstants.URL_PATH_DEIM);
        collectionUrl.append("pub/viewColDetails.jspx?collection.id=" + collection.getId() + "&collection.owner.id=" + collection.getOwner().getId() + "&viewType=anonymous");
        // create handle if handle service is enabled
        String hdlEnabledStr = configSetting.getPropValue(ConfigSettings.HANDLE_SERVICE_ENABLED);
        String existedHandle = collection.getPersistIdentifier();

        // if no existed hanlde. it will be created if handle ws is enabaled
        if (existedHandle == null) {
            if (Boolean.valueOf(hdlEnabledStr)) {
                try {
                    String handle = pidService.genHandleIdentifier(collectionUrl.toString());
                    // String hdlResolver = configSetting.getPropValue(ConfigSettings.HANDLE_RESOLVER_SERVER);
                    // collection.setPersistIdentifier(hdlResolver + "/" + handle);
                    collection.setPersistIdentifier(handle);
                } catch (Exception e) {
                    logger.error(e);
                    addActionError(getText("ands.md.registration.create.handle.failed"));
                    setNavAfterExc();
                    return INPUT;
                }
            } else {
                collection.setPersistIdentifier(collection.getUniqueKey());
            }
        }

        try {
            MetadataRegistrationBean mdRegBean = createMdRegistrationBean(serverQName, collectionUrl.toString());
            this.dmService.publishRifcs(mdRegBean);
            // save the metadata registration auditing info
            recordMDRegAuditEvent();
        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("ands.md.registration.register.failed"));
            setNavAfterExc();
            return INPUT;
        }
        addActionMessage(getText("ands.md.registration.register.success"));
        setActionSuccessMsg(getText("ands.md.registration.register.success"));
        setNavAfterSuccess();
        return SUCCESS;
    }

    // save the auditing information for metadata registration
    private void recordMDRegAuditEvent() {
        AuditEvent ev = new AuditEvent();
        ev.setCreatedTime(GregorianCalendar.getInstance().getTime());
        ev.setEvent(getText("ands.md.registration.audit.info", new String[]{collection.getName()}));
        ev.setEventOwner(collection.getOwner());
        ev.setOperator(user);
        recordActionAuditEvent(ev);
    }

    private MetadataRegistrationBean createMdRegistrationBean(String serverName, String collectionUrl) {

        MetadataRegistrationBean mdRegBean = new MetadataRegistrationBean();
        mdRegBean.setCollection(this.collection);
        mdRegBean.setPartyList(this.partyList);
        //get the licence since the licence is saved before the metadata registration
        this.licence = this.dmService.getLicenceByCollectionId(collection.getId());
        mdRegBean.setLicence(this.licence);
        //rifcs store location
        mdRegBean.setRifcsStoreLocation(configSetting.getPropValue(ConfigSettings.ANDS_RIFCS_STORE_LOCATION));
        //physical address of collection
        mdRegBean.setPhysicalAddress(configSetting.getPropValue(ConfigSettings.DATA_COLLECTIONS_PHYSICAL_LOCATION));
        //collection rifcs template
        mdRegBean.setRifcsCollectionTemplate(configSetting.getPropValue(ConfigSettings.RIFCS_COLLECTION_TEMPLATE));
        //none rm party rifcs template
        mdRegBean.setRifcsPartyTemplate(configSetting.getPropValue(ConfigSettings.RIFCS_NONE_RM_PARTY_TEMPLATE));
        //rm party rifcs template
        mdRegBean.setRifcsRMPartyTemplate(configSetting.getPropValue(ConfigSettings.RIFCS_RM_PARTY_TEMPLATE));
        //group name
        mdRegBean.setRifcsGroupName(configSetting.getPropValue(ConfigSettings.ANDS_RIFCS_REG_GROUP_NAME));
        //collection url
        mdRegBean.setCollectionUrl(CaptureUtil.replaceURLAmpsands(collectionUrl));
        //server name
        mdRegBean.setAppName(serverName);

        return mdRegBean;
    }

    public boolean validateMetadataReg() {
        boolean hasError = false;
        boolean atLeastOnePartySelected = false;
        if (partyList != null) {
            for (PartyBean ptb : partyList) {
                if (ptb.isSelected()) {
                    atLeastOnePartySelected = true;
                }
            }
        }
        if (!atLeastOnePartySelected) {
            addFieldError("partyRequired", getText("ands.md.registration.party.required"));
            hasError = true;
        }
        if (StringUtils.isBlank(licence.getContents())) {
            addFieldError("licence", getText("ands.md.registration.licence.required"));
            hasError = true;
        }
        // set navigations
        if (hasError) {
            setNavAfterExc();
            setViewColDetailLink(ActConstants.VIEW_COLLECTION_DETAILS_ACTION);
        }
        return hasError;
    }

    //populate the PartyBeans
    private List<PartyBean> populatePartyBean(List<Party> parties) {
        List<PartyBean> partyBeans = new ArrayList<PartyBean>();
        if (parties != null && parties.size() > 0) {
            for (Party party : parties) {
                PartyBean existedPartyBean = copyPartyToPartyBean(party);
                existedPartyBean.setSelected(true);
                partyBeans.add(existedPartyBean);
            }
        }
        return partyBeans;
    }

    //copy the Party into PartyBean
    private PartyBean copyPartyToPartyBean(Party p) {
        PartyBean pb = new PartyBean();
        pb.setId(p.getId());
        pb.setPartyKey(p.getPartyKey());
        pb.setPersonTitle(p.getPersonTitle());
        pb.setPersonGivenName(p.getPersonGivenName());
        pb.setPersonFamilyName(p.getPersonFamilyName());
        pb.setDescription(p.getDescription());
        pb.setUrl(p.getUrl());
        pb.setEmail(p.getEmail());
        pb.setAddress(p.getAddress());
        pb.setIdentifierType(p.getIdentifierType());
        pb.setIdentifierValue(p.getIdentifierValue());
        pb.setOriginateSourceType(p.getOriginateSourceType());
        pb.setOriginateSourceValue(p.getOriginateSourceValue());
        pb.setGroupName(p.getGroupName());
        pb.setFromRm(p.isFromRm());
        return pb;
    }

    private void setNavAfterSuccess() {
        String startNav = null;
        String startNavLink = null;
        String secondNav = collection.getName();
        String secondNavLink = null;
        String thirdNav = getText("ands.md.registration.title");
        if (viewType != null) {
            if (viewType.equals(UserViewType.USER.type())) {
                startNav = getText("mycollection.nav.label.name");
                startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
                secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
                        + collection.getOwner().getId() + "&viewType=" + viewType;
            }
            if (viewType.equals(UserViewType.ALL.type())) {
                startNav = getText("allcollection.nav.label.name");
                startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
                secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&amp;collection.owner.id="
                        + collection.getOwner().getId() + "&amp;viewType=" + viewType;
            }
            // set the new page title after successful creating a new collection.
            setPageTitle(startNav, secondNav + " - " + thirdNav);
            navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, thirdNav, null);
        }
    }

    private void setNavAfterExc() {

        String startNav = null;
        String startNavLink = null;

        //TODO: if collection is null?
        String secondNav = collection.getName();
        String secondNavLink = null;
        String thirdNav = getText("ands.md.registration.title");
        if (viewType != null) {
            if (viewType.equals(UserViewType.USER.type())) {
                startNav = getText("mycollection.nav.label.name");
                startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
                secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
                        + collection.getOwner().getId() + "&viewType=" + viewType;
            }
            if (viewType.equals(UserViewType.ALL.type())) {
                startNav = getText("allcollection.nav.label.name");
                startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
                secondNavLink = ActConstants.VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
                        + collection.getOwner().getId() + "&viewType=" + viewType;
            }
            // set the new page title after successful creating a new collection.
            setPageTitle(startNav, secondNav + " - " + thirdNav + " Error");
            navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, thirdNav, null);
        }
    }

    public RegisterActivity getActivity() {
        return activity;
    }

    public void setActivity(RegisterActivity activity) {
        this.activity = activity;
    }

    public Licence getLicence() {
        return licence;
    }

    public void setLicence(Licence licence) {
        this.licence = licence;
    }

    public List<PartyBean> getPartyList() {
        return partyList;
    }

    public void setPartyList(List<PartyBean> partyList) {
        this.partyList = partyList;
    }
}
