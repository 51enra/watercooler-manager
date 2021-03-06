package com.telekom.networkautomation.example.dtwatercoolermanager.config;

import com.telekom.networkautomation.example.dtwatercoolermanager.model.NetconfNode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

import java.util.Arrays;

@Slf4j
@Configuration
public class WebClientConfig {

    private final static Logger LOG = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${sdnc.baseurl}")
    String sdncUrl;

    @Value("${sdnc.restport}")
    String restPort;

    @Value("${sdnc.username}")
    String sdncUsername;

    @Value("${sdnc.password}")
    String sdncPassword;

    @Value("${netconf.ipaddress}")
    String netconfIpAddress;

    @Value("${netconf.port}")
    Integer netconfPort;

    @Value("${netconf.username}")
    String netconfUsername;

    @Value("${netconf.password}")
    String netconfPassword;

    private final boolean tcpOnly = false;

    @Bean
    NetconfNode netconfNode() {
        NetconfNode netconfNode = new NetconfNode();
        netconfNode.setIpAddress(this.netconfIpAddress);
        netconfNode.setPort(this.netconfPort);
        netconfNode.setUsername(this.netconfUsername);
        netconfNode.setPassword(this.netconfPassword);
        netconfNode.setTcpOnly(this.tcpOnly);
        return netconfNode;
    }

    @Bean
    WebClient webClient() {

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(
                this.sdncUrl + ":" + this.restPort);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().wiretap(true)))  // full logging of request / repsonse in DEBUG mode
                .uriBuilderFactory(factory) // avoid html escaping of special characters in uri template variables
                //.baseUrl(this.sdncUrl + ":" + this.restPort)
                .defaultHeaders( httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
                .defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth(
                        this.sdncUsername, this.sdncPassword))
                .defaultHeaders(httpHeaders -> httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON)))
                .build();
    }
}
