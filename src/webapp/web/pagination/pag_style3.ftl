<div class="page_style">
	<span class="total">Total ${userPagination.totalPages} Pages</span>
<#if userPagination.firstPage>
	<span class="disabled"> <img src="${base}/images/dis_first.png" class="image_position" /> Prev </span>
<#else>
	<a href="${base}/${pageLink}${pageSuffix}1"> <img src="${base}/images/first.png" class="image_position" /> First </a>
	<a href="${base}/${pageLink}${pageSuffix}<@s.property value='userPagination.prevPage' />"> <img src="${base}/images/prev.png" class="image_position" /> Prev </a>
</#if>
<#if userPagination.pageNo-5 gt 1>
	<#if userPagination.totalPages gt userPagination.pageNo+4>
		<#list userPagination.pageNo-5..userPagination.pageNo+4 as i>
			<#if i == userPagination.pageNo>
				<span class="current">${i}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i}</#if>">${i}</a>
			</#if>
		</#list>
	<#else>
		<#list userPagination.totalPages-9..userPagination.totalPages as i>
			<#if i == userPagination.pageNo>
				<span class="current">${i}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i}</#if>">${i}</a>
			</#if>
		</#list>
	</#if>
<#else>
	<#if userPagination.totalPages gt 10>
		<#list 1..10 as i>
			<#if i == userPagination.pageNo>
				<span class="current">${i}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i}</#if>">${i}</a>
			</#if>
		</#list>
	<#else>
		<#list 1..userPagination.totalPages as i>
			<#if i == userPagination.pageNo>
				<span class="current">${i}</span>
			<#else>
				<a href="${base}/${pageLink}${pageSuffix}<#if i gt 0>${i}</#if>">${i}</a>
			</#if>
		</#list>
	</#if>
</#if>
<#if userPagination.lastPage>	
	<span class="disabled"> Next <img src="${base}/images/dis_next.png" class="image_position" /> </span><span class="disabled"> Last <img src="${base}/images/dis_last.png" class="image_position" /> </span>
<#else>
	<a href="${base}/${pageLink}${pageSuffix}<@s.property value='userPagination.nextPage' />"> Next <img src="${base}/images/next.png" class="image_position" /> </a>
	<a href="${base}/${pageLink}${pageSuffix}<@s.property value='userPagination.totalPages' />"> Last <img src="${base}/images/last.png" class="image_position" /> </a>
</#if>
</div>