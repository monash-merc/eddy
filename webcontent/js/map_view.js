var map = null;
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
    //create a map
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
                    displayMapLocations(locations);
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
                var mapPoint = new MapPoint(location[0], map);
            }
            if (location != null && location.length > 1) {
                //disabled the polygon
                // var polygonCreator = new PolygonCreator(map, location);
            }
        });
    }
}

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
        normalizedStr = normalizedStr.replace(new RegExp('\\s*,\\s*', "g"), ',');
        // Convert all white space between pairs to spaces.
        normalizedStr = normalizedStr.replace(new RegExp('\\s+', "g"), ' ');
        // Remove any leading and/or trailing spaces.
        normalizedStr = normalizedStr.replace(new RegExp('^ '), '');
        normalizedStr = normalizedStr.replace(new RegExp(' $'), '');
    }
    return normalizedStr;
}

function MapPoint(latLng, map) {
    // property
    this.latLng = latLng;
    this.markerObj = new google.maps.Marker({
        position:this.latLng,
        map:map
    });

    var thisPoint = this;
    var infowindow = new google.maps.InfoWindow();
    this.addListener = function () {
        var thisMarker = this.markerObj;
        google.maps.event.addListener(thisMarker, 'click', function () {

            alert(" you click the site for: " + thisPoint.getData());

        });
//        google.maps.event.addListener(thisMarker, 'mouseover', function () {
//            infowindow.setContent("The site for :  " + thisPoint.getData());
//            infowindow.open(map,thisMarker);
//        });
    }

    this.addListener();

    // getter
    this.getLatLng = function () {
        return this.latLng;
    }

    this.getMarkerObj = function () {
        return this.markerObj;
    }


    this.remove = function () {
        this.markerObj.setMap(null);
    }

    this.getData = function () {
        if (this != null) {
            return this.latLng.lng().toFixed(6) + "," + this.latLng.lat().toFixed(6);
        } else {
            return null;
        }
    }
}


