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
                <div class="content_none_border_div">
                    <div class="content_title">
                        Public registration of the following metadata associated with this collection with the Research Data Australia
                        website
                    </div>
                </div>
            <@s.form action="preRegMd.jspx" namespace="/data" method="post" id="mdRegForm">
                <@s.hidden name="collection.id" id="col"/>
                <@s.hidden name="collection.name" id="coname"/>
                <@s.hidden name="collection.description" id="desc"/>
                <@s.hidden name="collection.owner.displayName" id="codisplayname" />
                <@s.hidden name="collection.owner.id" id="colowner" />
                <@s.hidden name="collection.createdTime" />
                <@s.hidden name="collection.modifiedTime" />
                <@s.hidden name="collection.modifiedByUser.displayName" />
                <@s.hidden name="viewType" id="viewtype"/>

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

                    <div class="data_action_link">
                        <a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}">View
                            details</a>
                    </div>
                    <div style="clear: both;"></div>
                </div>

            <div class="content_none_border_div">
                <table class="table_nobd">
                    <tr>
                        <td width="300">
                            <div class="grey_div_block">Please select the associated researcher(s)</div>
                        </td>
                        <td align="left">
                            <div class="links_div">
                                <a href="${base}/data/showSearchParty.jspx" title="Adding another researcher" id="addtionalParty">Add
                                    Researcher</a> &nbsp;
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="none_border_block2">
                <table class="ands_reg_tab_data" id="ands_reg_tab_data">
                    <@s.iterator status="ptState" value="partyList" id="party" >
                        <tr>
                            <td align="center" width="50">
                                <@s.checkbox name="partyList[${ptState.index}].selected" />
                            </td>
                            <td>
                                <div class="ands_blue">
                                    <@s.property value="#party.personTitle" /> <@s.property value="#party.personGivenName" /> <@s.property value="#party.personFamilyName" />
                                    ( <@s.property value="#party.groupName" /> - <@s.property value="#party.email" /> )
                                    <@s.hidden name="partyList[${ptState.index}].partyKey" />
                                    <@s.hidden name="partyList[${ptState.index}].personTitle" />
                                    <@s.hidden name="partyList[${ptState.index}].personGivenName" />
                                    <@s.hidden name="partyList[${ptState.index}].personFamilyName" />
                                    <@s.hidden name="partyList[${ptState.index}].email" />
                                    <@s.hidden name="partyList[${ptState.index}].address" />
                                    <@s.hidden name="partyList[${ptState.index}].url" />
                                    <@s.hidden name="partyList[${ptState.index}].identifierType"  />
                                    <@s.hidden name="partyList[${ptState.index}].identifierValue" />
                                    <@s.hidden name="partyList[${ptState.index}].originateSourceType" />
                                    <@s.hidden name="partyList[${ptState.index}].originateSourceValue" />
                                    <@s.hidden name="partyList[${ptState.index}].groupName" />
                                    <@s.hidden name="partyList[${ptState.index}].fromRm" />
                                </div>
                            </td>
                        </tr>
                    </@s.iterator>
                </table>
            </div>


                <@s.if test="%{partyList == null || partyList.size == 0}">
                <div class="border_div_block" id="party_not_found">
                    <div class="yellow_div_block">
                        The associated researcher(s) not found, please select an associated researcher
                    </div>
                    <div style="clear:both"></div>
                </div>
                </@s.if>

            <div class="none_border_block2">
                <table class="table_nobd">
                    <tr>
                        <td width="300">
                            <div class="grey_div_block">Please select the associated grant(s) or project(s)</div>
                        </td>
                        <td align="left">
                        </td>
                    </tr>
                </table>
            </div>

            <div class="none_border_block2">
                <table class="ands_reg_tab_data">
                    <@s.iterator status="pState" value="projectList" id="proj" >
                        <tr>
                            <td align="center" width="50">
                                <@s.checkbox name="projectList[${pState.index}].selected" />
                                        <@s.hidden name="projectList[${pState.index}].activityKey" />
                            </td>
                            <td>
                                <div class="ands_blue">
                                    <@s.property value="#proj.title" />
                                            <@s.hidden name="projectList[${pState.index}].title"  />
                                </div>
                                <div class="ands_gray">
                                    Grant Code: <@s.property value="#proj.grantCode" />
                                    <@s.hidden name="projectList[${pState.index}].grantCode"  />
                                </div>
                                <div class="ands_green">
                                    Project Date Applied: <@s.property value="#proj.appliedDate" />
                                    <@s.hidden name="projectList[${pState.index}].appliedDate"  />
                                </div>
                            </td>
                        </tr>
                    </@s.iterator>
                </table>
            </div>

                <@s.if test="%{projectList == null || projectList.size == 0}">
                <div class="border_div_block">
                    <div class="yellow_div_block">
                        The associated grant(s) or project(s) not found
                    </div>
                    <div style="clear:both"></div>
                </div>
                </@s.if>
            <div class="blank_separator"></div>
            <div class="none_border_block2">
                <table class="table_nobd">
                    <tr>
                        <td width="300">
                            <div class="grey_div_block">Please select the collection License</div>
                        </td>
                        <td align="left">
                            <div class="links_div">
                                <a href="${base}/data/rightsOptions.jspx?collection.id=<@s.property value='collection.id' />"
                                   title="Select License" id="selectRights">Select License</a> &nbsp;
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <@s.hidden name="rights.id" id="frights_id"/>
                                        <@s.hidden name="rights.rightsType" id="frights_type"/>
                                        <@s.hidden name="rights.commercial" id="frights_comm"/>
                                        <@s.hidden name="rights.derivatives" id="frights_deri"/>
                                        <@s.hidden name="rights.jurisdiction" id="frights_juri"/>
                                        <@s.hidden name="rights.rightContents" id="frights_cont" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <div class="mcpop_justify">
                                <div id="display_rights">
                                    <@s.if test="%{rights.rightContents == null}">
                                        &nbsp;
                                    </@s.if>
                                    <@s.property value="rights.rightContents" />
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="none_border_block2">
                <table class="table_nobd">
                    <tr>
                        <td width="300">
                            <div class="grey_div_block">Access rights</div>
                        </td>
                        <td align="left">
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <@s.hidden name="accessRights" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <div class="mcpop_justify">
                                <div>
                                    <@s.property value="accessRights" />
                                </div>
                            </div>
                            <br/>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="single_border_block">
                <p><b>Terms and Conditions</b></p>

                <div class="rep_license_div">
                    <p>You are about to publish or register the above research work outside Monash University to be available to the
                        general public via Internet sites that can harvest this information. Sites include but are not limited to:
                        Research Data Australia and search engines.</p>

                    <p>Before you proceed, please ensure you have selected a license to associate with your research data and
                        work.</p>

                    <p>By using this system to publish or register your research work you are continuing to agree to adhere to the
                        Terms and Conditions of use detailed at <a href="http://www.monash.edu/eresearch/about/ands-merc.html"
                                                                   target="_blank">http://www.monash.edu/eresearch/about/ands-merc.html</a>.
                        Please read these Terms and Conditions carefully before registering.</p>
                </div>
            </div>
            <br/>

            <div class="none_border_block2">
                <@s.submit value="I accept. Preview"  name="preview" cssClass="silver_b_input" id="reg_preview"/>
            </div>

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


