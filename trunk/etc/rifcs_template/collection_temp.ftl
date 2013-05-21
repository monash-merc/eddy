<?xml version="1.0"?>
<registryObjects xmlns="http://ands.org.au/standards/rif-cs/registryObjects"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/documentation/rifcs/1.2.0/schema/registryObjects.xsd">
    <registryObject group="OzFlux: Australian and New Zealand Flux Research and Monitoring">
        <key>${keyId}</key>
        <originatingSource>${originatingSrc}</originatingSource>
        <collection type="collection">
            <identifier type="local">${localKey}</identifier>
        <#if handleId??>
            <identifier type="handle">${handleId}</identifier>
        </#if>
            <name type="primary">
                <namePart>${collectionName}</namePart>
            </name>
            <location>
                <address>
                    <electronic type="url">
                        <value>${collectionUrl}</value>
                    </electronic>
                </address>
            </location>
            <location>
                <address>
                    <physical type="postalAddress">
                        <addressPart type="text">CSIRO Marine and Atmospheric Research, Pye Laboratory, Clunies Ross Street, ACTON, ACT 2600</addressPart>
                    </physical>
                </address>
            </location>
            <coverage>
            <#if location??>
                <spatial type="${location.spatialType}">${location.spatialCoverage}</spatial>
            </#if>
            <#if temporalDateFrom?? && temporalDateTo??>
                <temporal>
                    <date type="dateFrom" dateFormat="W3CDTF">${temporalDateFrom}</date>
                    <date type="dateTo" dateFormat="W3CDTF">${temporalDateTo}</date>
                </temporal>
            </#if>
            </coverage>
        <#list parties as party>
            <relatedObject>
                <key>${party.partyKey}</key>
                <relation type="isOwnedBy"/>
            </relatedObject>
        </#list>
            <relatedObject>
                <key>MON399d1cdc-a788-4a05-9f01-d3dcbcafdf8d</key>
                <relation type="isOutputOf"/>
            </relatedObject>
            <subject type="gcmd">Earth Science - Atmosphere - Atmospheric Pressure</subject>
            <subject type="gcmd">Earth Science - Atmosphere - Atmospheric Pressure</subject>
            <subject type="gcmd">Earth Science - Atmosphere - Atmospheric Temperature</subject>
            <subject type="gcmd">Earth Science - Atmosphere - Atmospheric Water Vapour</subject>
            <subject type="gcmd">Earth Science - Atmosphere - Atmospheric Winds</subject>
            <subject type="gcmd">Earth Science - Atmosphere - Precipitation</subject>
            <subject type="gcmd">Earth Science - Land Surface - Soils</subject>
            <subject type="gcmd">Earth Science - Land Surface - Surface Radiative Properties</subject>
            <subject type="anzsrc-for">0401</subject>
            <subject type="anzsrc-for">0406</subject>
            <subject type="anzsrc-for">0501</subject>
            <subject type="anzsrc-for">0503</subject>
            <subject type="anzsrc-for">0602</subject>
            <description type="full" xml:lang="en">
            ${collectionDesc}
            </description>
            <description type="rights" xml:lang="en">
            <#if tern>
                &lt;p&gt;
                    TERN Attribution-Share Alike-Non Commercial (TERN-BY-SA-NC) Data Licence v1.0
                &lt;/p&gt;

                &lt;p&gt;
                    This is a summary of the TERN Attribution-Share ALike-Non Commercial (TERN-BY-SA-NC) Data Licence v1.0 (http://www.tern.org.au/datalicence/TERN-BY-SA-NC/1.0).
                &lt;/p&gt;

                &lt;p&gt;
                    The TERN Attribution-Share Alike- Non Commercial (TERN BY-SA-NC) Data Licence v1.0 restricts the development of new data/products, so that a user can:
                &lt;/p&gt;

                &lt;ul&gt;
                    &lt;li&gt;copy, re-use, share and distribute the copies of the data &lt;/li&gt;
                    &lt;li&gt;not use the data for commercial purposes &lt;/li&gt;
                &lt;/ul&gt;

                &lt;p&gt;
                    provided that, whenever the data is copied, re-used, or distributed the user ensures that:
                &lt;/p&gt;

                &lt;ul&gt;
                    &lt;li&gt;credit is given to the original sources/s of the data (and any other nominated parties) in the manner stipulated (Attribution); &lt;/li&gt;
                    &lt;li&gt;the data cannot be used for commercial purposes (No Commercial); and &lt;/li&gt;
                    &lt;li&gt;If the data is altered, transformed, the resulting datasets can only be used under the same license conditions. &lt;/li&gt;
                &lt;/ul&gt;
            <#else>
            ${licenceContents}
            </#if>
            </description>
            <citationInfo>
                <fullCitation style="Harvard">
                ${creator} (${publicationYear}): ${collectionName}. ${publisher}. ${citationIdentifier}
                </fullCitation>
            </citationInfo>
        </collection>
    </registryObject>
</registryObjects>