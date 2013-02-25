<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="site.faq.action.title" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><a href="${base}/site/faq.jspx"><@s.text name="site.faq.action.title" /></a></div>
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
                        FAQs
                    </div>

                    <div class="sub_norm_title">
                        1. What data is available from this portal?
                    </div>
                    <div class="paragraph_div">
                        Insert text about ecosystem data here.
                    </div>

                    <div class="sub_norm_title">
                        2. What do I have to do before I can download the data?
                    </div>
                    <div class="paragraph_div">
                        Insert text about being able to browse but need to register to download.
                    </div>
                    <div class="sub_norm_title">
                        3. How do I register for an account?
                    </div>
                    <div class="paragraph_div">
                        Insert text about registration process.
                    </div>

                    <div class="sub_norm_title">
                        4. How do I download the data?
                    </div>
                    <div class="paragraph_div">
                        Insert text about downloading data.
                    </div>
                    <div class="sub_norm_title">
                        5. How do I read to files I have downloaded?
                    </div>
                    <div class="paragraph_div">
                        Insert text about netCDF files and utilities to read netCDF files.
                    </div>

                    <div class="sub_norm_title">
                        6. What do all the variable names in the files mean?
                    </div>
                    <div class="paragraph_div">
                        Insert text with brief explanation of variable names, link to variable name PDF.
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