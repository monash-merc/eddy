<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="site.netcdf.action.title" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><a href="${base}/site/netcdf.jspx"><@s.text name="site.netcdf.action.title" /></a></div>
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
                        NetCDF Files
                    </div>
                    <div class="paragraph_div">
                        The data on this portal are available as NetCDF files that conform to the CF Metadata
                        Conventions. NetCDF (Network Common Data Form) files are a self-describing, machine-
                        independent format for storing numerical data. Details on the NetCDF file format can
                        be found on the Unidata web site (<a href="http://www.unidata.ucar.edu/software/netcdf/">http://www.unidata.ucar.edu/software/netcdf</a>).
                        NetCDF files are binary files which means they can not be viewed in the standard
                        applications that come with most operating systems (text editors, word processors or
                        spreadsheet programs).
                    </div>
                    <div class="paragraph_div">
                        The NetCDF format was chosen because it allows data and metadata to be packaged into
                        a single file. The OzFlux NetCDF files contain metadata about the site, the data owner
                        and the data license in the global attributes section of the NetCDF file. Metadata about the
                        individual variables in the NetCDF files is contained in the variable attributes. A complete
                        description of the OzFlux NetCDF file contents is available on the User Guides page under
                        the Help menu.
                    </div>
                    <div class="paragraph_div">
                        Utilities for accessing data in NetCDF files are available from the Resources/<a href="${base}/site/utilities.jspx">Utilities</a> page.
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