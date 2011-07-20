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
package au.edu.monash.merc.capture.struts2.action.install;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import au.edu.monash.merc.capture.dto.ApplicationProperty;
import au.edu.monash.merc.capture.dto.JdbcProperty;
import au.edu.monash.merc.capture.dto.LdapProperty;
import au.edu.monash.merc.capture.dto.MailProperty;
import au.edu.monash.merc.capture.util.CaptureUtil;
import au.edu.monash.merc.capture.util.Installer;

@Scope("prototype")
@Controller("install.installAction")
public class InstallAction extends InstallBaseAction {

	private String webinfRoot;

	private String installTempConfPath;

	private String destPropConfRoot;

	private boolean accepted;

	private ApplicationProperty appProp;

	private JdbcProperty jdbcProp;

	private MailProperty mailProp;

	private LdapProperty ldapProp;

	private Map<String, String> dbTypeNames = new HashMap<String, String>();

	private Map<String, String> trueFalseMap = new HashMap<String, String>();

	private static String JDBC_PROP_FILE = "jdbc.properties";

	private static String MAIL_PROP_FILE = "mail.properties";

	private static String APP_PROP_FILE = "dataCapture.properties";

	private static String LDAP_PROP_FILE = "ldap.properties";

	private static String WEB_XML_FILE = "web.xml";

	private static String SPRING_CONF_FILE = "applicationContext.xml";

	private static String STRUTS_FILE = "struts.xml";

	private Logger logger = Logger.getLogger(this.getClass());

	public String setup() {
		defaultConf();
		setDefaultMaps();
		return SUCCESS;
	}

	private void setDefaultMaps() {
		dbTypeNames.put("mysql", "MySQL");
		dbTypeNames.put("oracle", "Oracle");
		dbTypeNames.put("postgresql", "PostgreSQL");

		trueFalseMap.put("true", "true");
		trueFalseMap.put("false", "false");
	}

	public String acceptCon() {
		if (!accepted) {

			// set the message
			return INPUT;
		}

		return SUCCESS;
	}

	public String install() {

		try {
			webinfRoot = getAppRoot() + "WEB-INF" + File.separator;
			installTempConfPath = webinfRoot + "install" + File.separator + "conf" + File.separator;
			destPropConfRoot = webinfRoot + "classes" + File.separator;

			// Application configuration file
			String appTempFile = installTempConfPath + APP_PROP_FILE;
			String destAppFile = destPropConfRoot + APP_PROP_FILE;

			// create application configuration file
			appProp.setAuthDomain(getServerQName());

			// write the application configuration file
			Installer.writeAppConfig(appProp, appTempFile, destAppFile);

			// JDBC configuration file
			String jdbcTempFile = installTempConfPath + JDBC_PROP_FILE;
			String destJdbcFile = destPropConfRoot + JDBC_PROP_FILE;
			// Create a database configuration file
			Installer.writeDbConfig(jdbcTempFile, jdbcProp.getDbType(), jdbcProp.getDbHost(), jdbcProp.getDbPort(), jdbcProp.getDbName(),
					jdbcProp.getDbUserName(), jdbcProp.getDbPassword(), destJdbcFile);

			// Mail configuration file
			String mailTempFile = installTempConfPath + MAIL_PROP_FILE;
			String destMailFile = destPropConfRoot + MAIL_PROP_FILE;
			// create mail configuration file
			Installer.writeMailConfig(mailTempFile, mailProp.getMailServer(), mailProp.getMailServerPort(), mailProp.isAuthenticated(),
					mailProp.isTlsEnabled(), mailProp.getUserName(), mailProp.getPassword(), destMailFile);

			// LDAP configuration file
			String ldapTempFile = installTempConfPath + LDAP_PROP_FILE;
			String destLdapFile = destPropConfRoot + LDAP_PROP_FILE;
			Installer.writeLdapConfig(ldapProp, ldapTempFile, destLdapFile);

			// Spring configuration file
			String springConfFile = installTempConfPath + SPRING_CONF_FILE;
			String destSpringConfFile = destPropConfRoot + SPRING_CONF_FILE;

			// Struts configuration file
			String strutsFile = installTempConfPath + STRUTS_FILE;
			String destStrutsFile = destPropConfRoot + STRUTS_FILE;

			// Web XML file
			String webxmlConfFile = installTempConfPath + WEB_XML_FILE;
			String destWebxmlFile = webinfRoot + WEB_XML_FILE;

			// copy the spring configuration file
			FileUtils.copyFile(new File(springConfFile), new File(destSpringConfFile));

			// copy the struts configuration file
			FileUtils.copyFile(new File(strutsFile), new File(destStrutsFile));

			// copy the web.xml file
			FileUtils.copyFile(new File(webxmlConfFile), new File(destWebxmlFile));

			System.out.println("Finished");
		} catch (Exception e) {
			addActionError(e.getMessage());
			setDefaultMaps();
			logger.error(e);
			return INPUT;
		}
		return SUCCESS;
	}

