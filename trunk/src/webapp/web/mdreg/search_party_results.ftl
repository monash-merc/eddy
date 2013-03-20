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
<div class="popup_main_div" >
<#include "../template/action_errors.ftl" />
    <div class="popup_title">
        Add an associated researcher
    </div>
<@s.form action="selectParty.jspx" namespace="/data" method="post">
    <div class="popup_row">
        <div class="popup_spec">
            Found the researcher(s) information as bellow:
        </div>
        <div class="popup_input_div">
            <div class="comments">* Please select one of the following researcher(s)</div>
        </div>

        <div class="content_none_border_div">
            <@s.iterator status="ptState" value="foundPartyBeans" id="researcherParty" >
                <table class="display_data_tab">
                    <tr>
                        <td align="center" rowspan="7">
                            <@s.checkbox name="foundPartyBeans[${ptState.index}].selected" cssClass="check_box"/>
                        </td>
                        <td width="100">Name:</td>
                        <td><@s.property value="#researcherParty.personTitle" /> <@s.property value="#researcherParty.personGivenName" /> <@s.property value="#researcherParty.personFamilyName" /></td>
                        <td align="center" rowspan="7">
                            <@s.if test="%{#researcherParty.fromRm == false}">
                                <div class="tab_link">
                                    <a href="${base}/data/showEditUDParty.jspx?selectedPartyBean.partyKey=<@s.property value='#researcherParty.partyKey' />&searchCnOrEmail=${searchCnOrEmail}">Update</a>
                                </div>
                            </@s.if>
                            <@s.else>
                                &nbsp;
                            </@s.else>
                        </td>
                    </tr>

                    <tr>
                        <td>E-mail:</td>
                        <td><@s.property value="#researcherParty.email" /></td>
                    </tr>
                    <tr>
                        <td>Address:</td>
                        <td><@s.property value="#researcherParty.address" /></td>
                    </tr>
                    <tr>
                        <td>Web URL:</td>
                        <td><@s.property value="#researcherParty.url" /></td>
                    </tr>
                    <tr>
                        <td>Description:</td>
                        <td><@s.property value="#researcherParty.description" /></td>
                    </tr>
                    <tr>
                        <td>Group Name:</td>
                        <td><@s.property value="#researcherParty.groupName" /></td>
                    </tr>
                    <tr>
                        <td>Group Web Site:</td>
                        <td>
                            <@s.property value="#researcherParty.originateSourceValue" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].partyKey" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].personTitle" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].personGivenName" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].personFamilyName" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].email" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].address" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].url" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].description" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].identifierType"  />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].identifierValue" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].originateSourceType" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].originateSourceValue" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].groupKey" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].groupName" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].fromRm" />
                            <@s.hidden name="searchCnOrEmail" />
                        </td>
                    </tr>
                </table>
                <div class="blank_separator"></div>
            </@s.iterator>
            <div class="blank_separator"></div>
        </div>
    </div>
    <div class="popup_button_div">
        <input type="button" value=" Cancel " class="input_button_style" onclick="window.location = '${base}/data/showSearchParty.jspx?searchCnOrEmail=${searchCnOrEmail}';"/> &nbsp;&nbsp; <input
            type="submit" value=" Next " class="input_button_style"/>
    </div>
</@s.form>
</div>
</body>
</html>