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
		Creative Commons CC0 Waiver
	</div>
	<div class="mcpop_field">
	<br/>
		<div class="license_waiver">
			<p>Are you certain you wish to waive all rights to your work? Once these rights are waived, you cannot reclaim them.</p>

			<p>In particular, if you are an artist or author who depends upon copyright for your income, Creative Commons does not recommend that you use this tool.</p>

			<p>If you don't own the rights to this work, then do not use CC0. If you believe that nobody owns rights to the work, then the Public Domain Mark may be what you're looking for.</p>
		</div>
	<br/>
	</div>
	<div class="mcpop_bddiv">
		<@s.hidden name="rights.rightsType" id="prights_type"/>
		<@s.hidden name="rights.commercial" id="prights_comm"/>
		<@s.hidden name="rights.derivatives" id="prights_deri"/>
		<@s.hidden name="rights.jurisdiction" id="prights_juri"/>
		<@s.hidden name="rights.rightContents" id="prights_cont" />
		<input type="button" value=" No, I Do Not Waive " class="waiver_button" onclick="window.location = '${base}/data/rightsOptions.jspx?collection.id=${collection.id}&rights.rightsType=${rights.rightsType}';">
		 &nbsp;&nbsp; <input type="button" value=" Yes, I Wavie " id="saveRights" class="waiver_button" />
	</div>
</div>
</body>
</html>