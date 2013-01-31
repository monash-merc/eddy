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
			<div class="none_border_block2">
                <div class="p_title3">Public registration of the following metadata associated with this collection with the Research Data Australia website</div>
            </div>
			<@s.form action="mdReg.jspx" namespace="/data" method="post">
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
								Created date: <@s.date name="collection.createdTime" format="yyyy-MM-dd hh:mm" /> &nbsp;&nbsp;&nbsp;&nbsp;
								Modified by <@s.property value="collection.modifiedByUser.displayName" />, &nbsp;&nbsp;&nbsp;&nbsp; 
								Modified date: <@s.date name="collection.modifiedTime" format="yyyy-MM-dd hh:mm" />
							</span>
						</td>
					</tr>
					<tr><td>&nbsp;</td></tr>
					<tr>
						<td>
				 	 		<div class="inline_td_div"> 
				 	 			<a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='collection.id' />&collection.owner.id=<@s.property value='collection.owner.id' />&viewType=${viewType}">&nbsp; View details &nbsp;</a>
				 	 	 	</div>
				 	 	</td>
					</tr>
					<tr>
						<td></td>
					</tr>
				</table>
			</div>
			<div class="blank_separator"></div>
			<div class="none_border_block2">
				<table class="ands_reg_tab_data" >
					<tr>
						<td align="left" width="150">
							<div class="ands_blue">
								Address: 
							</div>
						</td> 
						<td>
							<div class="ands_blue">
								${physicalAddress}
							</div>
						</td>
					</tr>
					<tr>
						<td align="left">
							<div class="ands_blue">
								Field of research (ANZSRC):
							</div> 
						</td> 
						<td>
							<div class="ands_blue">
								${anzSrcCode}
							</div>
						</td>
					</tr>
					<@s.if test="%{projectList == null || projectList.size == 0}">
					<tr>
						<td align="left">
							<div class="ands_blue">
								Output of:
							</div> 
						</td> 
						<td>
							<div class="ands_blue">
								Not Provided
							</div>
						</td>
					</tr>
					</@s.if>
					<@s.else>
						<@s.iterator status="pState" value="projectList" id="proj" >
						<tr>
							<td align="left">
								<div class="ands_blue">
									Output of:
								</div> 
							</td> 
							<td>
								<div class="ands_blue">
									<@s.property value="#proj.title" />
								</div>
								<@s.hidden name="projectList[${pState.index}].activityKey" />
								<@s.hidden name="projectList[${pState.index}].title" />
								<@s.hidden name="projectList[${pState.index}].grantCode" />
								<@s.hidden name="projectList[${pState.index}].appliedDate"  />
							</td>
						</tr>
						</@s.iterator>
					</@s.else>
					<@s.iterator status="ptState" value="partyList" id="party" >
					<tr>
						<td align="left">
							<div class="ands_blue">
								Managed by:
							</div> 
						</td> 
						<td>
							<div class="ands_blue">
								<@s.property value="#party.personTitle" /> <@s.property value="#party.personGivenName" /> ( <@s.property value="#party.groupName" /> - <@s.property value="#party.email" /> )
							</div>
							<@s.hidden name="partyList[${ptState.index}].partyKey" />
						    <@s.hidden name="partyList[${ptState.index}].personTitle" />
						    <@s.hidden name="partyList[${ptState.index}].personGivenName" />
						    <@s.hidden name="partyList[${ptState.index}].personFamilyName" />
						    <@s.hidden name="partyList[${ptState.index}].email" />
						    <@s.hidden name="partyList[${ptState.index}].address" />
						    <@s.hidden name="partyList[${ptState.index}].url" />
						    <@s.hidden name="partyList[${ptState.index}].identifierType" />
						    <@s.hidden name="partyList[${ptState.index}].identifierValue" />
						    <@s.hidden name="partyList[${ptState.index}].originateSourceType" />
						    <@s.hidden name="partyList[${ptState.index}].originateSourceValue" />
						    <@s.hidden name="partyList[${ptState.index}].groupName" />
						    <@s.hidden name="partyList[${ptState.index}].fromRm" />
						</td>
					</tr>
					</@s.iterator>
					<tr>
						<td align="left">
							<div class="ands_blue">
								License:
							</div> 
						</td> 
						<td>
							<div class="ands_blue">
								<@s.property value="rights.rightContents" />
							</div>
							<@s.hidden name="rights.id" id="frights_id"/>
							<@s.hidden name="rights.rightsType" id="frights_type"/>
							<@s.hidden name="rights.commercial" id="frights_comm"/>
							<@s.hidden name="rights.derivatives" id="frights_deri"/>
							<@s.hidden name="rights.jurisdiction" id="frights_juri"/>
							<@s.hidden name="rights.rightContents" id="frights_cont" />
						</td>
					</tr>
					<tr>
						<td align="left">
							<div class="ands_blue">
								Access rights:
							</div> 
						</td> 
						<td>
							<div class="ands_blue">
								<@s.property value="accessRights" />
							</div>
							 <@s.hidden name="accessRights" />
						</td>
					</tr>
				</table>
			</div>
			<div class="blank_separator"></div>

			<div class="none_border_block2">
				<@s.submit value="Register" id="wait_modal" name='wait_modal' cssClass="silver_b_input"/>
                <div id='mask'></div>
                <div id='modal_window' >
                    Registering the metadata, please wait ... <img src="${base}/images/wait_loader.gif" class="loading_image">
                </div>
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
<#include "../template/footer.ftl"/>
</body>
</html>


