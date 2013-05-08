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
    <div class="content_title">Permission Specifications</div>
</div>

<div class="content_div">
    <div class="permission_spec">There are three types of the accesss control permissions for a collection in the system:
        <table class="display_data_tab">
            <tr>
                <td><span class="span_spec_title">Anonymous Users Group Permissions</span></td>
                <td>The permissions for all anonymous users who are not logged in the system</td>
            </tr>
            <tr>
                <td><span class="span_spec_title">All Registered Users Group Permissions</span></td>
                <td>The permissions for all registered users who are logged in the system</td>
            </tr>
            <tr>
                <td><span class="span_spec_title">Individual User Permissions</span></td>
                <td>The permissions for an individual registered user who is logged in the system</td>
            </tr>
        </table>

        <p>Permissions can be granted to <span class="span_spec_title">Anonymous Users Group</span> or <span class="span_spec_title">All Registered Users Group</span> or <span class="span_spec_title">An Individual User</span>.

        <div>
            <table class="display_data_tab">
                <tr>
                    <th width="15%">Permission</th>
                    <th>Specification</th>
                    <th>Notes</th>
                </tr>
                <tr>
                    <td>View</td>
                    <td>View the details of a collection, including the metadata of a dataset file</td>
                    <td>Anybody</td>
                </tr>
                <tr>
                    <td>Export</td>
                    <td>Export any files from a collection</td>
                    <td>An user who has an 'Export' permission of this collection</td>
                </tr>
                <tr>
                    <td>Import</td>
                    <td>Import any files into a collection</td>
                    <td>An user who has an 'Import' permission of this collection</td>
                </tr>
                <tr>
                    <td>RA Control</td>
                    <td>Setup a restricted access for a file in a collection</td>
                    <td>An user who has a 'RA Control' permission of this collection</td>
                </tr>
                <tr>
                    <td>Edit</td>
                    <td>Update a collection</td>
                    <td>An user who has an 'Edit' permission of this collection</td>
                </tr>
                <tr>
                    <td>Delete</td>
                    <td>Delete a collection and its dataset files</td>
                    <td>An user who has a 'Delete' permission of this collection</td>
                </tr>
                <tr>
                    <td>Access Control</td>
                    <td>Setup the permissions for a collection</td>
                    <td>An user who has a 'Access Control' permission of this collection</td>
                </tr>
            </table>
        </div>
        <div class="blank_separator"></div>
    </div>
</div>

<div class="content_none_border_div">
    <div class="content_title">Permission Settings</div>
</div>

<div class="none_border_block">
    <div class="name_title">Grant permission to &nbsp;&nbsp;
    <@s.select name="selected_username" headerKey="-1" headerValue="-- Select User --" list="activeUsers" value="-1" cssClass="input_select_normal"/> &nbsp;&nbsp;
        <input type="button" name="add_permission" id="add_permission" value="Add" class="input_button_normal"/>
    </div>
</div>
<div class="blank_separator"></div>
<@s.form action="setColPermissions.jspx" namespace="/perm" method="post" id="cpermissions">
    <@s.hidden name="collection.id" />
    <@s.hidden name="collection.owner.id" />
    <@s.hidden name="viewType" />


<div class="content_none_border_div">
    <table class="display_data_tab" id="user_permissions">
        <thead>
        <tr>
            <th width="22%">Group | User</th>
            <th width="10%">View</th>
            <th width="10%">Export</th>
            <th width="10%">Import</th>
            <th width="10%">RA Control</th>
            <th width="10%">Edit</th>
            <th width="10%">Delete</th>
            <th width="13%">Access Control</th>
            <th width="5%">&nbsp;</th>
        </tr>
        <tr>
            <td>
                Anonymous Users Group
                <@s.hidden name="anonymousePerm.id"  />
                <@s.hidden name="anonymousePerm.uid" />
                <@s.hidden name="anonymousePerm.userName" />
            </td>
            <td>
                <@s.checkbox name="anonymousePerm.viewAllowed" cssClass="check_box" />
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
            <td>
                &nbsp;
            </td>
        </tr>
        <tr>
            <td>
                All Registered Users Group
                <@s.hidden name="allRegUserPerm.id" />
                <@s.hidden name="allRegUserPerm.uid" />
                <@s.hidden name="allRegUserPerm.userName" />
            </td>
            <td>
                <@s.checkbox name="allRegUserPerm.viewAllowed" cssClass="check_box" />
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
            <td>
                &nbsp;
            </td>
        </tr>
        <tr>
            <td colspan="9" style="background:#F2F2F2;"><span class="span_spec_title">Individual User Permissions</span></td>
        </tr>
        </thead>
        <tbody>
            <@s.iterator status="permStatus" value="regUserPerms" id="permBean" >
            <tr>
                <td>
                    <@s.property  value="#permBean.userName" />
                    <@s.hidden name="regUserPerms[%{#permStatus.index}].id"  id = "regUserPerms_id" value="%{#permBean.id}" />
                    <@s.hidden name="regUserPerms[%{#permStatus.index}].uid" id ="user_id" value="%{#permBean.uid}" />
                    <@s.hidden name="regUserPerms[%{#permStatus.index}].userName" id="regUserPerms_userName" value="%{#permBean.userName}" />
                </td>
                <td>
                    <@s.checkbox name="regUserPerms[%{#permStatus.index}].viewAllowed" id="regUserPerms_viewAllowed" cssClass="check_box"/>
                </td>
                <td>
                    <@s.checkbox name="regUserPerms[%{#permStatus.index}].exportAllowed" id="regUserPerms_exportAllowed" cssClass="check_box"/>
                </td>
                <td>
                    <@s.checkbox name="regUserPerms[%{#permStatus.index}].importAllowed" id="regUserPerms_importAllowed" cssClass="check_box"/>
                </td>
                <td>
                    <@s.checkbox name="regUserPerms[%{#permStatus.index}].racAllowed" id="regUserPerms_racAllowed" cssClass="check_box"/>
                </td>
                <td>
                    <@s.checkbox name="regUserPerms[%{#permStatus.index}].updateAllowed" id="regUserPerms_updateAllowed"  cssClass="check_box"/>
                </td>
                <td>
                    <@s.checkbox name="regUserPerms[%{#permStatus.index}].deleteAllowed" id="regUserPerms_deleteAllowed" cssClass="check_box"/>
                </td>
                <td>
                    <@s.checkbox name="regUserPerms[%{#permStatus.index}].acAllowed" id="regUserPerms_acAllowed" cssClass="check_box"/>
                </td>
                <td align="center">
                    <div class="remove_user_perm" title="remove this user permissions">&nbsp;</div>
                </td>
            </tr>
            </@s.iterator>
        </tbody>
    </table>
    <div class="blank_separator"></div>
    <div class="none_border_block">
        <@s.submit value="Save All" cssClass="input_button_style"  id="perm_form" />
    </div>
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


