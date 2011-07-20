<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
 <#assign sj=JspTaglibs["/WEB-INF/struts-jquery-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>import file</title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
	<div class="div_inline">&nbsp;&nbsp;</div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline"><a href="${base}/data/listUserCollections.jspx"><@s.text name="mycollection.nav.label.name" /></a></div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline">Import File</div>		
</div>
<div style="clear:both"></div> 

<div class="main_body_container">
<div class="main_big_border">
	<div class="left_container_panel">
		<br/>
		<#include "../template/action_errors.ftl" />	 
		<div class="left_middle_panel">
			<div class="none_border_block"></div>
			<div class="single_border_block">
				<div id="ajaxfileupload">
					<#assign doafter = 'undefined' />
					<#assign dobefore = 'undefined' />
					<div id="fileuploadProgress">    
						<div id="uploadFilename">Initialising, please wait.....</div>
						<div id="progress-bar">
							<div id="progress-bgrd"></div>
						
							<div id="progress-text"></div>
						</div>
						<br/>
					</div> 
					<span id="message"></span>
				</div>
				<br/>
				<div id="fileuploadForm">
					<@s.form id="ajaxFileUploadForm" onsubmit="return false" action="importFile.jspx" namespace="/data" method="post" enctype="multipart/form-data" >
						<table align="center">
							<tr>
								<td>Please select a file</td>
								<td><@s.file name="upload" id="upload" /></td>
							</tr>
							<tr>
								<td>Extract the Metadata</td>
								<td align="left"><@s.checkbox name="extractable" id="extract" value="true"/></td>
							</tr>
						 	<tr>
						 		<td colspan="2"><@s.submit value="Import" id="fileUpload" name="fileUpload" onclick="return mercajax.AjaxFileUpload.initialise(${dobefore}, ${doafter});" cssClass="input_button_normal"/></td>
						 	</tr>
						 </table>
					</@s.form>
				</div>
			</div>
		</div>
	</div>
	<div class="right_container_panel">
		<#include "../template/subnav_section.ftl" />
	</div>
	<br/>
	<div style="clear:both"></div>
</div>
</div>
<br/>
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>