<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="user.reset.passwd.action.title" /></title>

<#include "../template/header.ftl"/>

    <script type="text/javascript">
        function refresh() {
            document.getElementById("captcha_img").src = '${base}/captch/captchCode.jspx?now=' + new Date();
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
    <div class="div_inline"><a href="${base}/user/user_request_resetpwd"><@s.text name="user.reset.passwd.action.title" /></a></div>
</div>
<div style="clear:both"></div>
<!-- End of Navigation Title -->
<div class="main_body_container">
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "../template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
                <div class="reg_panel">
                <@s.form action="resetPassword.jspx" namespace="/user" method="post">
                    <@s.hidden name="user.id" />
                    <@s.hidden name="user.resetPasswdHashCode" />
                    <@s.hidden name="user.email" />
                    <@s.hidden name="user.displayName" />
                    <div class="reg_middle_panel">
                        <div class="input_field_row">
                            <div class="input_field_title">
                                <@s.text name="user.displayName" />:
                            </div>
                            <div class="input_field_value_section">
                                <@s.property value="user.displayName"/>
                            </div>
                        </div>
                        <div class="input_field_row">
                            <div class="input_field_title">
                                <@s.text name="user.email" />:
                            </div>
                            <div class="input_field_value_section">
                                <@s.property value="user.email"/>
                            </div>
                        </div>
                        <div class="input_field_row">
                            <div class="input_field_title">
                                <@s.text name="user.new.password" />:
                            </div>
                            <div class="input_field_value_section">
                                <@s.password name="user.password" />
                                <div class="comments">
                                    <@s.text name="user.new.password.hint" />
                                </div>
                            </div>
                        </div>
                        <div class="input_field_row">
                            <div class="input_field_title">
                                <@s.text name="user.new.rePassword" />:
                            </div>
                            <div class="input_field_value_section">
                                <@s.password name="rePassword" />
                                <div class="comments">
                                    <@s.text name="user.new.rePassword.hint" />
                                </div>
                            </div>
                        </div>
                        <div class="input_field_row">
                            <div class="input_field_title">
                                <@s.text name="security.code" />:
                            </div>
                            <div class="input_field_value_section">
                                <@s.textfield name="securityCode" />
                                <div class="comments">
                                    <@s.text name="security.code.hint" />
                                </div>
                            </div>
                        </div>
                        <div class="input_field_row">
                            <div class="input_field_title">
                                &nbsp;
                            </div>
                            <div class="input_field_value_section">
                                <div class="captch_div">
                                    <img src="${base}/captch/captchCode.jspx?now=new Date()" id="captcha_img" name="captcha_img"/>
                                    <a href="#" onclick="refresh()"> &nbsp;<img src="${base}/images/refresh.png" class="image_position"/> can't read this?</a>
                                </div>
                            </div>
                        </div>

                        <div class="blank_separator"></div>
                        <div class="blank_separator"></div>
                        <div class="input_field_row">
                            <div class="input_field_title">
                                &nbsp;
                            </div>
                            <div class="input_field_value_section">
                                <@s.submit value="%{getText('reset.password.button')}" cssClass="input_button_style" />
                                &nbsp; <@s.reset value="%{getText('reset.button')}" cssClass="input_button_style" />
                            </div>
                        </div>
                    </div>
                </@s.form>
                </div>
                <div style="clear:both"></div>
            </div>
        </div>
        <!-- right panel -->
        <div class="right_display_div">
            &nbsp;
        </div>
    </div>
    <div style="clear:both"></div>
</div>
<#include "../template/footer.ftl"/>
</body>
</html>
