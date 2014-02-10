<#if (actionMessages?? && actionMessages?size > 0 && !parameters.isEmptyList)>
<div class="action_msg_section"><div<#rt/>
    <#if parameters.id?if_exists != "">
            id="${parameters.id?html}"<#rt/>
    </#if>
    <#if parameters.cssClass??>
            class="${parameters.cssClass?html}"<#rt/>
    <#else>
            class="actionMessage"<#rt/>
    </#if>
    <#if parameters.cssStyle??>
            style="${parameters.cssStyle?html}"<#rt/>
    </#if>
        >
    <#list actionMessages as message>
        <#if message?if_exists != "">
            <span><#if parameters.escape>${message!?html}<#else>${message!}</#if></span><br/>
        </#if>
    </#list>
</div></div>
</#if>