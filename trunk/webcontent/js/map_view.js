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
                    displayErrorMsg("Failed to get all site locations");
                }
            },
            error:function (request, exception) {
                var errormsg = getErrorMsg(request, exception);
                displayErrorMsg(errormsg);
            }
        }
    )
}

function displayMapLocations(mapData) {
    if (mapData != null) {
        var mapLocations = new Array();
        var count = 0;
        $.each(mapData, function (key, coverage) {
            mapLocations[key] = coverage.spatialCoverage;
            var location = getCoordsFromCoverageStr(mapLocations[key]);
            if (location != null && location.length == 1) {
                var mapPoint = new MapPoint(location[0], map);
                count++;
            }
            if (location != null && location.length > 1) {
                //disabled the polygon
                // var polygonCreator = new PolygonCreator(map, location);
            }
        });
        displaySiteTitle(count);
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
        icon:'../images/ball_icon.png',
        map:map
    });

    var thisPoint = this;
    var infowindow = new google.maps.InfoWindow();
    this.addListener = function () {
        var thisMarker = this.markerObj;
        google.maps.event.addListener(thisMarker, 'click', function () {
            //alert(" you click the site for: " + thisPoint.getData());
            var foundLocation = thisPoint.getData();
            viewSites(foundLocation);
        });
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

function displaySiteTitle(number) {
    if (number > 0) {
        var mapSiteTitleDiv = $('.site_map_top');
        var siteNumber = $('#location_number_id');
        siteNumber.empty();
        var html = "A total of <span class='span_number'>" + number + "</span> site(s) on the map";
        siteNumber.append(html);
        mapSiteTitleDiv.show();
    }
}

function viewSites(location) {
    $.ajax({
            type:"get",
            url:'../mapview/listSites.jspx?spatialCoverage=' + location,
            cache:false,
            contentType:'application/json; charset=utf-8',
            dataType:'json',
            success:function (respdata) {
                var ok = respdata.succeed;
                if (ok) {
                    createCollectionListDetails(respdata);
                } else {
                    displayErrorMsg("Failed to get all collections for this site");
                }
            },
            error:function (request, exception) {
                var errormsg = getErrorMsg(request, exception);
                displayErrorMsg(errormsg);
            }
        }
    );
}

function createCollectionListDetails(sitesResponse) {

    var baseDiv = $('.collection_list_div');
    baseDiv.empty();

    if (sitesResponse != null) {
        var namespace = sitesResponse.viewSiteNamespace;
        var actname = sitesResponse.viewActName;
        var viewtype = sitesResponse.viewType;
        var sites = sitesResponse.siteBeans;
        var totalSize = sites.length;
        if (sites != null) {
            if (totalSize > 0) {
                var html = "<div class='site_co_info'>";
                html += "<span class='site_title'>A total of <span class='span_number'>" + totalSize + "</span> collections(s) on this site</span>";
                html += "<div class='comments'>[ Select a collection to view the details ]</div>";
                html += "</div>";
                html += "<ul class='collection_ul'>";
                $.each(sites, function (key, sitebean) {
                    var sitename = sitebean.name;
                    var siteid = sitebean.id;
                    var siteownerid = sitebean.ownerId;

                    html += "<li>";
                    html += "<a href='../" + namespace + "/" + actname + "?collection.id=" + siteid + "&collection.owner.id=" + siteownerid + "&viewType=" + viewtype + "' target='_blank'>" + sitename + "</a>";
                    html += "</li>";
                });
                html += "</ul>";
                baseDiv.append(html);
            } else {
                baseDiv.append("<div class='no_co_on_site'>There is no collection on this site</div>");
            }
        }
    }
}


function getErrorMsg(request, exception) {
    var errormsg = '';
    if (request.status === 0) {
        errormsg = 'Failed to connect to the server';
    } else if (request.status == 404) {
        errormsg = 'The requested page not found';
    } else if (request.status == 500) {
        errormsg = 'The internal server error';
    } else if (exception === 'parsererror') {
        errormsg = 'The requested JSON parse failed';
    } else if (exception === 'timeout') {
        errormsg = 'Connection time out';
    } else if (exception === 'abort') {
        errormsg = 'The request aborted';
    } else {
        errormsg = 'Failed to call the service. ' + request.responseText;
    }
    return errormsg;
}

function displayErrorMsg(message) {
    var mapviewErrorMsgDiv = $(".mapview_error_msg_div");
    var mapviewErrorMsg = $("#mapview_error_msg");
    mapviewErrorMsg.html(message);
    mapviewErrorMsgDiv.show();
}

