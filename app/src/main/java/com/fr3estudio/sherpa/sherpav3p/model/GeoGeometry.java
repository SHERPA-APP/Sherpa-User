package com.fr3estudio.sherpa.sherpav3p.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Alfr3 on 10/11/2015.
 */
public class GeoGeometry {
    GeoLocation location;

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }
    public LatLng getLatLng(){
        return new LatLng(location.getLat(),location.getLng());
    }
}

 class GeoLocation{
    double lat;
     double lng;

     public double getLat() {
         return lat;
     }

     public void setLat(double lat) {
         this.lat = lat;
     }

     public double getLng() {
         return lng;
     }

     public void setLng(double lng) {
         this.lng = lng;
     }
 }