	private void defaultConf() {
		// app
		appProp = new ApplicationProperty();
		appProp.setAppName("YourApplicationName");
		appProp.setAdminEmail("admin@adminemail.com");
		appProp.setAdminName("admin");
		appProp.setAdminPassword("pass2word!");
		appProp.setSystemServiceEmail("service@servicemail.com");
		appProp.setStoreLocation("/opt/datastore/ands");
		appProp.setCollectionPhysicalLocation("Monash University Clayton Campus Building 26 Clayton 3800 Victoria");

		appProp.setLoginTryTimes(3);
		appProp.setBlockWaitingTimes(15);
		appProp.setSecurityHashSeq("whateveryouwanttomakeitmoresecuritymerc!");
		// google map api key
		appProp.setGoogleApiKey("ABQIAAAA-mrDIEKQPrjqNppfCE72fRQtlyttTPx5mPekxQelw9V6C-nC5RQ8Sya-FroqqvlqOHnhCAtW38qDpg");

		appProp.setStageEnabled(false);
		appProp.setStageLocation("/opt/datastore/stage");

		appProp.setMdRegEnabled(true);
		appProp.setRifcsStoreLocation("/opt/publish/rifcs");
		appProp.setAndsRegGroupName("Monash University");
		appProp.setResearchFieldCode("960501");

		// researcher master web service call
		appProp.setRmWsName("ESB_AIRMANDSService_RS_Service");
		appProp.setRmWsEndpointAddress("http://mobs-dev.its.monash.edu.au:7778/event/AI/ResearchMaster/AIRMANDSService_RS");
		appProp.setRmWsTimeout(30000);

		// handle web service
		appProp.setHdlWsEnabled(true);
		appProp.setHdlWsHostName("https://test.ands.org.au");
		appProp.setHdlWsHostPort(8443);
		appProp.setHdlWsPath("pids");
		appProp.setHdlWsMethod("mint");
		appProp.setHdlWsAppId("c4b16dc56797f1dfbf545e2397ac7b6bcc54b8ec");
		appProp.setHdlResolverAddress("http://hdl.handle.net/");

		// creative commons license web service
		appProp.setCcLicenseWsAddress("http://api.creativecommons.org/rest/1.5/license/standard");

		// jdbc
		jdbcProp = new JdbcProperty();
		jdbcProp.setDbType("postgresql");
		jdbcProp.setDbHost("localhost");
		jdbcProp.setDbPort(5432);
		jdbcProp.setDbName("ands_db");
		jdbcProp.setDbUserName("mercdev");
		jdbcProp.setDbPassword("merc2dev");

		// mail
		mailProp = new MailProperty();
		mailProp.setMailServer("smtp.monash.edu.au");
		mailProp.setMailServerPort(25);
		mailProp.setAuthenticated(false);
		mailProp.setTlsEnabled(false);
		mailProp.setUserName("mailUser");
		mailProp.setPassword("mailUserPassword");

		// Ldap
		ldapProp = new LdapProperty();
		ldapProp.setLdapSupported(true);
		ldapProp.setLdapServer("directory.monash.edu.au");
		ldapProp.setBaseDN("o=Monash University, c=AU");
		ldapProp.setAttUID("uid");
		ldapProp.setAttMail("mail");
		ldapProp.setAttGender("gender");
		ldapProp.setAttCN("cn");
		ldapProp.setAttSn("sn");
		ldapProp.setAttPersonalTitle("personaltitle");
		ldapProp.setAttGivenname("givenname");

	}

