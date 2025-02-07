<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="user.display.home.action.title" /> - <@s.text name="user.profile.image.action.title" /></title>
<#include "../template/jquery_header.ftl"/>
    <script language="Javascript">
        jQuery(window).load(function () {
            jQuery('#cropbox').Jcrop({
                onChange:showPreview,
                onSelect:showPreview,
                aspectRatio:1,
                bgOpacity:.8,
                setSelect:[0, 0, 50, 50],
                minSize:[50, 50],
            });

        });
        var imageWidth = <@s.property value='imgWidth' />;
        var imageHeight = <@s.property value='imgHeight' />;

        // Our simple event handler, called from onChange and onSelect
        // event handlers, as per the Jcrop invocation above
        function showPreview(coords) {
            if (parseInt(coords.w) > 0) {
                var rx = 48 / coords.w;
                var ry = 48 / coords.h;

                jQuery('#preview').css({
                    width:Math.round(rx * imageWidth) + 'px',
                    height:Math.round(ry * imageHeight) + 'px',
                    marginLeft:'-' + Math.round(rx * coords.x) + 'px',
                    marginTop:'-' + Math.round(ry * coords.y) + 'px'
                });
                //set the coordinates.
                jQuery('#imageX1').val(coords.x);
                jQuery('#imageY1').val(coords.y);
                jQuery('#imageX2').val(coords.x2);
                jQuery('#imageY2').val(coords.y2);
                jQuery('#imageW').val(coords.w);
                jQuery('#imageH').val(coords.h);
            }
        }

    </script>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><a href="${base}/admin/displayUserHome.jspx"><@s.text name="user.display.home.action.title" /></a></div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><@s.text name="user.profile.image.action.title" /></div>
</div>
<div style="clear:both"></div>
<div class="main_body_container">
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "../template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
                <div class="content_div">
                    <table width="100%">
                        <tr>
                            <td width="60%">
                                <div class="uploaded_image">
                                    <img src="${base}/<@s.property value='userImageName' />" id="cropbox"/>
                                </div>
                            </td>
                            <td width="40%">
                                <div class="crop_image_div">
                                    <div class="upload_crop_image">
                                        <img src="${base}/<@s.property value='userImageName' />" id="preview"/>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="content_none_border_div">
                <@s.form action="saveAvatar.jspx" namespace="/admin" method="post">
                    <@s.hidden name="imageX1" id="imageX1"/>
                    <@s.hidden name="imageY1" id="imageY1"/>
                    <@s.hidden name="imageX2" id="imageX2"/>
                    <@s.hidden name="imageY2" id="imageY2"/>
                    <@s.hidden name="imageW" id="imageW"/>
                    <@s.hidden name="imageH" id="imageH"/>
                    <@s.hidden name="userImageName" />
                    <@s.hidden name="imgWidth" />
                    <@s.hidden name="imgHeight" />

                    <div class="input_field_row">
                        <div class="input_field_title">
                            &nbsp;
                        </div>
                        <div class="input_field_value_section">
                            <@s.submit value="Save" cssClass="input_button_style" />
                        </div>
                    </div>
                </@s.form>
                </div>
            </div>
            <!-- end of left inner -->
        </div>
        <!-- End of left panel -->
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