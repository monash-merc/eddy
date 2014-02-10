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
<body width="500">
<div class="popup_main_div">
<#include "../template/action_errors.ftl" />
    <div class="popup_title">
        Licence Options
    </div>
    <div class="popup_row">
        <div class="popup_spec">
            Select the Licence you want to apply to this collection so that interested people understand what they are entitled to do with your published data
        </div>
    </div>

<@s.form action="selectLicence.jspx" namespace="/data" method="post">
    <div class="popup_row">
        <div class="blank_separator"></div>
        <@s.radio name="licence.licenceType" theme = "merctheme" cssClass="radio_box" list="licenceMap" id="licenceType" value="licence.licenceType"  title="Please select a Licence"/>
        <br/>
    </div>

    <div class="popup_button_div">
        <@s.hidden name="collection.id" />
        <input type="button" value="Cancel" class="input_button_style" id="cancelLicence"/> &nbsp;&nbsp; <@s.submit value="%{getText('license.next.button')}" cssClass="input_button_style" />
    </div>
</@s.form>
</div>
</body>
</html>