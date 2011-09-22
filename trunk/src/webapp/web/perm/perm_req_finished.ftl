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
		 	<div class="none_border_block">
		 	 <@s.if test="%{actionSuccessMsg != null}">
			 	<#include "../template/action_success_msg.ftl"/>
			 </@s.if>
			</div>
		 	 <div class="dotted_border_div">
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
					<tr>
						<td>
		 	 	 		</td>
					</tr>
				 	 
				</table>
		 	 </div>
		 	 <div class="none_border_block">
		 	 	The permissions you already applied for
		 	 </div>
		 	 <div class="dotted_border_div">
		 	 	<@s.form action="applyForPerms.jspx" namespace="/perm" method="post" >
			 	 <table  class="table_data" id="permission_req">
					 <tr class="bg_grey_tr">
					 	 <td><center><b> View </b></center></td>
					 	 <td><center><b> Edit </b></center></td>
					 	 <td><center><b> Import </b></center></td>
					 	 <td><center><b> Export </b></center></td>
					 	 <td><center><b> Delete </b></center></td>
					 	 <td><center><b> Permission </b></center></td>
					 </tr>
					 <tr>
					 	 <td>
					 	 	<center><@s.checkbox name="permReq.viewAllowed" disabled="true" /></center>
					 	 </td>
					 	 <td>
					 	 	<center><@s.checkbox name="permReq.updateAllowed" disabled="true" /></center>
					 	 </td>
					 	 <td>
					 	 	<center><@s.checkbox name="permReq.importAllowed" disabled="true" /></center>
					 	 </td>
					 	 <td>
					 	 	<center><@s.checkbox name="permReq.exportAllowed" disabled="true" /></center>
					 	 </td>
					 	 <td>
					 	 	<center><@s.checkbox name="permReq.deleteAllowed" disabled="true" /></center>
					 	 </td>
					 	 <td>
					 	 	<center><@s.checkbox name="permReq.changePermAllowed" disabled="true" /></center>
					 	 </td>
					 </tr>
				 </table>
				 <div class="none_border_block">
					 <@s.hidden name="collection.id" />
					 <@s.submit value="Apply Again" cssClass="input_button_normal" />
		 		 </div>
		 		</@s.form>
		 	 </div>  
		 	 <br/>
             <br/>
             <br/>
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
<#include "../template/footer.ftl"/>
</body>
</html>