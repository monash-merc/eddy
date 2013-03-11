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
                <@s.hidden name="collection.funded" id="funded"/>

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
                    <@s.if test="%{collection.funded == true}">
                        <div class="data_tern_div">
                            [ <a href="http://www.tern.org.au" target="_blank">TERN-Funded</a> ]
                        </div>
                    </@s.if>
                    <div class="data_action_link">
                        <a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}">View
                            details</a>
                    </div>
                    <div style="clear: both;"></div>
                </div>
                <div class="content_none_border_div">
                    <div class="content_title">Associated Researcher(s)</div>
                </div>
                <div class="content_none_border_div">
                    <div class="metada_reg_display_div">
                        <div class="metadata_spec">
                            Please select the associated researcher(s)
                        </div>
                        <div class="metadata_act_link">
                            <a href="${base}/data/showSearchParty.jspx" title="Adding the associated researcher" id="addtionalParty">Add Researcher</a>
                        </div>
                        <div style="clear: both;"></div>
                    </div>
                </div>

                <div class="data_display_div">
                    <table class="display_data_tab2" id="ands_party_div">
                        <tbody>
                            <@s.if test="%{partyList != null && partyList.size > 0}">
                                <@s.iterator status="ptState" value="partyList" id="party" >
                                <tr>
                                    <td width="50">
                                        <@s.checkbox name="partyList[${ptState.index}].selected"  cssClass="check_box" />
                                    </td>
                                    <td>
                                        <div>
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
                            </@s.if>
                            <@s.else>
                            <div class="placeholder_div">
                                The associated researcher(s) not found, please select an associated researcher
                            </div>
                            </@s.else>
                        <tbody>
                    </table>
                </div>

                <div class="content_none_border_div">
                    <div class="content_title">Related Activity</div>
                </div>
                <div class="data_display_div">
                    <div class="ozflux_activity_name">
                        The name of OzFlux activity
                    </div>
                    <div class="ozflux_activity_desc">
                        The description of OzFlux activity
                    </div>
                </div>
                <div class="content_none_border_div">
                    <div class="content_title">The collection Licence</div>
                </div>
                <div class="content_none_border_div">
                    <@s.if test="%{collection.funded == false }">
                        <div class="metada_reg_display_div">
                            <div class="metadata_spec">
                                Please select the collection Licence
                            </div>
                            <div class="metadata_act_link">
                                <a href="${base}/data/licenceOptions.jspx?collection.id=<@s.property value='collection.id' />" title="Select Licence" id="selectLicence">Select Licence</a> &nbsp;
                            </div>
                            <div style="clear: both;"></div>
                        </div>
                    </@s.if>
                </div>
                <div class="data_display_div">
                    <@s.if test="%{licence == null}">
                        <div class="placeholder_div">
                            The licence not found, please selected a licence
                        </div>
                    </@s.if>
                </div>

                <div class="content_none_border_div">
                    <div class="content_title">Terms and Conditions</div>
                </div>

                <div class="data_display_div">
                    <div class="publish_term_conditions">
                        <p>
                            You are about to publish or register the above research work outside Monash University to be available to the
                            general public via Internet sites that can harvest this information. Sites include but are not limited to:
                            Research Data Australia and search engines.
                        </p>

                        <p>
                            Before you proceed, please ensure you have selected a license to associate with your research data and
                            work.
                        </p>

                        <p>
                            By using this system to publish or register your research work you are continuing to agree to adhere to the
                            Terms and Conditions of use detailed at <a href="http://www.monash.edu/eresearch/about/ands-merc.html"
                                                                       target="_blank">http://www.monash.edu/eresearch/about/ands-merc.html</a>.
                            Please read these Terms and Conditions carefully before registering.
                        </p>
                    </div>
                </div>

                <div class="content_none_border_div">

                    <div class="metada_reg_display_div">
                        <div class="metadata_spec">
                            &nbsp;
                        </div>
                        <div class="metadata_act_link">
                            <@s.submit value="I accept. Register"  name="register" cssClass="input_button_simple" />
                        </div>
                        <div style="clear: both;"></div>
                    </div>
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
