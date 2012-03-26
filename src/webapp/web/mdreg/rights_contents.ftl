<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<br/>
<div class="mcpop_pmain_div">
	<#include "../template/action_errors.ftl" />
	<div class="mcpop_title">
	<@s.if test="%{rights.rightsType == 'cccl_license'}">
		Creative Commons License
	</@s.if>
	<@s.if test="%{rights.rightsType == 'ccpd_license'}">
		Creative Commons CC0 Waiver
	</@s.if>
	<@s.if test="%{rights.rightsType == 'user_license'}">
		Define Your Own License
	</@s.if>
	</div>
	<div class="mcpop_field">
		<div class="mcpop_center">
		<@s.hidden name="rights.rightsType" id="prights_type" />
		<@s.hidden name="rights.commercial" id="prights_comm" />
		<@s.hidden name="rights.derivatives"  id="prights_deri" />
		<@s.hidden name="rights.jurisdiction" id="prights_juri" />
		<@s.hidden name="rights.rightContents" id="prights_cont" />
			<br/>
			<br/>
			<br/>
			<br/>
			${rights.rightContents}
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
		</div>
	</div>
	<div class="mcpop_bddiv">
		<@s.if test="%{rights.rightsType == 'cccl_license'}">
			<input type="button" value=" Back " class="mcpop_button" onclick="window.location = '${base}/data/selectRights.jspx?collection.id=<@s.property value='collection.id' />&rights.rightsType=${rights.rightsType}&rights.commericial=${rights.commercial}&rights.derivatives=${rights.derivatives}&rights.jurisdiction=${rights.jurisdiction}';">
		</@s.if>
		<@s.if test="%{rights.rightsType == 'user_license'}">
			<input type="button" value=" Back " class="mcpop_button" onclick="window.location = '${base}/data/rightsOptions.jspx?collection.id=<@s.property value='collection.id' />&rights.rightsType=${rights.rightsType}';">
		</@s.if>
		&nbsp;&nbsp; <input type="button"  value=" Cancel " class="mcpop_button" id="cancelRights" /> &nbsp;&nbsp; <input type="button" value=" Save " id="saveRights" class="mcpop_button" />
		
	</div>
</div>
</body>
</html>