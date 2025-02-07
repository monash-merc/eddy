<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Welcome to <@s.property value="appName" /></title>
<#include "template/header.ftl"/>
</head>

<body>
<!-- Navigation Section -->
<#include "template/nav_section.ftl" />
<!-- Navigation Title -->
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><@s.text name="aboutus.action.title" /></div>
</div>
<div class="main_body_container">
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
                <div class="content_none_border_div">
                    <div class="paragraph_div">
                        The OzFlux system was built by Monash eResearch Centre as part of the ANDS funded Monash
                        ARDC-EIF Data
                        Capture and Metadata Store Project. The solution developed for this project
                        has been specifically tailored to the OzFlux Research Community needs as articulated by Dr Peter
                        Isaac
                        of the Monash University School of Geography and Environmental Science within
                        the Faculty of Arts. This project aimed to develop software solutions that:
                    </div>
                    <ul id="toc_small">
                        <li>capture data and metadata from research instruments and devices</li>
                        <li>make available and automate feeds of data collection descriptions</li>
                        <li>enable data/metadata management and sharing</li>
                        <li>facilitate the re-use and discovery of data</li>
                        <li>recognise the need to facilitate the overall data stream/s from Monash to
                            the RDA which include collections with related party and activity descriptions
                            in compliance with the requirements set out in the ANDS Minimum Metadata Contents
                            for RIF-CSThis project is supported by the Australian National Data Service (<a
                                    href="http://ands.org.au/" target="_blank">ANDS</a>).
                            ANDS is supported by the Australian Government through the National Collaborative
                            Research Infrastructure Strategy Program and the Education Investment Fund (EIF) Super
                            Science
                            Initiative.
                        </li>
                    </ul>
                    <div class="paragraph_div">
                        For all enquiries and assistance, please contact:
                    </div>
                    <div class="paragraph_div">
                        <b> Peter Isaac</b> (<a
                            href="mailto:pisaac.ozflux@gmail.com?Subject=Ozflux">pisaac.ozflux@gmail.com</a>)
                    </div>

                    <div class="paragraph_div">
                        or
                    </div>

                    <div class="paragraph_div">
                        <b>Eva van Gorsel</b> (<a
                            href="mailto:eva.vangorsel@csiro.au?Subject=Ozflux">eva.vangorsel@csiro.au</a>)
                    </div>
                </div>
            </div>
        </div>
        <!-- right panel -->
        <div class="right_display_div">
        <@s.if test="%{#session.authentication_flag =='authenticated'}">
            <#include "template/sub_nav.ftl" />
        </@s.if>
        </div>
    </div>
    <div style="clear:both"></div>
</div>
<#include "template/footer.ftl"/>
</body>
</html>