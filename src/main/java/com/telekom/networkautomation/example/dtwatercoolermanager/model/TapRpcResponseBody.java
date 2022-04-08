package com.telekom.networkautomation.example.dtwatercoolermanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TapRpcResponseBody {

    @JsonProperty("watercooler:output")
    TapRpcResult output;

    public TapRpcResult getTapResult() {
        return output;
    }

}
