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
package au.edu.monash.merc.capture.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.AccessController;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import sun.security.action.GetPropertyAction;
import au.edu.monash.merc.capture.dto.ApplicationProperty;
import au.edu.monash.merc.capture.dto.LdapProperty;
import au.edu.monash.merc.capture.exception.ConfigException;

public class Installer {

	/**
	 * Create a jdbc connection properties file
	 * 
	 * @param dbTempFileName
	 * @param dbType
	 * @param dbHost
	 * @param dbPort
	 * @param dbName
	 * @param dbUser
	 * @param dbPassword
	 * @param destDbFile
	 */
	public static void writeDbConfig(String dbTempFileName, String dbType, String dbHost, int dbPort, String dbName, String dbUser,
			String dbPassword, String destDbFile) {

		Properties dbProps = Installer.load(dbTempFileName);
		String dbUrl = null;
		String driverClass = null;

		if (dbType.equalsIgnoreCase("mysql")) {
			driverClass = "com.mysql.jdbc.Driver";
			dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
		} else if (dbType.equalsIgnoreCase("postgresql")) {
			driverClass = "org.postgresql.Driver";
			dbUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;
		} else if (dbType.equalsIgnoreCase("oracle")) {
			driverClass = "oracle.jdbc.driver.OracleDriver";
			dbUrl = "jdbc:oracle:thin:@" + dbHost + ":" + dbPort + ":" + dbName;
		} else {
			throw new ConfigException("un-supported database type");
		}

		dbProps.setProperty("jdbc.driverClassName", driverClass);
		dbProps.setProperty("jdbc.url", dbUrl);
		dbProps.setProperty("jdbc.username", dbUser);
		dbProps.setProperty("jdbc.password", dbPassword);
		save(dbProps, destDbFile);
	}

	/**
	 * Create mail configuration properties file.
	 * 
	 * @param mailTempFileName
	 * @param mailServerName
	 * @param mailPort
	 * @param userName
	 * @param password
	 * @param destMailFile
	 */
	public static void writeMailConfig(String mailTempFileName, String mailServerName, int mailPort, boolean authenticated, boolean tlsenabled,
			String userName, String password, String destMailFile) {
		try {
			String s = FileUtils.readFileToString(new File(mailTempFileName));
			s = s.replaceFirst("MAIL_SERVER", mailServerName);
			s = s.replaceFirst("MAIL_SERVER_PORT", String.valueOf(mailPort));
			s = s.replaceFirst("AUTHENTICATED", String.valueOf(authenticated));
			s = s.replaceFirst("TLS_ENABLED", String.valueOf(tlsenabled));
			s = s.replaceFirst("USER_NAME", userName);
			s = s.replaceFirst("USER_PASSWORD", password);
			FileUtils.writeStringToFile(new File(destMailFile), s);
		} catch (Exception e) {
			throw new ConfigException(e);
		}
	}

