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

import au.edu.monash.merc.capture.domain.AuditEvent;
import au.edu.monash.merc.capture.dto.page.Pagination;

@Scope("prototype")
@Controller("admin.eventAction")
public class EventAction extends DMCoreAction {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private AuditEvent auditEvent;

	Pagination<AuditEvent> eventPagination;

	private String deleteEventLink;

	public String deleteEvent() {
		user = retrieveLoggedInUser();
		try {
			this.dmService.deleteEventByIdWithUserId(auditEvent.getId(), user.getId());
			getUserEvents();
			// set the delete user event action name.
			setDeleteEventActName();
			// set the navigation bar after action finished
			setActionSuccessMsg(getText("user.event.deleted.success"));
			setNavAfterSuccess();
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("delete.user.event.failed"));
			setNavAfterExc();
			return ERROR;
		}
		return SUCCESS;
	}

	public String listUserEvents() {
		try {
			// get user events in a pagination.
			getUserEvents();
			// set the delete user event action name.
			setDeleteEventActName();
			// set the navigation bar after action finished
			setNavAfterSuccess();
		} catch (Exception e) {
			logger.error(e);
			addActionError(getText("list.user.events.failed"));
			setNavAfterExc();
			return ERROR;
		}
		return SUCCESS;
	}

	// get user events in a pagination.
	private void getUserEvents() {
		populateEventPageParams();
		persistPageSortParamsInSession(ActConstants.SESSION_EVENTS_PAGE_SIZE, ActConstants.SESSION_EVENTS_ORDERBY,
				ActConstants.SESSION_EVENTS_ORDERBY_TYPE, ActConstants.OrderByActionType.EVENT.actionType());
		user = retrieveLoggedInUser();
		eventPagination = this.dmService.getEventByUserId(user.getId(), pageNo, sizePerPage, populateOrderBy());
		populatePaginationLinks(ActConstants.LIST_USER_EVENTS_ACTION, ActConstants.PAGINATION_SUFFUX);
	}

	private void setDeleteEventActName() {
		this.deleteEventLink = ActConstants.DELETE_USER_EVENT_ACTION;
	}

	private void setNavAfterSuccess() {
		String startNav = getText("user.events.action.title");
		String startNavLink = ActConstants.LIST_USER_EVENTS_ACTION;
		setPageTitle(startNav);
		navigationBar = generateNavLabel(startNav, startNavLink, null, null, null, null);
	}

	private void setNavAfterExc() {
		String startNav = getText("user.events.action.title");
		setPageTitle(startNav + " Error");
		navigationBar = generateNavLabel(startNav, null, null, null, null, null);
	}

	public AuditEvent getAuditEvent() {
		return auditEvent;
	}

	public void setAuditEvent(AuditEvent auditEvent) {
		this.auditEvent = auditEvent;
	}

	public Pagination<AuditEvent> getEventPagination() {
		return eventPagination;
	}

	public void setEventPagination(Pagination<AuditEvent> eventPagination) {
		this.eventPagination = eventPagination;
	}

	public String getDeleteEventLink() {
		return deleteEventLink;
	}

	public void setDeleteEventLink(String deleteEventLink) {
		this.deleteEventLink = deleteEventLink;
	}

}
