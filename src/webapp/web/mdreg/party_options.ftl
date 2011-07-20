<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<#include "../template/jquery_header.ftl"/>
<style type="text/css">
.error_msg_section{
	background: none repeat scroll 0 0 white;
    border: 1px solid #E1E8F0;
    margin: 10px auto;
    text-align: center;
    width: 460px;
}
</style>
</head>
<body>
<br/>
<div class="mcpop_pmain_div">
	<#include "../template/action_errors.ftl" />
	<div class="mcpop_title">Adding Researcher Options</div>
	<div class="mcpop_comments">
		Select one of the following adding researcher options:
	</div>
	<@s.form action="addPartyOpt.jspx" namespace="/data" method="post">
		<div class="mcpop_field">
			<@s.radio name="addPartyType" theme = "merctheme" list="addPartyTypeMap" id="addPartyType" value="addPartyType"  title="Please select an adding researcher option"/>
			<br/>
		</div>
		<div class="mcpop_bddiv">
			<input type="button"  value=" Cancel " class="mcpop_button" id="cancelAddParty" /> &nbsp;&nbsp; <@s.submit value="Next" cssClass="mcpop_button" />
		</div>
	</@s.form>
</div>
<br/>

</body>
</html>