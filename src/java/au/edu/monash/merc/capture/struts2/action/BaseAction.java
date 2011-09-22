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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;

import au.edu.monash.merc.capture.config.ConfigSettings;
import au.edu.monash.merc.capture.domain.User;
import au.edu.monash.merc.capture.dto.NavigationBar;
import au.edu.monash.merc.capture.dto.OrderBy;
import au.edu.monash.merc.capture.dto.OrderBy.OrderType;
import au.edu.monash.merc.capture.service.UserService;
import au.edu.monash.merc.capture.util.MD5;

import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport implements SessionAware, ServletRequestAware, ServletResponseAware {

	protected User user;

	@Autowired
	protected UserService userService;

	@Autowired
	protected ConfigSettings configSetting;

	protected Map<String, Object> session;

	protected HttpServletRequest request;

	protected HttpServletResponse response;

	public static final String JSON = "json";

	public static final String REDIRECT = "redirect";

	public static final String CHAIN = "chain";

	private String actionTitle;

	private String pageTitle;

	private String actionSuccessMsg;

	protected int pageNo = 1;

	protected int sizePerPage;

	protected static String storeRootPath;

	protected String orderBy;

	protected String orderByType;

	private Map<String, String> orderByMap = new HashMap<String, String>();

	private Map<Integer, Integer> pageSizeMap = new LinkedHashMap<Integer, Integer>();

	private Map<String, String> orderByTypeMap = new HashMap<String, String>();

	private String pageLink;

	private String pageSuffix;

	protected NavigationBar navigationBar;

	protected static User allRegUser;

	protected static User anonymous;

	protected static boolean superAdminExisted;

	protected static String REDIRECTCO = "redirectco";

	public void setConfigSetting(ConfigSettings configSetting) {
		this.configSetting = configSetting;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return this.session;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void saveInSession(String key, Object obj) {
		this.session.put(key, obj);
	}

	public void removeFromSession(String sessionKey) {
		this.session.remove(sessionKey);
	}

	public Object findInSession(String key) {
		return this.session.get(key);
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public void setPageTitle(String mainTitle, String event) {
		if (event != null) {
			setPageTitle(mainTitle + " - " + event);
		} else {
			setPageTitle(mainTitle);
		}
	}

	// TODO: remove
	public String getActionTitle() {
		return actionTitle;
	}

	public void setActionTitle(String actionTitle) {
		this.actionTitle = actionTitle;
	}

	public String getActionSuccessMsg() {
		return actionSuccessMsg;
	}

	public void setActionSuccessMsg(String actionSuccessMsg) {
		this.actionSuccessMsg = actionSuccessMsg;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getSizePerPage() {
		if (sizePerPage == 0) {
			sizePerPage = ActConstants.DEFAULT_SIZE_PER_PAGE;
		}
		return sizePerPage;
	}

	public void setSizePerPage(int sizePerPage) {
		this.sizePerPage = sizePerPage;
	}

	protected String generateSecurityHash(String value) {
		String systemHash = MD5.hash(configSetting.getPropValue(ConfigSettings.USER_HASH_SEQUENCE) + value);
		return MD5.hash(System.currentTimeMillis() + systemHash);
	}

	protected boolean isSecurityCodeError(String securityCode) {
		String code = (String) findInSession(ActConstants.SESSION_SECURITY_CODE);
		if (code == null) {
			return true;
		} else if (code.equalsIgnoreCase(securityCode)) {
			return false;
		} else {
			return true;
		}
	}

	public String getAppRealPath(String path) {
		return ServletActionContext.getServletContext().getRealPath(path);
	}

	public String getAppRoot() {
		return getAppRealPath("/");
	}

	public String getAppContextPath() {
		return ServletActionContext.getRequest().getContextPath();
	}

	public int getServerPort() {
		return ServletActionContext.getRequest().getServerPort();
	}

	public String getAppHostName() {
		return ServletActionContext.getRequest().getServerName();
	}

	public String getServerQName() {

		String scheme = request.getScheme();
		String hostName = request.getServerName();
		int port = request.getServerPort();

		StringBuffer buf = new StringBuffer();
		if (scheme.equals(ActConstants.HTTP_SCHEME)) {
			buf.append(ActConstants.HTTP_SCHEME).append(ActConstants.HTTP_SCHEME_DELIM);
		} else {
			buf.append(ActConstants.HTTPS_SCHEME).append(ActConstants.HTTP_SCHEME_DELIM);
		}
		buf.append(hostName);
		if (port == 80 || port == 443) {
			return new String(buf);
		}
		buf.append(ActConstants.COLON_DEIM).append(port);
		return new String(buf);
	}

	public String nlToBr(String textArea) {
		return StringUtils.replace(textArea, "\n", "<br/>");
	}

	protected void populatePaginationLinks(String paginationActionName, String paginationSuffix) {
		this.pageLink = paginationActionName;
		this.pageSuffix = paginationSuffix;
	}

	protected void populateCoPageSortParams() {
		// page size per page values
		pageSizeMap.put(5, 5);
		pageSizeMap.put(10, 10);
		pageSizeMap.put(15, 15);
		pageSizeMap.put(20, 20);
		pageSizeMap.put(25, 25);
		pageSizeMap.put(30, 30);
		pageSizeMap.put(40, 40);
		pageSizeMap.put(50, 50);

		// if sorted by user name required
		// orderByMap.put("user", "user");

		// orderby values
		orderByMap.put("name", "name");
		orderByMap.put("createdTime", "created date");
		orderByMap.put("modifiedTime", "modified date");

		// orderby type values
		orderByTypeMap.put("ASC", "asc");
		orderByTypeMap.put("DESC", "desc");
	}

	protected void populateDatasetPageParams() {
		// page size per page values
		pageSizeMap.put(5, 5);
		pageSizeMap.put(10, 10);
		pageSizeMap.put(15, 15);
		pageSizeMap.put(20, 20);
		pageSizeMap.put(25, 25);
		pageSizeMap.put(30, 30);
		pageSizeMap.put(40, 40);
		pageSizeMap.put(50, 50);

		orderByMap.put("name", "name");
		orderByMap.put("importDateTime", "imported time");
		orderByMap.put("runDateTime", "run time");

		// orderby type values
		orderByTypeMap.put("DESC", "desc");
		orderByTypeMap.put("ASC", "asc");
	}

	protected void populateUserPageParams() {
		// page size per page values
		pageSizeMap.put(5, 5);
		pageSizeMap.put(10, 10);
		pageSizeMap.put(15, 15);
		pageSizeMap.put(20, 20);
		pageSizeMap.put(25, 25);
		pageSizeMap.put(30, 30);
		pageSizeMap.put(40, 40);
		pageSizeMap.put(50, 50);

		orderByMap.put("displayName", "display name");
		orderByMap.put("userType", "user type");
		// orderby type values
		orderByTypeMap.put("ASC", "asc");
		orderByTypeMap.put("DESC", "desc");
	}

	protected void populateEventPageParams() {
		// page size per page values
		pageSizeMap.put(5, 5);
		pageSizeMap.put(10, 10);
		pageSizeMap.put(15, 15);
		pageSizeMap.put(20, 20);
		pageSizeMap.put(25, 25);
		pageSizeMap.put(30, 30);
		pageSizeMap.put(40, 40);
		pageSizeMap.put(50, 50);

		orderByMap.put("createdTime", "created date");

		// orderby type values
		orderByTypeMap.put("DESC", "desc");
		orderByTypeMap.put("ASC", "asc");
	}

	protected void persistPageSortParamsInSession(String pageSizeKey, String orderByKey, String orderByTypeKey, String actionType) {
		// size per page value
		if (sizePerPage == 0) {
			Object size = findInSession(pageSizeKey);
			if (size == null) {
				sizePerPage = ActConstants.DEFAULT_SIZE_PER_PAGE;
			} else {
				sizePerPage = ((Integer) size).intValue();
			}
		}
		saveInSession(pageSizeKey, sizePerPage);

		// orderBy value
		if (orderBy == null) {
			Object ord = findInSession(orderByKey);
			if (ord == null) {
				if (actionType.equals(ActConstants.OrderByActionType.CO.actionType())) {
					orderBy = "name";
				}
				if (actionType.equals(ActConstants.OrderByActionType.USER.actionType())) {
					orderBy = "displayName";
				}
				if (actionType.equals(ActConstants.OrderByActionType.EVENT.actionType())) {
					orderBy = "createdTime";
				}
				if (actionType.equals(ActConstants.OrderByActionType.SEARCHCO.actionType())) {
					orderBy = "name";
				}
				if (actionType.equals(ActConstants.OrderByActionType.SEARCHDS.actionType())) {
					orderBy = "name";
				}
			} else {
				orderBy = (String) ord;
			}
		}
		if (actionType.equals(ActConstants.OrderByActionType.CO.actionType())) {
			if (!orderBy.equals("name") && !orderBy.equals("createdTime") && !orderBy.equals("modifiedTime")) {
				orderBy = "name";
			}
		}

		if (actionType.equals(ActConstants.OrderByActionType.USER.actionType())) {
			if (!orderBy.equals("displayName") && !orderBy.equals("userType")) {
				orderBy = "displayName";
			}
		}

		if (actionType.equals(ActConstants.OrderByActionType.EVENT.actionType())) {
			if (!orderBy.equals("createdTime")) {
				orderBy = "createdTime";
			}
		}

		if (actionType.equals(ActConstants.OrderByActionType.SEARCHCO.actionType())) {
			if (!orderBy.equals("name") && !orderBy.equals("createdTime") && !orderBy.equals("modifiedTime")) {
				orderBy = "name";
			}
		}
		if (actionType.equals(ActConstants.OrderByActionType.SEARCHDS.actionType())) {
			if (!orderBy.equals("name") && !orderBy.equals("importDateTime") && !orderBy.equals("runDateTime")) {
				orderBy = "name";
			}
		}
		saveInSession(orderByKey, orderBy);

		// orderby type
		if (orderByType == null) {
			Object ordType = findInSession(orderByTypeKey);
			if (ordType == null) {
				if (actionType.equals(ActConstants.OrderByActionType.EVENT.actionType())) {
					orderByType = ActConstants.DESC_ORDERBY_TYPE;
				} else {
					orderByType = ActConstants.DEFAULT_ORDERBY_TYPE;
				}
			} else {
				orderByType = (String) ordType;
			}
		}

		if (!orderByType.equals("ASC") && !orderByType.equals("DESC")) {
			orderByType = "ASC";
		}
		saveInSession(orderByTypeKey, orderByType);

	}

	protected OrderBy[] populateOrderBy() {

		if (orderByType.equals(OrderType.ASC.toString())) {
			return new OrderBy[] { OrderBy.asc(orderBy) };
		} else {
			return new OrderBy[] { OrderBy.desc(orderBy) };
		}
	}

	public Map<String, String> getOrderByMap() {
		return orderByMap;
	}

	public void setOrderByMap(Map<String, String> orderByMap) {
		this.orderByMap = orderByMap;
	}

	public Map<Integer, Integer> getPageSizeMap() {
		return pageSizeMap;
	}

	public void setPageSizeMap(Map<Integer, Integer> pageSizeMap) {
		this.pageSizeMap = pageSizeMap;
	}

	public Map<String, String> getOrderByTypeMap() {
		return orderByTypeMap;
	}

	public void setOrderByTypeMap(Map<String, String> orderByTypeMap) {
		this.orderByTypeMap = orderByTypeMap;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderByType() {
		return orderByType;
	}

	public void setOrderByType(String orderByType) {
		this.orderByType = orderByType;
	}

	public String getPageLink() {
		return pageLink;
	}

	public void setPageLink(String pageLink) {
		this.pageLink = pageLink;
	}

	public String getPageSuffix() {
		return pageSuffix;
	}

	public void setPageSuffix(String pageSuffix) {
		this.pageSuffix = pageSuffix;
	}

	public NavigationBar getNavigationBar() {
		return navigationBar;
	}

	public void setNavigationBar(NavigationBar navigationBar) {
		this.navigationBar = navigationBar;
	}

	protected NavigationBar generateNavLabel(String startNav, String startNavLink, String secondNav, String secondNavLink, String thirdNav,
			String thirdNavLink) {
		return new NavigationBar(startNav, startNavLink, secondNav, secondNavLink, thirdNav, thirdNavLink);
	}

	protected long getLoginUsrIdFromSession() {
		Object login_uid = findInSession(ActConstants.SESSION_AUTHEN_USER_ID);
		if (login_uid == null) {
			return 0;
		} else {
			return ((Long) login_uid).longValue();
		}
	}

	protected User retrieveLoggedInUser() {
		long userId = getLoginUsrIdFromSession();
		return this.userService.getUserById(userId);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
