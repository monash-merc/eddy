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

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.config.SystemPropertiesConfigurer;
import au.edu.monash.merc.capture.domain.Party;
import au.edu.monash.merc.capture.dto.PartyBean;
import au.edu.monash.merc.capture.dto.ldap.LdapUser;
import au.edu.monash.merc.capture.exception.DataCaptureException;
import au.edu.monash.merc.capture.rifcs.PartyActivityWSService;
import au.edu.monash.merc.capture.util.CaptureUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 7/03/13 6:17 PM
 */
@Scope("prototype")
@Controller("data.partyAction")
public class PartyAction extends DMCoreAction {
    @Autowired
    private PartyActivityWSService paWsService;

    private PartyBean selectedPartyBean;

    private String searchCnOrEmail;

    private List<PartyBean> foundPartyBeans;

    private Map<String, String> organizations = new LinkedHashMap<String, String>();

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    @Qualifier("nlaIdPropertyConfigurer")
    private SystemPropertiesConfigurer nlaIdPropertyConfigurer;

    @PostConstruct
    public void init() {
        setOrganizationsMap();
    }

    public void setNlaIdPropertyConfigurer(SystemPropertiesConfigurer nlaIdPropertyConfigurer) {
        this.nlaIdPropertyConfigurer = nlaIdPropertyConfigurer;
    }

    private void setOrganizationsMap() {
        if (organizations == null || organizations.size() == 0) {
            Map<String, String> cMp = this.nlaIdPropertyConfigurer.getResolvedProps();
            organizations = CaptureUtil.sortByValue(cMp);
        }
    }


    public String showSearchParty() {
        return SUCCESS;
    }

