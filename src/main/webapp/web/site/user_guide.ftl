<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
    <div class="div_inline"><a href="${base}/site/userguide.jspx"><@s.text name="site.userguide.action.title" /></a>
    </div>
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
                        <a  href="${base}/site/ddoc.jspx?fname=OzFlux%20User%20Guide.pdf">here</a> (PDF).
                    </div>
                    <!--<div class="paragraph_div">
                        A manual for the Python scripts used by the OzFlux community to quality control and post-
                        process the flux tower data is available <a href="http://ozflux.org.au/portal/docs/OzFluxQC_Manual.pdf" target="_blank">here</a> (PDF).
                    </div> -->
                    <div class="paragraph_div">
                        The OzFlux community uses <a href="https://github.com/OzFlux/PyFluxPro" target="_blank">PyFluxPro</a> for data
                        processing.
                        Please refer to the PyFluxPro wiki for more information.
                    </div>
                    <div class="paragraph_div">
                        <!-- A document that describes the variable names used for the OzFlux data is available
                        <a href="http://eddy.googlecode.com/files/VariableNamesandDefinitions.pdf" target="_blank">here</a> (PDF). -->
                        <!-- A document that describes the variable names used for the OzFlux data is available
                        <a href="http://ozflux.org.au/portal/docs/VariableNamesandDefinitions.pdf" target="_blank">here</a> (PDF). -->
                        A list of variable names can be found as part of the
                        <a href="https://github.com/OzFlux/PyFluxPro/wiki/Variable-names-and-attributes" target="_blank">PyFluxPro
                            wiki</a>,
                        names use the <a href="https://cfconventions.org/" target="_blank">CF Metadata Convention</a>.
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