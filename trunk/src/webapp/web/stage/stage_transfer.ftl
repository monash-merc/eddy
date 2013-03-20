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
<!-- Navigation Title -->
<#include "../template/action_title.ftl" />
<!-- End of Navigation Title -->

<div class="main_body_container">
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "../template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
                <div class="content_div">
                    <table class="table_col">
                        <tr>
                            <td>
                                <div class="name_title"><@s.property value="collection.name"/></div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div class="inline_span_justify">
                                <@s.property  value="collection.description" escape=false />
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                            <span class="inline_span2">
                                Created by <@s.property value="collection.owner.displayName" />, &nbsp;&nbsp;&nbsp;&nbsp;
                                Created date: <@s.date name="collection.createdTime" format="yyyy-MM-dd hh:mm" /> &nbsp;&nbsp;&nbsp;&nbsp;
                                Modified by <@s.property value="collection.modifiedByUser.displayName" />, &nbsp;&nbsp;&nbsp;&nbsp;
                                Modified date: <@s.date name="collection.modifiedTime" format="yyyy-MM-dd hh:mm" />
                            </span>
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <div class="inline_td_div">
                                    <a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}">
                                        &nbsp; View details &nbsp;</a>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td></td>
                        </tr>
                    </table>
                </div>
                <div class="blank_separator"></div>
            <@s.form id="stage_transfer"  action="stageTransfer.jspx" namespace="/data" method="post" >
                <div class="single_border_block">
                    <@s.hidden name="collection.id" id="col"/>
                    <@s.hidden name="collection.name" id="coname"/>
                    <@s.hidden name="collection.description" id="desc"/>
                    <@s.hidden name="collection.owner.displayName" id="codisplayname" />
                    <@s.hidden name="collection.owner.id" id="colowner" />
                    <@s.hidden name="collection.createdTime" />
                    <@s.hidden name="collection.modifiedTime" />
                    <@s.hidden name="collection.modifiedByUser.displayName" />
                    <@s.hidden name="viewType" id="viewtype"/>
                    <table align="center" width="100%">
                        <tr>
                            <td align="right" width="280"><@s.checkbox name="extractRequired" id="extract" /></td>
                            <td align="left"><span class="stage_pane_div">Extract metadata</span></td>
                        </tr>
                        <tr>
                            <td></td>
                            <td align="left">
                                <div class="grey_line_div">
                                    <@s.radio name="extractAllOrGlobal" list="extractChoices" id="all_or_global_meta" value="extractAllOrGlobal" />
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td align="right"><@s.checkbox name="ignoreExisted" id="extract"  /></td>
                            <td align="left"><span
                                    class="stage_pane_div">Ignore transferring if file already exists</span></td>
                        </tr>
                        <tr>
                            <td align="right"><@s.checkbox name="sendEmailRequired" id="extract"  /></td>
                            <td align="left"><span class="stage_pane_div">Send email notification after transferring finished</span>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2"></td>
                        </tr>
                        <tr>
                            <td colspan="2"><@s.submit value="Transfer" name="Transfer" cssClass="input_button_normal"/></td>
                        </tr>
                    </table>
                </div>
                <div class="left_middle_no_border_panel">
                    <table class="table_data">
                        <tr>
                            <td width="100">
                                <center>Select All <br/><@s.checkbox name="selectedAll" id="ds_select_all" /></center>
                            </td>
                            <td width="600">File Name</td>
                        </tr>
                        <@s.if test = "%{stageFiles != null && stageFiles.size() ==0}">
                            <tr>
                                <td colspan="2" align="center">There no file in the stage area.</td>
                            </tr>
                        </@s.if>
                        <@s.if test = "%{stageFiles != null && stageFiles.size() > 0}">
                            <@s.iterator status="sState" value="stageFiles" id="sfile" >
                                <tr>
                                    <td align="center"><@s.checkbox name="stageFiles[${sState.index}].selected" id="dataset_file" /></td>
                                    <td><@s.property value="#sfile.fileName" /> <@s.hidden name="stageFiles[${sState.index}].fileName"  /></td>
                                </tr>
                            </@s.iterator>
                        </@s.if>
                    </table>
                </div>
            </@s.form>
                <br/>
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


