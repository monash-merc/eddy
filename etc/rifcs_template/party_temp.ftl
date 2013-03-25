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
            <subject type="gcmd">Earth Science, Atmosphere, Land Surface, Atmospheric Pressure, Atmospheric Radiation, Atmospheric Temperature, Atmospheric Water Vapour, Atmospheric Winds</subject>
            <subject type="gcmd">Eddy correlation devices, Sonic anemometers, Wind vanes, Rain gauges, Barometers, Net radiometers, Humidity sensors, Temperature sensors</subject>
            <subject type="gcmd">In Situ Land-based Platforms, Fixed Observation Stations, Ground-based observations,Weather stations, Networks</subject>
            <subject type="gcmd">Continent, Geographic Region, Vertical Location, Southern Hemisphere, Western Hemisphere, Australia, New Zealand</subject>
            <subject type="gcmd">Government Agencies-Non-US, Marine and Atmospheric Research, CSIRO, CMAR</subject>
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
