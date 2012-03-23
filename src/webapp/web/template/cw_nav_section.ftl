<!-- START OF Logo and Menu Section -->	
<div>
	<br/>
	<div class="menu_logo">
    	<a href="${base}/home"><img src="${base}/images/logo/cw_logo.jpg" border="0"/></a>
    	<!-- img src="${base}/images/logo/ozflux_title.gif"/ -->
    </div>
    
    <div class="menu_logo_right">
    <@s.if test="%{#session.authentication_flag =='authenticated'}">
		 <a href="${base}/user/userLogout.jspx">Logout</a> 
	</@s.if>
	<@s.else>
 	 	<a href="${base}/user/showLogin.jspx">Login</a> <a href="${base}/user/register_options">Register</a> 
	</@s.else>
	</div>
</div>
<div class="main_menu_bar">
    <div class="main_menu_bar_center">
		<!-- Main Menu START-->     
		 <a href="${base}/home" class="main_menu_link">Home</a>
		<@s.if test="%{#session.authentication_flag =='authenticated'}">
			<a href="${base}/data/listAllCollections.jspx" class="main_menu_link"><@s.text name="allcollection.nav.label.name" /></a>
			<a href="${base}/admin/listUsers.jspx" class="main_menu_link">Users</a> 
		</@s.if> 
		<@s.else>
			<a href="${base}/pub/listPubCollections.jspx" class="main_menu_link"><@s.text name="pubcollection.nav.label.name" /></a>
		</@s.else>         
		
    	<a href="${base}/search/showSearch.jspx" class="main_menu_link">Search</a>
		<a href="${base}/aboutus" class="main_menu_link" style="border-right-width: 1px;border-right-style: solid;	border-right-color: #595E7B;">About Us</a>
		<!-- Main Menu END-->
	</div>
</div>
<div class="main_menu_bar_bottom">&nbsp;</div>
<div style="clear:both"></div> 	
<br/>	
<!-- END OF Logo and Menu Section -->