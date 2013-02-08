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
                <div class="content_title">Profile</div>
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
                            Gender:
                        </div>
                        <div class="input_field_value_section">
                        <@s.if test="%{profile.gender != null}">
                            <@s.property value="profile.gender" />
                        </@s.if>
                        <@s.else>
                            Not specified
                        </@s.else>
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
                            Email:
                        </div>
                        <div class="input_field_value_section">
                        <@s.property value="user.email" />
                        </div>
                    </div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            Organization:
                        </div>
                        <div class="input_field_value_section">
                        <@s.if test="%{profile.organization != null}">
                            <@s.property value="profile.organization" />
                        </@s.if>
                        <@s.else>
                            Not specified
                        </@s.else>
                        </div>
                    </div>
                    <div class="content_act_div">
                        <a href="${base}/admin/showProfileUpdate.jspx">Edit Profile</a> <a href="${base}/admin/showChangePwd.jspx">Change Password</a>
                    </div>
                    <div style="clear:both"></div>
                </div>
                <div class="content_title">Permission Requests</div>
                <div class="content_div">
                <@s.if test="%{permReqPagination.pageResults.size() > 0}">
                    <ul class="content_ul">
                        <@s.iterator status="permReqStat" value="permReqPagination.pageResults" id="permReqResult" >
                            <@s.if test="%{#permReqStat.index +1 <= 5}">
                                <li>
                                    <span class="span_inline1">
                                        <@s.date name="#permReqResult.requestTime"  format="dd-MM-yyyy 'at' hh:mm a" />
                                    </span>
                                    <span class="span_inline2">
                                        <@s.property value="#permReqResult.requestUser.displayName" /> applied for the permissions of <@s.property value="#permReqResult.collection.name" />
                                    </span>
                                </li>
                            </@s.if>
                        </@s.iterator>
                    </ul>
                    <div class="content_act_div">
                        <a href="${base}/perm/listPermRequests.jspx">View Details</a>
                    </div>
                    <div style="clear:both"></div>
                </@s.if>
                <@s.else>
                    <div class="placeholder_div">
                        No Permissions Requests
                    </div>
                </@s.else>
                </div>

                <div class="content_title">Latest Events</div>
                <div class="content_div">
                <@s.if test="%{eventPagination.pageResults.size() > 0}">
                    <ul class="content_ul">
                        <@s.iterator status="eventStat" value="eventPagination.pageResults" id="eventResult" >
                            <@s.if test="%{#eventStat.index +1 <= 5}">
                                <li>
                                    <span class="span_inline1">
                                        <@s.date name="#eventResult.createdTime"  format="dd-MM-yyyy 'at' hh:mm a" />
                                    </span>
                                    <span class="span_inline2">
                                        <@s.property value="#eventResult.event" />, by <@s.property value="#eventResult.operator.displayName" />
                                    </span>
                                </li>
                            </@s.if>
                        </@s.iterator>
                    </ul>
                    <div class="content_act_div">
                        <a href="${base}/admin/listUserEvents.jspx">View Events</a>
                    </div>
                    <div style="clear:both"></div>
                </@s.if>
                <@s.else>
                    <div class="placeholder_div">
                        No Events
                    </div>
                </@s.else>
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