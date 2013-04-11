<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.property value="pageTitle" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<!-- Navigation Title -->
<#include "../template/action_title.ftl" />
<!-- End of Navigation Title -->
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
                    <div class="data_action_link">
                        <a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}">
                            View details
                        </a>
                    </div>
                    <div style="clear: both;"></div>
                </div>
                <div class="content_none_border_div">
                    <div class="content_title">Access Control Permissions</div>
                </div>

                <div class="content_div">
                    <div class="div_grey_section">There are three types of the accesss control permissions for a collection in the system:
                        <ul>
                            <li><b>All Anonymous Users Permissions</b> - Permissions which are granted to all users who are not logged in the system</li>
                            <li><b>All Registered Users Permissions</b> - Permissions which are granted to all registered users in the system</li>
                            <li><b>An Individual User Permissions</b> - Permissions which are granted to a registered user in the system</li>
                        </ul>
                        <p>Permissions can be granted to <b>All Registered Users</b> or<b> All Anonymous Users</b> or <b>An Individual User</b>.

                        <p>If the collection permissions are neither granted to <b>All Registered Users</b> nor <b>All Anonymous Users</b>, which means this collection is a private collection.</p>

                        <p>You can grant the specific permissions to an individual user in a collection, and the all allowed permissions for <b>All Anonymous Users</b> will be inherited by this
                            individual
                            user.</p>
                    </div>
                </div>

                <div class="content_none_border_div">
                    <div class="content_title">Granted Permissions</div>
                </div>

                <div class="none_border_block">
                    <div class="name_title">Grant permission to &nbsp;&nbsp;
                    <@s.select name="selected_username" headerKey="-1" headerValue="-- Select User --" list="activeUsers" value="-1" cssClass="input_select_normal"/> &nbsp;&nbsp;
                        <input type="button" name="add_permission" id="add_permission" value="Add" class="input_button_normal"/>
                    </div>
                </div>

            <@s.form action="setColPermissions.jspx" namespace="/perm" method="post">
                <@s.hidden name="collection.id" />
                <@s.hidden name="collection.owner.id" />
                <@s.hidden name="viewType" />

                <div class="none_border_block">
                    <@s.submit value="Save All" cssClass="input_button_normal" id="perm_form" />
                </div>

                <div class="content_none_border_div">
                    <table class="display_data_tab" id="user_permissions">
                        <thead>
                        <tr>
                            <th width="30%">User Name</th>
                            <th width="10%">View</th>
                            <th width="10%">Export</th>
                            <th width="10%">Import</th>
                            <th width="10%">RA Control</th>
                            <th width="10%">Edit</th>
                            <th width="10%">Delete</th>
                            <th width="10%">Access Control</th>
                        </tr>
                        <tr>
                            <td>
                                Anonymous user group
                                <@s.hidden name="anonymousePerm.id"  />
                                <@s.hidden name="anonymousePerm.uid" />
                                <@s.hidden name="anonymousePerm.userName" />
                            </td>
                            <td>
                                <@s.hidden name="anonymousePerm.viewAllowed" />
                                <@s.checkbox name="displayViewAllowed"  cssClass="check_box" disabled ="true" checked="checked"/>
                            </td>
                            <td>
                                <@s.checkbox name="anonymousePerm.exportAllowed" cssClass="check_box" />
                            </td>
                            <td>
                                <@s.hidden name="anonymousePerm.importAllowed" />
                            </td>
                            <td>
                                <@s.hidden name="anonymousePerm.racAllowed" />
                            </td>
                            <td>
                                <@s.hidden name="anonymousePerm.updateAllowed" />
                            </td>
                            <td>
                                <@s.hidden name="anonymousePerm.deleteAllowed" />
                            </td>
                            <td>
                                <@s.hidden name="anonymousePerm.acAllowed" />
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Registered user group
                                <@s.hidden name="allRegUserPerm.id" />
                                <@s.hidden name="allRegUserPerm.uid" />
                                <@s.hidden name="allRegUserPerm.userName" />
                            </td>
                            <td>
                                <@s.hidden name="allRegUserPerm.viewAllowed" />
                                <@s.checkbox name="displayViewAllowed"  cssClass="check_box" disabled ="true" checked="checked"/>
                            </td>
                            <td>
                                <@s.checkbox name="allRegUserPerm.exportAllowed" cssClass="check_box" />
                            </td>
                            <td>
                                <@s.hidden name="allRegUserPerm.importAllowed" />
                            </td>
                            <td>
                                <@s.hidden name="allRegUserPerm.racAllowed" />
                            </td>
                            <td>
                                <@s.hidden name="allRegUserPerm.updateAllowed" />
                            </td>
                            <td>
                                <@s.hidden name="allRegUserPerm.deleteAllowed" />
                            </td>
                            <td>
                                <@s.hidden name="allRegUserPerm.acAllowed" />
                            </td>
                        </tr>
                        <tr>
                            <td colspan="8">&nbsp;</td>
                        </tr>
                        </thead>
                        <tbody>
                            <@s.iterator status="permStatus" value="regUserPerms" id="permBean" >
                            <tr>
                                <td>
                                    <@s.property  value="#permBean.userName" />
                                    <@s.hidden name="regUserPerms[%{#permStatus.index}].id"  value="%{#permBean.id}" />
                                    <@s.hidden name="regUserPerms[%{#permStatus.index}].uid" id ="user_id" value="%{#permBean.uid}" />
                                    <@s.hidden name="regUserPerms[%{#permStatus.index}].userName" value="%{#permBean.userName}" />
                                </td>
                                <td>
                                    <@s.hidden name="regUserPerms[%{#permStatus.index}].viewAllowed"/>
                                    <@s.checkbox name="displayViewAllowed" cssClass="check_box" disabled ="true" checked="checked"/>
                                </td>
                                <td>
                                    <@s.checkbox name="regUserPerms[%{#permStatus.index}].exportAllowed" cssClass="check_box"/>
                                </td>
                                <td>
                                    <@s.checkbox name="regUserPerms[%{#permStatus.index}].importAllowed" cssClass="check_box"/>
                                </td>
                                <td>
                                    <@s.checkbox name="regUserPerms[%{#permStatus.index}].racAllowed" cssClass="check_box"/>
                                </td>
                                <td>
                                    <@s.checkbox name="regUserPerms[%{#permStatus.index}].updateAllowed" cssClass="check_box"/>
                                </td>
                                <td>
                                    <@s.checkbox name="regUserPerms[%{#permStatus.index}].deleteAllowed" cssClass="check_box"/>
                                </td>
                                <td>
                                    <@s.checkbox name="regUserPerms[%{#permStatus.index}].acAllowed" cssClass="check_box"/>
                                </td>
                            </tr>
                            </@s.iterator>
                        </tbody>
                    </table>
                </div>
                <div style="clear:both"></div>
            </@s.form>
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


