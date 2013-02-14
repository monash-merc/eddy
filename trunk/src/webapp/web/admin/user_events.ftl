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
                <div class="content_none_border_div">
                    <div class="none_border_block">
                    <@s.if test="%{actionSuccessMsg != null}">
                        <#include "../template/action_success_msg.ftl"/>
                    </@s.if>
                    </div>
                </div>
                <div class="none_border_block">
                    <span class="name_title">You have <font color="green"> ${eventPagination.totalRecords} </font> Events</span>
                </div>
            <@s.if test="%{eventPagination.pageResults.size() > 0}">
                <div class="msg_content">
                    <a href="${base}/${pageLink}${pageSuffix}<@s.property value='eventPagination.pageNo' />" class="page_url"></a>
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
                            <th width="20%">Date</th>
                            <th width="70%">Events</th>
                            <th width="10%">&nbsp;</th>
                        </tr>
                        </thead>
                        <tbody>
                            <@s.iterator status="eventStat" value="eventPagination.pageResults" id="eventResult" >
                            <tr>
                                <td>
                            <span class="span_inline1">
                                <@s.date name="#eventResult.createdTime"  format="dd-MM-yyyy 'at' hh:mm a" />
                            </span>
                                </td>
                                <td>
                            <span class="span_inline2">
                                <@s.property value="#eventResult.event" />, by <@s.property value="#eventResult.operator.displayName" />
                            </span>
                                </td>
                                <td>
                                    <div class="tab_link">
                                        <a href="${base}/${deleteEventLink}?pageNo=<@s.property value='eventPagination.pageNo' />&auditEvent.id=<@s.property value='#eventResult.id' />">Delete</a>
                                    </div>
                                </td>
                            </tr>
                            </@s.iterator>
                        </tbody>
                    </table>
                </div>
                <div class="blank_separator"></div>
                <#include "../pagination/pag_style2.ftl" />
            </@s.if>
            <@s.else>
                <div class="placeholder_div">
                    There is no event
                </div>
            </@s.else>
            </div>
        </div>
        <!-- End of left panel -->
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