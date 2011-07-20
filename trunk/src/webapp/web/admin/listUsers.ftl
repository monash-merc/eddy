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
		<#include "../template/action_errors.ftl" />
		<div class="left_middle_panel">		
		 
		<div class="none_border_block">
			<span class="name_title">Total <font color="green"> ${userPagination.totalRecords} </font> Users</span>
		</div>
		<@s.if test="%{userPagination.pageResults.size() > 0}">
			<div class="msg_content">
				<a href="${base}/${pageLink}${pageSuffix}${userPagination.pageNo}" class="page_url"></a>
			</div>
			<div class="single_line_center_block">	
				<span class="inline_span">				
					Page size: <@s.select id="item_select_size" name="sizePerPage" headerKey="${sizePerPage}"  list="pageSizeMap" cssClass="input_select_small" />
					&nbsp;Sorted by: <@s.select id="item_select_order" name="orderBy" headerKey="${orderBy}"  list="orderByMap" cssClass="input_select_small" />
					&nbsp;Ordered by: <@s.select id="item_select_otype" name="orderByType" headerKey="${orderByType}"  list="orderByTypeMap" cssClass="input_select_small" />
				</span>
			</div>
			<div class="none_border_block3">
				<br/>
				<table class="table_data">
					<tr class="bg_grey_tr">
				 		<td width="100"><center><b>Name</b></center></td>
				 	 	<td width="180" ><center><b>Email</b></center></td>
				 	 	<td width="200"><center><b>Organization</b></center></td>
				 	 	<td width="70"><center><b>User Type</b></center></td>
				 	 	<td width="40"><center><b>Active</b></center></td>
				 	 	<td width="70"><center><b>&nbsp;</b></center></td>
				 	 </tr>
				 	 <@s.iterator status="userStat" value="userPagination.pageResults" id="userResult" >
				 	 <tr class="tr_small">
				 	 	<td style="color: black"><center><@s.property value="#userResult.displayName" /></center></td>
				 	 	<td><center><@s.property value="#userResult.email" /></center></td>
				 	 	<td><center><@s.property value="#userResult.profile.organization" /></center></td>
				 	 	<td>
				 	 		<center>
				 	 			<@s.if test = "%{#userResult.userType == 1 }">Super Admin</@s.if>
				 	 			<@s.elseif test = "%{#userResult.userType == 2 }">Admin</@s.elseif>
				 	 			<@s.else>User</@s.else>
				 	 		</center>
				 	 	</td>
				 	 	<td><center><@s.property value="#userResult.activated"/></center></td>
				 	 	<td>
				 	 		<div class="tab_div">
					 	 		<center>
					 	 			<@s.if test="%{(user.userType == 1 || user.userType == 2) && (#session.authen_user_id != #userResult.id) && (#userResult.userType !=1)}">
					 	 				<a href="${base}/admin/showUserStatus.jspx?regUser.id=${userResult.id}">&nbsp; Manage &nbsp; </a>
					 	 			</@s.if>
					 	 			<@s.else>
					 	 				<a href="${base}/admin/showUserStatus.jspx?regUser.id=${userResult.id}">&nbsp;&nbsp;&nbsp;&nbsp; View &nbsp;&nbsp;&nbsp;&nbsp; </a>
					 	 			</@s.else>
					 	 		</center>
				 	 		</div>
				 	 	</td>
				 	 </tr>
				 	 </@s.iterator>
				</table>
			</div> 
			<@s.if test="%{userPagination.pageResults.size() < 4}">
					<div class="none_border_space_block"></div>
			</@s.if>
			<br/>
			<#include "../pagination/pag_style3.ftl" />
			<div style="clear:both"></div>
		</@s.if>
		<@s.else>
			<div class="bottom_border_block"></div>
			<br/>
			<div class="none_border_space_block"></div>
		</@s.else>
	</div>
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
</body>
</html>