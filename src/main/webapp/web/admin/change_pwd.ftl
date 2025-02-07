<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="user.display.home.action.title" /> - <@s.text name="user.change.password.action.title" /></title>
<#include "../template/jquery_header.ftl"/>

    <script type="text/javascript">
        function refresh() {
            document.getElementById("captcha_img").src = '${base}/captch/captchCode.jspx?now=' + new Date();
        }
    </script>
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
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "../template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
            <@s.form action="changePassword.jspx" namespace="/admin" method="post">
                <@s.hidden name="user.email" />
                <@s.hidden name="user.displayName" />
                <div class="content_div">
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
                            <@s.text name="user.current.password" />:
                        </div>
                        <div class="input_field_value_section">
                            <@s.password name="user.password" />
                            <div class="comments">
                                <@s.text name="user.current.password.hint" />
                            </div>
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            <@s.text name="user.new.password" />:
                        </div>
                        <div class="input_field_value_section">
                            <@s.password name="newPassword" />
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
                            <@s.submit value="Change" cssClass="input_button_style" /> &nbsp; <@s.reset value="%{getText('reset.button')}" cssClass="input_button_style" />
                        </div>
                    </div>
                </div>
            </@s.form>
            </div>
        </div>
        <!-- right panel -->
        <div class="right_display_div">
        <@s.if test="%{#session.authentication_flag =='authenticated'}">
            <#include "../template/sub_nav.ftl" />
        </@s.if>
        </div>
    </div>
    <div style="clear:both"></div>
</div>
<#include "../template/footer.ftl"/>
</body>
</html>