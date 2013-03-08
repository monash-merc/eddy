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

public interface ActConstants {

	static String SESSION_SECURITY_CODE = "security_code";

	static String SESSION_AUTHENTICATION_FLAG = "authentication_flag";

	static String SESSION_AUTHEN_USER_ID = "authen_user_id";

	static String SESSION_AUTHEN_USER_NAME = "authen_user_name";

	static String SESSION_LOGIN = "authenticated";

	static String REQUEST_URL = "request_url_path";
	
	static String SESSION_VIEW_PUBCO_FOR_LOGIN_USER = "view_pub_co_for_login_user";
	
	static String ANDS_SPATIAL_TYPE = "kmlPolyCoords";

    static String ANDS_SPATIAL_TEXT_TYPE = "text";

    static String ANDS_SPATIAL_GLOBAL = "Global";

	static String SEARCH_CONDITION_KEY = "search_condition_Key";

	static String ACTION_SUCCESS_MSG_TAG = "actionSuccessMsg";

	static String DOWNLOAD_USER_DATASET_ACTION_NAME = "downloadDataset";

	static String VIEW_USER_DATASET_ACTION_NAME = "viewDataset";

	static String DELETE_USER_DATASET_ACTION_NAME = "deleteDataset";
	
	// For all logged in user - collection pagination
	static String SESSION_VIEW_COLLECTION_PAGE_SIZE = "collection_page_size";

	static String SESSION_VIEW_COLLECTION_ORDERBY = "collection_orderby";

	static String SESSION_VIEW_COLLECTION_ORDERBY_TYPE = "collection_orderby_type";

	// For search pagination
	static String SESSION_SEARCH_PAGE_SIZE = "search_page_size";

	static String SESSION_SEARCH_ORDERBY = "search_orderby";

	static String SESSION_SEARCH_ORDERBY_TYPE = "search_orderby_type";

	// For Events view pagination
	static String SESSION_EVENTS_PAGE_SIZE = "event_page_size";

	static String SESSION_EVENTS_ORDERBY = "event_page_orderby";

	static String SESSION_EVENTS_ORDERBY_TYPE = "event_page_orderby_type";

	// For Users view pagination
	static String SESSION_USER_PAGE_SIZE = "user_page_size";

	static String SESSION_USER_ORDERBY = "user_page_orderby";

	static String SESSION_USER_ORDERBY_TYPE = "user_page_orderby_type";

	static String MANAGE_USER_ACTION_NAME = "admin/manageUser.jspx";

	static String HTTP_SCHEME = "http";

	static String HTTPS_SCHEME = "https";

	static String HTTP_SCHEME_DELIM = "://";

	static String URL_PATH_DEIM = "/";

	static String COLON_DEIM = ":";
	
	static String LAST_TIME_OF_DAY = " 23:59:59";
	
	static String RESET_PWD_ACTION_NAME = "ResetPasswd";

	static String ACTIVATION_ACTION_NAME = "activateAccount";

	static int DEFAULT_SIZE_PER_PAGE = 10;

	static String DEFAULT_ORDERBY = "name";

	static String DEFAULT_ORDERBY_TYPE = "ASC";

	static String DESC_ORDERBY_TYPE = "DESC";

	static String DATA_STORE_USER_ROOT_PREFIX = "uid_";

	static int BRIEF_DESCRIPTION_MAX_LENGTH = 200;

	static String PAGINATION_SUFFUX = "?pageNo=";

	// permission
	static String SET_COLLECTION_PERMISSION_ACTION = "perm/showSetColPermissions.jspx";
	
	static String VIEW_PERM_REQUESTS_ACTION = "perm/listPermRequests.jspx";

	static String SHOW_SEARCH_ACTION = "search/showSearch.jspx";

	static String SEARCH_ACTION = "search/search.jspx";

	static String VIEW_COLLECTION_DETAILS_ACTION = "data/viewColDetails.jspx";

	static String SHOW_COLLECTION_EDIT_ACTION = "data/showEditCollection.jspx";

	static String COLLECTION_DELETE_ACTION = "data/deleteCollection.jspx";

	static String DATASET_EXPORT_ACTION = "data/exportDataset.jspx";

	static String DATASET_VIEWDATA_ACTION = "data/viewDatasetData.jspx";

	static String DATASET_DELETE_ACTION = "data/deleteDataset.jspx";
	 
