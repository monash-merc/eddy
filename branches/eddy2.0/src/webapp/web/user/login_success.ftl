<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.property value="pageTitle" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<#include "../template/action_title.ftl" />
<div class="main_body_container">
<div class="main_big_border">
	<div class="left_container_panel">
	<br/>
		<div class="left_middle_panel">
		<br/>
		<br/>
		<br/>
		<br/>
			<div class="redirect_pane"> 
				<br/>
				<b><@s.property value="user.displayName"/></b> &nbsp;&nbsp; Logged in successfully.&nbsp;&nbsp;  Welcome to <b><@s.property value="applicationName"/></b>! 
				<br/>
				<br/>
				<span class="redirect_span">After a few seconds, the page will redirect ...</span>
				<br/>	
				<br/>
				<span class="redirect_span">Problems with the redirect? Please use this <a href='${base}/<@s.property value="requestUrl" escape=false />'>direct link</a>.</span>
				<br/>
			</div>
		 	<div class="none_border_space_block"></div>
		</div>
		<div style="clear:both"></div>
		<br/>
	</div>
	<div class="right_container_panel">		 
		 <#include "../template/subnav_section.ftl" />
	</div>
	<div style="clear:both"></div>
</div> 
</div>
<br/>
<#include "../template/footer.ftl"/>

<script>
function jump()
{
        location.href = '${base}/<@s.property value="requestUrl" escape=false />';
}
setTimeout("jump()", 3000);
</script>
</body>
</html>