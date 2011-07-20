<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.property value="pageTitle" /></title>
<#include "../template/jquery_header.ftl"/>
<script>
	function reject()
	{
		targetForm = document.forms[0];
		targetForm.action = "rejectUserAccount.jspx";
		targetForm.submit();
	}
</script>
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
			<div class="blank_separator"></div>
			<div class="dotted_border_div">
			<@s.form  action="activateUserAccount.jspx" namespace="/admin" method="post">
				<@s.hidden name="regUser.id" />
				<@s.hidden name="regUser.activationHashCode" />
				<@s.hidden name="regUser.email" />
				<@s.hidden name="organization" />
				
				<table class="no_bd_tab_data">
					<tr>
						<td width="180" align="center">
							<div class="reg_field"><@s.text name="user.displayName" />:</div>
						</td>
						<td style="text-indent: 5px;"><span class="grey_line_div"><@s.property value="regUser.displayName"/></span></td>
					</tr>
					<tr>
						<td align="center">
							<div class="reg_field"><@s.text name="user.email" />:</div>
						</td>
						<td style="text-indent: 5px;"><span class="grey_line_div"><@s.property value="regUser.email"/></span></td>
					</tr>
					<tr>
						<td align="center">
							<div class="reg_field"><@s.text name="user.organization" />:</div>
						</td>
						<td style="text-indent: 5px;"><span class="grey_line_div"><@s.property value="organization"/></span></td>
					</tr>
				</table>
				<table>
					<tr>
						<td width="120">&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
				
					<tr>
						<td>&nbsp;</td>
						<td>
							<div class="input_button_div"><@s.submit value="%{getText('activate.button')}" cssClass="input_button_style" /> &nbsp; <@s.reset value="%{getText('reject.button')}" onclick="reject();" cssClass="input_button_style" /></div>
						</td>
					</tr>
					
				</table>
			</@s.form>
			</div>
		 	<div class="none_border_space_block"></div>
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
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>