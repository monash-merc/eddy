<?xml version="1.0"?>
<registryObjects xmlns="http://ands.org.au/standards/rif-cs/registryObjects"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/documentation/rifcs/1.2.0/schema/registryObjects.xsd">
    <registryObject group="${groupName}">
        <key>${localKey}</key>
        <originatingSource>${originatingSrc}</originatingSource>
        <party type="person" dateModified="${dateModified}">
            <identifier type="local">${identifierKey}</identifier>
            <name type="primary">
                <namePart type="title">${personTitle}</namePart>
                <namePart type="given">${givenName}</namePart>
                <namePart type="family">${familyName}</namePart>
            </name>
            <location>
                <address>
                    <electronic type="url">
                        <value>${webSite}</value>
                    </electronic>
                    <electronic type="email">
                        <value>${emailAddress}</value>
                    </electronic>
                </address>
            </location>
            <relatedObject>
                <key>MON399d1cdc-a788-4a05-9f01-d3dcbcafdf8d</key>
                <relation type="isParticipantIn"/>
            </relatedObject>
            <subject type="gcmd">Strategic basic research</subject>
            <subject type="anzsrc-toa">Strategic basic research</subject>
            <subject type="anzsrc-toa">Applied research</subject>
            <subject type="anzsrc-for">0401</subject>
            <subject type="anzsrc-for">0406</subject>
            <subject type="anzsrc-for">0501</subject>
            <subject type="anzsrc-for">0503</subject>
            <subject type="anzsrc-for">0602</subject>
            <subject type="anzsrc-seo">8201</subject>
            <subject type="anzsrc-seo">8298</subject>
            <subject type="anzsrc-seo">9602</subject>
            <subject type="anzsrc-seo">9603</subject>
            <subject type="anzsrc-seo">9605</subject>
            <subject type="anzsrc-seo">9613</subject>
        <#if partyDesc??>
            <description type="full">
            ${partyDesc}
            </description>
        </#if>
        </party>
    </registryObject>
</registryObjects>
