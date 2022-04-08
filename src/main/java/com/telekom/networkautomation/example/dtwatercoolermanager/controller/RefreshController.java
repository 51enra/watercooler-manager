package com.telekom.networkautomation.example.dtwatercoolermanager.controller;

import com.telekom.networkautomation.example.dtwatercoolermanager.data.RefreshDTO;
import com.telekom.networkautomation.example.dtwatercoolermanager.model.TapResult;
import com.telekom.networkautomation.example.dtwatercoolermanager.model.TapSize;
import com.telekom.networkautomation.example.dtwatercoolermanager.service.WatercoolerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/home/refresh")
public class RefreshController {

    private final static Logger LOG = LoggerFactory.getLogger(RefreshController.class);

    private WatercoolerService watercoolerService;

    private RefreshController(WatercoolerService watercoolerService) {
        this.watercoolerService = watercoolerService;
    }

    @GetMapping()
    public RefreshDTO refreshElements() {
        RefreshDTO response = new RefreshDTO();

        response.setFillLevel(watercoolerService.getFillLevel());
        response.setOverflow(watercoolerService.isOverflowOn());
        response.setError(watercoolerService.isApiError());
        response.setMounted(watercoolerService.isMounted());
        response.setStarted(watercoolerService.isAutoRefillStarted());

        Map<TapSize, TapResult> tapStatus = new HashMap<>();
        for (TapSize tapSize: TapSize.values()) {
            TapResult result = watercoolerService.getTapResult(tapSize); //Can be read only once after a tap!
            if (result != null && result != TapResult.NOT_AVAILABLE) {
                tapStatus.put(tapSize, result);
            }
        }
        response.setTapStatus(tapStatus);

        response.setCustomerAlert(watercoolerService.getCustomerAlert());
        response.setOperatorAlert(watercoolerService.getOperatorAlert());

        return response;
    }
}
