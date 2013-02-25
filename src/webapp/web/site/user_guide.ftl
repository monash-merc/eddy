<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="site.userguide.action.title" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><a href="${base}/site/userguide.jspx"><@s.text name="site.userguide.action.title" /></a></div>
</div>
<div style="clear:both"></div>
<div class="main_body_container">
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "../template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
                <div class="content_none_border_div">
                    <div class="sub_b_title">
                        User Guides
                    </div>
                    <div class="paragraph_div">
                        User guides for using this portal and some of the utilities are available from this page.
                    </div>
                    <div class="paragraph_div">
                        A description of the data portal layout and how to navigate around this site is available
                        <a href="${base}/site/ddoc.jspx?fname=userguide.pdf">here</a> (PDF).
                    </div>
                    <div class="paragraph_div">
                        A manual for the Python scripts used by the OzFlux community to quality control and post-
                        process the flux tower data is available <a href="${base}/site/ddoc.jspx?fname=ozfluxqc.pdf">here</a> (PDF).
                    </div>
                    <div class="paragraph_div">
                        A document that describes the variable names used for the OzFlux data is available <a href="${base}/site/ddoc.jspx?fname=placeholder.txt">here</a> (PDF).
                        This document lists the variable names, gives
                        the CF Metadata standard name and provides a description of the data associated with the
                        variable name.
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