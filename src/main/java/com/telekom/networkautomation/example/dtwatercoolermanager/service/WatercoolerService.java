package com.telekom.networkautomation.example.dtwatercoolermanager.service;

import com.telekom.networkautomation.example.dtwatercoolermanager.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.HashMap;
import java.util.Map;

//TODO: This class should be broken down, e.g. by taking out the two inner classes Mounter and ControlLoop!

@Service
public class WatercoolerService {

    private final static Logger LOG = LoggerFactory.getLogger(WatercoolerService.class);

    private final static int CONTROL_LOOP_PERIOD_MS = 1000; // Delay between control loop executions in ms
    private final static int MOUNT_LOOP_PERIOD_MS = 1000; // Delay between checks if mount is completed in ms
    private final static String NODE_ID = "watercooler";
    private final static String TOPOLOGY_NAME = "topology-netconf";
    private final static String YANG_FILTER = "watercooler:watercooler";
    private final static String TAP_YANG_FILTER = "watercooler:tap";

    private SdncApiService sdncApiService;
    private NetconfNode watercoolerNode;

    private SdncNetconfTopology watercoolerTopology;
    private Watercooler watercooler;
    private ControlLoop controlLoop;

    private boolean apiError = false;
    private boolean mounted = false;
    private boolean watercoolerDataLoaded = false;
    private boolean controlLoopStarted = false;
    private boolean autoRefillStarted = false;
    private String operatorAlert;
    private String customerAlert;
    private Map<TapSize, String> tapSuccessful;

    private WatercoolerService(SdncApiService sdncApiService, NetconfNode watercoolerNode) {
        this.sdncApiService = sdncApiService;
        this.watercoolerNode = watercoolerNode;
        this.watercoolerNode.setNodeId(this.NODE_ID);
        this.tapSuccessful = new HashMap<>();
        initStatus();
        LOG.info("Watercooler device IP address: {}", this.watercoolerNode.getIpAddress());
    }

    private void initStatus() {
        this.mounted = false;
        this.watercoolerDataLoaded = false;
        this.watercoolerTopology = null;
        this.watercooler = null;
        this.operatorAlert = null;
        this.customerAlert = null;
        this.tapSuccessful.put(TapSize.SMALL, null);
        this.tapSuccessful.put(TapSize.MEDIUM, null);
        this.tapSuccessful.put(TapSize.LARGE, null);
    }

    private void handleError() {
        initStatus();
        apiError = true;
        this.autoRefillStarted = false;
        controlLoop.interrupt();
    }

