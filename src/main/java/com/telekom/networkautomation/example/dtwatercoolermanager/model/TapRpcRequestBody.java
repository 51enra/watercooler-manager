package com.telekom.networkautomation.example.dtwatercoolermanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class TapRpcRequestBody {

    @JsonProperty
    Map<String, String> input;

    public void setTapSize(TapSize tapSize) {
        this.input = new HashMap<>();
        this.input.put("cupSize", cupSize(tapSize));
    }

    private String cupSize(TapSize tapSize) {
        if (tapSize == TapSize.SMALL) {
            return "S";
        }
        if (tapSize == TapSize.MEDIUM) {
            return "M";
        }
        if (tapSize == TapSize.LARGE) {
            return "L";
        }
        return null;
    }
}
