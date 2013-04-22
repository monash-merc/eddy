<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="site.licensing.action.title" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><a href="${base}/site/licenceinfo.jspx"><@s.text name="site.licensing.action.title" /></a></div>
</div>
<div style="clear:both"></div>
<div class="main_body_container">
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "../template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
                <div class="content_none_border_div">
                    <div class="sub_b_title">
                        Licensing information
                    </div>
                    <div class="paragraph_div">
                        The data available for download from this portal is covered by one of the following
                        licenses:
                    </div>
                    <ul id="toc_num">
                        <li>
                            TERN-funded sites (indicated by the text “TERN funded” appearing alongside the collection name)
                            use the TERN Attribution-Share Alike-Non Commercial (TERN-BY-SA-NC) license. This license allows
                            data users to distribute, modify or build upon the data provided that they credit the original
                            source, license any modified data or products derived from the data under the same terms as the
                            original data and do not use the data for commercial purposes. These conditions can only be waived
                            by the relevant party. See the TERN web site (<a href="http://www.tern.org.au/datalicence/TERN-BY-SA-NC/1.0" target="_blank">http://www.tern.org.au/datalicence/TERN-BY-SA-NC/1.0</a>)
                            for a full description of the TERN-BY-SA-NC license.
                        </li>
                        <li>
                            Non-TERN funded sites may use the TERN-BY-SA-NC license or they may choose
                            to release their data under their own license.
                        </li>
                    </ul>
                    <div class="paragraph_div">
                        The type of license covering the data will be displayed when you download the data and
                        you will be asked to agree to the terms and conditions of the license before proceeding
                        with the download. OzFlux strongly recommends that you become familiar with the license
                        terms and conditions before you download the data.
                    </div>
                    <div class="paragraph_div">
                        In addition to the data license, OzFlux uses a Fair Use data policy based on the FluxNet
                        Fair Use policy. The Fair Use policy states OzFlux's intentions in making the data publicly
                        available and OzFlux's expectations of the data users responsibilities. As with the license,
                        you will be asked to confirm that you have read and understood the OzFlux Fair Use policy
                        before downloading the data.
                    </div>
                    <div class="paragraph_div">
                        In addition to the data license and Fair Use policy, OzFlux sites may choose to place
                        embargo on their data. The 2 types of embargo possible are:
                    </div>
                    <ul id="toc_num">
                        <li>
                            An embargo of no more than 3 months after submission of data to the OzFlux Data
                            Portal to allow the data provider to check the quality of the data.
                        </li>
                        <li>
                            An embargo of no more than 18 months after submission of the data to the OzFlux
                            Data Portal to allow post-graduate researchers working with the data provider to
                            undertake their studies. Use of this embargo excludes the use of an embargo for
                            quality control purposes. If the data has been under embargo for quality control
                            purposes, the embargo may be continued for post-graduate research purposes but
                            the total period must not extend beyond 18 months.
                        </li>
                    </ul>
                    <div class="paragraph_div">
                        Data which has been placed under an embargo will not have an “Export” button alongside
                        the data file name in the collection.
                    </div>

                    <div class="paragraph_div">
                        During either embargo period, anyone who wishes to use the embargoed data may
                        contact the collection owner and ask for permission to access the data, stating their
                        intended use and offering collaboration on the intended analysis. If the data owner does
                        not wish to allow access to the data, they must notify the person requesting access and
                        the OzFlux Director in writing, providing an explanation as to why access can not be
                        granted at this time.
                    </div>

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