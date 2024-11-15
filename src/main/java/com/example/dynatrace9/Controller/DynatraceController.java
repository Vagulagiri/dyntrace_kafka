package com.example.dynatrace9.Controller;

import com.example.dynatrace9.ProducerDynatrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DynatraceController {

    @Autowired
    private ProducerDynatrace dynatraceProducerService;

    @GetMapping("/produce/dynatrace")
    public String fetchDynatraceData() {
        try {
            dynatraceProducerService.fetchAndProcessData();
            return "Dynatrace data fetch initiated successfully.";
        } catch (Exception e) {
            return "Error in fetching Dynatrace data: " + e.getMessage();
        }
    }
}

