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
                        The data available for download from this portal is covered by one of the following licences:
                    </div>
                    <ul id="toc_num">
                        <li>
                            TERN-funded sites (indicated by the text ‘TERN funded’ appearing below the collection name) use the <a href="http://www.tern.org.au/datalicence/TERN-BY-SA-NC/1.0" target="_blank">TERN Attribution-Share Alike-Non Commercial (TERN-BY-SA-NC)</a> licence.

                            <p>
                                This licence allows data users to distribute, modify or build upon the data provided that they credit the original source, licence any modified data or products derived
                                from the data under the same terms as the original data and do not use the data for commercial purposes. These conditions can be waived but only by the data owner. See
                                the
                                TERN web site (<a href="http://www.tern.org.au/datalicence/TERN-BY-SA-NC/1.0" target="_blank">http://www.tern.org.au/datalicence/TERN-BY-SA-NC/1.0</a>) for a full
                                description of the TERN-BY-SA-NC licence.
                            </p>

                        </li>
                        <li>
                            Non-TERN funded sites may use the TERN-BY-SA-NC licence or they may choose to release their data under their own licence.
                        </li>
                    </ul>
                    <div class="paragraph_div">
                        The type of licence covering the data will be displayed when users download the data. Users will be asked to agree to the terms and conditions of the licence before proceeding
                        with the download. OzFlux strongly recommends that all users become familiar with the licence terms and conditions before downloading data.
                    </div>
                    <div class="paragraph_div">
                        In addition to the data licence, OzFlux uses a <a href="http://eddy.googlecode.com/files/OzFluxFairUseAndAcknowledgement.pdf" target="_blank">Fair Use data policy</a>
                        based on the FluxNet Fair Use policy. The Fair Use policy states OzFlux's
                        intentions in making the data publicly available and OzFlux's expectations of the data users responsibilities. As with the licence, users will be asked to confirm that they
                        have read and understood the OzFlux Fair Use policy before downloading the data.
                    </div>
                    <div class="paragraph_div">
                        In addition to the data licence and Fair Use policy, OzFlux sites may choose to restrict access to their data sets for a limited period.
                        The restricted access provisions are described in the <a href="${base}/site/rainfo.jspx">Restricted Access</a> page.
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