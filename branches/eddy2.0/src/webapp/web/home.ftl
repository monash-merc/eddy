<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Welcome to <@s.property value="appName" /></title>
<#include "template/header.ftl"/>
    <script>
        $(document).ready(function() {
            $('#merc').coinslider({ hoverPause: true, width: 250,height: 300, opacity: 0.5 });
        });
    </script>
</head>

<body>
<!-- Navigation Section -->
<#include "template/nav_section.ftl" />
<!-- Navigation Title -->
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><@s.text name="home.action.title" /></div>
</div>
<div class="main_body_container">
    <div class="main_body_big_left_panel">
        <br />
        <div>
            <div class="home_left_panel">
                <br/>
                <div class="slidshow_frame_panel">
                    <div id="merc">
                        <a href="#" >
                            <img src="${base}/slideshow/climateweather1.jpg" alt="Feature5" />
							<span>
								<b>Feature1</b><br />
							</span>
                        </a>

                        <a href="#" >
                            <img src="${base}/slideshow/climateweather2.jpg" alt="Feature5" />
							<span>
								<b>Feature2</b>
							</span>
                        </a>
                        <a href="#" >
                            <img src="${base}/slideshow/climateweather3.jpg" alt="Feature5" />
							<span>
								<b>Feature3</b>
							</span>
                        </a>
                        <a href="#" >
                            <img src="${base}/slideshow/climateweather4.jpg" alt="Feature5" />
							<span>
								<b>Feature4</b>
							</span>
                        </a>
                        <a href="#" >
                            <img src="${base}/slideshow/climateweather5.jpg" alt="Feature5" />
							<span>
								<b>Feature5</b>
							</span>
                        </a>
                    </div>
                </div>
            </div>

            <div class="home_right_panel">
                <div class="paragraph_title">Introduction</div>
                <br/>
                <p>
                    Monash researchers and research students create significant datasets, mainly through numerical climate model simulations.
                    The proposed infrastructure will help to revisit and reuse the work carried out by research students and postdoctoral researchers who have left MW&C (Monash Weather & Climate) after finishing their degree or when their contract has ended. Particularly large amounts of data will be created in the coming years when the newly deployed Australian ACCESS climate model is evaluated by a team of postdoctoral researchers. These simulations will be done on the NCI computer platforms in collaboration with other Australian universities, the Bureau of Meteorology and CSIRO. It will be very beneficial for the MW&C researchers to be able to manage this data efficiently and publish it so that other participants will be able to process the data further. Funding of more than $2,000,000 has been secured for this work. In 2010-12 MW&C will carry out a suite of computer simulations to study the urban meteorology in a changing climate. Simulations will assess rainfall patterns in localised urban environments to inform the development, adoption, and operation of stormwater harvesting solutions in collaboration with the Institute for Sustainable Water Researchers, Monash University.
                </p>
            </div>
            <div style="clear: both"></div>
        </div>
        <br/>
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