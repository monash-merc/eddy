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
            The researcher information details:
        </div>

        <div class="content_none_border_div">
            <div class="popup_input_div">
                <div class="popup_input_field_title">
                    Title:
                </div>
                <div class="input_field_value_section">
                <@s.property value="selectedPartyBean.personTitle" />
                </div>
            </div>
            <div style="clear: both;"></div>
            <div class="popup_input_div">
                <div class="popup_input_field_title">
                    First Name:
                </div>
                <div class="input_field_value_section">
                <@s.property value="selectedPartyBean.personGivenName" />
                </div>
            </div>
            <div style="clear: both;"></div>

            <div class="popup_input_div">
                <div class="popup_input_field_title">
                    Last Name:
                </div>
                <div class="input_field_value_section">
                <@s.property value="selectedPartyBean.personFamilyName" />
                </div>
            </div>
            <div style="clear: both;"></div>

            <div class="popup_input_div">
                <div class="popup_input_field_title">
                    Email:
                </div>
                <div class="input_field_value_section">
                <@s.property value="selectedPartyBean.email" />
                </div>
            </div>
            <div style="clear: both;"></div>

            <div class="popup_input_div">
                <div class="popup_input_field_title">
                    Address:
                </div>
                <div class="input_field_value_section">
                <@s.property value="selectedPartyBean.address" />
                </div>
            </div>
            <div style="clear: both;"></div>

            <div class="popup_input_div">
                <div class="popup_input_field_title">
                    Web URL:
                </div>
                <div class="input_field_value_section">
                <@s.property value="selectedPartyBean.url" />
                </div>
            </div>
            <div style="clear: both;"></div>

            <div class="popup_input_div">
                <div class="popup_input_field_title">
                    Description:
                </div>
                <div class="input_field_value_section">
                <@s.property value="selectedPartyBean.description" />
                </div>
            </div>
            <div style="clear: both;"></div>

            <div class="popup_input_div">
                <div class="popup_input_field_title">
                    Group Name:
                </div>
                <div class="input_field_value_section">
                <@s.property value="selectedPartyBean.groupName" />
                </div>
            </div>
            <div style="clear: both;"></div>

            <div class="popup_input_div">
                <div class="popup_input_field_title">
                    Group Web Site:
                </div>
                <div class="input_field_value_section">
                <@s.property value="selectedPartyBean.originateSourceValue" />
                </div>
            </div>
            <div>
            <@s.hidden name="selectedPartyBean.partyKey" id = "ands_p_key" />
				<@s.hidden name="selectedPartyBean.personTitle" id = "ands_p_title"/>
			    <@s.hidden name="selectedPartyBean.personGivenName" id = "ands_p_givenname" />
			    <@s.hidden name="selectedPartyBean.personFamilyName" id = "ands_p_sname"/>
			    <@s.hidden name="selectedPartyBean.email" id = "ands_p_email"/>
			    <@s.hidden name="selectedPartyBean.address" id = "ands_p_address" />
			    <@s.hidden name="selectedPartyBean.url" id = "ands_p_url"/>
			    <@s.hidden name="selectedPartyBean.description" id = "ands_p_desc"/>
			    <@s.hidden name="selectedPartyBean.identifierType" id = "ands_p_idtype"/>
			    <@s.hidden name="selectedPartyBean.identifierValue" id = "ands_p_idvalue" />
			    <@s.hidden name="selectedPartyBean.originateSourceType" id = "ands_p_srctype"/>
			    <@s.hidden name="selectedPartyBean.originateSourceValue" id = "ands_p_srcvalue" />
			    <@s.hidden name="selectedPartyBean.groupName" id = "ands_p_groupname"/>
			    <@s.hidden name="selectedPartyBean.fromRm" id = "ands_p_fromrm"/>
            </div>
            <div style="clear: both;"></div>
        </div>
    </div>
    <div style="clear:both"></div>
    <div class="popup_button_div">
        <input type="button" value=" Cancel " class="input_button_style" onclick="window.location = '${base}/data/showSearchParty.jspx?searchCnOrEmail=${searchCnOrEmail}';"/> &nbsp;&nbsp; <input
            type="submit" name="options" value=" Save " class="input_button_style" id="save_rm_party"/>
    </div>
</div>
</body>
</html>