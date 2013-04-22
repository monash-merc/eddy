<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="site.datainfo.action.title" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><a href="${base}/site/datainfo.jspx"><@s.text name="site.datainfo.action.title" /></a></div>
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
                        Data information
                    </div>
                    <div class="paragraph_div">
                        The data on this portal are measurements of ecosystem exchange of heat, water vapour
                        and carbon dioxide and supporting meteorological data for sites in Australian and New
                        Zealand. The data are stored in NetCDF (<a href="http://www.unidata.ucar.edu/software/netcdf" target="_blank">http://www.unidata.ucar.edu/software/netcdf</a>) files that
                        conform to the CF Metadata Convention (<a href="http://cf-pcmdi.llnl.gov" target="_blank">http://cf-pcmdi.llnl.gov</a>).
                        Information on the NetCDF files and utilities for accessing NetCDF files are available <a href="${base}/site/netcdf.jspx">here</a>.
                        A list of variable names used in the NetCDF files and their definitions is available here (internal link to PDF file containing variable name
                        definitions).
                    </div>
                    <div class="paragraph_div">
                        The data license terms and conditions are explained <a href="${base}/site/licenceinfo.jspx">here</a>.
                    </div>
                    <div class="paragraph_div">
                        The data in the NetCDF files is as follows:
                    </div>
                    <ul id="toc_small">
                        <li>
                            Meteorological data such as air temperature, humidity, wind speed and direction and precipitation.
                        </li>
                        <li>
                            Radiation data such as incoming and outgoing shortwave and longwave, net
                            radiation, photosynthetically active radiation (PAR, optional) and direct and diffuse
                            shortwave (optional).
                        </li>
                        <li>
                            Soil data such as soil heat flux, soil temperature and soil moisture.
                        </li>
                        <li>
                            Flux data such as friction velocity and the fluxes of momentum, sensible heat, latent
                            heat and carbon dioxide.
                        </li>
                    </ul>

                    <div class="paragraph_div">
                        The data is available on this portal at one of four processing levels:
                    </div>
                    <ul id="toc_num">
                        <li>
                            Level 1 – these files contain the characters "L1" in the name of the file. The data has not been subjected to any quality control or post-processing.
                        </li>
                        <li>
                            Level 2 – these files contain the characters "L2" in the name of the file. Data at this level have been subject to basic quality control checks but not to any post-
                            processing.
                        </li>
                        <li>
                            Level 3 – these files contain the characters "L3" in the name of the file. Data at this level has been subject to quality control and post-processing, however the data will
                            contain gaps due to the quality control process.
                        </li>
                        <li>
                            Level 4 – these files contain the characters "L4" in the name of the file. Data at this level has been subject to quality control, post-processing and gap-filling. The
                            gap-filling techniques used by OzFlux are still being developed and the current L4 data should be viewed as experimental only.
                        </li>
                    </ul>
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