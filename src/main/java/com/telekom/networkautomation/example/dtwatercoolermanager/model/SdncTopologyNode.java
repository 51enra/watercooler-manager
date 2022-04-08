package com.telekom.networkautomation.example.dtwatercoolermanager.model;

public class SdncTopologyNode {

    private String nodeId;
    private String username;
    private String password;
    private String host;
    private Integer port;
    private boolean tcpOnly;

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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean isTcpOnly() {
        return tcpOnly;
    }

    public void setTcpOnly(boolean tcpOnly) {
        this.tcpOnly = tcpOnly;
    }
}
