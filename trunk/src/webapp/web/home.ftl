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
							<img src="${base}/slideshow/DalyRiver_NT_small.jpg" alt="Daly River Site" />
							<span>
								<b>Daly River</b><br />
								OzFlux is a national ecosystem research network consisting of 10 flux stations.
							</span>
						</a>
						
						<a href="#" >
							<img src="${base}/slideshow/CapeTribulation_QLD_small.jpg" alt="CapeTribulation QLD" />
							<span>
								<b>Cape Tribulation QLD</b>
							</span>
						</a>
						<a href="#" >
							<img src="${base}/slideshow/NewWallaby_VIC_small.jpg" alt="New Wallaby VIC" />
							<span>
								<b>New Wallaby VIC</b>
							</span>
						</a>
						<a href="#" >
							<img src="${base}/slideshow/Tumbarumba2_NSW_small.jpg" alt="Tumbarumba NSW" />
							<span>
								<b>Tumbarumba NSW</b>
							</span>
						</a>
					</div>
				</div>
			</div>
			<div class="home_right_panel">
				<div class="paragraph_title">Introduction</div>
				<br/>
				<p>
				OzFlux is a national ecosystem research network consisting of 10 flux stations at present with a further 6 planned for installation in 2011,
				all funded under the TERN 1 initiative. Funding for 6 more sites has been requested under the TERN-EIF initiative.  The final network of 21 sites
				 will provide the Australian and global ecosystem modelling communities with nationally consistent observations of energy, carbon and water exchange 
				 between the atmosphere and key Australian ecosystems.  OzFlux is part of an international network (FluxNet) of over 500 flux stations that is designed
				  to provide continuous, long-term micrometeorological measurements to monitor the state of ecosystems globally.
				</p>
				<p>
				A Central Node administered by CSIRO Marine and Atmospheric Research coordinates the OzFlux network, determines protocols for measurements, 
				data processing and quality control, provides a database to archive data from each site and provides training to site operators as required.  
				A 7 member Steering Committee chaired by Dr Helen Cleugh (CMAR) and Associate Professor Mike Liddell (James Cook University) provides scientific 
				leadership for the network and coordinates logistics as required.
				</p>
			</div>
            <div style="clear:both"></div>
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