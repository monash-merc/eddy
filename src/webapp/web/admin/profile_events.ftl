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
			<#include "../template/action_errors.ftl" /> 
			<div class="non_border_div">
		 		<div class="p_title"><b>Profile</b></div>
		 	</div>
		 	<div class="dotted_border_div">
		 		<table width="100%">
		 			<tr>
		 				<td width="150"> <b> User name:</b> </td>
		 				<td><span class="grey_line_div"><@s.property value="user.displayName"/></span></td>
		 				<td></td>
		 			</tr>
		 			<tr>
		 				<td> <b> Gender: </b></td>
		 				<td>
		 					<span class="grey_line_div">
				 				<@s.if test="%{profile.gender != null}">
				 					<@s.property value="profile.gender" />
				 				</@s.if>
				 				<@s.else>
				 					Not specified
				 				</@s.else>
				 			</span>
				 		</td><td></td>
		 			</tr>
		 			<tr>
		 				<td> <b> Joined:</b> </td><td><span class="grey_line_div"><@s.date name="user.registedDate" format="yyyy-MM-dd" /></span></td><td></td>
		 			</tr>
		 			<tr>
		 				<td> <b> Email:</b> </td>
		 				<td><span class="grey_line_div"><@s.property value="user.email" /></span></td>
		 				<td></td>
		 			</tr>
		 			<tr>
		 				<td> <b> Organization:</b> </td>
		 				<td>
		 					<span class="grey_line_div">
		 					 	<@s.if test="%{profile.organization != null}">
				 					<@s.property value="profile.organization" />
				 				</@s.if>
				 				<@s.else>
				 					Not specified
				 				</@s.else>
		 					</span>
		 				</td>
		 				<td aligh="right"><div class="inline_small_div"><a href="${base}/admin/showProfileUpdate.jspx"> Edit Profile </a> <a href="${base}/admin/showChangePwd.jspx">Change Password</a> </div></td>
		 			</tr>
		 		</table>
		 	</div>
		 	<div class="non_border_div">
		 		<div class="p_title"><b>Permission Requests</b></div>
		 	</div>
		 	<div class="dotted_border_div">
			 	<@s.if test="%{permReqPagination.pageResults.size() > 0}">
			 		<@s.iterator status="permReqStat" value="permReqPagination.pageResults" id="permReqResult" >
						<@s.if test="%{#permReqStat.index +1 <= 5}">	
						<div class="left_inner_panel">
							<div class="record_data_img2"><img src="${base}/images/dot.png" align="top" border="0" /></div>
							<div class="record_data2">
								<div class="record_data_inline">
									<font color="#0E774A"><@s.date name="#permReqResult.requestTime"  format="dd-MM-yyyy 'at' hh:mm a" /></font> &nbsp;&nbsp;<@s.property value="#permReqResult.requestUser.displayName" /> applied for the permissions of <@s.property value="#permReqResult.collection.name" /> </div>
							</div>
							<div style="clear:both"></div>
						</div>
						</@s.if>
					</@s.iterator>
					<div class="record_data_link2"><a href="${base}/perm/listPermRequests.jspx"> &nbsp;&nbsp; Details ...&nbsp;</a></div> 
					<div style="clear:both"></div>
			 	</@s.if>
				<@s.else>
			 			<span class="grey_line_div">
							No Permissions Requests
						</span>
				</@s.else>
				<div class="blank_separator"></div>
		 	</div>
			<div class="non_border_div">
		 		<div class="p_title"><b>Latest Events</b></div>
		 	</div>
			<div class="dotted_border_div">
				<@s.if test="%{eventPagination.pageResults.size() > 0}">
					<@s.iterator status="eventStat" value="eventPagination.pageResults" id="eventResult" >
						<@s.if test="%{#eventStat.index +1 <= 5}">	
						<div class="left_inner_panel">
							<div class="record_data_img2"><img src="${base}/images/dot.png" align="top" border="0" /></div>
							<div class="record_data2">
								<div class="record_data_inline"><font color="#0E774A"><@s.date name="#eventResult.createdTime"  format="dd-MM-yyyy 'at' hh:mm a" /></font> &nbsp;&nbsp;<@s.property value="#eventResult.event" />, by <@s.property value="#eventResult.operator.displayName" /></div>
							</div>
							<div style="clear:both"></div>
						</div>
						</@s.if>
					</@s.iterator>
					
				</@s.if>
				<@s.else>
					<span class="grey_line_div">
						No Events
					</span>
				</@s.else>
				<@s.if test="%{eventPagination.pageResults.size() > 5}">
					<div class="record_data_link2"><a href="${base}/admin/listUserEvents.jspx"> &nbsp;&nbsp; More ...&nbsp; &nbsp;</a></div> 
					<div style="clear:both"></div>
				</@s.if>
				<div class="blank_separator"></div>
			</div>
			<br/>
		</div>
		<br/>
		<div style="clear:both"></div>
	</div>
	<div class="right_container_panel">		 
		 <#include "../template/subnav_section.ftl" />
	</div>
	<div style="clear:both"></div>
</div> 
</div>
<br/>
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>