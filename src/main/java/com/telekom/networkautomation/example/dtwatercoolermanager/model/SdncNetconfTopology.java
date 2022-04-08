package com.telekom.networkautomation.example.dtwatercoolermanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SdncNetconfTopology {

    @JsonProperty("network-topology:node")
    private List<NetconfNode> node;

    public List<NetconfNode> getNode() {
        return node;
    }

    public void setNode(List<NetconfNode> node) {
        this.node = node;
    }
}
