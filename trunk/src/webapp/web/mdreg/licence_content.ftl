<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<#include "../template/jquery_header.ftl"/>
    <style type="text/css">
        .error_msg_section {
            margin: 5px;
            width: 450px;
            text-align: left;
            display: none;
        }
    </style>
</head>
<body>
<div class="popup_main_div">
    <div class="error_msg_section"></div>
<@s.form action="saveLicence.jspx" namespace="/data" method="post" id="licence_form">
    <@s.hidden name="collection.id"/>
    <@s.hidden name="licence.licenceType" id="plicence_type"/>
    <@s.if test="%{licence.licenceType == 'tern'}">
        <div class="popup_title">
            TERN Licence
        </div>
        <div class="popup_row">
            <@s.hidden name="licence.contents" id="plicence_contents"/>
        </div>

        <div class="popup_row">
            <div class="tern_licence">
                <div class="licence_logo"><a href="http://www.tern.org.au/datalicence/TERN-BY-SA-NC/1.0" target="_blank">http://www.tern.org.au/datalicence/TERN-BY-SA-NC/1.0</a> &nbsp;&nbsp;&nbsp;&nbsp;
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
    </@s.if>
    <@s.else>
        <div class="popup_title">
            Define Your Own Licence
        </div>

        <div class="popup_row">
            <div class="popup_spec">
                Please edit | input the data licence bellow:
            </div>
        </div>
        <div class="licence_contents">
            <@s.textarea name="licence.contents" cssClass="input_textarea" style="width: 535px; height: 240px;" id="plicence_contents" />
            <div class="comments">
                <@s.text name="licence.add.user.defined.licence.hint" />
            </div>
        </div>
    </@s.else>
    <div class="popup_button_div">
        <input type="button" value="Back" class="input_button_style"
               onclick="window.location = '${base}/data/licenceOptions.jspx?collection.id=<@s.property value='collection.id' />&licence.licenceType=${licence.licenceType}';"> &nbsp;&nbsp;
        <input type="submit" value="Save" id="saveLicence" class="input_button_style"/>
    </div>
</@s.form>
</div>
</div>
</body>
</html>