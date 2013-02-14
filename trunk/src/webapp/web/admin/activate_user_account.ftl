<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.property value="pageTitle" /></title>
<#include "../template/jquery_header.ftl"/>
    <script>
        function reject() {
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
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "../template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
            <@s.form  action="activateUserAccount.jspx" namespace="/admin" method="post">
                <@s.hidden name="regUser.id" />
                <@s.hidden name="regUser.activationHashCode" />
                <@s.hidden name="regUser.email" />
                <@s.hidden name="organization" />
                <div class="content_div">
                    <div class="input_field_row">
                        <div class="input_field_title">
                            <@s.text name="user.displayName" />:
                        </div>
                        <div class="input_field_value_section">
                            <@s.property value="regUser.displayName"/>
                        </div>
                    </div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            <@s.text name="user.email" />:
                        </div>
                        <div class="input_field_value_section">
                            <@s.property value="regUser.email"/>
                        </div>
                    </div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            <@s.text name="user.organization" />:
                        </div>
                        <div class="input_field_value_section">
                            <@s.property value="organization"/>
                        </div>
                    </div>

                    <div class="blank_separator"></div>
                    <div class="blank_separator"></div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            &nbsp;
                        </div>
                        <div class="input_field_value_section">
                            <@s.submit value="%{getText('activate.button')}" cssClass="input_button_style" />
                            &nbsp; <@s.reset value="%{getText('reject.button')}" onclick="reject();" cssClass="input_button_style" />
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