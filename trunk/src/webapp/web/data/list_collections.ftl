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
                    <span class="name_title">A total of <font color="green"> ${pagination.totalRecords} </font> collection(s) in the repository</span>
                </div>
            <@s.if test="%{pagination.pageResults.size() > 0}">
                <div class="msg_content">
                    <a href="${base}/${pageLink}${pageSuffix}<@s.property value='pagination.pageNo' />" class="page_url"></a>
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
                <!-- START of Record -->
                <@s.iterator status="colStat" value="pagination.pageResults" id="colResult" >
                    <div class="data_display_div">
                        <div class="data_title">
                            <a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='#colResult.id '/>&collection.owner.id=<@s.property value='#colResult.owner.id' />&viewType=${viewType}"><@s.property value="#colResult.name" /></a>
                        </div>
                        <div class="tern_flag">
                            [ Tern-funded ]
                        </div>
                        <div class="data_desc_div">
                            <@s.property value="#colResult.briefDesc" />
                        </div>
                        <div class="data_other_info">
                            <span class="span_inline1">
                                Created by <@s.property value="#colResult.owner.displayName" />,
                            </span>
                            <span class="span_inline1">
                                Created date: <@s.date name="#colResult.createdTime" format="yyyy-MM-dd hh:mm" />,
                            </span>
                           <span class="span_inline1">
                                Modified by <@s.property value="#colResult.modifiedByUser.displayName" />,
                            </span>
                            <span class="span_inline1">
                                Modified date: <@s.date name="#colResult.modifiedTime" format="yyyy-MM-dd hh:mm" />
                            </span>
                        </div>

                        <div class="data_action_link">
                            <a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='#colResult.id' />&collection.owner.id=<@s.property value='#colResult.owner.id' />&viewType=${viewType}">View
                                details</a>
                        </div>
                        <div style="clear: both;"></div>
                    </div>
                </@s.iterator>

                <div class="content_none_border_div">
                    <div class="blank_separator"></div>
                    <#include "../pagination/pag_style.ftl" />
                </div>
            </@s.if>
            <@s.else>
                <div class="placeholder_div">
                    There is no collection
                </div>
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