	/**
	 * Write application configuration file
	 * 
	 * @param appProp
	 * @param appTempFileName
	 * @param destAppFile
	 */
	public static void writeAppConfig(ApplicationProperty appProp, String appTempFileName, String destAppFile) {
		try {
			String s = FileUtils.readFileToString(new File(appTempFileName));
			s = s.replaceFirst("APPLICATION_NAME", appProp.getAppName());
			String storepath = CaptureUtil.normalizePath(appProp.getStoreLocation());
			s = s.replaceFirst("DATASTORE_LOCATION", storepath);
			s = s.replaceFirst("COLLECTION_PHYSICAL_LOCATION", appProp.getCollectionPhysicalLocation());

			// admin and security
			s = s.replaceFirst("ADMIN_EMAIL", appProp.getAdminEmail());
			s = s.replaceFirst("ADMIN_NAME", appProp.getAdminName());
			s = s.replaceFirst("ADMIN_PASSWORD", appProp.getAdminPassword());
			s = s.replaceFirst("SYSTEM_SERVICE_EMAIL", appProp.getSystemServiceEmail());
			s = s.replaceFirst("LOGIN_TRY_TIMES", String.valueOf(appProp.getLoginTryTimes()));
			s = s.replaceFirst("BLOCK_WAITING_TIMES", String.valueOf(appProp.getBlockWaitingTimes()));
			String securityhash = appProp.getSecurityHashSeq();
			s = s.replaceFirst("SECURITY_HASH_SEQ", MD5.hash(securityhash));
			s = s.replaceFirst("GOOGLE_MAP_API_KEY", appProp.getGoogleApiKey());

			// set stage transfer properties if any
			s = s.replaceFirst("STAGE_TRANSFER_ENABLED", String.valueOf(appProp.isStageEnabled()));
			String stagepath = CaptureUtil.normalizePath(appProp.getStageLocation());
			s = s.replaceFirst("DATASTAGING_LOCATION", stagepath);

			// set publish properties and research master ws if any
			s = s.replaceFirst("ANDS_MD_REG_ENABLED", String.valueOf(appProp.isMdRegEnabled()));
			String rifcspath = CaptureUtil.normalizePath(appProp.getRifcsStoreLocation());
			s = s.replaceFirst("ANDS_RIFCS_STORE_LOCATION", rifcspath);
			s = s.replaceFirst("ANDS_MD_REG_GROUP_NAME", appProp.getAndsRegGroupName());
			s = s.replaceFirst("RESEARCH_FIELD_CODE", appProp.getResearchFieldCode());
			s = s.replaceFirst("RM_WS_NAME", appProp.getRmWsName());
			s = s.replaceFirst("RM_WS_ENDPOINT_ADDRESS", appProp.getRmWsEndpointAddress());
			s = s.replaceFirst("RM_WS_TIMEOUT", String.valueOf(appProp.getRmWsTimeout()));

			// handle web service property
			s = s.replaceFirst("HANDLE_WS_ENABLED", String.valueOf(appProp.isHdlWsEnabled()));

			// normalize the handle service host name
			String hdlHost = CaptureUtil.normalizePath(appProp.getHdlWsHostName());
			s = s.replaceFirst("HANDLE_WS_HOST_NAME", hdlHost);
			s = s.replaceFirst("HANDLE_WS_HOST_PORT", String.valueOf(appProp.getHdlWsHostPort()));
			s = s.replaceFirst("HANDLE_WS_PATH", appProp.getHdlWsPath());
			s = s.replaceFirst("HANDLE_WS_MINT_METHOD", appProp.getHdlWsMethod());
			s = s.replaceFirst("HANDLE_WS_APP_ID", appProp.getHdlWsAppId());
			s = s.replaceFirst("HANDLE_WS_IDENTIFIER", appProp.getAppName());
			s = s.replaceFirst("HANDLE_WS_AUTHDOMAIN", appProp.getAuthDomain());
			String hdlsrv = CaptureUtil.normalizePath(appProp.getHdlResolverAddress());
			s = s.replaceFirst("HANDLE_RESOLVER_SERVER", hdlsrv);
			// creative commons license web servive address
			s = s.replaceFirst("CC_LICENSE_WS_ADDRESS", appProp.getCcLicenseWsAddress());

			FileUtils.writeStringToFile(new File(destAppFile), s);
		} catch (Exception e) {
			throw new ConfigException(e);
		}

	}

	/**
	 * Create a ldap configuration properties file based on the ldap template configuration file.
	 * 
	 * @param ldapProperty
	 * @param ldapTempFileName
	 * @param destLdapFile
	 */
	public static void writeLdapConfig(LdapProperty ldapProperty, String ldapTempFileName, String destLdapFile) {
		try {
			String s = FileUtils.readFileToString(new File(ldapTempFileName));
			s = s.replaceFirst("LDAP_SUPPORTED", String.valueOf(ldapProperty.isLdapSupported()));
			if (ldapProperty.isLdapSupported()) {
				s = s.replaceFirst("LADP_SERVER", "ldap://" + ldapProperty.getLdapServer());
				s = s.replaceFirst("BASE_DN", ldapProperty.getBaseDN());
				s = s.replaceFirst("ATT_UID", ldapProperty.getAttUID());
				s = s.replaceFirst("ATT_MAIL", ldapProperty.getAttMail());
				s = s.replaceFirst("ATT_GENDER", ldapProperty.getAttGender());
				s = s.replaceFirst("ATT_CN", ldapProperty.getAttCN());
				s = s.replaceFirst("ATT_SN", ldapProperty.getAttSn());
				s = s.replaceFirst("ATT_GIVENNAME", ldapProperty.getAttGivenname());
				s = s.replaceFirst("ATT_PTITLE", ldapProperty.getAttPersonalTitle());
			}
			FileUtils.writeStringToFile(new File(destLdapFile), s);
		} catch (Exception e) {
			throw new ConfigException(e);
		}
	}

	/**
	 * Load the properties file
	 * 
	 * @param fileName
	 * @return a Properties object.
	 */
	public static Properties load(String fileName) {

		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(fileName)));
		} catch (Exception e) {
			throw new ConfigException(e);
		}
		return prop;
	}

	/**
	 * Save the properties into a file.
	 * 
	 * @param prop
	 * @param fileName
	 */
	public static void save(Properties prop, String fileName) {
		OutputStream out = null;
		try {
			String lineSeparator = (String) AccessController.doPrivileged(new GetPropertyAction("line.separator"));
			out = new FileOutputStream(new File(fileName));
			OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
			writer.write("#");
			writer.write(new Date().toString());
			writer.write(lineSeparator);
			writer.write("#" + fileName);
			writer.write(" - This is a system auto-generated file.");
			writer.write(lineSeparator);
			for (Enumeration<Object> e = prop.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = (String) prop.get(key);
				writer.write(key + "=" + value);
				writer.write(lineSeparator);
			}
			writer.flush();

		} catch (Exception e) {
			throw new ConfigException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					// ignore whatever caught.
				}
			}
		}

	}
}
