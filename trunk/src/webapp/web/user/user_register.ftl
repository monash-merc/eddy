<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.text name="user.register.action.title" /></title>

<#include "../template/header.ftl"/>

<script type="text/javascript">
function refresh()
{
	document.getElementById("imagevalue").src='${base}/captch/captchCode.jspx?now=' + new Date();
}
</script>
</head>

<body>
<!-- Navigation Section -->
<#include "../template/nav_section.ftl" />
<!-- Navigation Title -->

<div class="title_panel">
	<div class="div_inline">&nbsp;&nbsp;</div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline"><a href="${base}/user/register_options"><@s.text name="user.register.option.title" /></a></div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline"><a href="${base}/user/user_register"><@s.text name="user.register.action.title" /></a></div>
</div>
<div style="clear:both"></div> 
<!-- End of Navigation Title -->				
<div class="main_body_container">
	<div class="main_body_big_left_panel">	
	<br />
			<#include "../template/action_errors.ftl" />
		<div class="indicator_msg">
			<center><img src="${base}/images/hint.png" class="image_position"/> If you already have a Monash Authcate Account, you can create an account <a href="${base}/user/ldap_user_register">Here</a></center>
		</div>
		<!-- Registration section -->
		<div class="reg_panel">
            <div class="reg_middle_panel">
            <@s.form action="registerUser.jspx" namespace="/user" method="post">
            <table>
                <tr>
                    <td><div class="reg_field"><@s.text name="user.firstName" />:</div></td>
                    <td><@s.textfield name="user.firstName" cssClass="input_field" /></td>
                    <td><div class="reg_comment">* <@s.text name="user.reg.firstName.hint" /></div></td>
                </tr>
                <tr>
                    <td><div class="reg_field"><@s.text name="user.lastName" />:</div></td>
                    <td><@s.textfield name="user.lastName" cssClass="input_field" /></td>
                    <td><div class="reg_comment">* <@s.text name="user.reg.lastName.hint" /></div></td>
                </tr>
                <tr>
                    <td>
                    <div class="reg_field"><@s.text name="user.email" />:</div></td>
                    <td><@s.textfield name="user.email" cssClass="input_field" /></td>
                    <td><div class="reg_comment">* <@s.text name="user.reg.email.hint" /></div></td>
                </tr>
                <tr>
                    <td><div class="reg_field"><@s.text name="user.password" />:</div></td>
                    <td><@s.password name="user.password" cssClass="input_field" /></td>
                    <td><div class="reg_comment">* <@s.text name="user.reg.password.hint" /></div></td>
                </tr>
                <tr>
                    <td><div class="reg_field"><@s.text name="user.organization" />:</div></td>
                    <td><@s.textfield name="organization" cssClass="input_field" /></td>
                    <td><div class="reg_comment">* <@s.text name="user.reg.organization.hint" /></div></td>
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
                        <div class="input_button_div">
                        <@s.submit value="%{getText('registration.button')}" cssClass="input_button_style" /> &nbsp; <@s.reset value="%{getText('reset.button')}" cssClass="input_button_style" /></div>
                    </td>
                    <td><span class="inline_span">If you already have an account, please <a href="${base}/user/showLogin.jspx">Sign in now </a></span></td>
                </tr>
            </table>
            </@s.form>
            </div>
	    </div>
	    <br/>
	</div>
	<div style="clear:both"></div> 		
</div>
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>
