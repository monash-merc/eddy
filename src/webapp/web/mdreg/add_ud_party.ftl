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
    <@s.form action="addUDParty.jspx" namespace="/data" method="post">
        <div class="mcpop_field">
            <br/>

            <div class="mcpop_input_value">
                <span class="inline_span2">
                   Please input a researcher information bellow:
                </span>
            </div>

            <div style="clear:both"></div>
            <div class="blank_separator"></div>
            <div class="mcpop_input_value">
            <table width="450">
                <tr>
                    <td>
                        Title:
                        <div class="name_comment">* (<@s.text name="ands.add.party.party.title.hint" />)</div>
                    </td>
                </tr>
                <tr>
                    <td><@s.textfield name="addedPartyBean.personTitle" cssClass="pop_input_field" /></td>
                </tr>

                <tr>
                    <td>
                        First Name:
                        <div class="name_comment">* (<@s.text name="ands.add.party.party.first.name.hint" />)</div>
                    </td>
                </tr>
                <tr>
                    <td><@s.textfield name="addedPartyBean.personGivenName" cssClass="pop_input_field" /></td>
                </tr>
                <tr>
                    <td>
                        Last Name:
                        <div class="name_comment">* (<@s.text name="ands.add.party.party.last.name.hint" />)</div>
                    </td>
                </tr>
                <tr>
                    <td><@s.textfield name="addedPartyBean.personFamilyName" cssClass="pop_input_field" /></td>
                </tr>
                <tr>
                    <td>
                        Email:
                        <div class="name_comment">* (<@s.text name="ands.add.party.party.email.hint" />)</div>
                    </td>
                </tr>
                <tr>
                    <td><@s.textfield name="addedPartyBean.email" cssClass="pop_input_field" /></td>
                </tr>
                <tr>
                    <td>
                        Address:
                        <div class="name_comment">* (<@s.text name="ands.add.party.party.address.hint" />)</div>
                    </td>
                </tr>
                <tr>
                    <td><@s.textarea name="addedPartyBean.address"  cssClass="input_textarea" style="width: 300px; height: 80px;" /></td>
                </tr>
                <tr>
                    <td>
                        Web URL:
                        <div class="name_comment">* (<@s.text name="ands.add.party.party.url.hint" />)</div>
                    </td>
                </tr>
                <tr>
                    <td><@s.textfield name="addedPartyBean.url" cssClass="pop_input_field" /></td>
                </tr>
                <tr>
                    <td>
                        Group Name:
                        <div class="name_comment">* (<@s.text name="ands.add.party.party.group.name.hint" />)</div>
                    </td>
                </tr>
                <tr>
                    <td><@s.textfield name="addedPartyBean.groupName" cssClass="pop_input_field" /></td>
                </tr>
                <tr>
                    <td>
                        Group Web Site:
                        <div class="name_comment">* (<@s.text name="ands.add.party.party.group.url.hint" />)</div>
                    </td>
                </tr>
                <tr>
                    <td><@s.textfield name="addedPartyBean.originateSourceValue" cssClass="pop_input_field" />
                    <@s.hidden name="searchCnOrEmail" />
                    </td>
                </tr>
            </table>
            </div>
        </div>
        <div style="clear:both"></div>
        <div class="mcpop_bddiv">
            <input type="button" value=" Cancel " class="mcpop_button" onclick="window.location = '${base}/data/showSearchParty.jspx?searchCnOrEmail=${searchCnOrEmail}';" /> &nbsp;&nbsp; <input type="submit" name="options" value=" Next " class="mcpop_button" />
        </div>
    </@s.form>
</div>
</body>
</html>