package com.telekom.networkautomation.example.dtwatercoolermanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TapRpcResult {

    @JsonProperty
    private String tapSuccessful;

    @JsonProperty
    private int remainingFillLevel;

    public String getTapSuccessful() {
        return tapSuccessful;
    }

    public int getRemainingFillLevel() {
        return remainingFillLevel;
    }

}
