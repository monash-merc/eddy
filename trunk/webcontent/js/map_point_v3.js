function PointCreator(map, latLng) {
	this.map = map;
	this.pointPen = new PointPen(this.map);
	var thisOjb = this;
	if (latLng != null) {
		thisOjb.pointPen.drawPoint(latLng);
	}
	this.event = google.maps.event.addListener(thisOjb.map, 'click', function(
			event) {
		thisOjb.pointPen.drawPoint(event.latLng);
	});

	this.showData = function() {
		return this.pointPen.getData();
	}

	// destroy the pen
	this.destroy = function() {
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

	this.drawPoint = function(latLng, map, pen) {
		if (null != this.point) {
			alert("Press 'Clear' to draw another point");
		} else {
			this.point = new Point(latLng, this.map, this);
			var pointstr = this.point.getLatLng().lng() + ","
					+ this.point.getLatLng().lat();
			$("#spatialcvg").val(pointstr);
		}
	}
	// cancel
	this.cancel = function() {
		if (null != this.point) {
			(this.point.remove());
		}
		this.point = null;
	}
	// setter
	this.setPoint = function(dot) {
		this.point = point;
	}
	// getter
	this.getPoint = function() {
		return this.point;
	}

	// get point data(longitude, latitude)
	this.getData = function() {
		if (this.point != null) {
			var pdata = this.point.getLatLng();
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
		position : this.latLng,
		map : map
	});

	this.addListener = function() {
		var parent = this.parent;
		var thisMarker = this.markerObj;
		var thisPoint = this;
		google.maps.event.addListener(thisMarker, 'click', function() {
			parent.setPoint(thisPoint);
			parent.drawPoint(thisMarker.getPosition());
		});
	}
	this.addListener();

	// getter
	this.getLatLng = function() {
		return this.latLng;
	}

	this.getMarkerObj = function() {
		return this.markerObj;
	}

	this.remove = function() {
		this.markerObj.setMap(null);
	}
}