    public String searchParty() {
        if (StringUtils.isBlank(searchCnOrEmail)) {
            addFieldError("cnoremail", getText("ands.md.registration.search.party.cnoremail.must.be.provided"));
            return INPUT;
        }

        try {
            foundPartyBeans = searchPartyFromDb(searchCnOrEmail);
            //if parties found, just return
            if (foundPartyBeans != null && foundPartyBeans.size() > 0) {
                return SUCCESS;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            addActionError(getText("ands.md.registration.search.party.failed"));
            return ERROR;
        }

        //if party not found, then we start to search from research master ws
        if (foundPartyBeans == null) {
            foundPartyBeans = new ArrayList<PartyBean>();
        }

        try {
            PartyBean pb = searchPartyFromRMWS(searchCnOrEmail);
            if (pb != null) {
                foundPartyBeans.add(pb);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            addActionError(getText("ands.md.registration.ws.failed") + ", Failed to find a researcher");
            return ERROR;
        }
        //if party not found, then we try to create it
        if (foundPartyBeans.size() == 0) {
            setActionSuccessMsg(getText("ands.md.registration.search.party.not.found"));
            return PNOTFOUND;
        }
        return SUCCESS;
    }

    private List<PartyBean> searchPartyFromDb(String cnOrEmail) {
        List<PartyBean> tempPbs = new ArrayList<PartyBean>();
        List<Party> foundParties = this.dmService.getPartyByUserNameOrEmail(cnOrEmail);

        if (foundParties != null && foundParties.size() > 0) {
            for (Party p : foundParties) {
                if (p.isFromRm()) {
                    //always to get the new researcher master party.
                    String emailOrCn = p.getEmail();
                    if (StringUtils.isBlank(emailOrCn)) {
                        emailOrCn = p.getPersonGivenName() + " " + p.getPersonFamilyName();
                    }
                    PartyBean rmPartyBean = searchPartyFromRMWS(emailOrCn);
                    if (rmPartyBean != null) {
                        tempPbs.add(rmPartyBean);
                    }
                } else {
                    PartyBean partyBean = copyPartyToPartyBean(p);
                    tempPbs.add(partyBean);
                }
            }
        }
        return tempPbs;
    }

    private PartyBean searchPartyFromRMWS(String cnOrEmail) {

        LdapUser ldapUser = null;
        try {
            ldapUser = userService.ldapLookup(cnOrEmail);
        } catch (Exception e) {
            throw new DataCaptureException(e);
        }

        if (ldapUser == null) {
            logger.error("can't find an researcher from ldap");
            return null;
        }

        //if found the LDAP user, then search the rm web service for the party
        try {
            String rmNlaId = paWsService.getNlaId(ldapUser.getUid());
            // get party
            PartyBean rmPartyBean = paWsService.getParty(rmNlaId);
            return rmPartyBean;
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (StringUtils.containsIgnoreCase(errorMsg, "NLA Id not found") || StringUtils.containsIgnoreCase(errorMsg, "Invalid authcate username")) {
                logger.error(errorMsg);
                return null;
            } else {
                throw new DataCaptureException(e);
            }
        }
    }


    public String selectParty() {
        if (selectedPartyHasErrors()) {
            return INPUT;
        }
        for (PartyBean pb : foundPartyBeans) {
            if (pb.isSelected()) {
                selectedPartyBean = pb;
            }
        }

        try {
            Party foundParty = this.dmService.getPartyByPartyKey(selectedPartyBean.getPartyKey());
            Party party = copyPartyBeanToParty(selectedPartyBean);
            //if this party is not saved, normally it comes from the researcher master ws.
            //and if this party is a researcher master party, we try to update it.
            if (foundParty == null) {
                this.dmService.saveParty(party);
            } else {
                if (foundParty.isFromRm()) {
                    party.setId(foundParty.getId());
                    this.dmService.updateParty(party);
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
            addActionError(getText("ands.add.party.failed.to.save.the.selected.party"));
            return ERROR;
        }
        return SUCCESS;
    }

    private boolean selectedPartyHasErrors() {
        int totalSelectedCounter = 0;
        for (PartyBean pb : foundPartyBeans) {
            if (pb.isSelected()) {
                totalSelectedCounter++;
            }
        }
        if (totalSelectedCounter == 0) {
            addFieldError("nopartyselected", getText("ands.md.registration.add.party.one.party.must.be.selected"));
            return true;
        }
        if (totalSelectedCounter > 1) {
            addFieldError("morepartyselected", getText("ands.md.registration.add.party.only.one.party.needed"));
            return true;
        }
        return false;
    }


    private PartyBean copyPartyToPartyBean(Party p) {
        PartyBean pb = new PartyBean();
        pb.setId(p.getId());
        pb.setPartyKey(p.getPartyKey());
        pb.setPersonTitle(p.getPersonTitle());
        pb.setPersonGivenName(p.getPersonGivenName());
        pb.setPersonFamilyName(p.getPersonFamilyName());
        pb.setUrl(p.getUrl());
        pb.setEmail(p.getEmail());
        pb.setAddress(p.getAddress());
        pb.setIdentifierType(p.getIdentifierType());
        pb.setIdentifierValue(p.getIdentifierValue());
        pb.setOriginateSourceType(p.getOriginateSourceType());
        pb.setOriginateSourceValue(p.getOriginateSourceValue());
        pb.setDescription(p.getDescription());
        String groupKey = p.getGroupKey();
        //if the party is not a research master party and doesn't have a group key, we set it a default ozflux key
        if (!p.isFromRm() && StringUtils.isBlank(groupKey)) {
            groupKey = "-1";
        }
        pb.setGroupKey(groupKey);
        pb.setGroupName(p.getGroupName());
        pb.setFromRm(p.isFromRm());
        return pb;
    }

    private Party copyPartyBeanToParty(PartyBean pb) {
        Party party = new Party();
        if (pb.getId() != 0) {
            party.setId(pb.getId());
        }
        party.setPartyKey(pb.getPartyKey());
        party.setPersonTitle(pb.getPersonTitle());
        party.setPersonGivenName(pb.getPersonGivenName());
        party.setPersonFamilyName(pb.getPersonFamilyName());
        party.setUrl(pb.getUrl());
        party.setEmail(pb.getEmail());
        party.setAddress(pb.getAddress());
        party.setIdentifierType(pb.getIdentifierType());
        party.setIdentifierValue(pb.getIdentifierValue());
        party.setOriginateSourceType(pb.getOriginateSourceType());
        party.setOriginateSourceValue(pb.getOriginateSourceValue());
        party.setDescription(pb.getDescription());
        String groupKey = pb.getGroupKey();
        party.setGroupKey(groupKey);
        //set the group key and group name first
        party.setGroupKey(pb.getGroupKey());
        party.setGroupName(pb.getGroupName());
        boolean fromRM = pb.isFromRm();
        party.setFromRm(fromRM);
        //if party is not from researcher master, then we have to adjust it
        if (!fromRM) {
            if (groupKey.equals("-1")) {
                party.setGroupName(this.configSetting.getPropValue(ConfigSettings.ANDS_RIFCS_REG_GROUP_NAME));
            } else {
                String groupName = organizations.get(groupKey);
                party.setGroupName(groupName);
            }
        }
        return party;
    }

    private void setSearchPartyValueToPartyBean(String searchValue) {
        selectedPartyBean = new PartyBean();
        selectedPartyBean.setGroupKey("-1");
        selectedPartyBean.setGroupName(organizations.get("-1"));
        if (StringUtils.isNotBlank(searchValue)) {
            if (StringUtils.contains(searchValue, "@")) {
                selectedPartyBean.setEmail(searchValue);
            } else {
                String[] names = StringUtils.split(searchValue, " ");
                if (names != null && names.length >= 2) {
                    String firstName = names[0];
                    String lastName = names[1];
                    selectedPartyBean.setPersonGivenName(firstName);
                    selectedPartyBean.setPersonFamilyName(lastName);
                }
                if (names != null && names.length == 1) {
                    String firstName = names[0];
                    selectedPartyBean.setPersonGivenName(firstName);
                }
            }
        }
    }

    public String showAddUDParty() {
        setSearchPartyValueToPartyBean(searchCnOrEmail);
        return SUCCESS;
    }

    public String addUDParty() {

        if (selectedPartyBean != null) {
            try {
                Party p = dmService.getPartyByEmail(selectedPartyBean.getEmail());
                if (p != null) {
                    logger.error(getText("ands.md.registration.add.party.already.existed"));
                    addActionError(getText("ands.md.registration.add.party.already.existed"));
                    return INPUT;
                }
            } catch (Exception ex) {
                logger.error("failed to get a party, " + ex.getMessage());
                addActionError(getText("ands.md.registration.add.party.failed"));
                return INPUT;
            }

            try {
                PartyBean partyBean = searchPartyFromRMWS(selectedPartyBean.getEmail());
                if (partyBean != null) {
                    logger.error(getText("ands.md.registration.add.party.already.existed"));
                    addActionError(getText("ands.md.registration.add.party.already.existed"));
                    return INPUT;
                }
            } catch (Exception ex) {
                logger.error("failed to search a party from research master web service, " + ex.getMessage());
                addActionError(getText("ands.md.registration.add.party.failed"));
                return INPUT;
            }
        }
        try {
            //create a new party
            Party p = copyPartyBeanToParty(selectedPartyBean);
            String localKey = pidService.genUUIDWithPrefix();
            p.setPartyKey(localKey);
            p.setIdentifierValue(localKey);
            p.setIdentifierType("local");
            p.setOriginateSourceType("authoritative");

            this.dmService.saveParty(p);
            selectedPartyBean = copyPartyToPartyBean(p);
        } catch (Exception e) {
            logger.error("failed to save party, " + e.getMessage());
            addActionError(getText("ands.md.registration.add.party.failed"));
            return INPUT;
        }
        // just return
        return SUCCESS;
    }

    public String showEditUDParty() {
        try {
            Party p = dmService.getPartyByPartyKey(selectedPartyBean.getPartyKey());
            if (p != null) {
                selectedPartyBean = copyPartyToPartyBean(p);
            } else {
                addActionError(getText("ands.md.registration.edit.party.not.existed"));
                return ERROR;
            }
        } catch (Exception ex) {
            addActionError(getText("ands.md.registration.edit.party.get.party.failed"));
            return ERROR;
        }
        return SUCCESS;
    }

    public String updateUDParty() {
        try {
            if (StringUtils.isBlank(selectedPartyBean.getPartyKey())) {
                addFieldError("partyKey", getText("ands.md.registration.edit.party.key.must.be.provided"));
                return INPUT;
            }
            Party party = copyPartyBeanToParty(selectedPartyBean);
            this.dmService.updateParty(party);
            selectedPartyBean = copyPartyToPartyBean(party);
        } catch (Exception ex) {
            addActionError(getText("ands.md.registration.edit.party.failed"));
            return ERROR;
        }
        return SUCCESS;
    }


    public String getSearchCnOrEmail() {
        return searchCnOrEmail;
    }

    public void setSearchCnOrEmail(String searchCnOrEmail) {
        this.searchCnOrEmail = searchCnOrEmail;
    }

    public PartyBean getSelectedPartyBean() {
        return selectedPartyBean;
    }

    public void setSelectedPartyBean(PartyBean selectedPartyBean) {
        this.selectedPartyBean = selectedPartyBean;
    }

    public List<PartyBean> getFoundPartyBeans() {
        return foundPartyBeans;
    }

    public void setFoundPartyBeans(List<PartyBean> foundPartyBeans) {
        this.foundPartyBeans = foundPartyBeans;
    }

    public Map<String, String> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Map<String, String> organizations) {
        this.organizations = organizations;
    }
}
