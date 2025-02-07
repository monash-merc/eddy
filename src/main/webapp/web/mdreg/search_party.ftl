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
        Add an associated researcher
    </div>
    <div class="popup_row">
        <div class="popup_spec">
            To add an associated researcher, please enter the researcher name or E-mail address (preferred method). If the researcher does not currently exist in Ozflux, you will need to enter a
            researcher information manually
        </div>
    </div>
<@s.form action="searchParty.jspx" namespace="/data" method="post">
    <div class="popup_row">
        <div class="popup_spec">
            Enter a researcher name or E-mail below:
        </div>
        <div class="popup_input_div">
            <@s.textfield name="searchCnOrEmail" />
            <div class="comments">(e.g. John Smith or john.smith@ozflux.org)</div>
        </div>
        <div class="blank_separator"></div>
    </div>
    <div class="popup_button_div">
        <@s.submit value="Next" cssClass="input_button_style" />
    </div>
</@s.form>
    <br/>
    <br/>
</div>
</body>
</html>