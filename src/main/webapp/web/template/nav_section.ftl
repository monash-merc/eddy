<!-- START OF Logo and Menu Section -->
<div class="site_top">
    <br/>
    <div class="site_top_left">
        <div class="menu_logo">
            <a href="http://ozflux.org.au" target="_blank"
               title="Australia and New Zealand Flux Research and Monitoring"><img
                        src="${base}/images/logo/ozflux_logo.gif"/></a>
        </div>
        <div class="menu_text">
            <img src="${base}/images/logo/ozflux_title.gif"/>
        </div>
    </div>
    <div class="site_top_right">
        <div class="right_login">
            <@s.if test="%{#session.authentication_flag =='authenticated'}">
                <a href="${base}/user/userLogout.jspx">Logout</a>
            </@s.if>
            <@s.else>
                <a href="${base}/user/showLogin.jspx">Login</a> <a href="${base}/user/user_register">Register</a>
            </@s.else>
        </div>

        <div class="right_logo">
            <a href="http://www.tern.org.au/" target="_blank" title="Terrestrial Ecosystem Research Network"><img
                        src="${base}/images/logo/tern_big_logo.jpg" border="0"/></a>
        </div>
    </div>
    <div style="clear: both;"/>
</div>

<div class="site-top-nav-section">
    <div class="nav-item">
        <div class="nav-menu">
            <a href="${base}/home">Home</a>
        </div>
    </div>

    <div class="nav-item">
        <@s.if test="%{#session.authentication_flag =='authenticated' && mapEnabled == false }">
            <div class="nav-menu">
                <a href="${base}/data/listAllCollections.jspx">Collections</a>
            </div>
        </@s.if>
        <@s.if test="%{#session.authentication_flag =='authenticated' && mapEnabled == true }">
            <div class="nav-menu">Collections</div>
            <div class="dropdown-menu">
                <a href="${base}/data/listAllCollections.jspx">List View</a>
                <a href="${base}/mapview/showMapView.jspx">Map View</a>
            </div>
        </@s.if>
        <@s.if test="%{#session.authentication_flag !='authenticated' && mapEnabled == false }">
            <div class="nav-menu">
                <a href="${base}/pub/listPubCollections.jspx">Collections</a>
            </div>
        </@s.if>
        <@s.if test="%{#session.authentication_flag !='authenticated' && mapEnabled == true }">
            <div class="nav-menu">Collections</div>
            <div class="dropdown-menu">
                <a href="${base}/pub/listPubCollections.jspx">List View</a>
                <a href="${base}/mapview/showMapView.jspx">Map View</a>
            </div>
        </@s.if>
    </div>

    <@s.if test="%{#session.authentication_flag =='authenticated'}">
        <div class="nav-item">
            <div class="nav-menu">
                <a href="${base}/admin/listUsers.jspx">Users</a>
            </div>
        </div>
    </@s.if>

    <div class="nav-item">
        <div class="nav-menu">
            <a href="${base}/search/showSearch.jspx">Search</a>
        </div>
    </div>

    <div class="nav-item">
        <div class="nav-menu">Resources</div>
        <div class="dropdown-menu">
            <a href="${base}/site/datainfo.jspx">Data Information</a>
            <a href="${base}/site/licenceinfo.jspx">Licencing Information</a>
            <a href="${base}/site/rainfo.jspx">Restricted Access</a>
            <a href="${base}/site/netcdf.jspx">NetCDF Files</a>
            <a href="${base}/site/utilities.jspx">Utilities</a>
        </div>
    </div>

    <div class="nav-item">
        <div class="nav-menu">Help</div>
        <div class="dropdown-menu">
            <a href="${base}/site/faq.jspx">FAQs</a>
            <a href="${base}/site/userguide.jspx">User Guides</a>
        </div>
    </div>

    <div class="nav-item">
        <div class="nav-menu">
            <a href="${base}/aboutus">About Us</a>
        </div>
    </div>
</div>
<div style="clear:both"></div>



