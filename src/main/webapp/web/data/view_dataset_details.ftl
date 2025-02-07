<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<div class="popup_main_div">
	<table class="dataset_md_tab">
		<tr class="dataset_md_title">
			<td colspan="2" height="20"><b>Global Attributes</b></td>
		</tr>
		<tr class="dataset_md_var">
			<td height="20"><b>Name</b></td>
			<td><b> Value </b></td>
		</tr>
		<@s.iterator status="dsgMeta" value="dataset.globalMetadata" id="gmata" >
		<tr>
			<td><@s.property value="#gmata.name" /></td>
			<td><@s.property value="#gmata.value" /></td>
		</tr>
		</@s.iterator>
		<tr class="dataset_md_title">
			<td colspan="2" height="20"><b>Variables</b></td>
		</tr>
		 
		<@s.iterator status="vMeta" value="dataset.metaVariables" id="vdata" >
			<tr class="dataset_md_var">
				<td><@s.property value="#vdata.dataType" /></td><td><@s.property value="#vdata.nameDimensions" /></td>
			</tr>
			<@s.iterator status="vMetaAttr" value="#vdata.metaAttributes" id="metaAttr" >
				<tr class="dataset_md_attr">
					<td align="right"><@s.property value="#metaAttr.name" /> = </td>
					<td align="left"><@s.property value="#metaAttr.value" /></td>
				</tr>
			</@s.iterator>
		</@s.iterator>
	</table>
</div>
<br/>
</body>
</html>


