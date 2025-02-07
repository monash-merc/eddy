<div class="title_panel">
	<div class="div_inline">&nbsp;&nbsp;</div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	
	<@s.if test = "%{navigationBar != null}">
		<@s.if test = "%{navigationBar.startNavName != null}">
			<div class="div_inline">		
				<@s.if test="%{navigationBar.startNavLink != null}">
					<a href="${base}/${navigationBar.startNavLink}">${navigationBar.startNavName}</a>
				</@s.if>
				<@s.else>
					${navigationBar.startNavName}
				</@s.else>
			</div>
		</@s.if>
		
		<@s.if test = "%{navigationBar.secondNavName != null}">
			<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
			<div class="div_inline">
				<@s.if test = "%{navigationBar.secondNavLink != null}">
					<a href="${base}/${navigationBar.secondNavLink}">${navigationBar.secondNavName}</a>
				</@s.if>
				<@s.else>
					${navigationBar.secondNavName}
				</@s.else>
			</div>
		</@s.if>
		
		<@s.if test = "%{navigationBar.thirdNavName != null}">
			<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
			<div class="div_inline">
				<@s.if test = "%{navigationBar.thirdNavLink != null}">
					<a href="${base}/${navigationBar.thirdNavLink}">${navigationBar.thirdNavName}</a>
				</@s.if>
				<@s.else>
					${navigationBar.thirdNavName}
				</@s.else>
			</div>
		</@s.if>
	
	</@s.if>
	
</div>
<div style="clear:both"></div> 
