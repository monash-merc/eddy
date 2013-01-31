<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.property value="pageTitle" /></title>

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
<#include "../template/action_title.ftl" />
<!-- End of Navigation Title -->
		
<div class="main_body_container">
	<div class="main_body_big_left_panel">
	<br />
		<#include "../template/action_errors.ftl" />		
		<br />
		<!-- login section -->
		<div class="reg_panel">
            <div class="hints_panel">
                 <@s.property value="loginTryMsg" />
            </div>
            <div class="reg_middle_panel">
                <@s.form action="userLogin.jspx" namespace="/user" method="post">
                <table>
                    <tr>
                        <td><div class="reg_field"><@s.text name="user.uniqueId" />:</div></td>
                        <td><@s.textfield name="user.uniqueId" cssClass="input_field" /></td>
                        <td><div class="reg_comment">* <@s.text name="user.login.uniqueId.hint" /></div></td>
                    </tr>
                    <tr>
                        <td><div class="reg_field"><@s.text name="user.password" />:</div></td>
                        <td><@s.password name="user.password" cssClass="input_field" /></td>
                        <td><div class="reg_comment">* <@s.text name="user.login.password.hint" /></div></td>
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
                        <td><div class="reg_comment">&nbsp;<a href="#" onclick="refresh()"><img src="${base}/images/refresh.png" class="image_position"/> can't read this?</a></div></td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td>
                            <div class="input_button_div"><@s.submit value="%{getText('login.button')}" cssClass="input_button_style" /> &nbsp; <@s.reset value="%{getText('reset.button')}" cssClass="input_button_style" /></div>
                        </td>
                        <td><span class="inline_span">Don't have an Account, <a href="${base}/user/register_options">Register an account now </a></span></td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td><span class="inline_span"><a href="${base}/user/user_request_resetpwd">Forgot your password?</a></span></td>
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
