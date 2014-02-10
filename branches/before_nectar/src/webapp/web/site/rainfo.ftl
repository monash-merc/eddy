<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="site.restricted.access.action.title" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><a href="${base}/site/licenceinfo.jspx"><@s.text name="site.restricted.access.action.title" /></a></div>
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
                        Restricted Access
                    </div>

                    <div class="paragraph_div">
                        A restricted access period of no more than 18 months after submission of the data to the OzFlux Data Portal can be used by collection owners to allow post-graduate researchers
                        working with the data provider to undertake their studies.
                    </div>
                    <div class="paragraph_div">
                        Data which has been placed under restricted access will not have an <b>Export</b> button alongside the data file name in the collection.
                    </div>
                    <div class="paragraph_div">
                        During the restricted access period, anyone who wishes to use the data may contact the collection owner and ask for permission to access the data, stating their intended use
                        and offering collaboration on the intended analysis. If the data owner does not wish to allow access to the data, the data owner must notify the person requesting access and
                        the OzFlux Director in writing, providing an explanation as to why access can not be granted at this time.
                    </div>
                    <div class="paragraph_div">
                        In the event of a dispute between the data owner and a person wishing to use restricted access data, either party can contact the OzFlux Steering Committee to ask for the
                        matter to be resolved. If either party are unwilling to accept the decision of the OzFlux Steering Committee the final appeal will be to the Director of the Terrestrial
                        Ecosystem Research Network.
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