	public void validateInstall() {
		boolean hasError = false;

		// application config validations;
		if (StringUtils.isBlank(appProp.getAppName())) {
			addFieldError("appName", "The application name must be provided");
			hasError = true;
		}
		if (StringUtils.isBlank(appProp.getCollectionPhysicalLocation())) {
			addFieldError("physicalAddress", "The data physical location must be provided");
			hasError = true;
		}
		if (StringUtils.isBlank(appProp.getAdminName())) {
			addFieldError("adminName", "The system admin name must be provided");
			hasError = true;
		}
		if (StringUtils.isBlank(appProp.getAdminEmail())) {
			addFieldError("adminEmail", "The system admin email must be provided");
			hasError = true;
		}
		if (StringUtils.isNotBlank(appProp.getAdminEmail()) && (!CaptureUtil.validateEmail(appProp.getAdminEmail()))) {
			addFieldError("adminEmailInvalid", "The system admin email is invalid");
			hasError = true;
		}
		if (StringUtils.isBlank(appProp.getAdminPassword())) {
			addFieldError("adminPasswd", "The system admin password must be provided");
			hasError = true;
		}
		if (StringUtils.isBlank(appProp.getSystemServiceEmail())) {
			addFieldError("systemserviceEmail", "The system service email must be provided");
			hasError = true;
		}
		if (StringUtils.isNotBlank(appProp.getSystemServiceEmail()) && (!CaptureUtil.validateEmail(appProp.getSystemServiceEmail()))) {
			addFieldError("sysEmailInvalid", "The system service email is invalid");
			hasError = true;
		}
		if (appProp.getLoginTryTimes() == 0) {
			addFieldError("logintry", "Login try times must be provided");
			hasError = true;
		}
		if (appProp.getBlockWaitingTimes() == 0) {
			addFieldError("blockwaittimes", "Login re-try waiting times must be provided");
			hasError = true;
		}
		if (StringUtils.isBlank(appProp.getSecurityHashSeq())) {
			addFieldError("securityHash", "The security hash sequence must be provided");
			hasError = true;
		}
		if (StringUtils.isBlank(appProp.getGoogleApiKey())) {
			addFieldError("googlemapkey", "The Google Map API Key must be provided");
			hasError = true;
		}

		// stage transfer enabled or not
		boolean stageEnabled = appProp.isStageEnabled();
		if (stageEnabled) {
			if (StringUtils.isBlank(appProp.getStageLocation())) {
				addFieldError("stagelocation", "The staging location must be provided");
				hasError = true;
			}
		}

		// publish enabled
		boolean publishEnabled = appProp.isMdRegEnabled();
		if (publishEnabled) {
			if (StringUtils.isBlank(appProp.getRifcsStoreLocation())) {
				addFieldError("rifcslocation", "The rif-cs store location must be provided");
				hasError = true;
			}
			if (StringUtils.isBlank(appProp.getAndsRegGroupName())) {
				addFieldError("groupname", "The group name in the rif-cs must be provided");
				hasError = true;
			}
			if (StringUtils.isBlank(appProp.getResearchFieldCode())) {
				addFieldError("anzsrcode", "The research field code must be provided");
				hasError = true;
			}
			if (StringUtils.isBlank(appProp.getRmWsName())) {
				addFieldError("rmwsname", "The researcher master web service name must be provided");
				hasError = true;
			}
			if (StringUtils.isBlank(appProp.getRmWsEndpointAddress())) {
				addFieldError("rmwsaddress", "The researcher master web service endpoint address must be provided");
				hasError = true;
			}
			if (appProp.getRmWsTimeout() == 0) {
				addFieldError("rmwsatimeout", "The researcher master web service timeout value must be provided");
				hasError = true;
			}
			boolean handleWsEnabled = appProp.isHdlWsEnabled();
			if (handleWsEnabled) {
				if (StringUtils.isBlank(appProp.getHdlWsHostName())) {
					addFieldError("hdlwshost", "The handle web service host must be provided");
					hasError = true;
				} else {
					if (!StringUtils.startsWith(appProp.getHdlWsHostName(), "https://")
							&& (!StringUtils.startsWith(appProp.getHdlWsHostName(), "http://"))) {
						addFieldError("hdlwshost", "The protocol (https or http) must be included in the handle web service host");
						hasError = true;
					}
				}
				if (appProp.getHdlWsHostPort() == 0) {
					addFieldError("hdlwshostport", "The handle web service host port must be provided");
					hasError = true;
				}
				if (StringUtils.isBlank(appProp.getHdlWsPath())) {
					addFieldError("hdlwsapath", "The handle web service path must be provided");
					hasError = true;
				}
				if (StringUtils.isBlank(appProp.getHdlWsMethod())) {
					addFieldError("hdlwsmintmethod", "The handle web service mint method must be provided");
					hasError = true;
				}
				if (StringUtils.isBlank(appProp.getHdlWsAppId())) {
					addFieldError("hdlwsappid", "The handle web service application id must be provided");
					hasError = true;
				}
				if (StringUtils.isBlank(appProp.getHdlResolverAddress())) {
					addFieldError("hdlresolver", "The handle resolver server must be provided");
					hasError = true;
				}
			}

			if (StringUtils.isBlank(appProp.getCcLicenseWsAddress())) {
				addFieldError("cclicense", "The creative commons license web service address must be provided");
				hasError = true;
			}
		}

		// database config validations
		if (StringUtils.isBlank(jdbcProp.getDbHost())) {
			addFieldError("dbhost", "The Database server must be provided ");
			hasError = true;
		}

		if (jdbcProp.getDbPort() == 0) {
			addFieldError("dbport", "The Database server port must be provided");
			hasError = true;
		}
		if (StringUtils.isBlank(jdbcProp.getDbName())) {
			addFieldError("dbname", "The Database name must be provided ");
			hasError = true;
		}

		if (StringUtils.isBlank(jdbcProp.getDbUserName())) {
			addFieldError("dbuser", "The Database user name must be provided ");
			hasError = true;
		}

		if (StringUtils.isBlank(jdbcProp.getDbPassword())) {
			addFieldError("dbpassword", "The Database user password must be provided ");
			hasError = true;
		}
		// mail configuration
		if (StringUtils.isBlank(mailProp.getMailServer())) {
			addFieldError("mailserver", "The mail server must be provided ");
			hasError = true;
		}
		if (mailProp.getMailServerPort() == 0) {
			addFieldError("mailport", "The mail server port must be provided ");
			hasError = true;
		}
		if (mailProp.isAuthenticated()) {
			if (StringUtils.isBlank(mailProp.getUserName())) {
				addFieldError("mailuser", "The mail user name must be provided");
				hasError = true;
			}
			if (StringUtils.isBlank(mailProp.getPassword())) {
				addFieldError("mailuserpassword", "The mail user password must be provided");
				hasError = true;
			}
		}

		// ldap configuration validation
		if (ldapProp.isLdapSupported()) {
			if (StringUtils.isBlank(ldapProp.getLdapServer())) {
				addFieldError("ldapserver", "The ldap server must be provided");
				hasError = true;
			}
			if (StringUtils.isBlank(ldapProp.getBaseDN())) {
				addFieldError("basedn", "The ldap server base dn must be provided");
				hasError = true;
			}
			if (StringUtils.isBlank(ldapProp.getAttUID())) {
				addFieldError("attuid", "The attribute uid name must be provided");
				hasError = true;
			}

			if (StringUtils.isBlank(ldapProp.getAttMail())) {
				addFieldError("attmail", "The attribute mail name must be provided");
				hasError = true;
			}
			if (StringUtils.isBlank(ldapProp.getAttGender())) {
				addFieldError("attgender", "The attribute gender name must be provided");
				hasError = true;
			}
			if (StringUtils.isBlank(ldapProp.getAttCN())) {
				addFieldError("attcn", "The attribute cn name must be provided");
				hasError = true;
			}
			if (StringUtils.isBlank(ldapProp.getAttGivenname())) {
				addFieldError("attgivenname", "The attribute givenname name must be provided");
				hasError = true;
			}
			if (StringUtils.isBlank(ldapProp.getAttSn())) {
				addFieldError("attsn", "The attribute sn name must be provided");
				hasError = true;
			}
			if (StringUtils.isBlank(ldapProp.getAttPersonalTitle())) {
				addFieldError("attptitle", "The attribute personaltitle name must be provided");
				hasError = true;
			}
		}

		if (hasError) {
			setDefaultMaps();
		}
	}

