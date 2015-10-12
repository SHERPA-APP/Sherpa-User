package com.fr3estudio.sherpa.sherpav3p.model;

/**
 * Created by Alfr3 on 10/11/2015.
 */
public class GeoResult {
    String formatted_address;
    GeoGeometry geometry;

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public GeoGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(GeoGeometry geometry) {
        this.geometry = geometry;
    }
}
