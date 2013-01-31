<@s.if test="%{searched == true}">
	<div class="bgcolor_none_border_div">
		<div class="p_title"><b>Search Results</b></div>
		<div class="none_border_block">
			<span class="name_title">
				<@s.if test="%{searchBean.collectionOnly == true}">
					Found a total of <font color="green"> ${pagination.totalRecords} </font> collection(s)  in this repository
				</@s.if>
				<@s.else>
					Found a total of <font color="green"> ${dsPagination.totalRecords} </font> dataset(s)  in this repository
				</@s.else>
				
			</span>
		</div>
		<!-- page sorting block -->
		<div class="msg_content">
			<@s.if test="%{searchBean.collectionOnly == true}">
				<a href="${base}/${pageLink}${pageSuffix}<@s.property value='pagination.pageNo' />" class="page_url"></a>
			</@s.if>
			<@s.else>
				<a href="${base}/${pageLink}${pageSuffix}<@s.property value='dsPagination.pageNo' />" class="page_url"></a>
			</@s.else>
		</div>
		<div class="search_border_block">	
			<span class="inline_span">				
				Page size: <@s.select id="item_select_size" name="sizePerPage" headerKey="<@s.property value='sizePerPage' />"  list="pageSizeMap" cssClass="input_select_small" />
				&nbsp;Sorted by: <@s.select id="item_select_order" name="orderBy" headerKey="${orderBy}"  list="orderByMap" cssClass="input_select_small" />
				&nbsp;Ordered by: <@s.select id="item_select_otype" name="orderByType" headerKey="${orderByType}"  list="orderByTypeMap" cssClass="input_select_small" />
			</span>
			<div class="blank_separator"></div>
		</div>
		<!-- end of page sorting block -->
		<div class="blank_separator"></div>
		
		<!-- display the search results -->
		<@s.if test="%{searchBean.collectionOnly == true}">
			<@s.iterator status="colStat" value="pagination.pageResults" id="colResult" >
				<div class="left_inner_panel">
					<div class="search_data">
						<div class="record_data_link"><a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='#colResult.id' />&collection.owner.id=<@s.property value='#colResult.owner.id' />&viewType=${viewType}" /><@s.property value="#colResult.name" /></a></div>
						<div class="record_data_inline"><@s.property value="#colResult.briefDesc" /></div>
						<div class="record_data_inline2">
							Created by <@s.property value="#colResult.owner.displayName" />, &nbsp;&nbsp;&nbsp;&nbsp; 
							Creation date: <@s.date name="#colResult.createdTime" format="yyyy-MM-dd hh:mm" /> &nbsp;&nbsp;&nbsp;&nbsp;
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
			<@s.if test="%{pagination.pageResults.size() < 4}">
				<br/>
                <br/>
			</@s.if>
			<br/>
			<#include "../pagination/pag_style.ftl" />
		</@s.if>
		<@s.else>
			<@s.iterator status="dsStat" value="dsPagination.pageResults" id="dsResult" >
				<div class="left_inner_panel">
					<div class="search_data">
						<div class="record_data_link"><a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='#dsResult.collection.id' />&collection.owner.id=<@s.property value='#dsResult.collection.owner.id' />&viewType=${viewType}"><@s.property value="#dsResult.collection.name" /></a></div>
						<div class="record_data_inline"><@s.property value="#dsResult.collection.briefDesc" /></div>
						<div class="record_data_inline2">
							Created by <@s.property value="#dsResult.collection.owner.displayName" />, &nbsp;&nbsp;&nbsp;&nbsp; 
							Creation date: <@s.date name="#dsResult.collection.createdTime" format="yyyy-MM-dd" /> &nbsp;&nbsp;&nbsp;&nbsp;
							Modified by <@s.property value="#dsResult.collection.modifiedByUser.displayName" />, &nbsp;&nbsp;&nbsp;&nbsp; 
							Modified date: <@s.date name="#dsResult.collection.modifiedTime" format="yyyy-MM-dd" />
						</div>
						<div class="record_data_link"><a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='#dsResult.collection.id' />&collection.owner.id=<@s.property value='#dsResult.collection.owner.id' />&viewType=${viewType}"><@s.property value="#dsResult.name" /></a></div>
					</div>
				</div>
				<div style="clear:both"></div>
			</@s.iterator>	
			<@s.if test="%{dsPagination.pageResults.size() < 4}">
				<br/>
                <br/>
			</@s.if>
			<br/>
			<#include "../pagination/search_page_style.ftl" />
		</@s.else>
	</div>
</@s.if>
<@s.else>
	<br/>
    <br/>
</@s.else>
