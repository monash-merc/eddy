<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="site.faq.action.title" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><a href="${base}/site/faq.jspx"><@s.text name="site.faq.action.title" /></a></div>
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
    Table of contents
</div>
<ul id="toc_num">
    <li><a href="#section1">How do I use this portal? </a></li>
    <li><a href="#section2">What data is available from this portal?</a></li>
    <li><a href="#section3">What do I have to do before I can download the data?</a></li>
    <li><a href="#section4">How do I register for an account? </a></li>
    <li><a href="#section5">How do I download the data? </a></li>
    <li><a href="#section6">What does ‘Restricted Access’ mean? </a></li>
    <li><a href="#section7">How do I apply a restricted access period to my data file?</a></li>
    <li><a href="#section8">I am trying to upload a file with an identical name to an existing file in the OzFlux Data Portal. It doesnt allow me to do so. Howcome?</a></li>
    <li><a href="#section9">What kind of files can I upload to a collection? And will they capture my metadata?</a></li>
    <li><a href="#section10">How do I read the files I have downloaded? </a></li>
    <li><a href="#section11">What do all the variable names in the files mean? </a></li>
</ul>

<div class="sub_norm_title" id="section1">
    1. How do I use this portal?
</div>
<div class="paragraph_div">
    <p>
        You can use this portal to browse and download data from the OzFlux flux tower sites. OzFlux and the sites in the network are described on the <a
            href="http://ozflux.org.au" target="_blank">OzFlux</a> web site. Anonymous users can browse the contents of this portal but you will need to register for an account to
        be able to download data.
    </p>

    <p>
        Anonymous users can navigate around the portal via the main menu at the top of the page. Users who login have an additional user menu at the right of the page.
        The data on this portal are organised into collections with one collection containing the data from at least one site. You can browse the collections via
        the <@s.if test="%{#session.authentication_flag =='authenticated'}">
        <a href="${base}/data/listAllCollections.jspx"><b>List View</b></a>
    </@s.if>
    <@s.else>
        <a href="${base}/pub/listPubCollections.jspx"><b>List View</b></a>
    </@s.else> or <a href="${base}/mapview/showMapView.jspx"><b>Map View</b></a> under the Collections menu.
    </p>

    <p>
        In <b>List View</b>, clicking on the collection title or on the <b>View Details</b> button will open the collection and display a description of the collection and a
        list of data files available for the collection.
        All files in the collection will have this button that allows the user to see the global attributes, variables and variable attributes of the data file.
        Files that are not restricted access will have an <b>Export</b> button that allows you to download data. Files that are restricted access will not have an <b>Export</b>
        button visible. If you would like access to restricted data, use the <b>Contact Owner</b> button at the bottom of the collection description text to send an email to the
        collection owner requesting access.
    </p>

    <p>
        In <b>Map View</b>, clicking on the collection marker pin will display the collection below the map. You can then access the collection as you would in <b>List View</b>.
    </p>

    <p>
        The <b>Users</b> menu displays a list of users registered with accounts on the OzFlux Data Portal.
    </p>

    <p>
        The <b>Search</b> menu allows you to search for a data collection using a number of criteria.
    </p>

    <p>
        The <b>Resources</b> menu contains entries with information on the data held on the portal, the licence covering the data and the restricted access provisions, the NetCDF
        files stored on the portal and utilities for viewing and accessing the NetCDF files.
    </p>
</div>

<div class="sub_norm_title" id="section2">
    2. What data is available from this portal?
</div>
<div class="paragraph_div">
    The data available from this portal are measurements of the exchange of energy and mass between the surface and the atmosphere made at many locations around Australia. In
    addition to the fluxes of momentum, heat, water vapour and carbon dioxide, there is supporting meteorological data such as incoming and outgoing radiation, air temperature,
    humidity, wind speed and direction, soil temperature, soil moisture and precipitation. Further information can be found under
    the <a href="${base}/site/datainfo.jspx">Data information</a> menu.
</div>
<div class="sub_norm_title" id="section3">
    3. What do I have to do before I can download the data?
</div>
<div class="paragraph_div">
    You have to be a registered and logged in user before you can download data from the OzFlux Data Portal, see below for details.
    <p>
        Data files on the portal can be either open or restricted access.
    </p>

    <p>
        Open access files can be downloaded by clicking on the <b>Export</b> button to the right of the file name in the collection view.
    </p>

    <p>
        Restricted access can be applied to data files by the data owner to protect the work of a post-graduate student. The maximum restricted access period is 18 months from the
        date of setting restriction. Users are still able to request access to restricted data by contacting the data owner. Details of the restricted access provisions are given
        in the <a href="${base}/site/rainfo.jspx">Restricted Access</a> menu.
    </p>
</div>

<div class="sub_norm_title" id="section4">
    4. How do I register for an account?
</div>
<div class="paragraph_div">
    <p>
        You can register for an account on the OzFlux Data Portal by clicking the <b>Register</b> button at the top of the OzFlux Data Portal Welcome/Home page. The <b>Register</b>
        button is to the right of the OzFlux logo and to the right of the Login button and the TERN logo.
    </p>

    <p>
        Monash researchers can use the <b>Monash User Registration</b> link. The registration process will then acquire your information from the Monash Authcate database.
        Non-Monash researchers must use the <b>User Self-Registration</b> link, complete the details on the form and click the <b>Register</b> button at the bottom of the page.
        This will send an
        email to the portal administrator who will then activate your account. Activation normally takes two to three days.
    </p>
