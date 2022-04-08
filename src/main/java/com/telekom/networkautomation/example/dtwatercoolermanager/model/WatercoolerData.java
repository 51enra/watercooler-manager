package com.telekom.networkautomation.example.dtwatercoolermanager.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WatercoolerData {

    private String watercoolerManufacturer;
    private String watercoolerModelNumber;
    private String overflowIndicator;
    private Integer fillLevel;
    private Integer refillRate;

    public String getWatercoolerManufacturer() {
        return watercoolerManufacturer;
    }

    public String getWatercoolerModelNumber() {
        return watercoolerModelNumber;
    }

    public void setWatercoolerModelNumber(String watercoolerModelNumber) {
        this.watercoolerModelNumber = watercoolerModelNumber;
    }

    public String getOverflowIndicator() {
        return overflowIndicator;
    }

    public void setOverflowIndicator(String overflowIndicator) {
        this.overflowIndicator = overflowIndicator;
    }

    public Integer getFillLevel() {
        return fillLevel;
    }

    public void setFillLevel(Integer fillLevel) {
        this.fillLevel = fillLevel;
    }

    public Integer getRefillRate() {
        return refillRate;
    }

    public void setRefillRate(Integer refillRate) {
        this.refillRate = refillRate;
    }
}
