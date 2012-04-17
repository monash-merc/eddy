<div class="page_style">
	<span class="total">Total ${pagination.totalPages} Pages</span>
<#if pagination.firstPage>
	<span class="disabled"> <img src="${base}/images/dis_first.png" class="image_position" /> Prev </span>
<#else>
	<a href="${base}/${pageLink}${pageSuffix}1"> <img src="${base}/images/first.png" class="image_position" /> First </a>
	<a href="${base}/${pageLink}${pageSuffix}<@s.property value='pagination.prevPage' />"> <img src="${base}/images/prev.png" class="image_position" /> Prev </a>
</#if>
<#if pagination.pageNo-5 gt 1>
	<#if pagination.totalPages gt pagination.pageNo+4>
		<#list pagination.pageNo-5..pagination.pageNo+4 as i>
			<#if i == pagination.pageNo>
				<span class="current">${i?c}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i?c}</#if>">${i?c}</a>
			</#if>
		</#list>
	<#else>
		<#list pagination.totalPages-9..pagination.totalPages as i>
			<#if i == pagination.pageNo>
				<span class="current">${i?c}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i?c}</#if>">${i?c}</a>
			</#if>
		</#list>
	</#if>
<#else>
	<#if pagination.totalPages gt 10>
		<#list 1..10 as i>
			<#if i == pagination.pageNo>
				<span class="current">${i?c}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i?c}</#if>">${i?c}</a>
			</#if>
		</#list>
	<#else>
		<#list 1..pagination.totalPages as i>
			<#if i == pagination.pageNo>
				<span class="current">${i?c}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i?c}</#if>">${i?c}</a>
			</#if>
		</#list>
	</#if>
</#if>
<#if pagination.lastPage>	
	<span class="disabled"> Next <img src="${base}/images/dis_next.png" class="image_position" /> </span><span class="disabled"> Last <img src="${base}/images/dis_last.png" class="image_position" /> </span>
<#else>
	<a href="${base}/${pageLink}${pageSuffix}<@s.property value='pagination.nextPage' />"> Next <img src="${base}/images/next.png" class="image_position" /> </a>
	<a href="${base}/${pageLink}${pageSuffix}<@s.property value='pagination.totalPages' />"> Last <img src="${base}/images/last.png" class="image_position" /> </a>
</#if>
</div>