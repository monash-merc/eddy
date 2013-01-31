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
    <@s.if test="%{#session.authentication_flag =='authenticated'}">
		<div class="left_container_panel">
	</@s.if>
	<@s.else>
		<div class="none_boder_left_container">
	</@s.else>
		<br/>
		<#include "../template/action_errors.ftl" />
		<div class="left_middle_panel">
			<br/>		
			<div class="none_border_block">
				<span class="name_title">A total of <font color="green"> ${pagination.totalRecords} </font> collection(s) in this repository</span>
			</div>
			<@s.if test="%{pagination.pageResults.size() > 0}">
				<!-- Hidden msg content and it's used by page view sort -->
				<div class="msg_content">
					<a href="${base}/${pageLink}${pageSuffix}<@s.property value='pagination.pageNo' />" class="page_url"></a>
				</div>
				<div class="single_line_center_block">	
					<span class="inline_span">				
						Page size: <@s.select id="item_select_size" name="sizePerPage" headerKey="<@s.property value='sizePerPage' />"  list="pageSizeMap" cssClass="input_select_small" />
						&nbsp;Sorted by: <@s.select id="item_select_order" name="orderBy" headerKey="${orderBy}"  list="orderByMap" cssClass="input_select_small" />
						&nbsp;Ordered by: <@s.select id="item_select_otype" name="orderByType" headerKey="${orderByType}"  list="orderByTypeMap" cssClass="input_select_small" />
					</span>
				</div>
				<!-- START of Record -->
				<@s.iterator status="colStat" value="pagination.pageResults" id="colResult" >
				<div class="left_inner_panel">
					<div class="record_data">
						<div class="record_data_link"><a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='#colResult.id '/>&collection.owner.id=<@s.property value='#colResult.owner.id' />&viewType=${viewType}"><@s.property value="#colResult.name" /></a></div>
						<div class="record_data_inline"><@s.property value="#colResult.briefDesc" /></div>
						<div class="record_data_inline2">
							Created by <@s.property value="#colResult.owner.displayName" />, &nbsp;&nbsp;&nbsp;&nbsp; 
							Created date: <@s.date name="#colResult.createdTime" format="yyyy-MM-dd hh:mm" /> &nbsp;&nbsp;&nbsp;&nbsp;
							Modified by <@s.property value="#colResult.modifiedByUser.displayName" />, &nbsp;&nbsp;&nbsp;&nbsp; 
							Modified date: <@s.date name="#colResult.modifiedTime" format="yyyy-MM-dd hh:mm" />
					 	</div>

					 	<div class="record_data_link2">
					 	 	<a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='#colResult.id' />&collection.owner.id=<@s.property value='#colResult.owner.id' />&viewType=${viewType}">View details</a>
					 	</div>
					</div>
				</div>
				<div style="clear:both"></div>
				</@s.iterator>
				<!-- END of Record -->
				<div style="clear:both"></div>
				
				<@s.if test="%{pagination.pageResults.size() < 2}">
					<div class="none_border_space_small"></div>
				</@s.if>
				<br/>
				<#include "../pagination/pag_style.ftl" />
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
		<@s.if test="%{#session.authentication_flag =='authenticated'}">  
		 	<#include "../template/subnav_section.ftl" />
		</@s.if>
	</div>
	<div style="clear:both"></div>
</div> 
</div>
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>