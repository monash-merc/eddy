/*
 * Copyright (c) 2010-2011, Monash e-Research Centre
 * (Monash University, Australia)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of the Monash University nor the names of its
 * 	  contributors may be used to endorse or promote products derived from
 * 	  this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package au.edu.monash.merc.capture.struts2.action;

import au.edu.monash.merc.capture.common.CoverageType;
import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.domain.Location;
import au.edu.monash.merc.capture.dto.MapLocation;
import au.edu.monash.merc.capture.dto.LocationResponse;
import au.edu.monash.merc.capture.dto.SiteBean;
import au.edu.monash.merc.capture.dto.SitesResponse;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Yu
 *         <p/>
 *         Email: xiaoming.yu@monash.edu
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        Date: 13/02/13 12:36 PM
 */
@Scope("prototype")
@Controller("data.locationAction")
public class LocationAction extends DMCoreAction {

    private static String SUCCESS_MSG = "success";
    private static String ERROR_MSG = "failed";

    private LocationResponse locationResponse;

    private SitesResponse sitesResponse;

    private String spatialCoverage;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public String showMapView() {
        return SUCCESS;
    }

    public String viewLocations() {
        try {
            //the kml type locations
            List<Location> locations = this.dmService.getLocations(CoverageType.KML.type());
            locationResponse = new LocationResponse();
            locationResponse.setMapLocations(copyFromLocations(locations));
            locationResponse.setSucceed(true);
        } catch (Exception ex) {
            logger.error(ex);
            locationResponse = new LocationResponse();
            locationResponse.setSucceed(false);
            locationResponse.setMsg(ERROR_MSG);
        }
        return SUCCESS;
    }

    private List<MapLocation> copyFromLocations(List<Location> locations) {
        List<MapLocation> tempLos = new ArrayList<MapLocation>();
        if (locations != null) {
            for (Location loc : locations) {
                if (loc != null) {
                    long id = loc.getId();
                    String type = loc.getSpatialType();
                    String coverage = loc.getSpatialCoverage();
                    MapLocation mapLocation = new MapLocation();
                    mapLocation.setSpatialCoverage(coverage);
                    tempLos.add(mapLocation);
                }
            }
        }
        return tempLos;
    }

    public String listSites() {
        sitesResponse = new SitesResponse();
        try {
            String viewSiteNamespace = "pub";
            String viewActName = "viewColDetails.jspx";
            String viewType = "anonymous";
            long userId = getLoginUsrIdFromSession();
            if (userId != 0) {
                viewSiteNamespace = "data";
                viewType = "all";
            }
            List<Collection> collections = this.dmService.getCollectionsByLocation(CoverageType.KML.type(), this.spatialCoverage);
            List<SiteBean> siteBeans = covertToSiteBeans(collections);
            sitesResponse.setSucceed(true);
            sitesResponse.setMsg(SUCCESS_MSG);
            sitesResponse.setSiteBeans(siteBeans);
            sitesResponse.setViewSiteNamespace(viewSiteNamespace);
            sitesResponse.setViewActName(viewActName);
            sitesResponse.setViewType(viewType);
        } catch (Exception ex) {
            logger.error(ex);
            sitesResponse.setSucceed(false);
            sitesResponse.setMsg(ERROR_MSG);
        }
        return SUCCESS;
    }

    private List<SiteBean> covertToSiteBeans(List<Collection> collections) {
        List<SiteBean> tmp = new ArrayList<SiteBean>();
        if (collections != null) {
            for (Collection co : collections) {
                String name = co.getName();
                long id = co.getId();
                long ownerId = co.getOwner().getId();
                SiteBean siteBean = new SiteBean();
                siteBean.setName(name);
                siteBean.setId(id);
                siteBean.setOwnerId(ownerId);
                tmp.add(siteBean);
            }
        }
        return tmp;
    }

    public LocationResponse getLocationResponse() {
        return locationResponse;
    }

    public void setLocationResponse(LocationResponse locationResponse) {
        this.locationResponse = locationResponse;
    }

    public SitesResponse getSitesResponse() {
        return sitesResponse;
    }

    public void setSitesResponse(SitesResponse sitesResponse) {
        this.sitesResponse = sitesResponse;
    }

    public String getSpatialCoverage() {
        return spatialCoverage;
    }

    public void setSpatialCoverage(String spatialCoverage) {
        this.spatialCoverage = spatialCoverage;
    }
}
