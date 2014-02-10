<table width="100%">
    <tr>
        <td width="20%" align="right">Researcher Name:</td>
        <td width="30%"><@s.textfield name="searchBean.researcherName" /></td>
        <td width="10%"></td>
        <td width="40%"></td>
    </tr>
    <tr>
        <td align="right">Collection Name:</td>
        <td><@s.textfield name="searchBean.collectionName" /></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td align="right">Created Date From:</td>
        <td><@sj.datepicker name="searchBean.startDate" id="startdate" displayFormat="yy-mm-dd"  buttonImageOnly="true" /></td>
        <td align="right">To:</td>
        <td><@sj.datepicker name="searchBean.endDate" id="enddate" displayFormat="yy-mm-dd"  buttonImageOnly="true" /></td>
    </tr>

    <tr>
        <td align="right">Site Name:</td>
        <td>
        <@s.textfield name="searchBean.siteName" />
        </td>
        <td></td>
        <td></td>
    </tr>

    <tr>
        <td align="right">Dataset Name:</td>
        <td>
        <@s.textfield name="searchBean.datasetName" />
        </td>
        <td></td>
        <td></td>
    </tr>

    <tr>
        <td align="right">Dataset Level:</td>
        <td align="left">
        <@s.select name="searchBean.datasetLevel" headerKey="L0" headerValue="-- Please Select --" list="dsLevels" cssClass="input_select_normal"  />
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td align="right">&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td align="right">&nbsp;</td>
        <td align="left">
        <@s.submit value="Search" cssClass="input_button_style" />
            &nbsp;  <@s.reset value="%{getText('reset.button')}" cssClass="input_button_style" />
        <td></td>
        <td></td>
    </tr>
</table>
