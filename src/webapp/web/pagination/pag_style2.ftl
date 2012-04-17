<div class="page_style">
	<span class="total">Total ${eventPagination.totalPages} Pages</span>
<#if eventPagination.firstPage>
	<span class="disabled"> <img src="${base}/images/dis_first.png" class="image_position" /> Prev </span>
<#else>
	<a href="${base}/${pageLink}${pageSuffix}1"> <img src="${base}/images/first.png" class="image_position" /> First </a>
	<a href="${base}/${pageLink}${pageSuffix}<@s.property value='eventPagination.prevPage' />"> <img src="${base}/images/prev.png" class="image_position" /> Prev </a>
</#if>
<#if eventPagination.pageNo-5 gt 1>
	<#if eventPagination.totalPages gt eventPagination.pageNo+4>
		<#list eventPagination.pageNo-5..eventPagination.pageNo+4 as i>
			<#if i == eventPagination.pageNo>
				<span class="current">${i?c}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i?c}</#if>">${i?c}</a>
			</#if>
		</#list>
	<#else>
		<#list eventPagination.totalPages-9..eventPagination.totalPages as i>
			<#if i == eventPagination.pageNo>
				<span class="current">${i?c}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i?c}</#if>">${i?c}</a>
			</#if>
		</#list>
	</#if>
<#else>
	<#if eventPagination.totalPages gt 10>
		<#list 1..10 as i>
			<#if i == eventPagination.pageNo>
				<span class="current">${i?c}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i?c}</#if>">${i?c}</a>
			</#if>
		</#list>
	<#else>
		<#list 1..eventPagination.totalPages as i>
			<#if i == eventPagination.pageNo>
				<span class="current">${i?c}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i?c}</#if>">${i?c}</a>
			</#if>
		</#list>
	</#if>
</#if>
<#if eventPagination.lastPage>	
	<span class="disabled"> Next <img src="${base}/images/dis_next.png" class="image_position" /> </span><span class="disabled"> Last <img src="${base}/images/dis_last.png" class="image_position" /> </span>
<#else>
	<a href="${base}/${pageLink}${pageSuffix}<@s.property value='eventPagination.nextPage' />"> Next <img src="${base}/images/next.png" class="image_position" /> </a>
	<a href="${base}/${pageLink}${pageSuffix}<@s.property value='eventPagination.totalPages' />"> Last <img src="${base}/images/last.png" class="image_position" /> </a>
</#if>
</div>