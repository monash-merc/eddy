<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.property value="pageTitle" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<#include "../template/action_title.ftl" />
 
<div class="main_body_container">
<div class="main_big_border">
	<div class="left_container_panel">
		<br/>
		<#include "../template/action_errors.ftl" />
		<div class="left_middle_panel">		
		<div class="none_border_block">
		 	 <@s.if test="%{actionSuccessMsg != null}">
			 	<#include "../template/action_success_msg.ftl"/>
			 </@s.if>
		</div>
		<div class="none_border_block">
			<span class="name_title">You have <font color="green"> ${eventPagination.totalRecords} </font> Events</span>
		</div>
		<@s.if test="%{eventPagination.pageResults.size() > 0}">
			<div class="msg_content">
				<a href="${base}/${pageLink}${pageSuffix}${eventPagination.pageNo}" class="page_url"></a>
			</div>
			<div class="single_line_center_block">	
				<span class="inline_span">				
					Page size: <@s.select id="item_select_size" name="sizePerPage" headerKey="${sizePerPage}"  list="pageSizeMap" cssClass="input_select_small" />
					&nbsp;Sorted by: <@s.select id="item_select_order" name="orderBy" headerKey="${orderBy}"  list="orderByMap" cssClass="input_select_small" />
					&nbsp;Ordered by: <@s.select id="item_select_otype" name="orderByType" headerKey="${orderByType}"  list="orderByTypeMap" cssClass="input_select_small" />
				</span>
			</div>
			 
			<@s.iterator status="eventStat" value="eventPagination.pageResults" id="eventResult" >
				<div class="left_inner_panel">
					<div class="record_data">
						<div class="record_data_inline"><font color="#0E774A"><@s.date name="#eventResult.createdTime"  format="dd-MM-yyyy 'at' hh:mm a" /></font> &nbsp;&nbsp;<@s.property value="#eventResult.event" />, by <@s.property value="#eventResult.operator.displayName" /></div>
					 	<div class="record_data_link2">
					 		<a href="${base}/${deleteEventLink}?pageNo=${eventPagination.pageNo}&auditEvent.id=<@s.property value='#eventResult.id' />">&nbsp;&nbsp;&nbsp; Delete &nbsp;&nbsp;&nbsp;</a>
					 	</div>
					</div>
				</div>
				<div style="clear:both"></div>
			</@s.iterator>
			<!-- END of Record -->
			<div style="clear:both"></div>
			<@s.if test="%{eventPagination.pageResults.size() < 4}">
					<div class="none_border_space_small"></div>
			</@s.if>
			<br/>
			<#include "../pagination/pag_style2.ftl" />
		</@s.if>
		<@s.else>
			<div class="bottom_border_block"></div>
			<br/>
			<div class="none_border_space_block"></div>
		</@s.else>
	</div>
	<br/>
	</div>
	<div class="right_container_panel">		 
		 <#include "../template/subnav_section.ftl" />
	</div>
	<div style="clear:both"></div>
</div> 
</div>
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>