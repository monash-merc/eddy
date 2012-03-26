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
<!-- Navigation Title -->
<#include "../template/action_title.ftl" />
<!-- End of Navigation Title -->
<div class="main_body_container">
<div class="main_big_border">
	<div class="left_container_panel">
	<br/>
	<#include "../template/action_errors.ftl" />
	<div class="left_middle_panel">
		 <div class="none_border_block">
		 	 <@s.if test="%{actionSuccessMsg != null}">
			 	<#include "../template/action_success_msg.ftl"/>
			 </@s.if>
		</div>
		<div class="blank_separator"></div>
			<div class="single_border_block">
				<table class="table_col">
					<tr>
						<td><div class="name_title"><@s.property value="collection.name"/></div></td>
					</tr>
					<tr>
						<td><div class="inline_span_justify"><@s.property  value="collection.briefDesc" escape=false /></div></td>
					</tr>
					<tr>
						<td>
							<span class="inline_span2">Created by <@s.property  value="collection.owner.displayName"  />, &nbsp;&nbsp;&nbsp;&nbsp; Creation date: <@s.date name="collection.createdTime" format="yyyy-MM-dd hh:mm" />
							&nbsp;&nbsp;&nbsp;&nbsp; Modified by <@s.property value="collection.modifiedByUser.displayName" />, &nbsp;&nbsp;&nbsp;&nbsp; 
						 		Modified date: <@s.date name="collection.modifiedTime" format="yyyy-MM-dd hh:mm" /></span>
						</td>
					</tr>
					<tr><td>&nbsp;</td></tr>
					<tr>
						<td>
		 	 	 			<div class="inline_td_div"> 
		 	 	 				<a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}"">&nbsp; View details &nbsp;</a>
		 	 	 		 	</div>
		 	 	 		</td>
					</tr>
				 	<tr>
						<td></td>
					</tr>
				</table>
		 	 </div>
		 	<div class="blank_separator"></div>
		 	<div class="bgcolor_none_border_div">
		 		<div class="p_title"><b>Access Control Permissions</b></div>
		 	</div>	
		 	<div class="blank_separator"></div>
		 	<div class="single_border_block">
		 		<div class="grey_font_span">There are three types of the accesss control permissions for a collection in the system:
		 			<ul>
		 				<li><b>All Anonymous Users Permissions</b> - Permissions which are granted to all users who are not logged in the system</li>
		 				<li><b>All Registered Users Permissions</b> - Permissions which are granted to all registered users in the system</li>
		 				<li><b>An Individual User Permissions</b> - Permissions which are granted to a registered user in the system</li>
		 			</ul>
		 			<p>Permissions can be granted to <b>All Registered Users</b> or<b> All Anonymous Users</b> or <b>An Individual User</b>.
		 			<p>If the collection permissions are neither granted to <b>All Registered Users</b> nor <b>All Anonymous Users</b>, which means this collection is a private collection.</p>
		 			<p>You can grant the specific permissions to an individual user in a collection, and the all allowed permissions for <b>All Anonymous Users</b> will be inherited by this individual user.</p>
		 		</div>
		 	</div>
		 	<div class="blank_separator"></div>
		 	<div class="bgcolor_none_border_div">
		 		<div class="p_title"><b>Permissions</b></div>
		 	 </div>	
		 	 <div class="none_border_block">
		 		<div class="name_title">Grant permission to &nbsp;&nbsp;
		 		<@s.select name="selected_username" headerKey="-1" headerValue="-- Select User --" list="activeUsers" value="-1" cssClass="input_select_normal"/> &nbsp;&nbsp;
				<input type="button" name="add_permission" id="add_permission" value = "Add" class="input_button_normal" />
				</div>
		 	</div>
		 	<form action="setColPermissions.jspx" namespace="/perm" method="post">	
		 	<@s.hidden name="collection.id" />
			<@s.hidden name="collection.owner.id" />
			<@s.hidden name="viewType" />
		 	 
		 	<div class="none_border_block">
		 		<@s.submit value="Save All" cssClass="input_button_normal" id="perm_form" />
		 	</div>
		 	<div class="none_border_block2">
			 
		 	 <table class="table_data" id="user_permissions">
		 	 	<thead>
			 		 <tr class="bg_grey_tr">
			 	 		<td width="200"><center><b> User Name </b></center></td>
			 	 		<td width="30"><center><b> View </b></center></td>
			 	 		<td width="30"><center><b> Edit </b></center></td>
			 	 		<td width="30"><center><b> Import </b></center></td>
			 	 		<td width="30"><center><b> Export </b></center></td>
			 	 		<td width="30"><center><b> Delete </b></center></td>
			 	 		<td width="65"><center><b> Access Control </b></center></td>
			 	 	</tr>
			 	  	<tr>
						<td>
					 	 	<center>All anonymous users</center>
					 	 	<@s.hidden name="coPermForAnony.id"  />
					 	 	<@s.hidden name="coPermForAnony.uid" />
					 	 	<@s.hidden name="coPermForAnony.userName" />
					 	</td>
					 	<td><center><@s.checkbox name="coPermForAnony.viewAllowed" /></center></td>
					 	<td><center><@s.hidden name="coPermForAnony.editAllowed" /></center></td>
					 	<td><center><@s.hidden name="coPermForAnony.importAllowed" /></center></td>
					 	<td><center><@s.checkbox name="coPermForAnony.exportAllowed" /></center></td>
					 	<td><center><@s.hidden name="coPermForAnony.deleteAllowed" /></center></td>
					 	<td><center><@s.hidden name="coPermForAnony.changePermAllowed" /></center></td>
					</tr>
					<tr>
				 	 	<td class="perm_delim" colspan="7"></td>
				 	</tr>
			 	 	<tr>
				 	 	<td>
				 	 		<center>All registered users</center>
				 	 		<@s.hidden name="coPermForAllUser.id" />
				 	 		<@s.hidden name="coPermForAllUser.uid" />
				 	 		<@s.hidden name="coPermForAllUser.userName" />
				 	 	</td>
				 	 	<td><center><@s.checkbox name="coPermForAllUser.viewAllowed" /></center></td>
				 	 	<td><center><@s.checkbox name="coPermForAllUser.editAllowed" /></center></td>
				 	 	<td><center><@s.checkbox name="coPermForAllUser.importAllowed" /></center></td>
				 	 	<td><center><@s.checkbox name="coPermForAllUser.exportAllowed" /></center></td>
				 	 	<td><center><@s.checkbox name="coPermForAllUser.deleteAllowed" /></center></td>
				 	 	<td><center><@s.checkbox name="coPermForAllUser.changePermAllowed" /></center></td>
				 	 </tr>
				 	 <tr>
				 	 	<td class="perm_delim" colspan="7"></td>
				 	 </tr>
				</thead>
				<tbody>
					<@s.iterator status="permStatus" value="permissionBeans" id="permBean" >
					<tr>
					 	<td>
					 	 	<center><@s.property  value="#permBean.userName" /></center>
					 	 	<@s.hidden name="permissionBeans[%{#permStatus.index}].id"  value="%{#permBean.id}" />
					 	 	<@s.hidden name="permissionBeans[%{#permStatus.index}].uid" id ="user_id" value="%{#permBean.uid}" />
					 	 	<@s.hidden name="permissionBeans[%{#permStatus.index}].userName" value="%{#permBean.userName}" />
					 	</td>
					 	<td><center><@s.checkbox name="permissionBeans[%{#permStatus.index}].viewAllowed" /></center></td>
					 	<td><center><@s.checkbox name="permissionBeans[%{#permStatus.index}].editAllowed" /></center></td>
					 	<td><center><@s.checkbox name="permissionBeans[%{#permStatus.index}].importAllowed" /></center></td>
					 	<td><center><@s.checkbox name="permissionBeans[%{#permStatus.index}].exportAllowed" /></center></td>
					 	<td><center><@s.checkbox name="permissionBeans[%{#permStatus.index}].deleteAllowed" /></center></td>
					 	<td><center><@s.checkbox name="permissionBeans[%{#permStatus.index}].changePermAllowed" /></center></td>
					</tr>
					</@s.iterator>
				</tbody>
			 </table>
			 </div>
			 </form>
			 <div class="blank_separator"></div>
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


