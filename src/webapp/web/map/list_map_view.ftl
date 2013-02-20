<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<#assign sj=JspTaglibs["/WEB-INF/struts-jquery-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><@s.text name="mycollection.nav.label.name" /> - <@s.text name="create.new.collection" /></title>
<#include "../template/jquery_header.ftl"/>
<#include "../template/googlemap_header_v3.ftl"/>
    <script type="text/javascript">

    </script>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
    <div class="div_inline">&nbsp;&nbsp;</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline">Collection</div>
    <div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
    <div class="div_inline"><a href="${base}/mapview/showMapView.jspx">Map View</a></div>
</div>
<div style="clear:both"></div>

<div class="main_body_container">
    <div class="display_middel_div">
        <div class="left_display_div">
        <#include "../template/action_errors.ftl" />
            <div style="clear:both"></div>
            <div class="left_display_inner">
                <div class="content_none_border_div">
                    <div class="file_success_msg_div">
                        <p id="success_msg">Some text</p>
                    </div>

                    <div class="mapview_error_msg_div">
                        <div class="mapview_error_msg_item">
                            <ul>
                                <li><p id="mapview_error_msg">&nbsp;</p></li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div class="content_div">
                    <div class="site_map_top">
                        <span class="site_title" id="location_number_id">&nbsp;</span>
                        <div class="comments">[ Select a site to view the collection(s) on the map ]</div>
                    </div>
                    <div class="map_view_div">
                        <div class="map_view" id="map_view">
                        </div>

                        <div class="collection_info_div">
                            <div class="collection_list_div">

                            </div>
                        </div>

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