<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
	<div class="main_body_big_left_panel">
        <div class="none_boder_left_container">
            <br/>
            <div class="left_middle_panel">
                <div class="aboutus_div">
                   The Climate and Weather system was built by Monash eResearch Centre as part of the ANDS funded Monash ARDC-EIF Data Capture
                    and Metadata Store Project. The solution developed for this project has been specifically tailored to the Monash University
                    Climate and Weather Research Community needs as articulated by Dr Simon Clarke of the Monash University School of Mathematical
                    Sciences within the Faculty of Science. This project aimed to develop software solutions that:
                    <ul>
		 				<li>capture data and metadata from research instruments and devices</li>
		 				<li>make available and automate feeds of data collection descriptions</li>
                        <li>enable data/metadata management and sharing</li>
                        <li>facilitate the re-use and discovery of data</li>
                        <li>recognise the need to facilitate the overall data stream/s from Monash to
                            the RDA which include collections with related party and activity descriptions
                            in compliance with the requirements set out in the ANDS Minimum Metadata Contents
                            for RIF-CSThis project is supported by the <a href="http://ands.org.au/" target="_blank">Australian National Data Service (ANDS)</a>.
                            ANDS is supported by the Australian Government through the National Collaborative
                            Research Infrastructure Strategy Program and the Education Investment Fund (EIF) Super Science Initiative.
                        </li>
		 			</ul>

                    <p>
                        For all enquiries and assistance please contact the Monash eResearch Centre at
                        <a href="mailto:merc@monash.edu?Subject=Ozflux">merc@monash.edu</a>
                    </p>

                 </div>
            </div>
            <br/>
        </div>
	</div>
    <div class="right_container_panel">

    </div>
	<div style="clear:both"></div>
</div>
<br/>
<!--
<div class="page_fixed_div">
	<div class="home_middle_div">
				asdfsad
	</div>
</div>
-->
<#include "template/footer.ftl"/>
</body>
</html>