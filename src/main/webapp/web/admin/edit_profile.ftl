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
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "../template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
            <@s.form action="updateProfile.jspx" namespace="/admin" method="post">
                <div class="content_title">Your basic information</div>
                <div class="content_div">
                    <@s.hidden name="user.displayName" />
                    <@s.hidden name="user.registedDate" />
                    <@s.hidden name="user.email" />
                    <div class="input_field_row">
                        <div class="input_field_title">
                            User name:
                        </div>
                        <div class="input_field_value_section">
                            <@s.property value="user.displayName"/>
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            Date since joined:
                        </div>
                        <div class="input_field_value_section">
                            <@s.date name="user.registedDate" format="yyyy-MM-dd" />
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            Gender:
                        </div>
                        <div class="input_field_value_section">
                            <@s.select name="profile.gender"  headerKey="${profile.gender}" list="genderMap"  cssClass="input_select_small"/>
                        </div>
                    </div>
                </div>
                <div class="content_title">Your contact details</div>
                <div class="content_div">
                    <div class="input_field_row">
                        <div class="input_field_title">
                            Email:
                        </div>
                        <div class="input_field_value_section">
                            <@s.property value="user.email" />
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            Contact Details:
                        </div>
                        <div class="input_field_value_section">
                            <@s.textarea  name="profile.contactDetails" cols="50" rows="2" cssClass="input_textarea" />
                        </div>
                    </div>
                </div>

                <div class="content_title">Your location</div>
                <div class="content_div">
                    <div class="input_field_row">
                        <div class="input_field_title">
                            Address:
                        </div>
                        <div class="input_field_value_section">
                            <@s.textfield name="profile.address" />
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            City:
                        </div>
                        <div class="input_field_value_section">
                            <@s.textfield name="profile.city" />
                        </div>
                    </div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            State:
                        </div>
                        <div class="input_field_value_section">
                            <@s.textfield name="profile.state" />
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            Post code:
                        </div>
                        <div class="input_field_value_section">
                            <@s.textfield name="profile.postcode" />
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            Country:
                        </div>
                        <div class="input_field_value_section">
                            <@s.select name="profile.country" headerKey="${profile.country}"  list="countryMap" cssClass="input_select_normal"/>
                        </div>
                    </div>
                </div>

                <div class="content_title">Your work(professional) life</div>
                <div class="content_div">
                    <div class="input_field_row">
                        <div class="input_field_title">
                            Field (Industry):
                        </div>
                        <div class="input_field_value_section">
                            <@s.textfield name="profile.industryField" />
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            Occupation (Roles):
                        </div>
                        <div class="input_field_value_section">
                            <@s.textfield name="profile.occupation" />
                        </div>
                    </div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            Organization you belong to:
                        </div>
                        <div class="input_field_value_section">
                            <@s.textfield name="profile.organization" />
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            Professional Interests:
                        </div>
                        <div class="input_field_value_section">
                            <@s.textarea  name="profile.interests" cols="50" rows="2" cssClass="input_textarea" />
                        </div>
                    </div>
                </div>
                <div class="content_none_border_div">
                    <div class="input_field_row">
                        <div class="input_field_title">
                            &nbsp;
                        </div>
                        <div class="input_field_value_section">
                            <@s.submit value="%{getText('data.edit.button')}" cssClass="input_button_style" /> &nbsp; <@s.reset value="%{getText('reset.button')}" cssClass="input_button_style" />
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