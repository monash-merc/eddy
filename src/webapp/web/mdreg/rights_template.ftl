<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<#include "../template/jquery_header.ftl"/>
<style type="text/css">
.error_msg_section{
	background: none repeat scroll 0 0 white;
    border: 1px solid #E1E8F0;
    margin: 10px auto;
    text-align: center;
    width: 460px;
}
</style>
</head>
<body>
<br/>
<div class="mcpop_pmain_div">
	<#include "../template/action_errors.ftl" />
	<div class="mcpop_title">
	<@s.if test="%{rights.rightsType == 'cccl_license'}">
		Creative Commons License
	</@s.if>
	<@s.if test="%{rights.rightsType == 'ccpd_license'}">
		Creative Commons CC0 Waiver
	</@s.if>
	<@s.if test="%{rights.rightsType == 'user_license'}">
		Define Your Own License
	</@s.if>
	</div>
		<!-- creative commons license -->
		<@s.if test="%{rights.rightsType == 'cccl_license'}">
			<@s.form action="cccllicense.jspx" namespace="/data" method="post">
			<div class="mcpop_field">
				<div class="license_div"><b>${commercialField.label}</b></div>
				<@s.iterator status="cmf_stat" value="commercialField.licenseFields" id="cmf" >
					<div class="license_div">
						<@s.if test="%{rights.commercial == #cmf.id}">
							<input type="radio" name ="rights.commercial" value='${cmf.id}' checked="checked" /> 
						</@s.if>
						<@s.else>
							<input type="radio" name ="rights.commercial" value='${cmf.id}' /> 
						</@s.else>
						${cmf.label} &nbsp;&nbsp; <img src="${base}/images/info.png" class="lInfo_image" title="${cmf.description}" />
					</div>
				</@s.iterator>
				<div style="clear:both"></div>
				
				<div class="license_div"><b>${derivativesField.label}</b></div>
				<@s.iterator status="derf_stat" value="derivativesField.licenseFields" id="derf" >
					<div class="license_div">
					<@s.if test="%{rights.derivatives == #derf.id}">
						<input type="radio" name ="rights.derivatives" value='${derf.id}' checked="checked"/>
					</@s.if>
					<@s.else>	
						<input type="radio" name ="rights.derivatives" value='${derf.id}' />
					</@s.else>
						${derf.label} &nbsp;&nbsp; <img src="${base}/images/info.png" class="lInfo_image" title="${derf.description}" />
					</div>
				</@s.iterator>
				<div style="clear:both"></div> 
				<div class="license_div"><b>${jurisdictionField.label}</b> &nbsp;&nbsp;  <img src="${base}/images/info.png" class="lInfo_image" title="${jurisdictionField.description}" /></div>
				<div class="license_div"><@s.select name="rights.jurisdiction" headerKey="${rights.jurisdiction}" list="jurisMap" cssClass="input_select_normal"/></div> 
				<div style="clear:both"></div>
				<br/>
			</div>
			<div class="mcpop_bddiv">
				<@s.hidden name="rights.rightsType" />
				<@s.hidden name="collection.id" />
				<input type="button" value=" Back " class="mcpop_button" onclick="window.location = '${base}/data/rightsOptions.jspx?collection.id=${collection.id}&rights.rightsType=${rights.rightsType}';"> &nbsp;&nbsp; <input type="button"  value=" Cancel " class="mcpop_button" id="cancelRights" /> &nbsp;&nbsp; <input type="submit" name="options" value=" Next " class="mcpop_button" />
			</div>
			</@s.form>
		</@s.if>
		
		<!-- creative waiver license -->
		<@s.if test="%{rights.rightsType == 'ccpd_license'}">
			<@s.form action="ccpdlicense.jspx" namespace="/data" method="post">
			<div class="mcpop_field">
				<div class="license_field">
					<div class="legalc_select">
						<@s.checkbox name="confirmed" id="confirm_details"/>
					</div>
					<div class="legalc_confirm">I hereby waive all copyright and related or neighboring rights together with all associated claims and causes of action with respect to this work to the extent possible under the law.</div>
				</div>
				<div class="license_field">
					<iframe class="mcpop_frame" src="${base}/data/legalcode.jspx"></iframe>
				</div>
				<div class="license_field">
					<div class="legalc_select">
						<@s.checkbox name="understood" id="confirm_understand"/>
					</div>
					<div class="legalc_confirm">I have read and understand the terms and intended legal effect of CC0, and hereby voluntarily elect to apply it to this work.</div>
					<div style="clear:both"></div> 
				</div>
				<div style="clear:both"></div>
			</div>
			<div class="mcpop_bddiv">
				<@s.hidden name="rights.rightsType" />
				<@s.hidden name="collection.id" />
				<input type="button" value=" Back " class="mcpop_button" onclick="window.location = '${base}/data/rightsOptions.jspx?collection.id=${collection.id}&rights.rightsType=${rights.rightsType}';"/> &nbsp;&nbsp; <input type="button"  value=" Cancel " class="mcpop_button" id="cancelRights" /> &nbsp;&nbsp; <input type="submit" name="options" value=" Next " class="mcpop_button" />
			</div>
			</@s.form>
		</@s.if>
		
		<!-- user own license -->
		<@s.if test="%{rights.rightsType == 'user_license'}">
		
		<div class="mcpop_field">
			<div class="license_field">
				<@s.textarea name="rights.rightContents" id="prights_cont" cssClass="pop_textarea" />
			</div>
		</div>
		<div class="mcpop_bddiv">
			<@s.hidden name="rights.rightsType" id="prights_type"/>
			<@s.hidden name="rights.commercial" id="prights_comm"/>
			<@s.hidden name="rights.derivatives" id="prights_deri"/>
			<@s.hidden name="rights.jurisdiction" id="prights_juri"/>
			<@s.hidden name="collection.id" />
			<input type="button" value=" Back " class="mcpop_button" onclick="window.location = '${base}/data/rightsOptions.jspx?collection.id=${collection.id}&rights.rightsType=${rights.rightsType}';"> &nbsp;&nbsp; 
			<input type="button"  value=" Cancel " class="mcpop_button" id="cancelRights" /> &nbsp;&nbsp; <input type="button"  value=" Save " id="saveRights" class="mcpop_button" />
		</div>
		
		</@s.if>
</div>

</body>
</html>