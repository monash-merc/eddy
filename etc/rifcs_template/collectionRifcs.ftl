<?xml version="1.0" encoding="utf-8"?>
<registryObjects xmlns="http://ands.org.au/standards/rif-cs/registryObjects"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/documentation/rifcs/1.2.0/schema/registryObjects.xsd">
    <registryObject group="somegroup">
        <key>identifier</key>
        <originatingSource>http://ozflux.its.monash.edu.au</originatingSource>
        <collection type="dataset">
            <identifier type="local">${Localkey}</identifier>
            <name type="primary">
                <namePart type="">
                CollectionName
                </namePart>
            </name>
            <location>
                <address>
                    <electronic type="url">
                        <value>url</value>
                    </electronic>
                </address>
            </location>
            <#if parentP??>
                <relatedObject>
                    <key>parent_party_key_${parentP}</key>
                    <relation type="isAssociatedWith"/>
                </relatedObject>
            <#else>
                <relatedObject>
                    <key>no parent</key>
                    <relation type="isAssociatedWith"/>
                </relatedObject>
            </#if>

            <#list parties as party>
                <relatedObject>
                    <key>${party.partyKey}</key>
                    <relation type="isManagedBy"/>
                </relatedObject>
            </#list>
            <relatedObject>
                <key>MONebcb3d9b-324f-47d5-9591-a6b1414823f2</key>
                <relation type="isProducedBy"/>
            </relatedObject>
            <description type="full" xml:lang="en">
            desc
            </description>
            <description type="rights" xml:lang="en">
                here is a licence
            </description>
            <subject type="anzsrc-for">060109</subject>
            <subject type="anzsrc-for">110106</subject>
        </collection>
    </registryObject>
</registryObjects>
