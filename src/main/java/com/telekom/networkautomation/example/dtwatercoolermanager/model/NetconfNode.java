package com.telekom.networkautomation.example.dtwatercoolermanager.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetconfNode {

    @JsonProperty("node-id")
    String nodeId;

    @JsonProperty("netconf-node-topology:username")
    String username;

    @JsonProperty("netconf-node-topology:password")
    String password;

    @JsonProperty("netconf-node-topology:host")
    String ipAddress;

    @JsonProperty("netconf-node-topology:port")
    Integer port;

    @JsonProperty("netconf-node-topology:connection-status")
    String connectionStatus;

    @JsonProperty("netconf-node-topology:tcp-only")
    boolean tcpOnly;

    @JsonProperty("netconf-node-topology:available-capabilities")
    Map<String, List<NetconfCapability>> availableCapabilities;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public boolean isTcpOnly() {
        return tcpOnly;
    }

    public void setTcpOnly(boolean tcpOnly) {
        this.tcpOnly = tcpOnly;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Map<String, List<NetconfCapability>> getAvailableCapabilities() {
        return availableCapabilities;
    }

}
