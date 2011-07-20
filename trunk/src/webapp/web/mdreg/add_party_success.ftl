<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<#include "../template/jquery_header.ftl"/>
<style type="text/css">
.error_msg_section{
	background: none repeat scroll 0 0 white;
    margin: 10px auto;
    text-align: center;
    width: 410px;
}
</style>
</head>
<body>
<br/>
	<div class="mcpop_pmain_div">
		<#include "../template/action_errors.ftl" />
		<div class="mcpop_title">
			<@s.if test="%{addPartyType == 'rm_party'}">
				Search a researcher from the Research Master Web Service
			</@s.if>
			<@s.if test="%{addPartyType == 'user_defined_party'}">
				Input a researcher information manually
			</@s.if>
	    </div>
	    
	 	<div class="mcpop_field">
			<br/>
			<div class="mcpop_input_value">
				<span class="inline_span2">
				<@s.if test="%{addPartyType == 'rm_party'}">
					Found a researcher information as bellow:
				</@s.if>
				<@s.else>
					&nbsp;
				</@s.else>
				</span>
			</div>
			<div style="clear:both"></div>
		 	<div class="blank_separator"></div>
			<div class="mcpop_out_display">
				 <@s.property value="addedPartyBean.personTitle" /> <@s.property value="addedPartyBean.personGivenName" /> <@s.property value="addedPartyBean.personFamilyName" /> - <@s.property value="addedPartyBean.groupName" />
			</div>
			<div class="mcpop_input_value">
				<@s.hidden name="addedPartyBean.partyKey" id = "ands_p_key" />
				<@s.hidden name="addedPartyBean.personTitle" id = "ands_p_title"/>
			    <@s.hidden name="addedPartyBean.personGivenName" id = "ands_p_givenname" />
			    <@s.hidden name="addedPartyBean.personFamilyName" id = "ands_p_sname"/>
			    <@s.hidden name="addedPartyBean.email" id = "ands_p_email"/>
			    <@s.hidden name="addedPartyBean.address" id = "ands_p_address" />
			    <@s.hidden name="addedPartyBean.url" id = "ands_p_url"/>
			    <@s.hidden name="addedPartyBean.identifierType" id = "ands_p_idtype"/>
			    <@s.hidden name="addedPartyBean.identifierValue" id = "ands_p_idvalue" />
			    <@s.hidden name="addedPartyBean.originateSourceType" id = "ands_p_srctype"/>
			    <@s.hidden name="addedPartyBean.originateSourceValue" id = "ands_p_srcvalue" />
			    <@s.hidden name="addedPartyBean.groupName" id = "ands_p_groupname"/>
			    <@s.hidden name="addedPartyBean.userOwned" id = "ands_p_userowned"/>
			    <@s.hidden name="addedPartyBean.fromRm" id = "ands_p_fromrm"/>
			</div>
			<br/>
			<br/>
			<br/>
			<br/>
		</div>
		<div class="mcpop_bddiv">
			<input type="button" value=" Back " class="mcpop_button" onclick="window.location = '${base}/data/addPartyOpt.jspx?addPartyType=${addPartyType}';" /> &nbsp;&nbsp; <input type="button"  value=" Cancel " class="mcpop_button" id="cancelAddParty" /> &nbsp;&nbsp; <input type="submit" name="options" value=" Save " class="mcpop_button" id="save_rm_party" />
		</div>
		 
	</div>
</body>
</html>