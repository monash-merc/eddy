<#if (actionErrors?? && actionErrors?size > 0)>
<div class="error_msg_section">
    <div class="actionError">
        <ul<#rt/>
            <#if parameters.id?if_exists != "">
                    id="${parameters.id?html}"<#rt/>
            </#if>
            <#if parameters.cssClass??>
                    class="${parameters.cssClass?html}"<#rt/>
            <#else>
                    class="errorMessage"<#rt/>
            </#if>
            <#if parameters.cssStyle??>
                    style="${parameters.cssStyle?html}"<#rt/>
            </#if>
                >
            <#list actionErrors as error>
                <#if error?if_exists != "">
                    <li><span><#if parameters.escape>${error!?html}<#else>${error!}</#if></span><#rt/></li><#rt/>
                </#if>
            </#list>
        </ul>
    </div>
</div>
</#if>