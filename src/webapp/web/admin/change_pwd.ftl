<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.text name="user.display.home.action.title" /> - <@s.text name="user.change.password.action.title" /></title>
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
	<div class="div_inline"><@s.text name="user.change.password.action.title" /></div>		
</div>
<div style="clear:both"></div> 
 
<div class="main_body_container">
<div class="main_big_border">
	<div class="left_container_panel">
		<br/>
		<#include "../template/action_errors.ftl" />
		<div class="left_middle_panel">		
		<@s.form action="changePassword.jspx" namespace="/admin" method="post">
		<@s.hidden name="user.email" />
		<@s.hidden name="user.displayName" />
		<table>
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>
				<div class="reg_field"><@s.text name="user.displayName" />:</div></td>
				<td> <@s.property value="user.displayName"/></td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>
				<div class="reg_field"><@s.text name="user.email" />:</div></td>
				<td> <@s.property value="user.email"/></td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td><div class="reg_field"><@s.text name="user.current.password" />:</div></td>
				<td><@s.password name="user.password" cssClass="input_field" /></td>
				<td><div class="reg_comment">* <@s.text name="user.current.password.hint" /></div></td>
			</tr>
			<tr>
				<td><div class="reg_field"><@s.text name="user.new.password" />:</div></td>
				<td><@s.password name="newPassword" cssClass="input_field" /></td>
				<td><div class="reg_comment">* <@s.text name="user.new.password.hint" /></div></td>
			</tr>
			<tr>
				<td><div class="reg_field"><@s.text name="user.new.rePassword" />:</div></td>
				<td><@s.password name="rePassword" cssClass="input_field" /></td>
				<td><div class="reg_comment">* <@s.text name="user.new.rePassword.hint" /></div></td>
			</tr>
			<tr><td><div class="reg_field"><@s.text name="security.code" />:</div></td>
				<td><@s.textfield name="securityCode" cssClass="input_field" /></td>
				<td><div class="reg_comment">* <@s.text name="security.code.hint" /></div>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td align="center"><img src="${base}/captch/captchCode.jspx?now=new Date()" border="0" id="imagevalue" name="imagevalue" /></td>
				<td><div class="reg_comment">&nbsp;<a href="#" onclick="refresh()"><img src="${base}/images/refresh.png" class="image_position" /> can't read this?</a></div></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>
					<div class="input_button_div"><@s.submit value="Change" cssClass="input_button_style" /> &nbsp; <@s.reset value="%{getText('reset.button')}" cssClass="input_button_style" /></div>
				</td>
				<td>&nbsp;</td>
			</tr>
		</table>
		</@s.form>
		<br/>
		<br/>
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