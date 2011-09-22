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
<!-- Navigation Title -->
<#include "../template/action_title.ftl" />
<!-- End of Navigation Title -->

<div class="main_body_container">
<div class="main_big_border">
	<div class="left_container_panel">
	<br/> 
		<div class="left_middle_panel">
			<#include "../template/action_errors.ftl" />
			<div class="none_border_block">
				 <#include "../template/action_message.ftl" />
			</div>
			<br/>
			<div class="single_border_block">
				<table class="table_col">
					<tr>
						<td><div class="name_title"><@s.property value="collection.name"/></div></td>
					</tr>
					<tr>
						<td>	
							<div class="inline_span_justify">
								<@s.property  value="collection.description" escape=false />
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<span class="inline_span2">
								Created by <@s.property value="collection.owner.displayName" />, &nbsp;&nbsp;&nbsp;&nbsp; 
								Creation date: <@s.date name="collection.createdTime" format="yyyy-MM-dd hh:mm" /> &nbsp;&nbsp;&nbsp;&nbsp;
								Modified by <@s.property value="collection.modifiedByUser.displayName" />, &nbsp;&nbsp;&nbsp;&nbsp; 
								Modified date: <@s.date name="collection.modifiedTime" format="yyyy-MM-dd hh:mm" />
							</span>
						</td>
					</tr>
                    <tr>
                        <td>
                            <div class="status_name_div">Metadata Registered: </div>
                            <div class="status_value_div"><@s.property value="collection.published" /></div>
                        </td>
                    </tr>
					<tr>
						<td>
				 	 		<div class="inline_td_div"> 
				 	 			<a href="${base}/${viewColDetailLink}?collection.id=${collection.id}&collection.owner.id=${collection.owner.id}&viewType=${viewType}"">&nbsp; View details &nbsp;</a>
				 	 	 	</div>
				 	 	</td>
					</tr>
					<tr>
						<td></td>
					</tr>
				</table>
			</div>
			<div class="none_border_space_block"></div>
			<div style="clear:both"></div>
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


