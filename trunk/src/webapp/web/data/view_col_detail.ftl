<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.property value="pageTitle" /></title>
<#include "../template/jquery_header.ftl"/>
<script type="text/javascript">
function doAfterImport(success) {
    if(success) {
		<@s.if test = "%{navigationBar.secondNavLink != null}">
				 setTimeout('window.location.href = "${base}/${navigationBar.secondNavLink}"', 3000);
		</@s.if>	    
    }
};
</script>
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
 	<@s.if test="%{collectionError == true}">
		<div class="left_middle_panel">
			<#include "../template/action_errors.ftl" />
		 	<div class="none_border_space_block"></div>
		</div>
	</@s.if>
	<@s.else>
		<#include "../template/action_errors.ftl" />
		<div class="left_middle_panel">
			<@s.if test="%{actionSuccessMsg != null}">
		 		<div class="none_border_block">
			 		<#include "../template/action_success_msg.ftl"/>
				</div>
			</@s.if>
			<!-- File importing message block -->
			<div class="none_border_block">
				<div class="file_success_msg_div">
					<p id="success_msg">&nbsp;</p>
					<img src="${base}/images/btn-delete.png" alt="hidden" class="hidden_msg" />
				</div>
				
				<div class="file_error_msg_div">
			 	 	<div class="error_msg_item_div">
			 	 		<ul>
			 	 		   <li><p id="error_msg">test</p></li>
			 	 		 </ul>
			 	 	</div>
		 	 	</div>
			</div>
			<!-- End of file importing message block -->
			
			<@s.if test="%{permissionBean.viewAllowed}">
			 <!-- Display total data files in this collection -->
		 	<div class="none_border_block">
		 		<span class="name_title">A total of <font color="green"> 
		 			<@s.if test = "%{datasets != null}"><@s.property value="datasets.size" /></@s.if>
		 			<@s.else>0</@s.else> 
		 		</font> data file(s) in this collection</span>
		 	</div>
		 	</@s.if>
		 	
		 	<div class="blank_separator"></div>
		 	
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
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>
		 	 	 			<div class="inline_td_div"> 
		 	 	 				
			 	 	 		   	<@s.if test="%{permissionBean.viewAllowed == false}">
			 	 	 		   		<a href="${base}/perm/applyForPerms.jspx?collection.id=${collection.id}">Apply For Permissions</a>
			 	 	 			</@s.if>
		 	 	 				
		 	 	 				<div id='confirm-dialog'>
		 	 	 					<@s.if test="%{permissionBean.editAllowed == true}">
		 	 	 		 				<a href="${base}/${showColEditLink}?collection.id=${collection.id}&collection.owner.id=${collection.owner.id}&viewType=${viewType}">&nbsp; &nbsp; &nbsp; &nbsp; Edit &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; </a> &nbsp;&nbsp;
			 	   					</@s.if>
			 	   					<div class="msg_content">All data will be removed from the repository permanently!<p>Are you sure to delet this collection?</p></div>
									<div id='confirm'>
										<div class='header'><span>Deleting Collection Confirm</span></div>
										<div class='message'></div>
										<div class='buttons'>
											<div class='no simplemodal-close'>No</div>
											<div class='yes'>Yes</div>
										</div>
									</div>
									<@s.if test="%{permissionBean.deleteAllowed}">
										<a href="${base}/${deleteColLink}?collection.id=${collection.id}&collection.owner.id=${collection.owner.id}&viewType=${viewType}" class="confirm">&nbsp; &nbsp; &nbsp; Delete &nbsp; &nbsp; &nbsp;</a>&nbsp;&nbsp;
					 				</@s.if>
					 				<@s.if test="%{permissionBean.changePermAllowed}">
					 					<a href="${base}/${permissionLink}?collection.id=${collection.id}&collection.owner.id=${collection.owner.id}&viewType=${viewType}">&nbsp;Permissions&nbsp;</a>
					 				</@s.if>
					 				<@s.if test="%{mdRegEnabled}"> 
						 				<@s.if test="%{collection.owner.id == user.id}">
						 					<!-- modal window for register with ands -->
						 					<a href="${base}/${andsMdRegLink}?collection.id=${collection.id}&collection.owner.id=${collection.owner.id}&viewType=${viewType}" id="wait_modal" name='wait_modal' title="Public registration of the metadata associated with this collection with the Research Data Australia website">
						 						<@s.text name="ands.md.registration.title" />
						 					</a>
						 					<div id='mask'></div>
										    <div id='modal_window' >
										    	Calling Web Service, please wait ... <img src="${base}/images/wait_loader.gif" class="loading_image">
										    </div>        
						 				</@s.if>
					 				</@s.if>
					 			</div>
		 	 	 		 	</div>
		 	 	 		</td>
					</tr>
				 	<tr>
						<td></td>
					</tr>
				</table>
		 	</div>
			
		 	<!-- Import the dataset file -->
			<@s.if test="%{permissionBean.importAllowed}">
		 	<div class="blank_separator"></div>
		 	<div class="none_border_block2">
				<div class="p_title2"><b>Local File Import</b></div>
				<div id="ajaxfileupload">
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
			 </div>
			 <div class="single_border_block">
				<div id="fileuploadForm">
					<@s.form id="ajaxFileUploadForm" onsubmit="return false" action="importFile.jspx" namespace="/data" method="post" enctype="multipart/form-data" >
					<@s.hidden name="collection.id" id="col"/>
					<@s.hidden name="collection.owner.id" id="colowner" />
					<@s.hidden name="viewType" id="viewtype"/>
						<table align="center">
							<tr>
								<td align="left">Please select a file</td>
								<td align="left"><@s.file name="upload" id="upload" /></td>
							</tr>
							<tr>
								<td align="left">Extract the Metadata</td>
								<td align="left"><@s.checkbox name="extractable" id="extract" value="true"/></td>
							</tr>
						 	<tr>
						 		<td colspan="2"><@s.submit value="Import" id="fileUpload" name="fileUpload" onclick="merc.AjaxFileUpload.initialise(${dobefore}, doAfterImport);" cssClass="input_button_normal"/></td>
						 	</tr>
						 </table>
					</@s.form>
				</div>
		 	 </div>
		 	 <!-- End of import dataset file -->
		 	 <@s.if test="%{stageTransferEnabled}"> 
			 	 <div class="none_border_block2">
				 	 <div class="p_title2"><b>Staging Area File Transfer</b></div>
				 	 <br/>
				 	 <div class="stage_div"> 
				 	 	<@s.form id="stageTransfer"  action="viewStageFiles.jspx" namespace="/data" method="post" >
				 	 		<@s.hidden name="collection.id" id="col"/>
							<@s.hidden name="collection.name" id="coname"/>
							<@s.hidden name="collection.description" id="desc"/>
							<@s.hidden name="collection.owner.displayName" id="codisplayname" />
							<@s.hidden name="collection.owner.id" id="colowner" />
							<@s.hidden name="collection.createdTime" />
							<@s.hidden name="collection.modifiedTime" />
							<@s.hidden name="collection.modifiedByUser.displayName" />
							<@s.hidden name="viewType" id="viewtype"/>
							<table align="center">
								<tr>
									<td><@s.submit value="Transfer"  name="transfer" cssClass="input_button_normal"/></td>
								</tr>
							</table>	 
						</@s.form>		
				 	 </div>
			 	 </div>
			 </@s.if>
		 	 <div style="clear:both"></div>
		 	 </@s.if>
		 	<!-- end of allow to import file or stage transfer -->
		 	 <!-- display the datasets -->
		 	 <@s.if test = "%{datasets != null & datasets.size >0}">
			 	<div class="none_border_block2">
			 	<table class="table_data" >
			 		  <tr class="bg_grey_tr">
			 	 		<td width="200" height="20"><center><b>Name</b></center></td>
			 	 		<td width="100"><center><b>Site Name</b></center></td>
			 	 		<td width="60"><center><b> Level </b></center></td>
			 	 		<td width="160">&nbsp;</td>
			 	 	</tr>
			 	 	<@s.iterator status="dsState" value="datasets" id="ds" >
			 	 		<tr class="tr_normal" onMouseOver="this.className='tr_highlight'" onMouseOut="this.className='tr_normal'">
			 	 			<td><@s.property value="#ds.name" /></td>
			 	 			<td><@s.property value="#ds.siteName" /></td>
			 	 			<td><center><@s.property value="#ds.netCDFLevel" /></center></td>
			 	 			<td>
			 	 				<div class="inline_td_div"> 
			 	 					<div id='confirm-dialog'>
										<@s.if test="%{permissionBean.viewAllowed}">
											<@s.if test="%{#ds.extracted}">
					 	 						<a href="${base}/${viewDatasetLink}?dataset.id=${ds.id}&collection.id=${collection.id}&collection.owner.id=${collection.owner.id}&viewType=${viewType}" title="Dataset - ${ds.name}" id="viewdataset">View Data</a> &nbsp;
					 	   					</@s.if>
					 	   				</@s.if>
					 	   				<!--
					 	   				<a href="${base}/${viewDatasetLink}?dataset.id=${ds.id}&collection.id=${collection.id}&collection.owner.id=${collection.owner.id}&viewType=${viewType}" title="Dataset - ${ds.name}" class="greybox">View Data </a> &nbsp;
					 	   				-->
					 	   				<@s.if test="%{permissionBean.exportAllowed}">
					 	   					<a href="${base}/${downloadDatasetLink}?dataset.id=${ds.id}&collection.id=${collection.id}&collection.owner.id=${collection.owner.id}&viewType=${viewType}">&nbsp;&nbsp;Export&nbsp;&nbsp;</a> &nbsp;
					 	   				</@s.if>
					 	   				<@s.if test="%{permissionBean.deleteAllowed}">
					 	   				<div class="msg_content">The data will be removed from the repository permanently!<p>Are you sure to delet this dataset?</p></div>
										<div id='confirm'>
											<div class='header'><span>Deleting Dataset Confirm</span></div>
											<div class='message'></div>
											<div class='buttons'>
												<div class='no simplemodal-close'>No</div>
												<div class='yes'>Yes</div>
											</div>
										</div>
					 	   					<a href="${base}/${deleteDatasetLink}?dataset.id=${ds.id}&collection.id=${collection.id}&collection.owner.id=${collection.owner.id}&viewType=${viewType}" class='confirm'>&nbsp;&nbsp;Delete&nbsp;&nbsp;</a>
			 	 						</@s.if>
			 	 					</div>
			 	 				</div>
			 	 			</td>
			 	 		</tr>
			 	 	</@s.iterator>
			 	</table>		 	 	
			 	</div>
		 	 </@s.if>
		 	 <div style="clear:both"></div>
		 	 <br/>
             <br/>
             <br/>
             <@s.if test="%{permissionBean.viewAllowed == false}">
                <div class="none_border_space_block"></div>
             </@s.if>
		</div>
        <br/>
	</@s.else>
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
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>


