<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
 <#assign sj=JspTaglibs["/WEB-INF/struts-jquery-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.text name="mycollection.nav.label.name" /> - <@s.text name="create.new.collection" /></title>
<#include "../template/jquery_header.ftl"/>
<#include "../template/googlemap_header.ftl"/>
<script type="text/javascript">
    $("input[type=checkbox]").live('click',function(){
        if($(this).is(":checked")){
            //call map control javascript
            mctEmptyMap('mct_control_spatialcvg');
            //reset checkbox to true
            $(this).attr("checked", true);
        }
    });
</script>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
	<div class="div_inline">&nbsp;&nbsp;</div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline"><a href="${base}/data/listUserCollections.jspx"><@s.text name="mycollection.nav.label.name" /></a></div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline"><@s.text name="create.new.collection" /></div>		
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
				<@s.form action="createCollection.jspx" namespace="/data" method="post">
				 
		 		
		 		<table width="100%" class="collection_tab">
		 			<tr>
		 				<td align="left">
		 					<@s.text name="collection.name" />:
		 					<div class="name_comment">* (<@s.text name="collection.name.hint" />)</div>
		 				</td>
		 				<td></td>
		 			</tr>
		 			<tr>
		 				<td align="left"><@s.textfield name="collection.name" cssClass="input_field" /> </td>
		 				<td></td>
		 			</tr>

		 			<tr>
		 				<td align="left">
		 					<@s.text name="collection.temporal.from" />:
		 					<div class="name_comment">* (<@s.text name="collection.start.date.hint" />)</div>
		 				</td>
		 				<td></td>
		 			</tr>
		 			<tr>
		 				<td align="left">
							 <@sj.datepicker name="collection.dateFrom" id="startdate" displayFormat="yy-mm-dd"  buttonImageOnly="true" />
						</td>
						<td></td>
		 			</tr>
		 			<tr>
		 				<td align="left">
		 					<@s.text name="collection.temporal.to" />:
		 					<div class="name_comment">* (<@s.text name="collection.end.date.hint" />)</div>
		 				</td>
		 				<td></td>
		 			</tr>
		 			<tr>
		 				<td align="left">
							 <@sj.datepicker name="collection.dateTo" id="enddate" displayFormat="yy-mm-dd"  buttonImageOnly="true" />
							 <br/>
						</td>
						<td></td>
		 			</tr>
		 			<tr>
		 				<td align="left">
		 					<@s.text name="collection.desc" />:
		 					<div class="name_comment">* (<@s.text name="collection.desc.hint" />)</div>
		 				</td>
		 				<td></td>
		 			</tr>
		 			<tr>
		 				<td align="left">
							 <@s.textarea  name="collection.description" cssStyle="width: 560px; height: 190px;" cssClass="input_textarea" />
							 <br/>
						</td>
						<td></td>
		 			</tr>
		 			 
		 			<tr>
		 				<td align="left">
                            <br/>
                            Global Coverage: <@s.checkbox name="globalCoverage" id="global_coverage" />
                            <div class="name_comment">* (The global coverage, please select the tick box)</div>
		 					<br/>
		 					<@s.text name="collection.spatial.coverage"/>:
                            <div class="name_comment">* (<@s.text name="collection.spatial.coverage.hint" />)</div>
                            <div class="blank_separator"></div>
		 				</td>
		 				<td></td>
		 			</tr>

		 			<tr>
		 				<td align="left">
		 					<@s.textarea  id="spatialcvg" name="collection.location.spatialCoverage" cssStyle="width: 200px; height: 80px;" cssClass="input_textarea" readonly ="true" />
		 				</td>
		 				<td></td>
		 			</tr>
		 			<tr>
		 				<td align="left">
		 					<div class="name_comment">Choose a method for marking spatial coverage from the options in the grey bar above the map.</div>
		 				</td>
		 				<td></td>
		 			</tr>
		 			 
		 			<tr>
		 				<td>
		 					<script type="text/javascript">mctSetMapControl("spatialcvg");</script>
						</td>
		 				<td align="left"></td>
		 			</tr>
		 			
		 			<tr>
		 				<td>&nbsp;</td>
		 				<td align="left"></td>
		 			</tr>
		 			<tr>
						<td align="center"> 
							<@s.submit value="%{getText('data.create.button')}" cssClass="input_button_style" /> &nbsp; <@s.reset value="%{getText('reset.button')}" cssClass="input_button_style" />
						</td>
						<td></td>
					</tr>
		 		</table>
		 		 
				</@s.form>
			</div>
			<div class="none_border_block"></div>
		</div>	
		 <br/>
	</div>
	<div class="right_container_panel">
		<#include "../template/subnav_section.ftl" />
	</div>
	<br/>
	<div style="clear:both"></div>
</div>
</div>
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>