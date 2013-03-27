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
            <subject type="gcmd">Earth Science</subject>
            <subject type="gcmd">Atmosphere</subject>
            <subject type="gcmd">Land Surface</subject>
            <subject type="gcmd">Atmospheric Pressure</subject>
            <subject type="gcmd">Atmospheric Radiation</subject>
            <subject type="gcmd">Atmospheric Temperature</subject>
            <subject type="gcmd">Atmospheric Water Vapour</subject>
            <subject type="gcmd">Atmospheric Winds</subject>
            <subject type="gcmd">Precipitation</subject>
            <subject type="gcmd">Soils</subject>
            <subject type="gcmd">Surface Radiative Properties</subject>
            <subject type="gcmd">Surface pressure</subject>
            <subject type="gcmd">Surface air temperature</subject>
            <subject type="gcmd">Humidity</subject>
            <subject type="gcmd">Evapotranspiration</subject>
            <subject type="gcmd">Surface winds</subject>
            <subject type="gcmd">Turbulence</subject>
            <subject type="gcmd">Wind stress</subject>
            <subject type="gcmd">Rain</subject>
            <subject type="gcmd">Soil moisture</subject>
            <subject type="gcmd">water content</subject>
            <subject type="gcmd">Soil temperature</subject>
            <subject type="gcmd">Albedo</subject>
            <subject type="gcmd">Eddy correlation devices</subject>
            <subject type="gcmd">Sonic anemometers</subject>
            <subject type="gcmd">Wind vanes</subject>
            <subject type="gcmd">Rain gauges</subject>
            <subject type="gcmd">Barometers</subject>
            <subject type="gcmd">Net radiometers</subject>
            <subject type="gcmd">Humidity sensors</subject>
            <subject type="gcmd">Temperature sensors</subject>
            <subject type="gcmd">In Situ Land-based Platforms</subject>
            <subject type="gcmd">Fixed Observation Stations</subject>
            <subject type="gcmd">Ground-based observations</subject>
            <subject type="gcmd">Weather stations</subject>
            <subject type="gcmd">Continent</subject>
            <subject type="gcmd">Geographic Region</subject>
            <subject type="gcmd">Vertical Location</subject>
            <subject type="gcmd">Southern Hemisphere</subject>
            <subject type="gcmd">Western Hemisphere</subject>
            <subject type="gcmd">Government Agencies-Non-US</subject>
            <subject type="gcmd">Marine and Atmospheric Research</subject>
            <subject type="anzsrc-toa">Strategic basic research</subject>
            <subject type="anzsrc-toa">Applied research</subject>
            <subject type="anzsrc-for">0401</subject>
            <subject type="anzsrc-for">0406</subject>
            <subject type="anzsrc-for">0602</subject>
            <subject type="anzsrc-seo">9602</subject>
            <subject type="anzsrc-seo">9603</subject>
            <subject type="anzsrc-seo">9605</subject>
        <#if partyDesc??>
            <description type="full">
            ${partyDesc}
            </description>
        </#if>
        </party>
    </registryObject>
</registryObjects>
