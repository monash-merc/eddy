<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="site.utilities.action.title" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><a href="${base}/site/utilities.jspx"><@s.text name="site.utilities.action.title" /></a></div>
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
                        Utilities
                    </div>

                    <div class="paragraph_div">
                        Data from this portal are available as NetCDF files. Information on this file format is
                        available <a href="http://www.unidata.ucar.edu/software/netcdf/" target="_blank">here</a> and information on the OzFlux
                        use of NetCDF files is available <a href="${base}/site/netcdf.jspx">here</a>.
                    </div>

                    <div class="paragraph_div">
                        There is a large range of options for viewing or reading the NetCDF files available from this portal.
                    </div>

                    <div class="paragraph_div">
                        Many of the applications commonly used for data processing (IDL, GDL, Python, MatLab, R etc) are able to read and write NetCDF files.
                        OzFlux uses and recommends the <a href="https://www.enthought.com/products/epd/" target="_blank">Enthought
                        Python DistributionM</a> for manipulating the NetCDF data files available on this portal.
                    </div>

                    <div class="paragraph_div">
                        Libraries for reading and writing NetCDF files are also available for Fortran, C and other programming languages, see the <a
                            href="http://www.unidata.ucar.edu/software/netcdf/software.html" target="_blank">UniData</a> web site for details.
                    </div>

                    <div class="paragraph_div">
                        Various command line utilities are also available for viewing the contents of NetCDF files such as ncBROWSE and ncVIEW, see the <a
                            href="http://www.unidata.ucar.edu/software/netcdf/software.html" target="_blank">UniData</a> site for a complete list of third party software for use with NetCDF
                        files.
                    </div>

                    <div class="paragraph_div">
                        OzFlux uses a suite of Python scripts to process the data from its flux towers. The scripts are available in the <a
                            href="http://ozflux.its.monash.edu.au/ecosystem/pub/viewColDetails.jspx?collection.id=151&collection.owner.id=50&viewType=anonymous">Public Access</a> collection on
                        the OzFlux Data Portal.
                        Two of the scripts provide basic access to the NetCDF files. <b>nclist.py</b> provides a listing of the contents of a NetCDF file to the screen and to a text file that can be
                        viewed in a suitable editor. <b>nc2fn.py</b> will read the contents of a NetCDF file and write selected data to a CSV (comma separated value) text file. This file can then be
                        imported
                        into any spreadsheet application.
                    </div>

                    <div class="paragraph_div">
                        An add-in that allows Excel to read and write NetCDF files is also available from <a href="http://code.google.com/p/netcdf4excel" target="_blank">here</a>.
                        The add-in only works with Excel 2007 and the 32-bit version of Excel 2010 but provides a convenient way to view
                        and manipulate the NetCDF files available from this portal.
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