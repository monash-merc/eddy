<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/html">
<head>
    <title>Welcome to <@s.property value="appName" /></title>
<#include "template/header.ftl"/>
    <script>
        $(document).ready(function () {
            $('#merc').coinslider({ hoverPause:true, width:250, height:300, opacity:0.5 });
        });
    </script>
</head>

<body>
<!-- Navigation Section -->
<#include "template/nav_section.ftl" />
<div style="clear:both"></div>
<!-- Navigation Title -->
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><@s.text name="home.action.title" /></div>
</div>
<div class="main_body_container">
    <div class="display_middel_div">
        <div class="home_left_panel">
            <div class="slidshow_frame">
                <div id="merc">
                    <a href="#">
                        <img src="${base}/slideshow/DalyRiver_NT_small.jpg" alt="Daly River Site"/>
							<span>
								<b>Daly River</b><br/>
								OzFlux is a national ecosystem research network consisting of 10 flux stations.
							</span>
                    </a>

                    <a href="#">
                        <img src="${base}/slideshow/CapeTribulation_QLD_small.jpg" alt="CapeTribulation QLD"/>
							<span>
								<b>Cape Tribulation QLD</b>
							</span>
                    </a>
                    <a href="#">
                        <img src="${base}/slideshow/NewWallaby_VIC_small.jpg" alt="New Wallaby VIC"/>
							<span>
								<b>New Wallaby VIC</b>
							</span>
                    </a>
                    <a href="#">
                        <img src="${base}/slideshow/Tumbarumba2_NSW_small.jpg" alt="Tumbarumba NSW"/>
							<span>
								<b>Tumbarumba NSW</b>
							</span>
                    </a>
                </div>
            </div>
        </div>
        <div class="home_right_panel">
            <div class="paragraph_title">Welcome to the OzFlux Data Portal</div>
            <p>
                <b>OzFlux</b> is part of the Australian Terrestrial Ecosystem Research Network (<a href="http://wwww.tern.org.au"><b>TERN</b></a>). The OzFlux
                network consists of nearly 30 flux towers in Australia and New Zealand. OzFlux is also a
                member of the global FluxNet community.
            </p>

            <p>
                Data from the OzFlux network of flux towers is available from this portal. The data are
                organised into collections with each collection representing at least one site.
            </p>

            <p>
                Users can browse the contents of this portal by clicking on the <b>"Collections"</b> entry above
                and using the "<a href="${base}/mapview/showMapView.jspx"><b>Map View</b></a>" or the
                <@s.if test="%{#session.authentication_flag =='authenticated'}">
                    "<a href="${base}/data/listAllCollections.jspx"><b>List View</b></a>"
                </@s.if>
                <@s.else>
                    "<a href="${base}/pub/listPubCollections.jspx"><b>List View</b></a>"
                </@s.else>
                to view the data collections.
            </p>

            <p>
                The "<a href="${base}/search/showSearch.jspx"><b>Search</b></a>" entry allows users to search for data based on attributes such as site name,
                location, period and researcher. The <b>"Resources"</b> entry contains information on the data
                available, the data format, license information and information on utilities for accessing the
                data files stored on this portal.
            </p>
            <p>
                Users wishing to download data will need to log in to the portal. If you already have an
                account, click the "<b>Login</b>" button above. If you would like to ask for an account, click on
                "<b>Register</b>". Registration is free and your account will normally be activated within two
                business days.
            </p>
        </div>
        <div style="clear:both"></div>
    </div>
    <div style="clear:both"></div>
</div>
<#include "template/footer.ftl"/>
</body>
</html>