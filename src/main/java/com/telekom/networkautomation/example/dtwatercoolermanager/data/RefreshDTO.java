package com.telekom.networkautomation.example.dtwatercoolermanager.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.telekom.networkautomation.example.dtwatercoolermanager.model.TapResult;
import com.telekom.networkautomation.example.dtwatercoolermanager.model.TapSize;

import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefreshDTO {

    @JsonProperty
    String fillLevel;

    @JsonProperty
    boolean overflow;

    @JsonProperty
    boolean error;

    @JsonProperty
    boolean mounted;

    @JsonProperty
    boolean started;

    @JsonProperty
    Map<TapSize, TapResult> tapStatus;

    @JsonProperty
    String operatorAlert;

    @JsonProperty
    String customerAlert;

    public void setFillLevel(String fillLevel) {
        this.fillLevel = fillLevel;
    }

    public void setOverflow(boolean overflow) {
        this.overflow = overflow;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setMounted(boolean mounted) {
        this.mounted = mounted;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public void setTapStatus(Map<TapSize, TapResult> tapStatus) {
        this.tapStatus = tapStatus;
    }

    public void setOperatorAlert(String operatorAlert) {
        this.operatorAlert = operatorAlert;
    }

    public void setCustomerAlert(String customerAlert) {
        this.customerAlert = customerAlert;
    }
}
