<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.property value="pageTitle" /></title>
<#include "../template/jquery_header.ftl"/>
    <script type="text/javascript">
        function doAfterImport(success) {
            if (success) {
            <@s.if test = "%{navigationBar.secondNavLink != null}">
                setTimeout('window.location.href = "${base}/${navigationBar.secondNavLink}"', 3500);
            </@s.if>
            }
        }
    </script>
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

<@s.if test="%{collectionError == false }">
    <@s.if test="%{actionSuccessMsg != null}">
    <div class="content_none_border_div">
        <div class="none_border_block">
            <#include "../template/action_success_msg.ftl"/>
        </div>
    </div>
    </@s.if>
<!-- File importing message block -->
<div class="content_none_border_div">
    <div class="file_success_msg_div">
        <p id="success_msg">Some text</p>
    </div>

    <div class="file_error_msg_div">
        <div class="error_msg_item_div">
            <ul>
                <li><p id="error_msg">&nbsp;</p></li>
            </ul>
        </div>
    </div>
</div>
<!-- End of file importing message block -->

<div class="data_display_div">
    <div class="data_title">
        <@s.property value="collection.name"/>
    </div>
    <div class="data_desc_div">
        <@s.property  value="collection.description" escape=false />
    </div>
    <div class="data_other_info">
        <span class="span_inline1">
            Created by <@s.property value="collection.owner.displayName" />,
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
    <div class="input_field_row">
        <div class="status_field_name_div">Temporal Coverage:</div>
        <div class="status_field_value_div">
            <@s.date name="collection.dateFrom" format="yyyy-MM-dd" />
            &nbsp;-&nbsp;<@s.date name="collection.dateTo" format="yyyy-MM-dd" />
        </div>
    </div>

    <div class="input_field_row">
        <div class="status_field_name_div">Metadata Registered:</div>
        <div class="status_field_value_div"><@s.property value="collection.published" /></div>
    </div>


    <div class="data_action_link2">
        <@s.if test="%{permissionBean.viewAllowed == false }">
            <a href="${base}/perm/applyForPerms.jspx?collection.id=<@s.property value='collection.id' />">
                Apply For Permissions
            </a>
        </@s.if>
        <@s.if test="%{permissionBean.editAllowed == true}">
            <a href="${base}/${showColEditLink}?collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}">
                Edit
            </a>
        </@s.if>
        <@s.if test="%{permissionBean.deleteAllowed}">
            <div class="msg_content">All data will be removed from the repository permanently!<p>Are you sure to delet
                this collection?</p></div>
            <div id='confirm_dialog'>
                <div class='header'><span>Deleting Collection Confirm</span></div>
                <div class='message'></div>
                <div class='buttons'>
                    <div class='no simplemodal-close'>No</div>
                    <div class='yes'>Yes</div>
                </div>
            </div>
            <a href="${base}/${deleteColLink}?collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}"
               class="confirm">
                Delete
            </a>
        </@s.if>
        <@s.if test="%{permissionBean.changePermAllowed}">
            <a href="${base}/${permissionLink}?collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}">
                Permissions
            </a>
        </@s.if>
        <@s.if test="%{mdRegEnabled}">
            <@s.if test="%{collection.owner.id == user.id || user.userType == 1 || user.userType ==2}">
                <!-- modal window for register with ands -->
                <a href="${base}/${andsMdRegLink}?collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}"
                   id="wait_modal" name='wait_modal'
                   title="Public registration of the metadata associated with this collection with the Research Data Australia website">
                    <@s.text name="ands.md.registration.title" />
                </a>

                <div id='mask'></div>
                <div id='modal_window'>
                    Calling Metadata Registration Service, please wait ... <img src="${base}/images/wait_loader.gif"
                                                                                class="loading_image">
                </div>
            </@s.if>
        </@s.if>
    </div>
    <div style="clear: both;"></div>