	/**
	 * Ajax call for checking store permission
	 * 
	 * @return a String represents SUCCESS or ERROR.
	 */
	public String checkDatastore() {

		return SUCCESS;
	}

	/**
	 * Ajax call for checking the database connection
	 * 
	 * @return a String represents SUCCESS or ERROR.
	 */
	public String checkDbConn() {

		return SUCCESS;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public Map<String, String> getDbTypeNames() {
		return dbTypeNames;
	}

	public void setDbTypeNames(Map<String, String> dbTypeNames) {
		this.dbTypeNames = dbTypeNames;
	}

	public Map<String, String> getTrueFalseMap() {
		return trueFalseMap;
	}

	public void setTrueFalseMap(Map<String, String> trueFalseMap) {
		this.trueFalseMap = trueFalseMap;
	}

	public String getWebinfRoot() {
		return webinfRoot;
	}

	public void setWebinfRoot(String webinfRoot) {
		this.webinfRoot = webinfRoot;
	}

	public String getInstallTempConfPath() {
		return installTempConfPath;
	}

	public void setInstallTempConfPath(String installTempConfPath) {
		this.installTempConfPath = installTempConfPath;
	}

	public String getDestPropConfRoot() {
		return destPropConfRoot;
	}

	public void setDestPropConfRoot(String destPropConfRoot) {
		this.destPropConfRoot = destPropConfRoot;
	}

	public ApplicationProperty getAppProp() {
		return appProp;
	}

	public void setAppProp(ApplicationProperty appProp) {
		this.appProp = appProp;
	}

	public JdbcProperty getJdbcProp() {
		return jdbcProp;
	}

	public void setJdbcProp(JdbcProperty jdbcProp) {
		this.jdbcProp = jdbcProp;
	}

	public MailProperty getMailProp() {
		return mailProp;
	}

	public void setMailProp(MailProperty mailProp) {
		this.mailProp = mailProp;
	}

	public LdapProperty getLdapProp() {
		return ldapProp;
	}

	public void setLdapProp(LdapProperty ldapProp) {
		this.ldapProp = ldapProp;
	}

}
