<div class="user_avatar_div">
    <img src='${base}/user/viewImage.jspx?avatarUserId=<@s.property value="#session.authen_user_id" />'/>
</div>
<div style="clear: both;"></div>
<div class="user_info_div">
<@s.if test="%{user.userType == 1}">
    Super Admin: &nbsp;
</@s.if>
<@s.elseif test="%{user.userType == 2}">
    Admin: &nbsp;
</@s.elseif>
<@s.property value="#session.authen_user_name" />
    &nbsp;&nbsp&nbsp;<a href="${base}/admin/showImageUpload.jspx"><img src="${base}/images/edit.png" border="0"/></a>
</div>
<div style="clear: both;"></div>
<div class="sub_nav_div">
    <ul>
        <li><a href="${base}/admin/displayUserHome.jspx">My Home</a></li>
        <li><a href="${base}/data/listUserCollections.jspx"><@s.text name="mycollection.nav.label.name" /></a></li>
        <li><a href="${base}/data/showCreateCollection.jspx"><@s.text name="create.new.collection" /></a></li>
        <li><a href="${base}/data/listAllCollections.jspx"><@s.text name="allcollection.nav.label.name" /></a></li>
        <li><a href="${base}/admin/listUserEvents.jspx"><@s.text name="user.events.action.title" /></a></li>
    </ul>
</div>

