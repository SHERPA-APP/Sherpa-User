package com.fr3estudio.sherpa.sherpav3p.model;

import java.util.ArrayList;

/**
 * Created by Alfr3 on 10/11/2015.
 */
public class GeoCodeResults {
    String status;

    public ArrayList<GeoResult> getResults() {
        return results;
    }

    public void setResults(ArrayList<GeoResult> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    ArrayList<GeoResult> results;

}
