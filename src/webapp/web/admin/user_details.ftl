<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.text name="user.all.users.title" /> - <@s.text name="admin.view.user.details" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
	<div class="div_inline">&nbsp;&nbsp;</div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline"><a href="${base}/admin/listUsers.jspx"><@s.text name="user.all.users.title" /></a></div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline"><@s.text name="admin.view.user.details" /></div>		
</div>
<div style="clear:both"></div> 
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
		 	<!-- User Details -->
		 	<div class="dotted_border_div">
		 		
		 		<table width="100%" class="no_bd_tab_data" >
		 			<tr>
		 				<td width="100" rowspan="5"><div class="user_avatar"><img src="${base}/user/viewImage.jspx?avatarUserId=${regUser.id}"></div></td>
		 			</tr>
		 			<tr>
		 				<td align="right" width="100"> <b> User name: </b></td>
		 				<td style="text-indent: 5px;"><span class="grey_line_div"><@s.property value="regUser.displayName"/></span></td>
		 				<td></td>
		 			</tr>
		 			<tr>
		 				<td align="right">  <b> Gender: </b> </td>
		 				<td style="text-indent: 5px;">
		 					<span class="grey_line_div">
				 				<@s.if test="%{regUser.profile.gender != null}">
				 					<@s.property value="regUser.profile.gender" />
				 				</@s.if>
				 				<@s.else>
				 					Not specified
				 				</@s.else>
				 			</span>
				 		</td>
				 		<td></td>
		 			</tr>
		 			<tr>
		 				<td align="right">  <b> Joined: </b></td>
		 				<td style="text-indent: 5px;"><span class="grey_line_div"><@s.date name="regUser.registedDate" format="yyyy-MM-dd" /></span></td>
		 				<td></td>
		 			</tr>
		 			<tr>
		 				<td align="right">  <b> Email: </b></td>
		 				<td style="text-indent: 5px;"><span class="grey_line_div"><@s.property value="regUser.email" /></span></td>
		 				<td ></td>
		 			</tr>
		 			<tr>
		 				<td></td>
		 				<td align="right"> <b> Active: </b></td>
		 				<td style="text-indent: 5px;"><span class="grey_line_div"><@s.property value="regUser.activated"/></span></td>
		 				<td align="center">
		 					<@s.if test="%{(user.userType == 1 || user.userType == 2) && (#session.authen_user_id != regUser.id) && (regUser.userType !=1)}">
		 					<div class="tab_div">
		 					<@s.form action="manageUser.jspx" namespace="/admin" method="post">
		 						<@s.hidden name="regUser.id" />
		 						<@s.if test = "%{regUser.activated == true }">
		 							<@s.hidden name="manageType" value="deactivate" />
		 							<@s.submit value="Deactivate" cssClass="input_button_normal" />
		 						</@s.if>
		 						<@s.else>
		 							<@s.hidden name="manageType" value="activate" />
		 							<@s.submit value="Activate" cssClass="input_button_normal" />
		 						</@s.else>
		 					</@s.form>
		 					</div>
		 					</@s.if>
		 				</td> 
		 			</tr>
		 			<tr>
		 				<td></td>
		 				<td align="right"> <b> User Type: </b></td>
		 				<td style="text-indent: 5px;">
		 					<span class="grey_line_div">
		 						<@s.if test = "%{regUser.userType == 1 }">Super Admin</@s.if>
		 						<@s.elseif  test = "%{regUser.userType == 2 }">Admin</@s.elseif>
				 	 			<@s.else>User</@s.else>
				 	 		</span>
		 				</td>
		 				<td align="center">
		 				<@s.if test="%{(user.userType == 1 || user.userType == 2) && (#session.authen_user_id != regUser.id)}">
		 					<@s.form action="manageUser.jspx" namespace="/admin" method="post">
		 					<@s.hidden name="regUser.id" />
		 						<@s.if test = "%{regUser.userType ==3 && regUser.activated == true }">
				 					<div class="tab_div">
				 						<@s.hidden name="manageType" value="setasadmin" />
				 						<@s.submit value="Set As Admin" cssClass="input_button_normal" />
				 					</div>
				 				</@s.if>
				 				<@s.if test = "%{regUser.userType ==2 && regUser.activated == true }">
				 					<div class="tab_div">
				 						<@s.hidden name="manageType" value="setasuser" />
				 						<@s.submit value="Set As User" cssClass="input_button_normal" />
				 					</div>
				 				</@s.if>
			 				</@s.form>
			 			</@s.if>
		 				</td> 
		 			</tr>
		 		</table>
		 		
		 		<div class="dot_bottom_border_block">&nbsp;</div>
		 		<table class="no_bd_tab_data">
		 			<tr>
		 				<td> <b> Organization:</b> </td>
		 			</tr>
		 			<tr>
		 				<td><span class="grey_line_div">
		 					 	<@s.if test="%{regUser.profile.organization != null}">
				 					<@s.property value="regUser.profile.organization" />
				 				</@s.if>
				 				<@s.else>
				 					Not specified
				 				</@s.else>
		 					</span>
		 				</td>
		 			</tr>
		 			<tr>
		 				<td> <b>Contact Details: </b></td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div">
		 						<@s.if test="%{regUser.profile.contactDetails != null}">
				 					<@s.property value="regUser.profile.contactDetails"  escape=false/>
				 				</@s.if>
				 				<@s.else>
				 					Not specified
				 				</@s.else>
		 					</span>
		 				</td>
		 			</tr>
		 			
		 			<tr>
		 				<td> <b>Address: </b></td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div">
		 						<@s.if test="%{regUser.profile.country != null}">
				 						<@s.property value="regUser.profile.address" />&nbsp; 
				 						<@s.property value="regUser.profile.city" />&nbsp; 
				 						<@s.property value="regUser.profile.state" />&nbsp;
				 						<@s.property value="regUser.profile.postcode" />&nbsp;  
					 					<@s.property value="regUser.profile.country" />
				 				</@s.if>
				 				<@s.else>
				 					Not specified
				 				</@s.else>
		 					</span>
		 				</td>
		 			</tr>
		 		 
		 			<tr>
		 				<td> <b>Industry | Field: </b></td>
		 			</tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div">
		 						<@s.if test="%{regUser.profile.industryField != null}">
				 					<@s.property value="regUser.profile.industryField" />
				 				</@s.if>
				 				<@s.else>
				 					Not specified
				 				</@s.else>
		 					</span>
		 				</td>
		 			</tr>
		 			<tr>	
		 				<td> <b>Occupation | Role(s): </b></td>
		 			 </tr>
		 			<tr>
		 				<td>
		 					<span class="grey_line_div">
		 						<@s.if test="%{regUser.profile.occupation != null}">
				 					<@s.property value="regUser.profile.occupation" />
				 				</@s.if>
				 				<@s.else>
				 					Not specified
				 				</@s.else>
		 					</span>
		 				</td>
		 			</tr>
		 			<tr>	
		 				<td> <b>Professional Interests:</b></td>
		 			</tr>
		 		
		 			<tr>
		 				<td>
		 					<span class="grey_line_div">
		 						<@s.if test="%{regUser.profile.interests != null}">
				 					<@s.property value="regUser.profile.interests"  escape=false />
				 				</@s.if>
				 				<@s.else>
				 					Not specified
				 				</@s.else>
		 					</span>
		 				</td>
		 			</tr>
		 		</table>
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
<#include "../template/footer.ftl"/>
</body>
</html>