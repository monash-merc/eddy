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
			<div class="error_msg_div">
			    <#include "../template/action_errors.ftl" />
			</div>
			<div class="none_border_space_block"></div>
            <br/>
            <br/>
		</div>
		<br/>
	</div>
	<div class="right_container_panel">		
		<@s.if test="%{#session.authentication_flag =='authenticated'}">   
			<#include "../template/subnav_section.ftl" />
		</@s.if>
	</div>
	<div style="clear:both"></div>
</div> 
</div>
<br/>
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>