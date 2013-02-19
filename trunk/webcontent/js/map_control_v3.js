var map;
var errorMessage = "";


$(document).ready(function () {
    // create map
    var melCenter = new google.maps.LatLng(-28.071980, 147.480469);
    var myOptions = {
        zoom:3,
        center:melCenter,
        panControl:true,
        zoomControl:true,
        zoomControlOptions:{
            style:google.maps.ZoomControlStyle.SMALL
        },
        mapTypeControl:true,
        scaleControl:true,
        rotateControl:true,
        mapTypeId:google.maps.MapTypeId.ROADMAP
    }
    map = new google.maps.Map(document.getElementById('map_view'), myOptions);
    //ajax call to get all map locations for collections
    getMapLocations();
});

function getMapLocations() {
    $.ajax({
            type:"get",
            url:'../mapview/viewLocations.jspx',
            cache:false,
            contentType:'application/json; charset=utf-8',
            dataType:'json',
            success:function (respdata) {
                var ok = respdata.succeed;
                if (ok) {
                    var locations = respdata.mapLocations;
                    displayMapLocations(locations)
                } else {
                    alert("error.");
                }
            },
            error:function (request, exception) {
                var errormsg = getErrorMsg(request, exception);
                alert(errormsg)
            }
        }
    )
}

function displayMapLocations(mapData) {
    if (mapData != null) {
        var mapLocations = new Array();
        $.each(mapData, function (key, coverage) {
            mapLocations[key] = coverage.spatialCoverage;
            var location = getCoordsFromCoverageStr(mapLocations[key]);
            if (location != null && location.length == 1) {
                var pointCreator = new PointCreator(map, location[0]);
            }
            if (location != null && location.length > 1) {
                //disabled the polygon
                // var polygonCreator = new PolygonCreator(map, location);
            }
        });
        alert("size: " + mapLocations.length);
        if (mapLocations != null && mapLocations.length == 0) {
            var pointCreator = new PointCreator(map);
        }
    } else {
        var pointCreator = new PointCreator(map);
    }
}


//initialize the map points
//function initialize(map, data) {
//    if (data != null) {
//        for (var i = 0; i < data.length; i++) {
//            var location = getCoordsFromCoverageStr(data[i]);
//            if (location != null && location.length == 1) {
//                var pointCreator = new PointCreator(map, location[0]);
//            }
//            if (location != null && location.length > 1) {
//                // var polygonCreator = new PolygonCreator(map, location);
//            }
//        }
//    } else {
//        var pointCreator = new PointCreator(map);
//    }
//}

function getCoordsFromCoverageStr(longlatStr) {
    var coords = new Array();
    var lonlatText = normalizeCoverageStr(longlatStr);
    if (lonlatText != "") {
        var coordsStr = lonlatText.split(' ');
        for (var i = 0; i < coordsStr.length; i++) {
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


$('#point_button').live('click', function () {
    $(this).attr('class', 'button_hht');
    $('#polygon_button').attr('class', 'button_normal');
    cleanAll();
    $('#spatialtype').val('point');
    $('#spatialcvg').val(null);
    pointCreator = new PointCreator(map);
});

$('#polygon_button').live('click', function () {
    cleanAll();
    $(this).attr('class', 'button_hht');
    $('#point_button').attr('class', 'button_normal');
    $('#spatialtype').val('polygon');
    $('#spatialcvg').val(null);
    polygonCreator = new PolygonCreator(map);
});

$('#clear_button').live('click', function () {
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
}
;
function setErrorMessage(message) {
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

        for (var i = 0; i < coords.length && valid; i++) {
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
