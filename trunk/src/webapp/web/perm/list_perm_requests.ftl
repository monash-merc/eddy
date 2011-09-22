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
		 	<div class="none_border_block">
		 	 <@s.if test="%{actionSuccessMsg != null}">
			 	<#include "../template/action_success_msg.ftl"/>
			 </@s.if>
			</div>
		 	<@s.if test="%{permRequests.size() > 0}">
			 		<@s.iterator status="permReqStat" value="permRequests" id="pRequest" >
					<div class="left_inner_panel">
						<div class="record_data">
							<div class="name_title"><@s.property value="#pRequest.collection.name" /></div>
							<div class="record_data_inline"><@s.property value="#pRequest.collection.briefDesc" /></div>
						 	<div class="dot_bottom_border_block"></div>
					 		<div class="perm_req_left">
							 	<table width="100%">
							 		<tr>
							 			<td colspan="3">&nbsp;</td>
							 		</tr>
							 		<tr>
							 			<td colspan="3"> </td>
							 		</tr>
						 			<tr>
						 				<td width="100">User name:</td>
						 				<td><span class="grey_line_div"><@s.property value="#pRequest.requestUser.displayName" /></span></td>
						 				<td></td>
						 			</tr>
						 			<tr>
						 				<td>Email:</td>
						 				<td><span class="grey_line_div"><@s.property value="#pRequest.requestUser.email" /></span></td>
						 				<td></td>
						 			</tr>
						 			<tr>
						 				<td>Organization:</td>
						 				<td><span class="grey_line_div"><@s.property value="#pRequest.requestUser.profile.organization" /></span></td>
						 				<td></td>
						 			</tr>
						 		</table>
							 </div> 
							 <div class="perm_req_right">
								 <@s.form action="approvePermReq.jspx" namespace="/perm" method="post" >
								 	<table>
								 		<tr>
											 <td colspan="4"><center><span class="grey_line_div">Requsted Permissions</span></center></td>
										</tr>
										<tr>
											 <td colspan="4">	
											 	<@s.hidden name="permRequest.id" value="%{#pRequest.id}" />
											 	<@s.hidden name="permRequest.collection.name" value="%{#pRequest.collection.name}" />
					 	 					</td>
										</tr>
										<tr>
										 	 <td width="80"><center> View </center></td>
										 	 <td width="80"><center><@s.checkbox name="permRequest.viewAllowed"  value="%{#pRequest.viewAllowed}" /></center></td>
										 	 <td width="80"><center>Export</center></td>
										 	 <td width="80"><center><@s.checkbox name="permRequest.exportAllowed" value="%{#pRequest.exportAllowed}" /></center></td>
										</tr>
										<tr>
										 	 <td><center>Edit</center></td>
										 	 <td><center><@s.checkbox name="permRequest.updateAllowed" value="%{#pRequest.updateAllowed}"/></center></td>
										 	 <td><center>Delete</center></td>
										 	  <td><center><@s.checkbox name="permRequest.deleteAllowed"  value="%{#pRequest.deleteAllowed}"/></center></td>
										</tr>
										<tr>
										 	 <td><center>Import</center></td>
										 	 <td><center><@s.checkbox name="permRequest.importAllowed" value="%{#pRequest.importAllowed}"/></center></td>
										 	 <td><center>Permission</center></td>
										 	 <td><center><@s.checkbox name="permRequest.changePermAllowed" value="%{#pRequest.changePermAllowed}"/></center></td>
										</tr>
										<tr>
											 <td colspan="4"></td>
										</tr>
										<tr>
										 	 <td><center> </center></td>
										 	 <td><center> <@s.submit value="Grant" cssClass="input_button_sm" /> </center></td>
										 	 <td>
										 	 	<center> 
										 	 	<div class="inline_td_div"> 
										 	 		<a href="${base}/perm/rejectPermReq.jspx?permRequest.id=${pRequest.id}&permRequest.collection.name=${pRequest.collection.name}">&nbsp; Reject &nbsp; </a>
										 	 	</div>
										 	 	</center>
										 	 </td>
										 	 <td></td>
										</tr>
								 </table>
								 </@s.form>
							 </div>
						</div>
						<div style="clear:both"></div>
					 </div>
					</@s.iterator>
			 	</@s.if>
				<@s.else>
					<div class="none_border_block">
	   					<span class="name_title">You have <font color="green"> 0 </font> Permissions Requests</span>
	   				</div>
					 
					<div class="none_border_space_block"></div>
					<div style="clear:both"></div>
				</@s.else>
				
				<div class="blank_separator"></div>
		</div>
		<br/>
		<br/>
		<br/>
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