    private class Mounter extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                // TODO: Better way to evaluate response status if we don't need the response otherwise?
                sdncApiService.mountNetconfNode(TOPOLOGY_NAME, NODE_ID, watercoolerNode)
                        .bodyToMono(String.class)
                        .block();
                // Wait until mounting is completed
                while (watercoolerTopology == null || watercoolerTopology.getNode() == null ||
                        watercoolerTopology.getNode().size() == 0 ||
                        watercoolerTopology.getNode().get(0).getConnectionStatus() == null ||
                        !watercoolerTopology.getNode().get(0).getConnectionStatus().equals("connected")) {
                    Thread.sleep(MOUNT_LOOP_PERIOD_MS);
                    watercoolerTopology = sdncApiService.getTopologyForNode(TOPOLOGY_NAME, NODE_ID)
                            .bodyToMono(SdncNetconfTopology.class)
                            .block();
                }
                mounted = true;
                setFillRate(0);
                updateWatercoolerData();
                watercoolerDataLoaded = true;
                LOG.info("Watercooler mounted successfully.");
                if (!controlLoopStarted) {
                    controlLoop = new ControlLoop();
                    controlLoop.start();
                } else {
                    LOG.warn("Control loop already running!");
                }
            } catch (WebClientException | InterruptedException e) {
                LOG.error(e.getMessage());
                LOG.info("Mounting failed - trying to unmount watercooler.");
                handleError();
            }
        }
    }

    private class ControlLoop extends Thread {
        @Override
        public void run() {
            super.run();
            controlLoopStarted = true;
            while (!Thread.currentThread().isInterrupted()) {
                updateWatercoolerData();
                int fillRate = 0;
                if (autoRefillStarted) {
                    // LOG.debug("Fill level is: {}", watercooler.getWatercoolerData().getFillLevel());
                    fillRate = Integer.min((100 - watercooler.getWatercoolerData().getFillLevel()) / 4, 10);
                    if (fillRate != watercooler.getWatercoolerData().getRefillRate()) {
                        setFillRate(fillRate);
                        LOG.debug("Fillrate set to {}", fillRate);
                    }
                }
                try {
                    Thread.sleep(CONTROL_LOOP_PERIOD_MS);
                } catch (InterruptedException e) {
                    LOG.debug("Control loop interrupt received.");
                    controlLoopStarted = false;
                    Thread.currentThread().interrupt();
                }
            }
            controlLoopStarted = false;
        }
    }

    public void mountWatercooler() {
        if (!mounted) {
            this.apiError = false;
            Mounter mounter = new Mounter();
            mounter.start();
        }
    }

    public void unmountWatercooler() {
        if (mounted) {
            String response = "";
            this.autoRefillStarted = false;
            controlLoop.interrupt();
            try {
                response = sdncApiService.unmountNetconfNode(TOPOLOGY_NAME, NODE_ID)
                        .bodyToMono(String.class)
                        .block();
                LOG.info("Watercooler unmounted successfully.");
                initStatus();
            } catch (WebClientException e) {
                LOG.error(e.getMessage());
                LOG.error(response);
                handleError();
            }
        }
    }

    public void startAutoRefill() {
        if (mounted) {
            this.autoRefillStarted = true;
        } else {
            this.operatorAlert = "Mount watercooler before attempting to start auto refill!";
            LOG.warn(this.operatorAlert);
        }
    }

    public void stopAutoRefill() {
        this.autoRefillStarted = false;
        setFillRate(0);
    }

    public boolean isApiError() {
        return apiError;
    }

    public boolean isMounted() {
        return mounted;
    }

    public boolean isAutoRefillStarted() {
        return autoRefillStarted;
    }

    public TapResult getTapResult(TapSize tapSize) {
        String tapSuccessful = this.tapSuccessful.get(tapSize);
        this.tapSuccessful.put(tapSize, null); // Reset after reading
        TapResult result = TapResult.NOT_AVAILABLE;
        if (tapSuccessful != null) {
            switch (tapSuccessful) {
                case "Yes":
                    result = TapResult.SUCCESS;
                    break;
                case "No__insufficient_water_level":
                    result = TapResult.FAILURE;
                    break;
                default:
                    result = TapResult.NOT_AVAILABLE;
            }
        }
        return result;
    }

    public String getFillLevel() {
        return this.watercoolerDataLoaded ?
                this.watercooler.getWatercoolerData().getFillLevel().toString() + "%" : "0%";
    }

    public boolean isOverflowOn() {
        return this.watercoolerDataLoaded ?
                this.watercooler.getWatercoolerData().getOverflowIndicator().equals("on") : false;
    }

    public String getOperatorAlert() {
        String alert = this.operatorAlert;
        this.operatorAlert = null; // clear alert
        return alert;
    }

    public String getCustomerAlert() {
        String alert = this.customerAlert;
        this.customerAlert = null; // clear alert
        return alert;
    }

    public void setFillRate(int fillRate) {
        if (mounted) {
            WatercoolerData watercoolerData = new WatercoolerData();
            watercoolerData.setRefillRate(fillRate);
            Watercooler fillRequestBody = new Watercooler();
            fillRequestBody.setWatercoolerData(watercoolerData);
            try {
                String response = sdncApiService.editNodeConfig(TOPOLOGY_NAME, NODE_ID, YANG_FILTER,
                        fillRequestBody, Watercooler.class)
                        .bodyToMono(String.class)
                        .block();
            } catch (WebClientException e) {
                LOG.error(e.getMessage());
                handleError();
            }
        } else {
            this.operatorAlert = "Mount watercooler before attempting to set fillrate!";
            LOG.warn(this.operatorAlert);
        }
    }

    public void tapWater(TapSize tapSize) {
        if (mounted) {
            try {
                TapRpcRequestBody body = new TapRpcRequestBody();
                body.setTapSize(tapSize);
                TapRpcResponseBody response =
                        sdncApiService.sendRpc(TOPOLOGY_NAME, NODE_ID, TAP_YANG_FILTER, body, TapRpcRequestBody.class)
                                .bodyToMono(TapRpcResponseBody.class)
                                .block();
                this.tapSuccessful.put(tapSize, response.getTapResult().getTapSuccessful());
                // LOG.debug("Tap Response: {}, {}",
                //        response.getTapResult().getTapSuccessful(),
                //        response.getTapResult().getRemainingFillLevel());
            } catch (WebClientException e) {
                LOG.error(e.getMessage());
                handleError();
            }
        } else {
            this.customerAlert = "Service not available - please contact customer support!";
            LOG.warn("Tap attempt while device is unmounted");
        }
    }

    private void updateWatercoolerData() {
        try {
            this.watercooler = sdncApiService.getNodeData(TOPOLOGY_NAME, NODE_ID, YANG_FILTER)
                    .bodyToMono(Watercooler.class)
                    .block();
        } catch (WebClientException e) {
            LOG.error(e.getMessage());
            handleError();
        }
    }

}
