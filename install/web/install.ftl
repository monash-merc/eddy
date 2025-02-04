<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN""http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Welcome to System Installation</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="shortcut icon" href="${base}/images/favicon.ico" type="image/x-icon"/>

    <link href="${base}/css/merc.css" rel="stylesheet" type="text/css">
    <script>

    </script>
    <style>
        .ldap_config_tab {
            display: none;
        }
    </style>
</head>
<body>
<br/>
<br/>

<div class="main_body_container">
    <div class="install_none_border">
        <center>
            <div class="main_install_head">System Installation</div>
        </center>
    </div>
    <div class="install_none_border">
        <br/>
        <@s.actionerror  escape=false />
        <@s.fielderror escape=false />
    </div>

    <div class="p_title"><b>Basic Settings</b></div>
    <@s.form action="install.jspx" namespace="/install" method="post">
        <div class="main_install_inner">
            <table>
                <tr>
                    <td width="250">Application Name:</td>
                    <td><@s.textfield name="appProp.appName" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The application name</div>
                    </td>
                </tr>
                <tr>
                    <td>Data Store Location:</td>
                    <td><@s.textfield name="appProp.storeLocation" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The dataset files store location</div>
                    </td>
                </tr>
                <tr>
                    <td>Data Licence:</td>
                    <td><@s.textfield name="appProp.dataLicence" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The Data Licence Url (TERN Licence)</div>
                    </td>
                </tr>
                <tr>
                    <td>Data Physical Location:</td>
                    <td><@s.textarea name="appProp.collectionPhysicalLocation" cssStyle="width: 300px; height: 50px;" cssClass="input_textarea" /></td>
                    <td>
                        <div class="name_comment">* The dataset physical location</div>
                    </td>
                </tr>
                <tr>
                    <td>Administrator Name:</td>
                    <td><@s.textfield name="appProp.adminName" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The system administrator name</div>
                    </td>
                </tr>
                <tr>
                    <td>Administrator Email:</td>
                    <td><@s.textfield name="appProp.adminEmail" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The system administrator email</div>
                    </td>
                </tr>
                <tr>
                    <td>Administrator Password:</td>
                    <td><@s.textfield name="appProp.adminPassword" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The system administrator password, if administrator is a ldap user,
                            just put 'ldap' as password
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>System Service Email:</td>
                    <td><@s.textfield name="appProp.systemServiceEmail" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The system service email for sending and receiving emails</div>
                    </td>
                </tr>
                <tr>
                    <td>Login Try Times:</td>
                    <td><@s.textfield name="appProp.loginTryTimes" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* An user can try login times</div>
                    </td>
                </tr>
                <tr>
                    <td>Login Re-try Waiting Times:</td>
                    <td><@s.textfield name="appProp.blockWaitingTimes" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* Login re-try waiting times after ip blocked</div>
                    </td>
                </tr>
                <tr>
                    <td>Security Hash Code Sequence:</td>
                    <td><@s.textfield name="appProp.securityHashSeq" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The security hash sequence</div>
                    </td>
                </tr>
                <tr>
                    <td>Group Name:</td>
                    <td><@s.textfield name="appProp.andsRegGroupName" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The group name in the Citation info</div>
                    </td>
                </tr>
                <tr>
                    <td>RIF-CS store Location:</td>
                    <td><@s.textfield name="appProp.rifcsStoreLocation" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The published rif-cs store location</div>
                    </td>
                </tr>
                <tr>
                    <td>Google Map Enabled:</td>
                    <td><@s.select name="appProp.mapEnabled"  list="trueFalseMap" cssClass="input_select_normal"  /></td>
                    <td>
                        <div class="name_comment">* Is Google map enabled</div>
                    </td>
                </tr>
            </table>
        </div>

        <div class="p_title"><b>Database Configuration</b></div>
        <div class="main_install_inner">
            <table>
                <tr>
                    <td width="250">Database Type:</td>
                    <td>
                        <@s.select name="jdbcProp.dbType" headerKey = "${jdbcProp.dbType}" list="dbTypeNames" cssClass="input_select_normal"  />
                    <td>
                        <div class="name_comment">* The database type</div>
                    </td>
                </tr>
                <tr>
                    <td>Database Server:</td>
                    <td><@s.textfield name="jdbcProp.dbHost" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The database server url</div>
                    </td>
                </tr>
                <tr>
                    <td>Database Server Port:</td>
                    <td><@s.textfield name="jdbcProp.dbPort" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The database server port</div>
                    </td>
                </tr>
                <tr>
                    <td>Database Name:</td>
                    <td><@s.textfield name="jdbcProp.dbName" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The database name</div>
                    </td>
                </tr>
                <tr>
                    <td>Database User Name:</td>
                    <td><@s.textfield name="jdbcProp.dbUserName" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The database user name</div>
                    </td>
                </tr>
                <tr>
                    <td>Database User Password:</td>
                    <td><@s.textfield name="jdbcProp.dbPassword" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The database user password</div>
                    </td>
                </tr>
            </table>
        </div>


        <div class="p_title"><b>Mail Server Configuration</b></div>
        <div class="main_install_inner">
            <table>
                <tr>
                    <td width="250">Mail Server:</td>
                    <td><@s.textfield name="mailProp.mailServer" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The mail server</div>
                    </td>
                </tr>
                <tr>
                    <td>Mail Server Port:</td>
                    <td><@s.textfield name="mailProp.mailServerPort" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The mail server port</div>
                    </td>
                </tr>
                <tr>
                    <td>Use SMTP-AUTH:</td>
                    <td><@s.select name="mailProp.authenticated"  list="trueFalseMap" cssClass="input_select_normal"  /></td>
                    <td>
                        <div class="name_comment">* Use authentication to connect to SMTP server</div>
                    </td>
                </tr>
                <tr>
                    <td>TLS Enabled</td>
                    <td><@s.select name="mailProp.tlsEnabled"  list="trueFalseMap" cssClass="input_select_normal"  /></td>
                    <td>
                        <div class="name_comment">* Use TLS to encrypt communication with SMTP server</div>
                    </td>
                </tr>
                <tr>
                    <td>User Name:</td>
                    <td><@s.textfield name="mailProp.userName" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The mail user name</div>
                    </td>
                </tr>
                <tr>
                    <td>User Password:</td>
                    <td><@s.textfield name="mailProp.password" cssClass="install_input_field" /></td>
                    <td>
                        <div class="name_comment">* The mail user password</div>
                    </td>
                </tr>
            </table>
        </div>
        <div style="clear:both"></div>
        <table>
            <tr>
                <td width="250">&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>
                    <@s.submit value="Install" cssClass="input_button_style" />
                    &nbsp; <@s.reset value="%{getText('reset.button')}" cssClass="input_button_style" />
                </td>
                <td>&nbsp;</td>
            </tr>
        </table>
    </@s.form>
    <br/>
    <br>
    <div style="clear:both"></div>
</div>
<br/>
<br/>
<br/>
<br/>

</body>
</html>