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
package au.edu.monash.merc.capture.struts2.action;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.struts2.action.ActConstants;

@Scope("prototype")
@Controller("data.listAllColAction")
public class ListAllCollectionsAction extends DMCoreAction {
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public String listAllCollections() {

		try {
			user = retrieveLoggedInUser();
			listCollections(ActConstants.UserViewType.ALL.toString());
			setNavAfterSuccess();
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("failed.to.list.all.collections"));
			setNavAfterException();
			return INPUT;
		}
		return SUCCESS;
	}

	public String listUserCollections() {
		try {
			user = retrieveLoggedInUser();
			listCollections(ActConstants.UserViewType.USER.toString());
			setNavAfterSuccess();
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("failed.to.list.all.collections"));
			setNavAfterException();
			return INPUT;
		}

		return SUCCESS;
	}

	public String listPubCollections() {
		try {
			listCollections(ActConstants.UserViewType.ANONYMOUS.toString());
			setNavAfterSuccess();
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("failed.to.list.all.collections"));
			setNavAfterException();
			return INPUT;
		}
		return SUCCESS;
	}

	private void listCollections(String userViewType) {
		// populate the view options params values to sort the pagination.
		populateCoPageSortParams();
		// persist the view page size, orderby and orderby type value in the session
		if (userViewType.equals(ActConstants.UserViewType.ANONYMOUS.toString())) {// for anonymous user
			persistPageSortParamsInSession(ActConstants.SESSION_VIEW_COLLECTION_PAGE_SIZE, ActConstants.SESSION_VIEW_COLLECTION_ORDERBY,
					ActConstants.SESSION_VIEW_COLLECTION_ORDERBY_TYPE, ActConstants.OrderByActionType.CO.actionType());
			// list all public collections from the database
			pagination = this.dmService.getAllPublicCollections(pageNo, sizePerPage, populateOrderBy());

			populatePaginationLinks(ActConstants.PUB_LIST_COLLECTION_ACTION, ActConstants.PAGINATION_SUFFUX);
			viewType = ActConstants.UserViewType.ANONYMOUS.toString();
			setViewColDetailActionName(ActConstants.PUB_VIEW_COLLECTION_DETAILS_ACTION);

		} else {// for logged in user
			persistPageSortParamsInSession(ActConstants.SESSION_VIEW_COLLECTION_PAGE_SIZE, ActConstants.SESSION_VIEW_COLLECTION_ORDERBY,
					ActConstants.SESSION_VIEW_COLLECTION_ORDERBY_TYPE, ActConstants.OrderByActionType.CO.actionType());
			if (userViewType.equals(ActConstants.UserViewType.USER.toString())) {
				long userId = getLoginUsrIdFromSession();
				pagination = this.dmService.getCollectionsByUserId(userId, pageNo, sizePerPage, populateOrderBy());
				// populate the pagination links and collection details link
				populatePaginationLinks(ActConstants.USER_LIST_COLLECTION_ACTION, ActConstants.PAGINATION_SUFFUX);
				viewType = ActConstants.UserViewType.USER.toString();

			} else if (userViewType.equals(ActConstants.UserViewType.ALL.toString())) {
				// get all collection in a pagination
				pagination = this.dmService.getAllCollections(pageNo, sizePerPage, populateOrderBy());
				// populate the pagination links and collection details link
				populatePaginationLinks(ActConstants.LIST_ALL_COLLECTIONS_ACTION, ActConstants.PAGINATION_SUFFUX);
				// set the view type for all
				viewType = ActConstants.UserViewType.ALL.toString();
			}
			setViewColDetailActionName(ActConstants.VIEW_COLLECTION_DETAILS_ACTION);
		}
	}

	private void setNavAfterException() {
		String startNav = null;
		if (viewType != null) {
			if (viewType.equals(ActConstants.UserViewType.USER.toString())) {
				startNav = getText("mycollection.nav.label.name");
			} else {
				startNav = getText("allcollection.nav.label.name");
			}
		}
		String secondNav = getText("list.all.collections.error");
		setPageTitle(startNav, secondNav);
		navigationBar = generateNavLabel(startNav, null, secondNav, null, null, null);
	}

	private void setNavAfterSuccess() {
		String startNav = null;
		String startNavLink = null;
		if (viewType.equals(ActConstants.UserViewType.USER.toString())) {
			startNav = getText("mycollection.nav.label.name");
			startNavLink = ActConstants.USER_LIST_COLLECTION_ACTION;
		}
		if (viewType.equals(ActConstants.UserViewType.ALL.toString())) {
			startNav = getText("allcollection.nav.label.name");
			startNavLink = ActConstants.LIST_ALL_COLLECTIONS_ACTION;
		}
		if (viewType.equals(ActConstants.UserViewType.ANONYMOUS.toString())) {
			startNav = getText("pubcollection.nav.label.name");
			startNavLink = ActConstants.PUB_LIST_COLLECTION_ACTION;
		}
		String secondNav = getText("list.all.collections");
		setPageTitle(startNav, secondNav);
		navigationBar = generateNavLabel(startNav, startNavLink, secondNav, null, null, null);
	}

}
