

function PointCreator(map, latLng) {
    this.map = map;
    this.pointPen = new PointPen(this.map);
    var thisOjb = this;
    if (latLng != null) {
        thisOjb.pointPen.drawPoint(latLng);
    }
    this.event = google.maps.event.addListener(thisOjb.map, 'click', function (event) {
        thisOjb.pointPen.drawPoint(event.latLng);
    });

    this.showData = function () {
        return this.pointPen.getData();
    }

    // destroy the pen
    this.destroy = function () {
        if (null != this.pointPen.point) {
            this.pointPen.cancel();
        }
        google.maps.event.removeListener(this.event);
    }
}

/*
 * PointPen class
 */
function PointPen(map) {
    this.map = map;
    this.point = null;

    this.drawPoint = function (latLng, map, pen) {
        if (null != this.point) {
            //point already existed;
        } else {
           this.point = new Point(latLng, this.map, this);
        }
    }

    // cancel
    this.cancel = function () {
        if (null != this.point) {
            (this.point.remove());
        }
        this.point = null;
    }
    // setter
    this.setPoint = function (dot) {
        this.point = point;
    }
    // getter
    this.getPoint = function () {
        return this.point;
    }

    this.getLatLng = function(){
        return this.point.getLatLng();
    }

    // get point data(longitude, latitude)
    this.getData = function () {
        if (this.point != null) {
            var pdata = this.getLatLng();
            var lnglatStr = pdata.lng() + "," + pdata.lat();
            return lnglatStr;
        } else {
            return null;
        }
    }
}

/*
 * Child of PointPen class Point class
 */
function Point(latLng, map, pointPen) {
    // property
    this.latLng = latLng;
    this.parent = pointPen;

    this.markerObj = new google.maps.Marker({
        position:this.latLng,
        map:map
    });

    this.addListener = function () {
        var parent = this.parent;
        var thisMarker = this.markerObj;
        var thisPoint = this;
        //add a event listener
         var infowindow = new google.maps.InfoWindow();
        google.maps.event.addListener(thisMarker, 'click', function () {
            // parent.setPoint(thisPoint);
            //parent.drawPoint(thisMarker.getPosition());
            //alert(" you click the site for: " + parent.getLatLng() + "data: " + parent.getData());
            alert(" will display a site collection");
        });
        google.maps.event.addListener(thisMarker, 'mouseover', function () {
            // parent.setPoint(thisPoint);
            //parent.drawPoint(thisMarker.getPosition());
            //alert(" you click the site for: " + parent.getLatLng() + "data: " + parent.getData());
            infowindow.setContent("The site for : " + parent.getLatLng() + " data : " + parent.getData());
            infowindow.open(map,thisMarker);
        });
        google.maps.event.addListener(thisMarker, 'mouseout', function () {
            // parent.setPoint(thisPoint);
            //parent.drawPoint(thisMarker.getPosition());
            //alert(" you click the site for: " + parent.getLatLng() + "data: " + parent.getData());

            infowindow.close(map,thisMarker);
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
}