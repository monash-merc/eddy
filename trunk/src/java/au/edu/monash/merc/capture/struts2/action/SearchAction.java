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

import au.edu.monash.merc.capture.common.UserViewType;
import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.Dataset;
import au.edu.monash.merc.capture.dto.AttributeBean;
import au.edu.monash.merc.capture.dto.SearchBean;
import au.edu.monash.merc.capture.dto.VariableBean;
import au.edu.monash.merc.capture.dto.page.Pagination;
import au.edu.monash.merc.capture.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

@Scope("prototype")
@Controller("search.searchAction")
public class SearchAction extends DMCoreAction {

    @Autowired
    private SearchService searchService;

    private Map<String, String> comparisions = new HashMap<String, String>();

    private Map<String, String> dsLevels = new HashMap<String, String>();

    private SearchBean searchBean;

    private boolean searched;

    private Pagination<Dataset> dsPagination;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public String showSearch() {
        try {
            setNav();
            user = retrieveLoggedInUser();
            searchPreProcess();
        } catch (Exception e) {
            logger.error(e);
            addActionError("failed.to.show.search.page");
            return ERROR;
        }
        return SUCCESS;
    }

    private void searchPreProcess() {
        searchBean = new SearchBean();
        removeFromSession(ActConstants.SEARCH_CONDITION_KEY);
        // populateComparisions();
        populateDSLevels();
    }

    // private void populateComparisions() {
    // comparisions.put("equals", "equals");
    // comparisions.put("likes", "likes");
    // }

    private void populateDSLevels() {
        dsLevels.put("L1", "L1");
        dsLevels.put("L2", "L2");
        dsLevels.put("L3", "L3");
        dsLevels.put("L4", "L4");
    }

    public String search() {
        try {
            // set the page title and nav label.
            setNav();
            long uid = getLoginUsrIdFromSession();
            if (uid == 0) {
                viewType = UserViewType.ANONYMOUS.type();
                setViewColDetailActionName(ActConstants.PUB_VIEW_COLLECTION_DETAILS_ACTION);
            } else {
                user = retrieveLoggedInUser();
                viewType = UserViewType.ALL.type();
                setViewColDetailActionName(ActConstants.VIEW_COLLECTION_DETAILS_ACTION);
            }

            // if no search bean instance, then check it from the session
            if (searchBean == null) {
                searchBean = (SearchBean) findInSession(ActConstants.SEARCH_CONDITION_KEY);
                if (searchBean == null) {
                    searchBean = new SearchBean();
                }
            }
            // check the search condition errors
            if (checkSearchErrors()) {
                searchPostProcess();
                return INPUT;
            }
            // normalize the search condition
            nomalizeSearchCons();

            if (searchBean.isCollectionOnly()) {
                populateCoPageSortParams();
                persistPageSortParamsInSession(ActConstants.SESSION_SEARCH_PAGE_SIZE, ActConstants.SESSION_SEARCH_ORDERBY,
                        ActConstants.SESSION_SEARCH_ORDERBY_TYPE, ActConstants.OrderByActionType.SEARCHCO.actionType());
                pagination = this.searchService.searchCo(searchBean, pageNo, sizePerPage, populateOrderBy());

            } else {
                populateDatasetPageParams();
                persistPageSortParamsInSession(ActConstants.SESSION_SEARCH_PAGE_SIZE, ActConstants.SESSION_SEARCH_ORDERBY,
                        ActConstants.SESSION_SEARCH_ORDERBY_TYPE, ActConstants.OrderByActionType.SEARCHDS.actionType());
                dsPagination = this.searchService.searchDs(searchBean, pageNo, sizePerPage, populateOrderBy());

            }
            // set the pagingation link
            setPaginationLinks(ActConstants.SEARCH_ACTION, ActConstants.PAGINATION_SUFFUX);

            saveInSession(ActConstants.SEARCH_CONDITION_KEY, searchBean);
            // searching post-processing for set the search conditions
            searchPostProcess();
            // set the searched flag to true
            searched = true;
        } catch (Exception e) {
            logger.error(e);
            searchPostProcess();
            addActionError(getText("search.collection.or.dataset.failed"));
            return ERROR;
        }
        return SUCCESS;
    }

