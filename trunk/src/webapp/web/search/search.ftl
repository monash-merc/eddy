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

	<@s.if test="%{#session.authentication_flag =='authenticated'}">  
		<div class="left_container_panel">
	</@s.if>
	<@s.else>
		<div class="none_boder_left_container">
	</@s.else>
		<br/>
		<#include "../template/action_errors.ftl" />
		<div class="left_middle_panel">
			<div class="blank_separator"></div>
			<div class="blank_separator"></div>
			<div class="single_border_block">
				<@s.form action="search.jspx" namespace="/search" method="post">
		 		 <#include "../search/search_conditions.ftl" />
		 		</@s.form>
		 	</div>
		  	<#include "../search/search_results.ftl" />
		</div>
		<br/>
	</div>
	<@s.if test="%{#session.authentication_flag =='authenticated'}">  
		<div class="right_container_panel">		
			 <#include "../template/subnav_section.ftl" />
		</div>
	</@s.if>
	<div style="clear:both"></div>
</div> 
</div>
<br/>
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>