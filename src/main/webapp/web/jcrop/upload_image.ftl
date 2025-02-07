<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="user.display.home.action.title" /> - <@s.text name="user.profile.image.action.title" /></title>
<#include "../template/jquery_header.ftl"/>
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
                    <br/>
                    <br/>
                    <br/>
                <@s.form action="uploadImage.jspx" namespace="/admin" method="post" enctype="multipart/form-data" >
                    <div class="input_field_row">
                        <div class="input_field_title">
                            Upload Your Profile Image:
                        </div>
                        <div class="input_field_value_section">
                            <@s.file name="image"  cssClass="input_file"/>
                            <div class="comments">
                                (Only the <b>jpg</b>, <b>png</b> and <b>gif</b> image formats are supported. The minimum image size:[48x48] )
                            </div>
                        </div>
                    </div>
                    <div class="input_field_row">
                        <div class="input_field_title">
                            &nbsp;
                        </div>
                        <div class="input_field_value_section">
                            <@s.submit value="Upload" cssClass="input_button_style" />
                        </div>
                    </div>
                </@s.form>
                    <br/>
                    <br/>
                    <br/>
                    <br/>
                    <br/>
                </div>
            </div>
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