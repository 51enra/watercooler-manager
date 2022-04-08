package com.telekom.networkautomation.example.dtwatercoolermanager.service;

import com.telekom.networkautomation.example.dtwatercoolermanager.model.NetconfNode;
import com.telekom.networkautomation.example.dtwatercoolermanager.model.SdncNetconfTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class SdncApiService {

    private final static Logger LOG = LoggerFactory.getLogger(SdncApiService.class);

    private final String pathTemplate =
            "/rests/{apiMode}/network-topology:network-topology/topology={topology}/node={node}";

    private final String yangPathTemplate = "/yang-ext:mount/{filter}";

    private final WebClient webClient;

    private SdncApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    private Map<String, String> pathParameters(String apiMode, String topologyName, String nodeName) {
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("apiMode", apiMode);
        pathParameters.put("topology", topologyName);
        pathParameters.put("node", nodeName);
        return pathParameters;
    }

    private Map<String, String> pathParameters(String apiMode, String topologyName, String nodeName, String filter) {
        Map<String, String> pathParameters = pathParameters(apiMode, topologyName, nodeName);
        pathParameters.put("filter", filter);
        return pathParameters;
    }

    public WebClient.ResponseSpec mountNetconfNode(String topologyName, String nodeName, NetconfNode netconfNode) {
        SdncNetconfTopology netconfTopology = new SdncNetconfTopology();
        netconfTopology.setNode(Arrays.asList(netconfNode));

        return this.webClient.put().uri(uriBuilder -> uriBuilder
                        .path(pathTemplate)
                        .build(pathParameters("data", topologyName, nodeName)))
                        .body(Mono.just(netconfTopology), SdncNetconfTopology.class)
                        .retrieve();
    }

    public WebClient.ResponseSpec unmountNetconfNode(String topologyName, String nodeName) {

        return this.webClient.delete().uri(uriBuilder -> uriBuilder
                .path(pathTemplate)
                .build(pathParameters("data", topologyName, nodeName)))
                .retrieve();
    }

    public WebClient.ResponseSpec getTopologyForNode(String topologyName, String nodeName) {
        return this.webClient.get().uri(uriBuilder -> uriBuilder
                .path(pathTemplate)
                .build(pathParameters("data", topologyName, nodeName)))
                .retrieve();
    }

    public WebClient.ResponseSpec getNodeData(String topologyName, String nodeName, String filter) {
        // <filter> specifies a path element of the YANG model to be retrieved. If empty, the whole
        // YANG model data (config and operational) will be retrieved.
        return this.webClient.get().uri(uriBuilder -> uriBuilder
                .path(this.pathTemplate)
                .path(this.yangPathTemplate)
                .build(pathParameters("data", topologyName, nodeName, filter)))
                .retrieve();
    }

    public <T> WebClient.ResponseSpec editNodeConfig(String topologyName, String nodeName, String filter,
            T body, Class<T> bodyClass ) {
        // <filter> specifies the path element of the YANG model containing the config element. Must not be empty.

        return this.webClient.put().uri(uriBuilder -> uriBuilder
                .path(this.pathTemplate)
                .path(this.yangPathTemplate)
                .build(pathParameters("data", topologyName, nodeName, filter)))
                .body(Mono.just(body), bodyClass.getClass())
                .retrieve();
    }

    public <T> WebClient.ResponseSpec sendRpc(String topologyName, String nodeName, String filter,
            T body, Class<T> bodyClass ) {
        // <filter> specifies the path element of the YANG model containing the config element. Must not be empty.

        return this.webClient.post().uri(uriBuilder -> uriBuilder
                .path(this.pathTemplate)
                .path(this.yangPathTemplate)
                .build(pathParameters("operations", topologyName, nodeName, filter)))
                .body(Mono.just(body), bodyClass.getClass())
                .retrieve();
    }

}
