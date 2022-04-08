package com.telekom.networkautomation.example.dtwatercoolermanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetconfCapability {

    String capability;

    @JsonProperty("capability-origin")
    String capabilityOrigin;

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }

    public String getCapabilityOrigin() {
        return capabilityOrigin;
    }

    public void setCapabilityOrigin(String capabilityOrigin) {
        this.capabilityOrigin = capabilityOrigin;
    }
}
