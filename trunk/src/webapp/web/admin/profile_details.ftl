<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.property value="pageTitle" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<#include "../template/action_title.ftl" />
<div class="main_body_container">
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "../template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
                <div class="content_title">Your basic information</div>
                <div class="content_div">
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
                        <@s.property value="profile.gender" />
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
                        <@s.property value="profile.contactDetails" escape=false  />
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
                        <@s.property value="profile.address" />
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            City:
                        </div>
                        <div class="input_field_value_section">
                        <@s.property value="profile.city" />
                        </div>
                    </div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            State:
                        </div>
                        <div class="input_field_value_section">
                        <@s.property value="profile.state" />
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            Post code:
                        </div>
                        <div class="input_field_value_section">
                        <@s.property value="profile.postcode" />
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            Country:
                        </div>
                        <div class="input_field_value_section">
                        <@s.property value="profile.country" />
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
                        <@s.property value="profile.industryField" />
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            Occupation (Roles):
                        </div>
                        <div class="input_field_value_section">
                        <@s.property value="profile.occupation" />
                        </div>
                    </div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            Organization you belong to:
                        </div>
                        <div class="input_field_value_section">
                        <@s.property value="profile.organization" />
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            Professional Interests:
                        </div>
                        <div class="input_field_value_section">
                        <@s.property value="profile.interests" escape=false />
                        </div>
                    </div>
                </div>
                <div class="content_none_border_div">
                    <div class="content_act_div">
                        <a href="${base}/admin/showProfileUpdate.jspx">Edit Profile</a>
                    </div>
                </div>
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