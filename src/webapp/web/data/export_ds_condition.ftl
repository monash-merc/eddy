<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<#include "../template/jquery_header.ftl"/>

</head>
<body>
<div class="popup_main_div">
<#include "../template/action_errors.ftl" />
    <div class="export_row_div">
        <div class="popup_title">
            Data Licence
        </div>
    <@s.if test="%{licence.licenceType == 'tern'}">

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
        <div class="fair_use_policy">
            <b>The Fair use policy</b> is available <a href="#" target="_blank">here</a>
        </div>
    </@s.if>
    <@s.else>
        <div class="user_defined_licence">
            <@s.property value="licence.contents" />
        </div>
    </@s.else>
    </div>

    <div class="export_row_div">
        <div class="citation_info_div">
            <div class="exp_citation_hints">If you make use of this collection in your research, please cite:</div>
            <div class="citation_contents_div">
            <@s.property value='collection.owner.firstName' />  <@s.property value='collection.owner.lastName' /> (<@s.date name="collection.createdTime" format="yyyy" />
                ) <@s.property value="collection.name"/>
            <@s.property value="publisher"/>
            <@s.if test = '%{collection.persistIdentifier.indexOf("/") != -1}'>
                hdl: <@s.property value="collection.persistIdentifier"/>
            </@s.if>
            <@s.else>
                local: <@s.property value="collection.persistIdentifier"/>
            </@s.else>
            </div>
        </div>
    </div>
<@s.form id="exportDataset" action="exportDataset.jspx" >
    <@s.hidden name="collection.id" />
    <@s.hidden name="collection.owner.id" />
    <@s.hidden name="viewType" />
    <@s.hidden name="dataset.id" />
    <div class="accept_licence">
        <div><@s.checkbox name="acceptedLicence" id='accept_licence' cssClass="check_box" /> I have read the Fair use policy and agree to abide by the data licence.</div>
    </div>
    <div class="blank_separator"></div>
    <div class="export_action">
        <@s.submit value="Export" name="Export" cssClass="input_button_style"/>
    </div>
</@s.form>
</div>
</body>
</html>
