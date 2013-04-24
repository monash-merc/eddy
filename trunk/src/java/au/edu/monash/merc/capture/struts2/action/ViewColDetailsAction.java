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
package au.edu.monash.merc.capture.struts2.action;

import au.edu.monash.merc.capture.common.UserType;
import au.edu.monash.merc.capture.common.UserViewType;
import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.RestrictAccess;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.GregorianCalendar;

@Scope("prototype")
@Controller("data.viewColDetailsAction")
public class ViewColDetailsAction extends DMCoreAction {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private boolean mdRegEnabled;

    private String redActionName;

    private String redNamespace;

    private static String REDIRECT_ACTION_NAME = "viewColDetails";

    private static String REDIRECT_NAMESPACE = "/data";

    public String viewCollectionDetail() {

        try {
            // check the permission.
            try {
                permissionBean = checkPermission(collection.getId(), collection.getOwner().getId());
            } catch (Exception e) {
                addFieldError("checkPermission", getText("check.permissions.error"));
                setNavAfterException();
                return INPUT;
            }

            // check if the request url is a view collection details with the pub namespace:
            // and whether an user logined in already, then we redirect it to view collection details by login user
            // long userId = getLoginUsrIdFromSession();
            if (user != null && viewType.equals(UserViewType.ANONYMOUS.type())) {
                redNamespace = REDIRECT_NAMESPACE;
                redActionName = REDIRECT_ACTION_NAME;
                if (user.getId() == collection.getOwner().getId()) {
                    viewType = UserViewType.USER.type();
                } else {
                    viewType = UserViewType.ALL.type();
                }
                return REDIRECTCO;
            }

//            // if no permission for anonymose
//            if (viewType.equals(ActConstants.UserViewType.ANONYMOUS.toString())) {
//                if (!permissionBean.isViewAllowed()) {
//                    addFieldError("viewPermission", getText("failed.to.get.collection.permission.denied"));
//                    setNavAfterException();
//                    return INPUT;
//                }
//            }

            collection = this.dmService.getCollection(collection.getId(), collection.getOwner().getId());
            if (collection != null) {
                // convert any newline in the description into a br html tag
                String textAreaDesc = collection.getDescription();
                String htmlDesc = nlToBr(textAreaDesc);
                collection.setDescription(htmlDesc);

//                // check the view permissions
//                if (!permissionBean.isViewAllowed()) {
//                    setActionSuccessMsg(getText("no.permissions.to.view.collection"));
//                    setNavAfterSuccess();
//                    return SUCCESS;
//                }

                // populate the list dataset in this user collection.
                // datasets = this.dmService.getDatasetByCollectionIdUsrId(collection.getId(), collection.getOwner().getId());
                retrieveAllDatasets();
                // populate the collection links
                if (viewType.equals(UserViewType.ANONYMOUS.type())) {
                    populateLinksInPubCollection();
                } else {
                    populateLinksInUsrCollection();
                }

                // populate the rifcs registration if enabled
                String mdRegEnabledStr = configSetting.getPropValue(ConfigSettings.ANDS_RIFCS_REG_ENABLED);
                mdRegEnabled = Boolean.valueOf(mdRegEnabledStr).booleanValue();

                //The owner of a collection or an admin they can register the metadata
                if (user != null && mdRegEnabled) {
                    if ((user.getId() == collection.getOwner().getId()) || (user.getUserType() == UserType.ADMIN.code()) || (user.getUserType() == UserType.SUPERADMIN.code())) {
                        permissionBean.setMdRegAllowed(true);
                    }
                }
                // set page title and nav label
                setNavAfterSuccess();

            } else {
                addActionError(getText("failed.to.get.collection.not.exist"));
                setNavAfterException();
                return ERROR;
            }

        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("failed.to.get.collection.details"));
            setNavAfterException();
            return ERROR;
        }

        return SUCCESS;
    }

    public void validateViewCollectionDetail() {

        boolean hasError = false;
        if (collection == null) {
            addFieldError("collectionId", getText("invalid.collection.id"));
            addFieldError("ownerId", getText("invalid.collection.owner.id"));
            hasError = true;
        }
        if (collection.getId() <= 0) {
            addFieldError("collectionId", getText("invalid.collection.id"));
            hasError = true;
        }
        if (collection.getOwner().getId() <= 0) {
            addFieldError("ownerId", getText("invalid.collection.owner.id"));
            hasError = true;
        }
        if (StringUtils.isBlank(viewType)) {
            addFieldError("viewType", getText("view.type.must.be.provided"));
            hasError = true;
        }

        if (hasError) {
            setNavAfterException();
        }

    }

    private void setNavAfterException() {

        String startNav = null;
        String startNavLink = null;
        String secondNav = getText("view.collection.error");

        if (viewType != null) {
            if (viewType.equals(UserViewType.USER.type())) {
                startNav = getText("mycollection.nav.label.name");
                startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
            }
            if (viewType.equals(UserViewType.ALL.type())) {
                startNav = getText("allcollection.nav.label.name");
                startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
            }
            if (viewType.equals(UserViewType.ANONYMOUS.type())) {
                startNav = getText("pubcollection.nav.label.name");
                startNavLink = ActConstants.PUB_LIST_COLLECTION_ACTION;
            }
            setPageTitle(startNav, secondNav);
            navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
        }
    }

    private void setNavAfterSuccess() {

        String startNav = null;
        String startNavLink = null;

        String secondNav = collection.getName();
        String secondNavLink = null;
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
            if (viewType.equals(UserViewType.ANONYMOUS.type())) {
                startNav = getText("pubcollection.nav.label.name");
                startNavLink = ActConstants.PUB_LIST_COLLECTION_ACTION;
                secondNavLink = ActConstants.PUB_VIEW_COLLECTION_DETAILS_ACTION + "?collection.id=" + collection.getId() + "&collection.owner.id="
                        + collection.getOwner().getId() + "&viewType=" + viewType;
            }
            // set the new page title after successful creating a new collection.
            setPageTitle(startNav, secondNav);

            navigationBar = generateNavLabel(startNav, startNavLink, secondNav, secondNavLink, null, null);
        }
    }

    public boolean isMdRegEnabled() {
        return mdRegEnabled;
    }

    public void setMdRegEnabled(boolean mdRegEnabled) {
        this.mdRegEnabled = mdRegEnabled;
    }

    public String getRedActionName() {
        return redActionName;
    }

    public void setRedActionName(String redActionName) {
        this.redActionName = redActionName;
    }

    public String getRedNamespace() {
        return redNamespace;
    }

    public void setRedNamespace(String redNamespace) {
        this.redNamespace = redNamespace;
    }
}
