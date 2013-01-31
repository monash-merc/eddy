<!-- SUB-Menu Section -->
<div class="blank_big_separator"></div> 
<div class="blank_big_separator"></div> 
<div class="blank_separator"></div>

<div class="right_block_section">
	<div class="sub_nav_container">
		<ul>
			<li><a href="${base}/admin/displayUserHome.jspx">My Home</a></li>
			<li><a href="${base}/data/listUserCollections.jspx"><@s.text name="mycollection.nav.label.name" /></a></li>
			<li><a href="${base}/data/showCreateCollection.jspx"><@s.text name="create.new.collection" /></a></li>
			<li><a href="${base}/data/listAllCollections.jspx"><@s.text name="allcollection.nav.label.name" /></a></li>
			<li><a href="${base}/admin/listUserEvents.jspx"><@s.text name="user.events.action.title" /></a></li>
		</ul>
	</div>

	<div class="right_separator"></div>
	<div class="blank_separator"></div>
	<div class="blank_separator"></div> 
		<div class="user_avatar">
			<img  src='${base}/user/viewImage.jspx?avatarUserId=<@s.property value="#session.authen_user_id" />' />
		</div>
	<div class="user_id">
		<span class="inline_span">
		<@s.if test="%{user.userType == 1}">
			Super Admin: &nbsp;
		</@s.if>
		<@s.elseif test="%{user.userType == 2}">
			Admin: &nbsp;
		</@s.elseif>
		<@s.property value="#session.authen_user_name" />
		&nbsp;&nbsp&nbsp;<a href="${base}/admin/showImageUpload.jspx"><img src="${base}/images/edit.png" border="0"/></a>
		</span>
	</div>
	<div style="clear:both"></div>
	<!--
	<div class="blank_separator"></div>-->
	<div class="right_separator"></div>
	<!-- div class="blank_separator"></div> 
	
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>-->
</div>
<!-- End of Sub-Menu Section -->