    private boolean checkSearchErrors() {

        String dsLevelSearch = configSetting.getPropValue(ConfigSettings.DATASET_LEVEL_SEARCH_ENABLE);
        boolean dsLevelEnable = Boolean.valueOf(dsLevelSearch);

        if (dsLevelEnable) {
            //for ecosystem
            if (StringUtils.isBlank(searchBean.getResearcherName()) && StringUtils.isBlank(searchBean.getCollectionName())
                    && (searchBean.getStartDate() == null) && (searchBean.getEndDate() == null) && StringUtils.isBlank(searchBean.getSiteName())
                    && StringUtils.isBlank(searchBean.getDatasetName()) && StringUtils.equals(searchBean.getDatasetLevel(), "L0")) {
                addFieldError("searchConditionError", getText("at.least.one.search.condition.required"));
                return true;
            }
        } else {
            //for weather and climate
            if (StringUtils.isBlank(searchBean.getResearcherName()) && StringUtils.isBlank(searchBean.getCollectionName())
                    && (searchBean.getStartDate() == null) && (searchBean.getEndDate() == null) && StringUtils.isBlank(searchBean.getSiteName())
                    && StringUtils.isBlank(searchBean.getDatasetName())) {
                addFieldError("searchConditionError", getText("at.least.one.search.condition.required"));
                return true;
            }
        }

        Date startDate = searchBean.getStartDate();
        Date endDate = searchBean.getEndDate();
        if (startDate != null && endDate != null)

        {
            if (startDate.compareTo(endDate) > 0) {
                addFieldError("invalidStartEndDate", getText("search.started.datetime.must.be.not.greater.than.end.datetime"));
                return true;
            }
        }

        return false;
    }

    private void nomalizeSearchCons() {

        // if end time is not null, we have to set the max time of that day.
        if (searchBean.getEndDate() != null) {
            Date endDate = searchBean.getEndDate();
            searchBean.setEndDate(normalizeDate(endDate));
        }

        // remove the default dataset level L0
        String level = searchBean.getDatasetLevel();
        if (StringUtils.equals(level, "L0")) {
            searchBean.setDatasetLevel(null);
        }

        // if no dataset level query value available, then search collection only
        if (StringUtils.isBlank(searchBean.getSiteName()) && StringUtils.isBlank(searchBean.getDatasetName())
                && StringUtils.isBlank(searchBean.getDatasetLevel())) {
            searchBean.setCollectionOnly(true);
        }
    }

    private void searchPostProcess() {
        // populateComparisions();
        populateDSLevels();
    }

    private void setPaginationLinks(String paginationActionName, String paginationSuffix) {
        setPageLink(paginationActionName);
        setPageSuffix(paginationSuffix);
    }

    private void setNav() {
        String startNav = getText("search.home.action.title");
        String startNavLink = ActConstants.SHOW_SEARCH_ACTION;
        setPageTitle(startNav);
        navigationBar = generateNavLabel(startNav, startNavLink, null, null, null, null);
    }

    // not used
    protected List<VariableBean> createEmptyVars() {
        // create an empty variable bean list for search bean
        List<VariableBean> varBeans = new ArrayList<VariableBean>();
        // create an empty variable
        VariableBean vb = new VariableBean();
        // create an empty attribute bean list
        List<AttributeBean> var_attBeans = new ArrayList<AttributeBean>();
        // create attribute bean
        AttributeBean ab = new AttributeBean();
        // add the attribute bean into attribute bean list set
        var_attBeans.add(ab);
        // set the attribute bean list into a variable
        vb.setAttBeans(var_attBeans);
        // set a variable into variable bean list
        varBeans.add(vb);
        return varBeans;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public Map<String, String> getComparisions() {
        return comparisions;
    }

    public void setComparisions(Map<String, String> comparisions) {
        this.comparisions = comparisions;
    }

    public SearchBean getSearchBean() {
        return searchBean;
    }

    public void setSearchBean(SearchBean searchBean) {
        this.searchBean = searchBean;
    }

    public boolean isSearched() {
        return searched;
    }

    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public Pagination<Dataset> getDsPagination() {
        return dsPagination;
    }

    public void setDsPagination(Pagination<Dataset> dsPagination) {
        this.dsPagination = dsPagination;
    }

    public Map<String, String> getDsLevels() {
        return dsLevels;
    }

    public void setDsLevels(Map<String, String> dsLevels) {
        this.dsLevels = dsLevels;
    }

}
