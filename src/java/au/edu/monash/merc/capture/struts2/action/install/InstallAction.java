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
package au.edu.monash.merc.capture.struts2.action.install;

import au.edu.monash.merc.capture.dto.*;
import au.edu.monash.merc.capture.util.CaptureUtil;
import au.edu.monash.merc.capture.util.Installer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

    private Map<String, String> dbTypeNames = new HashMap<String, String>();

    private Map<String, String> trueFalseMap = new HashMap<String, String>();

    private static final String JDBC_PROP_FILE = "jdbc.properties";

    private static final String MAIL_PROP_FILE = "mail.properties";

    private static final String APP_PROP_FILE = "dataCapture.properties";

    private static final String WEB_XML_FILE = "web.xml";

    private static final String SPRING_CONF_FILE = "applicationContext.xml";

    private static final String STRUTS_FILE = "struts.xml";

    private final Logger logger = Logger.getLogger(this.getClass());

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
        appProp.setAdminEmail("your_admin_email");
        appProp.setAdminName("admin_name");
        appProp.setAdminPassword("admin_password");
        appProp.setSystemServiceEmail("service_email");
        appProp.setStoreLocation("/opt/datastore/eddy");
        appProp.setDataLicence("http://www.tern.org.au/datalicence/TERN-BY-SA-NC/1.0");
        appProp.setCollectionPhysicalLocation("Monash University Clayton Campus Building 26 Clayton 3800 Victoria");

        appProp.setLoginTryTimes(3);
        appProp.setBlockWaitingTimes(15);
        appProp.setSecurityHashSeq("whateveryouwanttomakeitmoresecuritymerc!");
        appProp.setAndsRegGroupName("OzFlux: Australian and New Zealand Flux Research and Monitoring");
        appProp.setRifcsStoreLocation("/opt/datastore/rifcs");
        appProp.setMapEnabled(true);

        // jdbc
        jdbcProp = new JdbcProperty();
        jdbcProp.setDbType("postgresql");
        jdbcProp.setDbHost("localhost");
        jdbcProp.setDbPort(5432);
        jdbcProp.setDbName("database name");
        jdbcProp.setDbUserName("user name");
        jdbcProp.setDbPassword("your password");

        // mail
        mailProp = new MailProperty();
        mailProp.setMailServer("smtp.gmail.com");
        mailProp.setMailServerPort(587);
        mailProp.setAuthenticated(true);
        mailProp.setTlsEnabled(true);
        mailProp.setUserName("your email address");
        mailProp.setPassword("your email password");
    }

    public void validateInstall() {
        boolean hasError = false;

        // application config validations;
        if (StringUtils.isBlank(appProp.getAppName())) {
            addFieldError("appName", "The application name must be provided");
            hasError = true;
        }
        if (StringUtils.isBlank(appProp.getStoreLocation())) {
            addFieldError("storeLocation", "The data store location must be provided");
            hasError = true;
        }
        if (StringUtils.isBlank(appProp.getDataLicence())) {
            addFieldError("dataLicence", "The data licence must be provided");
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

        if (StringUtils.isBlank(appProp.getAndsRegGroupName())) {
            addFieldError("groupname", "The group name in the rif-cs must be provided");
            hasError = true;
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
}