</div>
<div class="sub_norm_title" id="section5">
    5. How do I download the data?
</div>
<div class="paragraph_div">
    Click on the <b>Export</b> button to the right of the file name in the collection view. If the Export button is not visible, access to the file is
    <a href="${base}/site/rainfo.jspx">restricted</a>. You can contact the collection owner and request permission to access a data file using the <b>Contact Owner</b> button on
    the right of the collection description text.
</div>

<div class="sub_norm_title" id="section6">
    6. What does ‘Restricted Access’ mean?
</div>
<div class="paragraph_div">
    <p>
        Collection Owners have the ability to mark their data sets as ‘restricted’. This means that OzFlux Data Portal users will not be able to download the file unless permission
        is explicitly granted to them by the Collection Owner. Users can email the Collection Owner to request access to restricted data using the <b>Contact Owner</b> button on
        the collection page.
        This page can be accessed by clicking on the collection title in the collection
    <@s.if test="%{#session.authentication_flag =='authenticated'}">
        "<a href="${base}/data/listAllCollections.jspx"><b>List View</b></a>"
    </@s.if>
    <@s.else>
        "<a href="${base}/pub/listPubCollections.jspx"><b>List View</b></a>"
    </@s.else>.
    </p>

    <p>
        Restricted access can be set for no more than 18 months after the submission of data to the portal.
    </p>

    <p>
        Data which has been placed under restricted access will not have an <b>Export</b> button alongside the data file name in the collection.
        Refer to the <a href="${base}/site/rainfo.jspx">Restricted Access</a> page for more information.
    </p>
</div>

<div class="sub_norm_title" id="section7">
    7. How do I apply a restricted access period to my data file?
</div>
<div class="paragraph_div">
    <p>
        Owners of data collections on the OzFlux Data Portal have the ability to restrict access to data files in their collections in order to safeguard the intellectual property
        of post-graduate research students.
    </p>

    <p>
        To do this, ensure you are logged into the OzFlux Data Portal. Navigate to your collection and scroll down to the data file to which you want to restrict access.
        Click on the cog icon for <b>Manage restricted access</b>.
    </p>

    <p>
        The restriction period ‘start date’ will always be set to the data upload date or in the case of a currently restricted file for which the restricted access period is being
        modified, the date previously selected to be the start date.
    </p>

    <p>
        The restriction period ‘end date’ can be set to no more than 18 months from the submission of data to the portal.
    </p>

    <p>
        Once the restriction settings have been saved, the data file will no longer be available for export unless the collection owner explicitly grants permission to a user who requests
        permission to access the data.
    </p>

    <p>
        OzFlux Data Portal users are able to request access by using the ‘Contact Owner’ button to email the collection owner to request access to restricted files.

        To allow access to a restricted file, the collection owner will need to click on the Permissions button and scroll to the bottom of the page to the <b>Permission</b> Settings section. Click on
        the <b>Select User</b> drop down option, select a user and click <b>Add</b>.
    </p>

    <p>
        Their name will now appear under the Individual User Permissions table. Select the access you need to provide and click on <b>Save All</b>.
    </p>

    <p>
        To remove a user’s permissions, click on the “-” button to the right of the permission level check boxes.
    </p>

    <p>
        <b>Note</b>: Once the restricted access period for a file has expired it is not possible to apply a restricted access period to the file and the file will automatically be made available for
        export.
    </p>
</div>

<div class="sub_norm_title" id="section8">
    8. I am trying to upload a file with an identical name to an existing file in the OzFlux Data Portal. It doesnt allow me to do so. Howcome?
</div>
<div class="paragraph_div">
    To ensure naming and version control consistency, files with identical names cannot be imported into the OzFlux Data Portal. You will need to change the file name or use a version number to
    distinguish between the two. Refer file <a href="${base}/site/netcdf.jspx">naming convention</a>.
</div>

<div class="sub_norm_title" id="section9">
    9. What kind of files can I upload to a collection? And will they capture my metadata?
</div>
<div class="paragraph_div">
    Any type of file can be uploaded to the portal. However, metadata can only be extracted from NetCDF, GRIB and GRIB2 file types. The <b>View Metadata</b> button will only appear alongside files
    with these formats.
</div>

<div class="sub_norm_title" id="section10">
    10. How do I read the files I have downloaded?
</div>
<div class="paragraph_div">
    <p>
        The files stored on the OzFlux Data Portal are NetCDF files that follow the CF Metadata conventions.
    </p>

    <p>
        Information of the NetCDF files is available <a href="${base}/site/netcdf.jspx">here</a>.
    </p>

    <p>
        Information on utilities that provide access to NetCDF files is available <a href="${base}/site/utilities.jspx">here</a>.
    </p>
</div>

<div class="sub_norm_title" id="section11">
    11. What do all the variable names in the files mean?
</div>
<div class="paragraph_div">
    A PDF document that explains the variable OzFlux naming convention and defines the variable names used in the NetCDF files can be found <a href="http://eddy.googlecode.com/files/VariableNamesandDefinitions.pdf" target="_blank">here</a>.
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