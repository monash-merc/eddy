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
                <div class="hints_panel">
                    <img src="${base}/images/warn.png" /> &nbsp; All fields marked with * are mandatory
                </div>
                <div class="content_div">
                    <div class="blank_separator"></div>

                    <div class="input_field_row">
                        <div class="input_field_title">
                            * <@s.text name="collection.name" />:
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
                            <@s.checkbox name="collection.funded" cssClass="check_box" id="tern_funded" />
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
                                <@s.text name="collection.start.date.hint" />, Format: yyyy-mm-dd
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
                                <@s.text name="collection.end.date.hint" />, Format: yyyy-mm-dd
                            </div>
                        </div>
                    </div>
                    <div style="clear: both;"></div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            * <@s.text name="collection.desc" />:
                        </div>
                        <div class="input_field_value_section">
                            <@s.textarea  name="collection.description" cssStyle="width: 560px; height: 190px;" cssClass="input_textarea" />
                            <div class="comments">
                                <@s.text name="collection.desc.hint" />
                            </div>
                        </div>
                    </div>
                </div>
                <div class="content_div">
                    <div class="input_field_row">
                        <div class="input_field_title">
                           * Data Licence:
                        </div>
                        <div class="input_field_value_section">
                            <div class="licence_options">
                                <div class="licence_option_row" id="tern_option">
                                    <@s.hidden name="existed_licenct_type" value="${licence.licenceType}" id="existed_licenct_type"/>
                                    <@s.if test="%{licence.licenceType == 'tern'}">
                                        <input type="radio" name="licence.licenceType" value="tern" checked="checked" class="radio_box">TERN Licence (Recommended)
                                    </@s.if>
                                    <@s.else>
                                        <input type="radio" name="licence.licenceType" value="tern" class="radio_box">TERN Licence (Recommended)
                                    </@s.else>
                                </div>
                                <div class="licence_option_row" id="user_defined_option">
                                    <@s.if test="%{licence.licenceType == 'userdefined'}">
                                        <input type="radio" name="licence.licenceType" value="userdefined" checked="checked" class="radio_box">Define Your Own Licence
                                    </@s.if>
                                    <@s.else>
                                        <input type="radio" name="licence.licenceType" value="userdefined" class="radio_box">Define Your Own Licence
                                    </@s.else>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div style="clear: both;"></div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            &nbsp;
                        </div>
                        <div class="input_field_value_section">
                            <div class="tern_licence_div">
                                <div class="tern_licence">
                                    <div class="licence_logo"><a href="http://www.tern.org.au/datalicence/TERN-BY-SA-NC/1.0" target="_blank">http://www.tern.org.au/datalicence/TERN-BY-SA-NC/1.0</a>
                                        &nbsp;&nbsp;&nbsp;&nbsp;
                                        <img src="${base}/images/tern_by_nc_sa.png"/></div>
                                    <p>
                                        The TERN Attribution-Share Alike- Non Commercial (TERN BY-SA-NC) Data Licence v1.0 restricts the development of new data/products, so that a user can:
                                    </p>
                                    <ul>
                                        <li>copy, re-use, share and distribute the copies of the data</li>
                                        <li>not use the data for commercial purposes</li>
                                    </ul>
                                    <p>
                                        provided that, whenever the data is copied, re-used, or distributed the user ensures that:
                                    </p>
                                    <ul>
                                        <li>credit is given to the original sources/s of the data (and any other nominated parties) in the manner stipulated (Attribution);</li>
                                        <li>the data cannot be used for commercial purposes (No Commercial); and</li>
                                        <li>If the data is altered, transformed, the resulting datasets can only be used under the same license conditions.</li>
                                    </ul>
                                </div>
                            </div>

                            <div class="licence_contents_div">
                                <@s.textarea name="licence.contents" cssClass="input_textarea" cssStyle="width: 560px; height: 190px;"  id="licence_contents" />
                                <div class="comments">
                                   User-defined data licence. <@s.text name="licence.add.user.defined.licence.hint" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>


                <div class="content_div">
                    <div style="clear: both;"></div>
                    <div class="input_field_row" style="display: none;">
                        <div class="input_field_title">
                            <@s.text name="collection.spatial.coverage"/>:
                        </div>
                        <div class="input_field_value_section">
                            <@s.checkbox name="globalCoverage" id="global_coverage" cssClass="check_box" /> Global Coverage
                            <div class="comments">
                                If it's a global coverage, please select the tick box
                            </div>
                            <div style="display: none;">
                                <@s.textarea  id="spatialcvg" name="collection.location.spatialCoverage" cssStyle="width: 200px; height: 80px;" cssClass="input_textarea" readonly ="true" />
                            </div>
                        </div>
                    </div>
                    <div style="clear: both;"></div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            <@s.text name="collection.spatial.coverage"/>:
                        </div>
                        <div class="input_field_value_section">
                            <div class="comments">
                                <b>Spatial coverage can be chosen in two ways</b>:
                                <ul id="comment_num_ul">
                                    <li>
                                        Using the <b>point</b> button
                                        <ul>
                                            <li>Use the map zoom and pan controls to zoom in to the area of the site.</li>
                                            <li>Click on the <b>point</b> button.</li>
                                            <li>Position the cross-hair cursor over the site location and click the left mouse button.</li>
                                        </ul>
                                    </li>
                                    <li>
                                        Using the <b>coordinates</b> button
                                        <ul>
                                            <li>Click on the <b>coordinates</b> button.</li>
                                            <li>Enter the coordinates of the site into the pop-up box as longitude and latitude separated by a comma
                                                eg  148.15202,-35.65644 (Note: the coordinates must be in decimal degrees)</li>
                                            <li>Click on the <b>set</b> button.</li>
                                        </ul>
                                    </li>
                                </ul>
                                <b>* (A region is not supported on the collection map view)</b>
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