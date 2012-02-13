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

	 	<div class="mcpop_field">
		    <br/>

            <@s.if test="%{partyNotFound == false}">
			<div class="mcpop_input_value">
				<span class="inline_span2">
					Found the researcher(s) information as bellow:
				</span>
			</div>
			<div style="clear:both"></div>
		 	<div class="blank_separator"></div>

            <div class="mcpop_input_value">
                <div class="name_comment">* Please select one of the following researcher(s)</div>
            </div>

            <div class="none_border_block2">
				<table  class="mcpop_tab" width="450">
					<@s.iterator status="ptState" value="foundPartyBeans" id="fpb" >
                    <tr>
                        <td colspan="4" class="tdbgcolor"></td>
                    </tr>
					<tr>
						<td colspan="2" align="center" rowspan="7">
							<@s.checkbox name="foundPartyBeans[${ptState.index}].selected" />
						</td>
                    </tr>
                    <tr>
                        <td width="90">Name:</td>
                        <td><@s.property value="#fpb.personTitle" /> <@s.property value="#fpb.personGivenName" /> <@s.property value="#fpb.personFamilyName" /></td>
                    </tr>

                    <tr>
                        <td>E-mail:</td>
                        <td><@s.property value="#fpb.email" /></td>
                    </tr>
                    <tr>
                        <td>Address:</td>
                        <td><@s.property value="#fpb.address" /></td>
                    </tr>
                    <tr>
                        <td>Web URL:</td>
                        <td><@s.property value="#fpb.url" /></td>
                    </tr>
                    <tr>
                        <td>Group Name:</td>
                        <td><@s.property value="#fpb.groupName" /></td>
                    </tr>
                    <tr>
                        <td>Group Web Site:</td>
                        <td>
                            <@s.property value="#fpb.originateSourceValue" />
						</td>
					</tr>
                    <tr>
                        <td colspan="4" class="tdbgcolor">
                            <@s.hidden name="foundPartyBeans[${ptState.index}].partyKey" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].personTitle" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].personGivenName" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].personFamilyName" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].email" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].address" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].url" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].identifierType"  />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].identifierValue" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].originateSourceType" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].originateSourceValue" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].groupName" />
                            <@s.hidden name="foundPartyBeans[${ptState.index}].fromRm" />
                        </td>
                    </tr>
					</@s.iterator>
				</table>
                <div class="blank_separator"></div>
			</div>
            <div class="mcpop_bddiv">
		        <input type="submit" value=" Next " class="mcpop_button" />
		    </div>
            </@s.if>
            <@s.else>
             <div class="none_border_block2">
                Display manually input party information
             </div>
            </@s.else>
		</div>
    </div>

</body>
</html>