	//ands metadata registration
	static  String ANDS_MD_REG_SHOW_ACTION= "data/showMdRegister.jspx";
	
	static  String ANDS_MD_REG_SELECT_ACTIVITY_ACTION= "data/selectActivity.jspx";
	
	static  String ANDS_MD_REG_SELECT_RIGHTS_ACTION= "data/selectRights.jspx";
	
	static  String ANDS_MD_REG_PREVIEW_ACTION= "data/regPreview.jspx";
	
	static String ANDS_MD_REG_PARTY_RM_TYPE = "rm_party";
	
	static String ANDS_MD_REG_PARTY_RM_TYPE_LABEL = "Search a researcher from the Research Master Web Service";
	
	static String ANDS_MD_REG_PARTY_USER_DEFINED_TYPE = "user_defined_party";
	
	static String ANDS_MD_REG_PARTY_USER_DEFINED_TYPE_LABEL = "Manually Input a researcher information";
	
	// user collection
	static String USER_LIST_COLLECTION_ACTION = "data/listUserCollections.jspx";

	// list all user collections
	static String LIST_ALL_COLLECTIONS_ACTION = "data/listAllCollections.jspx";

	// public collection
	static String PUB_LIST_COLLECTION_ACTION = "pub/listPubCollections.jspx";

	static String PUB_VIEW_COLLECTION_DETAILS_ACTION = "pub/viewColDetails.jspx";

	static String PUB_DATASET_EXPORT_ACTION = "pub/exportDataset.jspx";

	static String PUB_DATASET_VIEWDATA_ACTION = "pub/viewDatasetData.jspx";

	static String LIST_ALL_USERS_ACTION = "admin/listUsers.jspx";

	static String LIST_USER_EVENTS_ACTION = "admin/listUserEvents.jspx";

	static String DELETE_USER_EVENT_ACTION = "admin/deleteUserEvent.jspx";

	static String DISPLAY_USER_HOME_ACTION = "admin/displayUserHome.jspx";
	
	static String RIGHTS_CCCL_TYPE = "cccl_license";
	
	static String RIGHTS_CCPD_TYPE = "ccpd_license";
	
	static String RIGHTS_USEROWNER_TYPE = "user_license";
	
	static String RIGHTS_CCCL_LABEL = "Creative Commons Copyright License (Recommended)";
	
	static String RIGHTS_CCPD_LABEL = "Creative Commons Public Domain CC0";
	
	static String RIGHTS_USEROWNER_LABEL = "Define Your Own License";
	
	static String COMMERCIAL_ID = "commercial";
	
	static String DERIVATIVEs_ID = "derivatives"; 
	
	static String JURISDICTION_ID = "jurisdiction";

	public static enum UserViewType {
		USER("user"), ALL("all"), ANONYMOUS("anonymous");

		private String type;

		UserViewType(String type) {
			this.type = type;
		}

		public String viewType() {
			return type;
		}

		public String toString() {
			switch (this) {
			case USER:
				return "user";
			case ALL:
				return "all";
			case ANONYMOUS:
				return "anonymous";
			default:
				return "anonymous";

			}
		}

	}

	public static enum OrderByActionType {
		USER("user"), CO("co"), EVENT("event"), SEARCHCO("searchco"),SEARCHDS("searchds");;
		private String type;

		OrderByActionType(String type) {
			this.type = type;
		}

		public String actionType() {
			return this.type;
		}

		public String toString() {
			switch (this) {
			case USER:
				return "user";
			case CO:
				return "co";
			case EVENT:
				return "event";
			case SEARCHCO:
				return "searchco";
			case SEARCHDS:
				return "searchds";
			default:
				return "co";

			}
		}

	}

	public static enum ManageType {
		ACTIVATE("activate"), DEACTIVATE("deactivate"), SETASADMIN("setasadmin"), SETASUSER("setasuser");

		private String type;

		ManageType(String manageType) {
			this.type = manageType;
		}

		public String manageType() {
			return this.type;
		}

		public String toString() {
			switch (this) {
			case ACTIVATE:
				return "activate";
			case DEACTIVATE:
				return "deactivate";
			case SETASADMIN:
				return "setasadmin";
			case SETASUSER:
				return "setasuser";
			default:
				return "activate";

			}
		}
	}
}
