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
	<div class="mcpop_pmain_div">
		<br/>
		<div class="mcpop_divbd"></div>
	 	<div class="mcpop_field2">
			<br/>
			<#include "../template/action_errors.ftl" /> 
			<br/>
			<br/>
			<br/>
			<div class="mcpop_errors">
				Please contact the system administrator 
			</div>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
		</div>
	</div>
</body>
</html>