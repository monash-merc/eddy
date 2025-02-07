<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<#include "../template/jquery_header.ftl"/>
    <style type="text/css">
        .error_msg_section {
            margin: 5px;
            width: 410px;
            text-align: left;
        }
    </style>
</head>
<body>
<div class="popup_main_div">
<#include "../template/action_errors.ftl" />
    <div class="popup_title">
        Licence Options
    </div>
    <div class="popup_row">
        <div class="blank_separator"></div>
        <div class="blank_separator"></div>
        <div class="blank_separator"></div>
        <div class="popup_spec">
            Please contact the system administrator
        </div>
        <div class="blank_separator"></div>
        <div class="blank_separator"></div>
        <div class="blank_separator"></div>
    </div>
</div>
</body>
</html>