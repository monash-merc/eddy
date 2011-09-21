<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.text name="user.reset.password.error" /></title>

<#include "../template/header.ftl"/>

<script type="text/javascript">
function refresh()
{
	document.getElementById("imagevalue").src='captchCode.action?now=' + new Date();
}
</script>
</head>

<body>
<!-- Navigation Section -->
<#include "../template/nav_section.ftl" />
<!-- Navigation Title -->
<div class="title_panel">
	<div class="div_inline">&nbsp;&nbsp;</div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline"><a href="${base}/user/user_request_resetpwd"><@s.text name="user.reset.passwd.action.title" /></a></div>
</div>	
<div style="clear:both"></div> 	
<!-- End of Navigation Title -->
<div class="main_body_container">
	<div class="main_big_border">

        <div class="none_boder_left_container">
            <br/>
            <div class="left_middle_panel">
                <div class="error_msg_div">
                    <#include "../template/action_errors.ftl" />
                </div>
                <div class="none_border_space_block"></div>
                <br/>
                <br/>
            </div>
            <div style="clear:both"></div>
            <div class="right_container_panel">
                  &nbsp;
            </div>
        </div>
        <div style="clear:both"></div>

	</div>
</div>
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>