</div>
<!-- import the file -->
    <@s.if test="%{permissionBean.importAllowed}">
    <div class="blank_separator"></div>
    <div class="content_none_border_div">
        <div class="content_title">File Import</div>
    </div>
    <!-- file uploading progress messages -->
    <div class="content_none_border_div">
        <div class="file_uploading_div">
            <div id="ajaxfileupload">
                <#assign dobefore = 'undefined' />
                <div id="fileuploadProgress">
                    <div id="uploadFilename">Initialising, please wait.....</div>
                    <div id="progress-bar">
                        <div id="progress-bgrd"></div>
                        <div id="progress-text"></div>
                    </div>
                    <br/>
                </div>
                <span id="message"></span>
            </div>
        </div>
    </div>
    <!-- upload file -->
    <div class="data_display_div">
        <div id="fileuploadForm">
            <@s.form id="ajaxFileUploadForm" onsubmit="return false" action="importFile.jspx" namespace="/data" method="post" enctype="multipart/form-data" >
                <@s.hidden name="collection.id" id="col"/>
                <@s.hidden name="collection.owner.id" id="colowner" />
                <@s.hidden name="viewType" id="viewtype"/>

                <div class="input_field_row">
                    <div class="input_field_title">
                        Please select a file:
                    </div>
                    <div class="input_field_value_section">
                        <@s.file name="upload" id="upload" cssClass="input_file" />
                    </div>
                </div>
                <div style="clear: both;"></div>

                <div class="input_field_row">
                    <div class="input_field_title">
                        Extract the Metadata:
                    </div>
                    <div class="input_field_value_section">
                        <@s.checkbox name="extractable"  id="extract" value="true" cssClass="check_box" />
                    </div>
                </div>
                <div style="clear: both;"></div>

                <div class="input_field_row">
                    <div class="input_field_title">
                        &nbsp;
                    </div>
                    <div class="input_field_value_section">
                        <@s.submit value="Import" id="fileUpload" name="fileUpload" onclick="merc.AjaxFileUpload.initialise(${dobefore}, doAfterImport);" cssClass="input_button_style"/>
                    </div>
                </div>
            </@s.form>
        </div>
    </div>
    </@s.if>
<!-- end of importing file -->
<div class="none_border_block">
        <span class="name_title">
             A total of <span class="span_number">
            <@s.if test = "%{datasets != null}"><@s.property value="datasets.size" /></@s.if>
            <@s.else>0</@s.else>
        </span> data file(s) in this collection
        </span>
</div>
    <@s.if test="%{datasets.size() > 0}">
    <div class="content_none_border_div">
        <table class="display_data_tab">
            <thead>
            <tr>
                <th width="30%">File Name</th>
                <th width="27%">Site Name</th>
                <th width="5%">Level</th>
                <th width="38%">&nbsp;</th>
            </tr>
            </thead>
            <tbody>
                <@s.iterator status="dsState" value="datasets" id="ds" >
                <tr class="tr_normal" onMouseOver="this.className='tr_highlight'"
                    onMouseOut="this.className='tr_normal'">
                    <td><@s.property value="#ds.name" /></td>
                    <td><@s.property value="#ds.siteName" /></td>
                    <td>
                        <@s.property value="#ds.netCDFLevel" />
                    </td>
                    <td>
                        <div class="data_action_link">
                            <@s.if test="%{permissionBean.viewAllowed}">
                                <@s.if test="%{#ds.extracted}">
                                    <a href="${base}/${viewDatasetLink}?dataset.id=<@s.property value='#ds.id' />&collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}"
                                       title="Dataset - ${ds.name}" id="viewdataset">View Metadata</a>
                                </@s.if>
                            </@s.if>

                            <@s.if test="%{permissionBean.exportAllowed}">
                                <a href="${base}/${downloadDatasetLink}?dataset.id=<@s.property value='#ds.id' />&collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}">
                                    Export</a>
                            </@s.if>

                            <@s.if test="%{permissionBean.deleteAllowed}">
                                <div id='confirm_dialog'>
                                    <div class="msg_content">The data will be removed from the repository permanently!
                                        <p>Are you sure to delet this dataset?</p></div>
                                    <div id='confirm'>
                                        <div class='header'><span>Deleting Dataset Confirm</span></div>
                                        <div class='message'></div>
                                        <div class='buttons'>
                                            <div class='no simplemodal-close'>No</div>
                                            <div class='yes'>Yes</div>
                                        </div>
                                    </div>
                                </div>
                                <a href="${base}/${deleteDatasetLink}?dataset.id=<@s.property value='#ds.id' />&collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}"
                                   class='confirm'>Delete</a>
                            </@s.if>

                        </div>
                    </td>
                </tr>
                </@s.iterator>
            </tbody>
        </table>
    </div>
    </@s.if>
</@s.if>
</div>
<div style="clear:both"></div>
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


