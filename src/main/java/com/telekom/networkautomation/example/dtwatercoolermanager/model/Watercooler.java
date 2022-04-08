package com.telekom.networkautomation.example.dtwatercoolermanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Watercooler {

    // "watercooler:watercooler" is module Id & container Id from YANG model
    @JsonProperty("watercooler:watercooler")
    WatercoolerData watercoolerData;

    public WatercoolerData getWatercoolerData() {
        return watercoolerData;
    }

    public void setWatercoolerData(WatercoolerData watercoolerData) {
        this.watercoolerData = watercoolerData;
    }
}
