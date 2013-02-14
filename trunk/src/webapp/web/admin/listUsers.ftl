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
                <div class="none_border_block">
                    <span class="name_title">Total <font color="green"> ${userPagination.totalRecords} </font> Users</span>
                </div>
            <@s.if test="%{userPagination.pageResults.size() > 0}">
                <div class="msg_content">
                    <a href="${base}/${pageLink}${pageSuffix}<@s.property value='userPagination.pageNo' />" class="page_url"></a>
                </div>
                <div class="content_none_border_div">
                    <div class="none_border_block">
                        <span class="filter_inline_span">
                            Page size: <@s.select id="item_select_size" name="sizePerPage" headerKey="<@s.property value='sizePerPage' />"  list="pageSizeMap" cssClass="input_select_small" />
                            &nbsp;Sorted by: <@s.select id="item_select_order" name="orderBy" headerKey="${orderBy}"  list="orderByMap" cssClass="input_select_small" />
                            &nbsp;Ordered by: <@s.select id="item_select_otype" name="orderByType" headerKey="${orderByType}"  list="orderByTypeMap" cssClass="input_select_small" />
                        </span>
                    </div>
                </div>

                <div class="content_none_border_div">
                    <table class="display_data_tab">
                        <thead>
                        <tr>
                            <th width="20%">Name</th>
                            <th width="20%">Email</th>
                            <th width="25%">Organization</th>
                            <th width="10%">User Type</th>
                            <th width="10%">Active Status</th>
                            <th width="15%">&nbsp;</th>
                        </tr>
                        </thead>
                        <tbody>
                            <@s.iterator status="userStat" value="userPagination.pageResults" id="userResult" >
                            <tr>
                                <td>
                                    <@s.property value="#userResult.displayName" />
                                </td>
                                <td>
                                    <@s.property value="#userResult.email" />
                                </td>
                                <td>
                                    <@s.property value="#userResult.profile.organization" />
                                </td>
                                <td>
                                    <@s.if test = "%{#userResult.userType == 1 }">Super Admin</@s.if>
                                    <@s.elseif test = "%{#userResult.userType == 2 }">Admin</@s.elseif>
                                    <@s.else>User</@s.else>
                                </td>
                                <td>
                                    <@s.property value="#userResult.activated"/>
                                </td>
                                <td>
                                    <div class="tab_link">
                                        <@s.if test="%{(user.userType == 1 || user.userType == 2) && (#session.authen_user_id != #userResult.id) && (#userResult.userType !=1)}">
                                            <a href="${base}/admin/showUserStatus.jspx?regUser.id=<@s.property value='#userResult.id' />">Manage</a>
                                        </@s.if>
                                        <@s.else>
                                            <a href="${base}/admin/showUserStatus.jspx?regUser.id=<@s.property value='#userResult.id' />">View</a>
                                        </@s.else>
                                    </div>
                                </td>
                            </tr>
                            </@s.iterator>
                        </tbody>
                    </table>
                </div>
                <div class="blank_separator"></div>
                <#include "../pagination/pag_style3.ftl" />
            </@s.if>
            <@s.else>
                <div class="empty_space_div"></div>
            </@s.else>
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