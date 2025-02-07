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
            <@s.if test="%{noLicenceError}">
                <div class="data_display_div">
                    <div class="data_title">
                        <@s.property value="collection.name"/>
                    </div>

                    <div class="data_desc_div">
                        <@s.property  value="collection.briefDesc" escape=false />
                    </div>
                    <div class="data_other_info">
                        <span class="span_inline1">
                            Created by <@s.property  value="collection.owner.displayName"  />,
                        </span>
                        <span class="span_inline1">
                            Creation date: <@s.date name="collection.createdTime" format="yyyy-MM-dd hh:mm" />,
                        </span>
                       <span class="span_inline1">
                            Modified by <@s.property value="collection.modifiedByUser.displayName" />,
                        </span>
                        <span class="span_inline1">
                            Modified date: <@s.date name="collection.modifiedTime" format="yyyy-MM-dd hh:mm" />
                        </span>
                    </div>
                    <div class="data_action_link">
                        <a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}">
                            View details
                        </a>
                    </div>
                    <div style="clear: both;"></div>
                </div>
            </@s.if>
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