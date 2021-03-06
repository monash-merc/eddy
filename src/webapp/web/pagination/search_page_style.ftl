<div class="page_style">
	<span class="total">Total ${dsPagination.totalPages} Pages</span>
<#if dsPagination.firstPage>
	<span class="disabled"> <img src="${base}/images/dis_first.png" class="image_position" /> Prev </span>
<#else>
	<a href="${base}/${pageLink}${pageSuffix}1"> <img src="${base}/images/first.png" class="image_position" /> First </a>
	<a href="${base}/${pageLink}${pageSuffix}<@s.property value='dsPagination.prevPage' />"> <img src="${base}/images/prev.png" class="image_position" /> Prev </a>
</#if>
<#if dsPagination.pageNo-5 gt 1>
	<#if dsPagination.totalPages gt dsPagination.pageNo+4>
		<#list dsPagination.pageNo-5..dsPagination.pageNo+4 as i>
			<#if i == dsPagination.pageNo>
				<span class="current">${i?c}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i?c}</#if>">${i?c}</a>
			</#if>
		</#list>
	<#else>
		<#list dsPagination.totalPages-9..dsPagination.totalPages as i>
			<#if i == dsPagination.pageNo>
				<span class="current">${i?c}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i?c}</#if>">${i?c}</a>
			</#if>
		</#list>
	</#if>
<#else>
	<#if dsPagination.totalPages gt 10>
		<#list 1..10 as i>
			<#if i == dsPagination.pageNo>
				<span class="current">${i?c}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i?c}</#if>">${i?c}</a>
			</#if>
		</#list>
	<#else>
		<#list 1..dsPagination.totalPages as i>
			<#if i == dsPagination.pageNo>
				<span class="current">${i?c}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i?c}</#if>">${i?c}</a>
			</#if>
		</#list>
	</#if>
</#if>
<#if dsPagination.lastPage>	
	<span class="disabled"> Next <img src="${base}/images/dis_next.png" class="image_position" /> </span><span class="disabled"> Last <img src="${base}/images/dis_last.png" class="image_position" /> </span>
<#else>
	<a href="${base}/${pageLink}${pageSuffix}<@s.property value='dsPagination.nextPage' />"> Next <img src="${base}/images/next.png" class="image_position" /> </a>
	<a href="${base}/${pageLink}${pageSuffix}<@s.property value='dsPagination.totalPages' />"> Last <img src="${base}/images/last.png" class="image_position" /> </a>
</#if>
</div>