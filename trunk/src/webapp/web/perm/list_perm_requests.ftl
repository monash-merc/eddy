<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.property value="pageTitle" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<#include "../template/action_title.ftl" />
<div class="main_body_container">
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "../template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
            <@s.if test="%{actionSuccessMsg != null}">
                <div class="content_none_border_div">
                    <div class="none_border_block">
                        <#include "../template/action_success_msg.ftl"/>
                    </div>
                </div>
            </@s.if>
            <@s.if test="%{permRequests.size() > 0}">
                <@s.iterator status="permReqStat" value="permRequests" id="pRequest" >
                    <div class="data_display_div">
                        <div class="content_none_border_div">
                            <div class="data_title">
                                <@s.property value="#pRequest.collection.name" />
                            </div>

                            <div class="data_desc_div">
                                <@s.property value="#pRequest.collection.briefDesc" />
                            </div>
                            <div style="clear: both;"></div>
                        </div>

                        <div class="content_title">User Details</div>

                        <div class="input_field_row">
                            <div class="input_field_title">
                                User name:
                            </div>
                            <div class="input_field_value_section">
                                <@s.property value="#pRequest.requestUser.displayName" />
                            </div>
                        </div>
                        <div style="clear: both;"></div>
                        <div class="input_field_row">
                            <div class="input_field_title">
                                Email:
                            </div>
                            <div class="input_field_value_section">
                                <@s.property value="#pRequest.requestUser.email" />
                            </div>
                        </div>
                        <div style="clear: both;"></div>
                        <div class="input_field_row">
                            <div class="input_field_title">
                                Organization:
                            </div>
                            <div class="input_field_value_section">
                                <@s.property value="#pRequest.requestUser.profile.organization" />
                            </div>
                        </div>
                        <div style="clear: both;"></div>

                        <div class="blank_separator"></div>

                        <div class="content_title">Requested Perimssions:</div>
                        <div class="content_none_border_div">
                            <@s.form action="approvePermReq.jspx" namespace="/perm" method="post" >
                                <@s.hidden name="permRequest.id" value="%{#pRequest.id}" />
                                <@s.hidden name="permRequest.collection.name" value="%{#pRequest.collection.name}" />
                                <table class="display_data_tab2">
                                    <thead>
                                    <tr>
                                        <th width="15%">View</th>
                                        <th width="15%">Edit</th>
                                        <th width="15%">Import</th>
                                        <th width="15%">Export</th>
                                        <th width="15%">Delete</th>
                                        <th width="25%">Access Control</th>
                                    </tr>
                                    </thead>
                                    <tbody
                                    <tr>
                                        <td>
                                            <@s.checkbox name="permRequest.viewAllowed"  value="%{#pRequest.viewAllowed}" cssClass="check_box" />
                                        </td>
                                        <td>
                                            <@s.checkbox name="permRequest.updateAllowed" value="%{#pRequest.updateAllowed}" cssClass="check_box" />
                                        </td>
                                        <td>
                                            <@s.checkbox name="permRequest.importAllowed" value="%{#pRequest.importAllowed}" cssClass="check_box" />
                                        </td>
                                        <td>
                                            <@s.checkbox name="permRequest.exportAllowed" value="%{#pRequest.exportAllowed}" cssClass="check_box" />
                                        </td>
                                        <td>
                                            <@s.checkbox name="permRequest.deleteAllowed"  value="%{#pRequest.deleteAllowed}" cssClass="check_box" />
                                        </td>
                                        <td>
                                            <@s.checkbox name="permRequest.acAllowed" value="%{#pRequest.acAllowed}" cssClass="check_box"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="6"></td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td align="right"><@s.submit value="Grant" cssClass="input_button_sm" /></td>
                                        <td>
                                            <div class="tab_link">
                                                <a href="${base}/perm/rejectPermReq.jspx?permRequest.id=<@s.property value='#pRequest.id' />&permRequest.collection.name=${pRequest.collection.name}">Reject</a>
                                            </div>
                                        </td>
                                        <td></td>
                                        <td></td>
                                        <td></td>
                                    </tr>
                                    </tbody>
                                </table>
                            </@s.form>
                        </div>
                    </div>
                </@s.iterator>
            </@s.if>
            <@s.else>
                <div class="placeholder_div">
                    You have <span class="span_number"> 0 </span> Permissions Request
                </div>
            </@s.else>
            </div>
        </div>
        <!-- right panel -->
        <div class="right_display_div">
        <@s.if test="%{#session.authentication_flag =='authenticated'}">
            <#include "../template/sub_nav.ftl" />
        </@s.if>
        </div>
    </div>
    <div style="clear:both"></div>
</div>
<#include "../template/footer.ftl"/>
</body>
</html>