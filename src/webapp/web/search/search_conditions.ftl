<table width="100%" >

    <tr>
        <td width="150" align="right">Researcher Name: </td>
        <td align="left"><@s.textfield name="searchBean.researcherName" cssClass="input_field" /></td>
        <td></td><td></td>
    </tr>
    <tr>
        <td width="150" align="right">Collection Name: </td>
        <td align="left"><@s.textfield name="searchBean.collectionName" cssClass="input_field" /></td>
        <td></td><td></td>
    </tr>
    <tr>
        <td width="150" align="right">Created Date From: </td>
        <td align="left"><@sj.datepicker name="searchBean.startDate" id="startdate" displayFormat="yy-mm-dd"  buttonImageOnly="true" /></td>
        <td width="30"  align="right">To:</td><td><@sj.datepicker name="searchBean.endDate" id="enddate" displayFormat="yy-mm-dd"  buttonImageOnly="true" /></td>
    </tr>

    <tr>
        <td  align="right">Site Name: </td>
        <td align="left">
        <@s.textfield name="searchBean.siteName" cssClass="input_field" />
        </td>
        </td><td></td><td></td>
    </tr>

    <tr>
        <td  align="right">Dataset Name: </td>
        <td align="left">
        <@s.textfield name="searchBean.datasetName" cssClass="input_field" />
        </td>
        </td><td></td><td></td>
    </tr>

</table>
<!-- div class="search_border_block"></div -->
<div class="blank_separator"></div>
<div style="clear:both"></div>
<table>
    <tr>
        <td colspan="4">
            &nbsp;
        <td>
    </tr>
    <tr>
        <td width="100"></td>
        <td align="center">
        <@s.submit value="Search" cssClass="input_button_style" />
        </td>
        <td align="center">
        <@s.reset value="%{getText('reset.button')}" cssClass="input_button_style" />
        </td>
        <td></td>
    </tr>
</table>