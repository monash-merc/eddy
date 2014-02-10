var polygonCreator;
var pointCreator;
var map;
var errorMessage = "";
$(document).ready(function() {
    // create map
    var melCenter = new google.maps.LatLng(-23.3533, 133.2057);
    var myOptions = {
        zoom : 3,
        center : melCenter,
        zoomControl : true,
        mapTypeId : google.maps.MapTypeId.ROADMAP
    }
    map = new google.maps.Map(document.getElementById('main_map'), myOptions);

    initialize(map);

});

function initialize(map) {
    var spcvg = $('#spatialcvg').val();
    if (spcvg != null) {
        var precoords = getCoordsFromCoverageStr(spcvg);
        if (precoords != null && precoords.length == 1) {
            $('#point_button').attr('class', 'button_hht');
            pointCreator = new PointCreator(map, precoords[0]);
        } else if (precoords != null && precoords.length > 1) {
            $('#polygon_button').attr('class', 'button_hht');
            polygonCreator = new PolygonCreator(map, precoords);
        } else {
            $('#point_button').attr('class', 'button_hht');
            $('#spatialtype').val('point');
            pointCreator = new PointCreator(map);
        }
    }
}

function getCoordsFromCoverageStr(longlatStr) {
    var coords = new Array();
    var lonlatText = normalizeCoverageStr(longlatStr);
    if (lonlatText != "") {
        var coordsStr = lonlatText.split(' ');
        for ( var i = 0; i < coordsStr.length; i++) {
            // Fill the array with LatLngs.
            coordsPair = coordsStr[i].split(",");
            coords[i] = new google.maps.LatLng(coordsPair[1], coordsPair[0]);
        }
    }
    return coords;
}

function normalizeCoverageStr(longlatStr) {
    var normalizedStr = longlatStr;

    if (normalizedStr != "") {
        // Remove white space from between latitude and longitude.
        normalizedStr = normalizedStr
            .replace(new RegExp('\\s*,\\s*', "g"), ',');
        // Convert all white space between pairs to spaces.
        normalizedStr = normalizedStr.replace(new RegExp('\\s+', "g"), ' ');
        // Remove any leading and/or trailing spaces.
        normalizedStr = normalizedStr.replace(new RegExp('^ '), '');
        normalizedStr = normalizedStr.replace(new RegExp(' $'), '');
    }
    return normalizedStr;
}

$('#point_button').live('click', function() {
    $(this).attr('class', 'button_hht');
    $('#polygon_button').attr('class', 'button_normal');
    cleanAll();
    $('#spatialtype').val('point');
    $('#spatialcvg').val(null);
    pointCreator = new PointCreator(map);
});

$('#polygon_button').live('click', function() {
    cleanAll();
    $(this).attr('class', 'button_hht');
    $('#point_button').attr('class', 'button_normal');
    $('#spatialtype').val('polygon');
    $('#spatialcvg').val(null);
    polygonCreator = new PolygonCreator(map);
});

$('#clear_button').live('click', function() {
    cleanAll();
    var type = $('#spatialtype').val();
    $('#spatialcvg').val(null);

    if (type == 'point') {
        pointCreator = new PointCreator(map);
    } else {
        polygonCreator = new PolygonCreator(map);
    }
});

function cleanAll() {
    if (pointCreator != null) {
        pointCreator.destroy();
        pointCreator = null;
    }

    if (polygonCreator != null) {
        polygonCreator.destroy();
        polygonCreator = null;
    }
};
function setErrorMessage(message)
{
    errorMessage = message;
}

function validateLonLat(lonlatText) {
    var valid = true;
    if (lonlatText != "") {
        var coords = lonlatText.split(' ');
        var lat = null;
        var lon = null;
        var coordsPair = null;

        // Test for a two point line.
        if (coords.length == 2) {
            setErrorMessage("The coordinates don't represent a point or a region.");
            valid = false;
        }

        for ( var i = 0; i < coords.length && valid; i++) {
            // Get the lat and lon.
            coordsPair = coords[i].split(",");
            lat = coordsPair[1];
            lon = coordsPair[0];

            // Test for numbers.
            if (isNaN(lat) || isNaN(lon)) {
                setErrorMessage('Some coordinates are not numbers.');
                valid = false;
                break;
            }
            // Test the limits.
            if (Math.abs(lat) > 90 || Math.abs(lon) > 180) {
                setErrorMessage('Some coordinates have invalid values.');
                valid = false;
                break;
            }

            // Test for an open region.
            if (i == coords.length - 1) {
                if (coords[0] != coords[i]) {
                    setErrorMessage("The coordinates don't represent a point or a region. To define a region the last point needs to be the same as the first.");
                    valid = false;
                }
            }
        }
    }
    return valid;
}
