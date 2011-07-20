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
			<div class="blank_separator"></div>
		 	<div class="dotted_border_div">
		 		<div class="blank_separator"></div>
		 		<div class="p_title"><b>Your basic information</b></div>
		 		<form action="updateProfile.jspx" namespace="/data" method="post">	
				<table width="100%">
		 			<tr>
		 				<td width="150">User name: </td>
		 				<td><span class="grey_line_div"><@s.property value="user.displayName"/></span></td>
		 				<td></td>
		 			</tr>
		 			<tr>
		 				<td>Joined: </td><td><span class="grey_line_div"><@s.date name="user.registedDate" format="yyyy-MM-dd" /></span></td>
		 				<td></td>
		 			</tr>
		 			 
		 			<tr>
		 				<td> Gender: </td>
		 				<td>
		 					<span class="grey_line_div"><@s.property value="profile.gender" /></span>
				 		</td>
				 		<td></td>
		 			</tr>
		 		</table>
		 	 
		 		<div class="p_title"><b>Your contact details</b></div>
		 		<table>
		 			<tr>
		 				<td> Email: </td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div"><@s.property value="user.email" /></span>
		 				</td>
		 			</tr>
		 			<tr>
		 				<td> Contact Details: </td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div"><@s.property value="profile.contactDetails" escape=false  /></span>
		 				</td>
		 			</tr>
		 		</table>
		 		<div class="p_title"><b>Your location</b></div>
		 		<table>
		 			<tr>
		 				<td> Address: </td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div"><@s.property value="profile.address" /></span>
		 				</td>
		 			</tr>
		 			<tr>
		 				<td> City: </td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div"><@s.property value="profile.city" /></span>
		 				</td>
		 			</tr>
		 			<tr>
		 				<td> State: </td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div"><@s.property value="profile.state" /></span>
		 				</td>
		 			</tr>
		 			<tr>
		 				<td> Post code: </td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div"><@s.property value="profile.postcode" /></span>
		 				</td>
		 			</tr>
		 			<tr>
		 				<td> Country: </td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div"><@s.property value="profile.country" /></span>
		 				</td>
		 			</tr>
		 		</table>
		 		<div class="p_title"><b>Your work(professional) life</b></div>
		 		<table>
		 			<tr>
		 				<td> Field(Industry): </td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div"><@s.property value="profile.industryField" /></span>
		 				</td>
		 			</tr>
		 			<tr>
		 				<td> Occupation(Roles): </td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div"><@s.property value="profile.occupation" /></span>
		 				</td>
		 			</tr>
					<tr>
		 				<td> Organization you belong to: </td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div"><@s.property value="profile.organization" /></span>
		 				</td>
		 			</tr>
		 			<tr>
		 				<td> Professional Interests: </td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div"><@s.property value="profile.interests" escape=false /></span>
		 				</td>
		 			</tr>
		 			<tr>
		 				<td><div class="inline_td_div"><a href="${base}/admin/showProfileUpdate.jspx">&nbsp;&nbsp;&nbsp;&nbsp; Edit &nbsp;&nbsp;&nbsp;&nbsp;</a></div></td>
		 			</tr>
		 			<tr>
		 				<td>&nbsp;</td>
		 			</tr>
		 		</table>
		 		</form>
		 	 </div>
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