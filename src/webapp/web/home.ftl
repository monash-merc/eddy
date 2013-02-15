<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
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
            <br/>

            <p>
                Data from the OzFlux network of flux towers is available from this portal.
            </p>

            <p>
                Users can browse the contents of this portal by clicking on the “All Collections” entry above
                and using the “Map view” or the “List view” to view the data collections.
            </p>

            <p>Users wishing to download data will need to log in to the portal. If you already have an
                account, click the “Login” button above. If you would like to ask for an account, click on
                “Register”.
            </p>

            <p>Information on the data available, the license conditions and the data file format are
                available on the Resources page.
            </p>
        </div>
        <div style="clear:both"></div>
    </div>
    <div style="clear:both"></div>
</div>
<#include "template/footer.ftl"/>
</body>
</html>