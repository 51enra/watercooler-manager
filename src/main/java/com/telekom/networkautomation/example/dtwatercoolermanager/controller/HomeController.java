package com.telekom.networkautomation.example.dtwatercoolermanager.controller;

import com.telekom.networkautomation.example.dtwatercoolermanager.service.WatercoolerService;
import com.telekom.networkautomation.example.dtwatercoolermanager.data.UserInputDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    private final Logger LOG = LoggerFactory.getLogger(HomeController.class);

    private WatercoolerService watercoolerService;

    public HomeController(WatercoolerService watercoolerService) {
        this.watercoolerService = watercoolerService;
    }

    @GetMapping("/home")
    public String getHomePage(Model model) {
        model.addAttribute("fillLevel", "0%");
        return "home";
    }

    @PostMapping("/customer")
    public String getCustomerInput(@ModelAttribute("userDTO") UserInputDTO userInput) {
        //LOG.debug("Button press received: {}", userInput.getCustomerAction());
        watercoolerService.tapWater(userInput.getCustomerAction());
        return "redirect:/home";
    }

    @PostMapping("/operator")
    public String getOperatorInput(@ModelAttribute("userDTO") UserInputDTO userInput) {
        if (userInput.getFillRate() != "") {
            Integer fillRate = Integer.valueOf(userInput.getFillRate());
            LOG.debug("Manual fill rate is: {}", fillRate);
            watercoolerService.setFillRate(fillRate);
        }
        if (userInput.getOperatorAction() != null) {
            switch (userInput.getOperatorAction()) {
                case "mount":
                    watercoolerService.mountWatercooler();
                    break;
                case "unmount":
                    watercoolerService.unmountWatercooler();
                    break;
                case "start":
                    watercoolerService.startAutoRefill();
                    break;
                case "stop":
                    watercoolerService.stopAutoRefill();
                    break;
                default:
                    LOG.warn("Requested operator action not recognized!");
            }
        }
        return "redirect:/home";
    }

}
