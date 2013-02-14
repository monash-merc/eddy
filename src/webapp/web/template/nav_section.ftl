<!-- START OF Logo and Menu Section -->
<div class="site_top">
    <br/>
    <div class="site_top_left">
        <div class="menu_logo">
            <a href="http://ozflux.org.au" target="_blank" title="Australia and New Zealand Flux Research and Monitoring"><img src="${base}/images/logo/ozflux_logo.gif"/></a>
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
            <a href="${base}/user/showLogin.jspx">Login</a> <a href="${base}/user/register_options">Register</a>
        </@s.else>
        </div>

        <div class="right_logo">
            <a href="http://www.tern.org.au/" target="_blank" title="Terrestrial Ecosystem Research Network"><img src="${base}/images/logo/tern_big_logo.jpg" border="0"/></a>
        </div>
    </div>
    <div style="clear: both;"/>
</div>
<div class="ozfux_nav_div">
    <div class="ozfux_nav">
        <ul>
            <li><a href="${base}/home">Home</a></li>
        <@s.if test="%{#session.authentication_flag =='authenticated'}">
            <li><a href="#">Collections</a>
                <ul>
                    <li><a href="${base}/data/listAllCollections.jspx">List View</a></li>
                    <li><a href="${base}/mapview/showMapView.jspx">Map View</a></li>
                </ul>
            </li>
            <li><a href="${base}/admin/listUsers.jspx">Users</a></li>
        </@s.if>
        <@s.else>
            <li><a href="#">Collections</a>
                <ul>
                    <li><a href="${base}/pub/listPubCollections.jspx">List View</a></li>
                    <li><a href="${base}/mapview/showMapView.jspx">Map View</a></li>
                </ul>
            </li>
        </@s.else>
            <li><a href="${base}/search/showSearch.jspx">Search</a></li>
            <li><a href="#">Resources</a>
                <ul>
                    <li><a href="#">QA Python Code</a></li>
                    <li><a href="#">Data Type</a></li>
                </ul>
            </li>
            <li><a href="${base}/aboutus">About Us</a></li>
        </ul>
    </div>
</div>
<div style="clear:both"></div>



