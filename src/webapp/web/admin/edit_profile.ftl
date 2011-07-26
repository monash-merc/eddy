<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.text name="user.display.home.action.title" /> - <@s.text name="user.profile.action.title" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
	<div class="div_inline">&nbsp;&nbsp;</div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline"><a href="${base}/admin/displayUserHome.jspx"><@s.text name="user.display.home.action.title" /></a></div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline"><@s.text name="user.profile.action.title" /></div>		
</div>
<div style="clear:both"></div> 
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
		 		<form action="updateProfile.jspx" namespace="/admin" method="post">	
				<table width="100%">
		 			<tr>
		 				<td width="150">User name: </td>
		 				<td><span class="grey_line_div"><@s.property value="user.displayName"/></span>  <@s.hidden name="user.displayName" /></td>
		 				<td></td>
		 			</tr>
		 			<tr>
		 				<td>Joined: </td><td><span class="grey_line_div"><@s.date name="user.registedDate" format="yyyy-MM-dd" /></span> <@s.hidden name="user.registedDate" /></td>
		 				<td></td>
		 			</tr>
		 			<tr>
		 				<td> Gender: </td>
		 				<td>
		 					 <@s.select name="profile.gender"  headerKey="${profile.gender}" list="genderMap"  cssClass="input_select_small"/>  
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
		 				<td><span class="grey_line_div"><@s.property value="user.email" /></span> <@s.hidden name="user.email" /></td>
		 			</tr>
		 			<tr>
		 				<td> Contact Details: </td>
		 			</tr>
		 			<tr>
		 				<td><@s.textarea  name="profile.contactDetails" cols="50" rows="2" cssClass="input_textarea" /></td>
		 			</tr>
		 		</table>
		 		<div class="p_title"><b>Your location</b></div>
		 		<table>
		 			<tr>
		 				<td> Address: </td>
		 			</tr>
		 			<tr>
		 				<td><@s.textfield name="profile.address" cssClass="input_field" /> </td>
		 			</tr>
		 			<tr>
		 				<td> City: </td>
		 			</tr>
		 			<tr>
		 				<td><@s.textfield name="profile.city" cssClass="input_field" /> </td>
		 			</tr>
		 			<tr>
		 				<td> State: </td>
		 			</tr>
		 			<tr>
		 				<td><@s.textfield name="profile.state" cssClass="input_field" /> </td>
		 			</tr>
		 			<tr>
		 				<td> Post code: </td>
		 			</tr>
		 			<tr>
		 				<td><@s.textfield name="profile.postcode" cssClass="input_field" /> </td>
		 			</tr>
		 			<tr>
		 				<td> Country: </td>
		 			</tr>
		 			<tr>
		 				<td>
		 				<@s.select name="profile.country" headerKey="${profile.country}"  list="countryMap" cssClass="input_select_normal"/> 
		 				</td>
		 			</tr>
		 		</table>
		 		<div class="p_title"><b>Your work(professional) life</b></div>
		 		<table>
		 			<tr>
		 				<td> Field(Industry): </td>
		 			</tr>
		 			<tr>
		 				<td><@s.textfield name="profile.industryField" cssClass="input_field" /> </td>
		 			</tr>
		 			<tr>
		 				<td> Occupation(Roles): </td>
		 			</tr>
		 			<tr>
		 				<td><@s.textfield name="profile.occupation" cssClass="input_field" /> </td>
		 			</tr>
					<tr>
		 				<td> Organization you belong to: </td>
		 			</tr>
		 			<tr>
		 				<td><@s.textfield name="profile.organization" cssClass="input_field" /> </td>
		 			</tr>
		 			<tr>
		 				<td> Professional Interests: </td>
		 			</tr>
		 			<tr>
		 				<td><@s.textarea  name="profile.interests" cols="50" rows="2" cssClass="input_textarea" /></td>
		 			</tr>
		 			<tr>
		 				<td><@s.submit value="%{getText('data.edit.button')}" cssClass="input_button_style" /> &nbsp; <@s.reset value="%{getText('reset.button')}" cssClass="input_button_style" /></td>
		 			</tr>
		 			<tr>
		 				<td>&nbsp;</td>
		 			</tr>
		 		</table>
		 		</form>
		 	 </div>
		 	 <div class="blank_separator"></div>
		</div>
		<div style="clear:both"></div>
		<br/>
	</div>
	<div class="blank_separator"></div>
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