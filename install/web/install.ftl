<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Welcome to System Installation</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="shortcut icon" href="${base}/images/favicon.ico" type="image/x-icon"/>

    <link href="${base}/css/merc.css" rel="stylesheet" type="text/css">
    <script>

    </script>
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
<div class="main_install_border">

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
                <div class="name_comment">* The system administrator password, if administrator is a ldap user, just put 'ldap' as password</div>
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
    </table>
</div>

<div class="p_title"><b>ANDS Metadata Registration Settings</b></div>
<div class="main_install_inner">
    <table>
        <tr>
            <td width="250">ANDS Metadat Registration Enabled:</td>
            <td><@s.select name="appProp.mdRegEnabled"  list="trueFalseMap" cssClass="input_select_normal"  /></td>
            <td>
                <div class="name_comment">* Select it if ANDS metadata registration enabled</div>
            </td>
        </tr>
        <tr>
            <td>Group Name:</td>
            <td><@s.textfield name="appProp.andsRegGroupName" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The group name in the RIF-CS file</div>
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
            <td>OzFlux Activity Key:</td>
            <td><@s.textfield name="appProp.activityKey" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The OzFlux activity rifcs key</div>
            </td>
        </tr>

        <tr>
            <td>Collection RIFCS Template:</td>
            <td><@s.textfield name="appProp.collectionRifcsTemplate" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The collection rifcs template name</div>
            </td>
        </tr>

        <tr>
            <td>Party RIFCS Template:</td>
            <td><@s.textfield name="appProp.partyRifcsTemplate" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The party rifcs template name</div>
            </td>
        </tr>

        <tr>
            <td>Research Master Party RIFCS Template:</td>
            <td><@s.textfield name="appProp.rmPartyRifcsTemplate" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The party rifcs template name</div>
            </td>
        </tr>

        <tr>
            <td>Researcher Master Web Service Name:</td>
            <td><@s.textfield name="appProp.rmWsName" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The researcher master web service name</div>
            </td>
        </tr>
        <tr>
            <td>Researcher Master Web Service Endpoint:</td>
            <td><@s.textfield name="appProp.rmWsEndpointAddress" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The researcher master web service endpoint address</div>
            </td>
        </tr>
        <tr>
            <td>Researcher Master Web Service Timeout:</td>
            <td><@s.textfield name="appProp.rmWsTimeout" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The researcher master web service timeout value</div>
            </td>
        </tr>
    </table>
