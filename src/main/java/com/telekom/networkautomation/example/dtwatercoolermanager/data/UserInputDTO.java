package com.telekom.networkautomation.example.dtwatercoolermanager.data;

import com.telekom.networkautomation.example.dtwatercoolermanager.model.TapSize;

public class UserInputDTO {
    private String operatorAction;
    private TapSize customerAction;
    private String fillRate;

    public String getFillRate() {
        return fillRate;
    }

    public void setFillRate(String fillRate) {
        this.fillRate = fillRate;
    }

    public String getOperatorAction() {
        return operatorAction;
    }

    public void setOperatorAction(String operatorAction) {
        this.operatorAction = operatorAction;
    }

    public TapSize getCustomerAction() {
        return customerAction;
    }

    public void setCustomerAction(TapSize customerAction) {
        this.customerAction = customerAction;
    }
}
