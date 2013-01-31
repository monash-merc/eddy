 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.property value="pageTitle" /></title>

<#include "../template/header.ftl"/>
</head>

<body>
<!-- Navigation Section -->
<#include "../template/nav_section.ftl" />
<!-- Navigation Title -->
<#include "../template/action_title.ftl" />
<!-- End of Navigation Title -->	
<div class="main_body_container">
    <div class="main_big_border">
        <div class="none_boder_left_container">
            <br/>
            <div class="left_middle_panel">
                <br/>
                <#include "../template/action_message.ftl" />
                <div class="none_border_space_block"></div>
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