</div>
<div class="p_title"><b>ANDS Handle Service Settings</b></div>
<div class="main_install_inner">
    <table>
        <tr>
            <td width="250">Handle Service Enabled:</td>
            <td><@s.select name="appProp.hdlWsEnabled"  list="trueFalseMap" cssClass="input_select_normal"  /></td>
            <td>
                <div class="name_comment">* Select it if ANDS handle persist identifier service enabled</div>
            </td>
        </tr>
        <tr>
            <td>Handle Service Host Name:</td>
            <td><@s.textfield name="appProp.hdlWsHostName" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* ANDS hanlde service host name (protocol included)</div>
            </td>
        </tr>
        <tr>
            <td>Handle Service Host Port:</td>
            <td><@s.textfield name="appProp.hdlWsHostPort" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* ANDS hanlde service host port</div>
            </td>
        </tr>
        <tr>
            <td>Handle Service Path:</td>
            <td><@s.textfield name="appProp.hdlWsPath" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* ANDS hanlde service path</div>
            </td>
        </tr>
        <tr>
            <td>Handle Service Mint Method Name:</td>
            <td><@s.textfield name="appProp.hdlWsMethod" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* ANDS hanlde service mint method name</div>
            </td>
        </tr>
        <tr>
            <td>Handle Service Application Id:</td>
            <td><@s.textfield name="appProp.hdlWsAppId" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* ANDS hanlde service application registered identifier</div>
            </td>
        </tr>
        <tr>
            <td>Handle Service Resolver Server:</td>
            <td><@s.textfield name="appProp.hdlResolverAddress" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* A hanlde resolver server name</div>
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
<div class="p_title"><b>Ldap Configuration</b></div>
<div class="main_install_inner">
    <table>
        <tr>
            <td width="250">LADP Authentication Supported:</td>
            <td><@s.select name="ldapProp.ldapSupported"  list="trueFalseMap" cssClass="input_select_normal"  /></td>
            <td>
                <div class="name_comment">* Ldap authentication supported</div>
            </td>
        </tr>


        <tr>
            <td width="250">LADP Authentication Web Service Enabled:</td>
            <td><@s.select name="ldapWsProp.ldapWsEnabled"  list="trueFalseMap" cssClass="input_select_normal"  /></td>
            <td>
                <div class="name_comment">* Ldap authentication web service enabled</div>
            </td>
        </tr>
        <tr>
            <td colspan="3"></td>
        <tr>
        <tr>
            <td colspan="3"><font color="#006400">Provide the following three fields when Ldap authentication web service enabled</font></td>
        </tr>
        <tr>
            <td colspan="3"></td>
        <tr>
        <tr>
            <td>LADP Authentication Web Service Host:</td>
            <td><@s.textfield name="ldapWsProp.ldapWsServer" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The ldap authentication web service host</div>
            </td>
        </tr>

        <tr>
            <td>LADP Authentication Web Service Port:</td>
            <td><@s.textfield name="ldapWsProp.ldapWsPort" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The ldap authentication web service Port</div>
            </td>
        </tr>

        <tr>
            <td>Ignore Certificate Error?</td>
            <td><@s.select name="ldapWsProp.certErrorIgnore"  list="trueFalseMap" cssClass="input_select_normal"  /></td>
            <td>
                <div class="name_comment">* If ignore LADP authentication web service certificate error, please select TRUE</div>
            </td>
        </tr>
        <tr>
            <td colspan="3"></td>
        <tr>
        <tr>
            <td colspan="3"><font color="#006400">Provide the following Ldap fields when Ldap authentication supported and ldap athentiction web service disabled</font></td>
        <tr>
        <tr>
            <td colspan="3"></td>
        <tr>
        <tr>
            <td>LADP Server:</td>
            <td><@s.textfield name="ldapProp.ldapServer" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The ldap server</div>
            </td>
        </tr>
        <tr>
            <td>Base DN:</td>
            <td><@s.textfield name="ldapProp.baseDN" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The base dn value</div>
            </td>
        </tr>
        <tr>
            <td>Bind Base DN Required:</td>
            <td><@s.select name="ldapProp.bindBaseDnRequired"  list="trueFalseMap" cssClass="input_select_normal"  /></td>
            <td>
                <div class="name_comment">* If binding the base dn required when connection to the ldap server</div>
            </td>
        </tr>
        <tr>
            <td>UID Attribute Name:</td>
            <td><@s.textfield name="ldapProp.attUID" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The uid attribute name in ldap server</div>
            </td>
        </tr>
        <tr>
            <td>Mail Attribute Name:</td>
            <td><@s.textfield name="ldapProp.attMail" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The mail attribute name in ldap server</div>
            </td>
        </tr>
        <tr>
            <td>Gender Attribute Name:</td>
            <td><@s.textfield name="ldapProp.attGender" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The gender attribute name in ldap server</div>
            </td>
        </tr>
        <tr>
            <td>CN Attribute Name:</td>
            <td><@s.textfield name="ldapProp.attCN" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The cn attribute name in ldap server</div>
            </td>
        </tr>
        <tr>
            <td>Givenname Attribute Name:</td>
            <td><@s.textfield name="ldapProp.attGivenname" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The givenname attribute name in ldap server</div>
            </td>
        </tr>
        <tr>
            <td>Surname Attribute Name:</td>
            <td><@s.textfield name="ldapProp.attSn" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The sn attribute name in ldap server</div>
            </td>
        </tr>
        <tr>
            <td>Personal title Attribute Name:</td>
            <td><@s.textfield name="ldapProp.attPersonalTitle" cssClass="install_input_field" /></td>
            <td>
                <div class="name_comment">* The personaltitle attribute name in ldap server</div>
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td>
                <@s.submit value="Install" cssClass="input_button_style" /> &nbsp; <@s.reset value="%{getText('reset.button')}" cssClass="input_button_style" />
            </td>
            <td>&nbsp;</td>
        </tr>
    </table>
</div>
</@s.form>
<br/>
<br>
</div>
<div style="clear:both"></div>
</div>
<br/>
<br/>
<br/>
<br/>

</body>
</html>