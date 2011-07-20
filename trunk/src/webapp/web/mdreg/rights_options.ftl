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
	<div class="mcpop_title">License Options</div>
	<div class="mcpop_comments">
		Select the License you want to apply to this collection so that interested people understand what they are entitled to do with your published data
	</div>
	<@s.form action="selectRights.jspx" namespace="/data" method="post">
		<div class="mcpop_field">
			<@s.radio name="rights.rightsType" theme = "merctheme" list="rightsMap" id="licenseType" value="rights.rightsType"  title="Please select a License"/>
			<br/>
		</div>
		<div class="mcpop_bddiv">
			<@s.hidden name="collection.id" />
			<input type="button"  value=" Cancel " class="mcpop_button" id="cancelRights" /> &nbsp;&nbsp; <@s.submit value="%{getText('license.next.button')}" cssClass="mcpop_button" />
		</div>
	</@s.form>
</div>
<br/>

</body>
</html>