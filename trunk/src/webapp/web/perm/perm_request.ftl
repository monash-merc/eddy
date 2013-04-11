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
                <div class="data_display_div">
                    <div class="data_title">
                    <@s.property value="collection.name"/>
                    </div>

                    <div class="data_desc_div">
                    <@s.property  value="collection.briefDesc" escape=false />
                    </div>
                    <div class="data_other_info">
                        <span class="span_inline1">
                            Created by <@s.property  value="collection.owner.displayName"  />,
                        </span>
                        <span class="span_inline1">
                            Creation date: <@s.date name="collection.createdTime" format="yyyy-MM-dd hh:mm" />,
                        </span>
                       <span class="span_inline1">
                            Modified by <@s.property value="collection.modifiedByUser.displayName" />,
                        </span>
                        <span class="span_inline1">
                            Modified date: <@s.date name="collection.modifiedTime" format="yyyy-MM-dd hh:mm" />
                        </span>
                    </div>
                    <div style="clear: both;"></div>
                </div>
                <div class="content_none_border_div">
                    <div class="content_title">Please select the following permissions</div>
                </div>
                <div class="content_none_border_div">
                <@s.form action="sendPermsReq.jspx" namespace="/perm" method="post" >
                    <@s.hidden name="collection.id" />
                    <@s.hidden name="collection.name"/>
                    <@s.hidden name="collection.briefDesc" />
                    <@s.hidden name="collection.owner.id" />
                    <@s.hidden name="collection.owner.displayName"  />
                    <@s.hidden name="collection.createdTime" />
                    <@s.hidden name=="collection.modifiedByUser.displayName" />
                    <@s.hidden name="collection.modifiedTime" />
                    <@s.hidden name="permReq.id" />
                    <table class="display_data_tab" id="permission_req">

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
                                <@s.checkbox name="permReq.viewAllowed" cssClass="check_box" />
                            </td>
                            <td>
                                <@s.checkbox name="permReq.updateAllowed" cssClass="check_box" />
                            </td>
                            <td>
                                <@s.checkbox name="permReq.importAllowed" cssClass="check_box" />
                            </td>
                            <td>
                                <@s.checkbox name="permReq.exportAllowed" cssClass="check_box" />
                            </td>
                            <td>
                                <@s.checkbox name="permReq.deleteAllowed" cssClass="check_box" />
                            </td>
                            <td>
                                <@s.checkbox name="permReq.acAllowed" cssClass="check_box" />
                            </td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="none_border_block">
                        <@s.submit value="Apply" cssClass="input_button_normal" />
                    </div>
                </@s.form>
                </div>
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