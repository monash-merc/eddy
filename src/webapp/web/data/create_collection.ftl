<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<#assign sj=JspTaglibs["/WEB-INF/struts-jquery-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="mycollection.nav.label.name" /> - <@s.text name="create.new.collection" /></title>
<#include "../template/jquery_header.ftl"/>
<#include "../template/googlemap_header.ftl"/>
    <script type="text/javascript">
        $("#global_coverage").live('click', function () {
            if ($(this).is(":checked")) {
                //call map control javascript
                mctEmptyMap('mct_control_spatialcvg');
                //reset checkbox to true
                $(this).attr("checked", true);
            }
        });
    </script>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><a href="${base}/data/listUserCollections.jspx"><@s.text name="mycollection.nav.label.name" /></a></div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><@s.text name="create.new.collection" /></div>
</div>
<div style="clear:both"></div>

<div class="main_body_container">
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "../template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
            <@s.form action="createCollection.jspx" namespace="/data" method="post">
                <div class="content_div">
                    <div class="blank_separator"></div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            <@s.text name="collection.name" />:
                        </div>
                        <div class="input_field_value_section">
                            <@s.textfield name="collection.name" />
                            <div class="comments">
                                <@s.text name="collection.name.hint" />
                            </div>
                        </div>
                    </div>
                    <div style="clear: both;"></div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            TERN Funded:
                        </div>
                        <div class="input_field_value_section">
                            <@s.checkbox name="collection.funded" cssClass="check_box" />
                            <div class="comments">
                                If it's a TERN funded project, please select the tick box
                            </div>
                        </div>
                    </div>
                    <div style="clear: both;"></div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            <@s.text name="collection.temporal.from" />:
                        </div>
                        <div class="input_field_value_section">
                            <@sj.datepicker name="collection.dateFrom" id="startdate" displayFormat="yy-mm-dd"  buttonImageOnly="true" />
                            <div class="comments">
                                <@s.text name="collection.start.date.hint" />
                            </div>
                        </div>
                    </div>
                    <div style="clear: both;"></div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            <@s.text name="collection.temporal.to" />:
                        </div>
                        <div class="input_field_value_section">
                            <@sj.datepicker name="collection.dateTo" id="enddate" displayFormat="yy-mm-dd"  buttonImageOnly="true" />
                            <div class="comments">
                                <@s.text name="collection.end.date.hint" />
                            </div>
                        </div>
                    </div>
                    <div style="clear: both;"></div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            <@s.text name="collection.desc" />:
                        </div>
                        <div class="input_field_value_section">
                            <@s.textarea  name="collection.description" cssStyle="width: 560px; height: 190px;" cssClass="input_textarea" />
                            <div class="comments">
                                <@s.text name="collection.desc.hint" />
                            </div>
                        </div>
                    </div>
                    <div style="clear: both;"></div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            Global Coverage:
                        </div>
                        <div class="input_field_value_section">
                            <@s.checkbox name="globalCoverage" id="global_coverage" cssClass="check_box" />
                            <div class="comments">
                                If it's a global coverage, please select the tick box
                            </div>
                        </div>
                    </div>
                    <div style="clear: both;"></div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            <@s.text name="collection.spatial.coverage"/>:
                        </div>
                        <div class="input_field_value_section">
                            <@s.textarea  id="spatialcvg" name="collection.location.spatialCoverage" cssStyle="width: 200px; height: 80px;" cssClass="input_textarea" readonly ="true" />
                            <div class="comments">
                                <@s.text name="collection.spatial.coverage.hint" />
                            </div>
                        </div>
                    </div>
                    <div style="clear: both;"></div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            &nbsp;
                        </div>
                        <div class="input_field_value_section">
                            <div class="comments">
                                Choose a method for marking spatial coverage from the options in the grey bar above the map.
                                <br/>
                                * (A region is not supported on the collection map view)
                            </div>
                            <script type="text/javascript">mctSetMapControl("spatialcvg");</script>
                        </div>
                    </div>
                    <div style="clear: both;"></div>
                    <div class="blank_separator"></div>
                    <div class="blank_separator"></div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            &nbsp;
                        </div>
                        <div class="input_field_value_section">
                            <@s.submit value="%{getText('data.create.button')}" cssClass="input_button_style" /> &nbsp; <@s.reset value="%{getText('reset.button')}" cssClass="input_button_style" />
                        </div>
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