<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<#include "../template/jquery_header.ftl"/>
<style type="text/css">
.error_msg_section{
	background: none repeat scroll 0 0 white;
    margin: 10px auto;
    text-align: center;
    width: 410px;
}
</style>
</head>
<body>
<br/>
<div class="mcpop_pmain_div">
    <#include "../template/action_errors.ftl" />
    <div class="mcpop_title">
        Add the associated researcher
    </div>
    <@s.form action="searchParty.jspx" namespace="/data" method="post">
    <div class="mcpop_field">
        <br/>
        <div class="mcpop_input_value">
            Enter a researcher full name or email bellow:
        </div>
        <div class="blank_separator"></div>
        <div class="mcpop_input_value">
            <@s.textfield name="searchCnOrEmail" cssClass="pop_input_field" /><div class="name_comment">(e.g. John Smith or john.smith@ozflux.org)</div>
        </div>
        <br/>
        <br/>
        <br/>
        <br/>
    </div>
    <div class="mcpop_bddiv">
        <input type="submit" value=" Next " class="mcpop_button" />
    </div>
    </@s.form>
    <br/>
    <br/>
</div>
</body>
</html>