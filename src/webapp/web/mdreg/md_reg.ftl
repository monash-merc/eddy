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
		<br/>
		<@s.form action="preRegMd.jspx" namespace="/data" method="post" id="mdRegForm">	
		<div class="single_border_block">
			<@s.hidden name="collection.id" id="col"/>
			<@s.hidden name="collection.name" id="coname"/>
			<@s.hidden name="collection.description" id="desc"/>
			<@s.hidden name="collection.owner.displayName" id="codisplayname" />
			<@s.hidden name="collection.owner.id" id="colowner" />
			<@s.hidden name="collection.createdTime" />
			<@s.hidden name="collection.modifiedTime" />
			<@s.hidden name="collection.modifiedByUser.displayName" />
			<@s.hidden name="viewType" id="viewtype"/>
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
					<tr><td>&nbsp;</td></tr>
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
			<div class="blank_separator"></div>
			<div class="bottom_sborder_block">
				Public registration of the following metadata associated with this collection with the Research Data Australia website
				<div class="blank_separator"></div>
			</div>
			
			<div class="none_border_block2">
				<table class="table_nobd">
					<tr>
						<td width="300">
							<div class="grey_div_block">Please select the associated researcher(s)</div>
						</td>
						<td align="left">
							<div class="links_div">
						 		 <a href="${base}/data/addPartyOptions.jspx" title="Adding another researcher" id="addtionalParty">Add Researcher</a> &nbsp;	
						 	</div>
					 	</td>
					</tr>
				</table>
			</div>
			<div class="none_border_block2">
				<table class="ands_reg_tab_data" id="ands_reg_tab_data" >
					<@s.iterator status="ptState" value="partyList" id="party" >
					<tr>
						<td align="center" width="50">
							<@s.checkbox name="partyList[${ptState.index}].selected" />
						</td> 
						<td>
							<div class="ands_blue">
								<@s.property value="#party.personTitle" /> <@s.property value="#party.personGivenName" /> <@s.property value="#party.personFamilyName" /> - <@s.property value="#party.groupName" />
								<@s.hidden name="partyList[${ptState.index}].partyKey" />
							    <@s.hidden name="partyList[${ptState.index}].personTitle" />
							    <@s.hidden name="partyList[${ptState.index}].personGivenName" />
							    <@s.hidden name="partyList[${ptState.index}].personFamilyName" />
							    <@s.hidden name="partyList[${ptState.index}].email" />
							    <@s.hidden name="partyList[${ptState.index}].address" />
							    <@s.hidden name="partyList[${ptState.index}].url" />
							    <@s.hidden name="partyList[${ptState.index}].identifierType"  />
							    <@s.hidden name="partyList[${ptState.index}].identifierValue" />
							    <@s.hidden name="partyList[${ptState.index}].originateSourceType" />
							    <@s.hidden name="partyList[${ptState.index}].originateSourceValue" />
							    <@s.hidden name="partyList[${ptState.index}].groupName" />
							    <@s.hidden name="partyList[${ptState.index}].fromRm" />
							</div>
						</td>
					</tr>
					</@s.iterator>
				</table>
			</div>
			
			
			<@s.if test="%{partyList == null || partyList.size == 0}">
			<div class="single_border_block">
				<div class="yellow_div_block">
					The associated researcher(s) not found
				</div>
				<div style="clear:both"></div>
			</div>
			</@s.if>
			
			<div class="none_border_block2">
				<table class="table_nobd">
					<tr>
						<td width="300">
							<div class="grey_div_block">Please select the associated grant(s) or project(s)</div>
						</td>
						<td align="left">
					 	</td>
					</tr>
				</table>
			</div>
			
			<div class="none_border_block2">
				<table class="ands_reg_tab_data" >
				<@s.iterator status="pState" value="projectList" id="proj" >
					<tr>
						<td align="center" width="50">
							<@s.checkbox name="projectList[${pState.index}].selected" /> 
							<@s.hidden name="projectList[${pState.index}].activityKey" />
						</td>
						<td>
							<div class="ands_blue">
								<@s.property value="#proj.title" />
								<@s.hidden name="projectList[${pState.index}].title"  />	
							</div>
							<div class="ands_gray">
								Grant Code: <@s.property value="#proj.grantCode" />
								<@s.hidden name="projectList[${pState.index}].grantCode"  />	
							</div> 
							<div class="ands_green">
								Project Date Applied: <@s.property value="#proj.appliedDate" />
								<@s.hidden name="projectList[${pState.index}].appliedDate"  />
							</div>
						</td>
					</tr>
				</@s.iterator>
				</table>
			</div>
			
			<@s.if test="%{projectList == null || projectList.size == 0}">
				<div class="single_border_block">
					<div class="yellow_div_block">
						The associated grant(s) or project(s) not found
					</div>
					<div style="clear:both"></div>
				</div>
			</@s.if>
			<div class="blank_separator"></div>
			<div class="none_border_block2">
				<table class="table_nobd">
					<tr>
						<td width="300">
							<div class="grey_div_block">Please select the collection License</div>
						</td>
						<td align="left">
							<div class="links_div">
						 		 <a href="${base}/data/rightsOptions.jspx?collection.id=${collection.id}" title="Select License" id="selectRights">Select License</a> &nbsp;	
						 	</div>
					 	</td>
					</tr>
					<tr>
						<td colspan="2">
							<@s.hidden name="rights.id" id="frights_id"/>
							<@s.hidden name="rights.rightsType" id="frights_type"/>
							<@s.hidden name="rights.commercial" id="frights_comm"/>
							<@s.hidden name="rights.derivatives" id="frights_deri"/>
							<@s.hidden name="rights.jurisdiction" id="frights_juri"/>
							<@s.hidden name="rights.rightContents" id="frights_cont" />
						</td>
					</tr>
					<tr>
					 	<td colspan="2">
					 		<div class="mcpop_justify">
						 		<div id="display_rights">
						 			<@s.property value="rights.rightContents" />
						 		</div>
						 	</div>
					 	</td>
					</tr>
				</table>
			</div>
			<div class="none_border_block2">
				<table class="table_nobd">
					<tr>
						<td width="300">
							<div class="grey_div_block">Access rights</div>
						</td>
						<td align="left">
					 	</td>
					</tr>
					<tr>
						<td colspan="2">
							<@s.hidden name="accessRights" />
						</td>
					</tr>
					<tr>
					 	<td colspan="2">
					 		<div class="mcpop_justify">
						 		<div id="display_rights">
						 			<@s.property value="accessRights" />
						 		</div>
						 	</div>
					 		<br/>
					 	</td>
					</tr>
				</table>
				<br/>
			</div>
		
			<div class="none_border_block2">
				<@s.submit value="Preview Metadata"  name="preview" cssClass="silver_b_input" id="reg_preview"/>
			</div>
		
			</@s.form>
			<br/>
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
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>

