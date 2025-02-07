<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="user.all.users.title" /> - <@s.text name="admin.view.user.details" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><a href="${base}/admin/listUsers.jspx"><@s.text name="user.all.users.title" /></a></div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><@s.text name="admin.view.user.details" /></div>
</div>
<div style="clear:both"></div>
<div class="main_body_container">
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "../template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
                <div class="content_div">
                    <div class="user_info_basic">
                        <div class="user_details_left">
                            <br/>
                            <br/>
                            <img src="${base}/user/viewImage.jspx?avatarUserId=<@s.property value='regUser.id' />">
                        </div>
                        <div class="user_details_right">
                            <div class="input_field_row">
                                <div class="input_field_title">
                                    User name:
                                </div>
                                <div class="input_field_value_section">
                                <@s.property value="regUser.displayName"/>
                                </div>
                            </div>
                            <div class="input_field_row">
                                <div class="input_field_title">
                                    Gender:
                                </div>
                                <div class="input_field_value_section">
                                <@s.property value="regUser.profile.gender" />
                                </div>
                            </div>
                            <div class="input_field_row">
                                <div class="input_field_title">
                                    Date since joined:
                                </div>
                                <div class="input_field_value_section">
                                <@s.date name="regUser.registedDate" format="yyyy-MM-dd" />
                                </div>
                            </div>
                            <div class="input_field_row">
                                <div class="input_field_title">
                                    Email:
                                </div>
                                <div class="input_field_value_section">
                                <@s.property value="regUser.email" />
                                </div>
                            </div>
                            <div class="input_field_row">
                                <div class="input_field_title">
                                    Active status:
                                </div>
                                <div class="input_field_value_section">
                                <@s.property value="regUser.activated"/>
                                </div>
                            </div>
                            <div class="input_field_row">
                                <div class="input_field_title">
                                    User type:
                                </div>
                                <div class="input_field_value_section">
                                <@s.if test = "%{regUser.userType == 1 }">
                                    Super Admin
                                </@s.if>
                                <@s.elseif  test = "%{regUser.userType == 2 }">
                                    Admin
                                </@s.elseif>
                                <@s.else>
                                    User
                                </@s.else>
                                </div>
                            </div>
                            <div style="clear:both"></div>

                            <div class="content_act_div">
                                <div class="content_form_act">
                                <@s.if test="%{(user.userType == 1 || user.userType == 2) && (#session.authen_user_id != regUser.id) && (regUser.userType !=1)}">
                                <@s.form action="manageUser.jspx" namespace="/admin" method="post">
                                    <@s.hidden name="regUser.id" />
                                    <@s.if test = "%{regUser.activated == true }">
                                        <@s.hidden name="manageType" value="deactivate" />
                                        <@s.submit value="Deactivate" cssClass="input_button_normal" />
                                    </@s.if>
                                    <@s.else>
                                        <@s.hidden name="manageType" value="activate" />
                                        <@s.submit value="Activate" cssClass="input_button_normal" />
                                    </@s.else>
                                </@s.form>
                            </@s.if>
                                </div>
                                <div class="content_form_act">
                                <@s.if test="%{(user.userType == 1 || user.userType == 2) && (#session.authen_user_id != regUser.id)}">
                                <@s.form action="manageUser.jspx" namespace="/admin" method="post">
                                    <@s.hidden name="regUser.id" />
                                    <@s.if test = "%{regUser.userType ==3 && regUser.activated == true }">
                                        <@s.hidden name="manageType" value="setasadmin" />
                                        <@s.submit value="Set As Admin" cssClass="input_button_normal" />
                                    </@s.if>
                                    <@s.if test = "%{regUser.userType ==2 && regUser.activated == true }">
                                        <@s.hidden name="manageType" value="setasuser" />
                                        <@s.submit value="Set As User" cssClass="input_button_normal" />
                                    </@s.if>
                                </@s.form>
                            </@s.if>
                                </div>
                            </div>
                        </div>
                        <div style="clear:both"></div>
                        <div class="input_field_row">
                            <div class="input_field_title">
                                Organization:
                            </div>
                            <div class="input_field_value_section">
                            <@s.property value="regUser.profile.organization" />
                            </div>
                        </div>
                        <div style="clear:both"></div>

                        <div class="input_field_row">
                            <div class="input_field_title">
                                Contact details:
                            </div>
                            <div class="input_field_value_section">
                            <@s.property value="regUser.profile.contactDetails"  escape=false/>
                            </div>
                        </div>
                        <div style="clear:both"></div>

                        <div class="input_field_row">
                            <div class="input_field_title">
                                Address:
                            </div>
                            <div class="input_field_value_section">
                            <@s.if test="%{regUser.profile.country != null}">
                                <@s.property value="regUser.profile.address" />&nbsp;
                                <@s.property value="regUser.profile.city" />&nbsp;
                                <@s.property value="regUser.profile.state" />&nbsp;
                                <@s.property value="regUser.profile.postcode" />&nbsp;
                                <@s.property value="regUser.profile.country" />
                            </@s.if>
                            </div>
                        </div>
                        <div style="clear:both"></div>

                        <div class="input_field_row">
                            <div class="input_field_title">
                                Industry | Field:
                            </div>
                            <div class="input_field_value_section">
                            <@s.property value="regUser.profile.industryField" />
                            </div>
                        </div>
                        <div style="clear:both"></div>

                        <div class="input_field_row">
                            <div class="input_field_title">
                                Occupation | Role(s):
                            </div>
                            <div class="input_field_value_section">
                            <@s.property value="regUser.profile.occupation" />
                            </div>
                        </div>
                        <div style="clear:both"></div>

                        <div class="input_field_row">
                            <div class="input_field_title">
                                Professional Interests:
                            </div>
                            <div class="input_field_value_section">
                            <@s.property value="regUser.profile.interests"  escape=false />
                            </div>
                        </div>
                        <div style="clear:both"></div>
                    </div>
                    <div style="clear:both"></div>
                </div>
            </div>
        </div>
        <!-- end of left panel -->
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