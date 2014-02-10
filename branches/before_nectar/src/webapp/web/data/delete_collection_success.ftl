<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.property value="pageTitle" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<!-- Navigation Title -->
<#include "../template/action_title.ftl" />

<div class="main_body_container">
    <div class="display_middel_div">
        <div class="left_display_div">

            <div style="clear:both"></div>
            <div class="left_display_inner">
                <div class="redirect_pane">
                    <br/>
                    <b><@s.property value="actionSuccessMsg" /></b>
                    <br/>
                    <br/>
                    <span class="redirect_span">After a few seconds, the page will redirect ...</span>
                    <br/>
                    <br/>
                        <span class="redirect_span">Problems with the redirect? Please use this <a href='${base}/<@s.property value="requestUrl" escape=false />'>direct link</a>.</span>
                    <br/>
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
<script>
    function jump() {
        location.href = '${base}/<@s.property value="requestUrl" escape=false />';
    }
    setTimeout("jump()", 5000);
</script>
</body>
</html>