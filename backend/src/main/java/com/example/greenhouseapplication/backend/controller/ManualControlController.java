package com.example.greenhouseapplication.backend.controller;

import com.example.greenhouseapplication.backend.model.Greenhouse;
import com.example.greenhouseapplication.backend.model.SensorData;
import com.example.greenhouseapplication.backend.service.GreenhouseService;
import com.example.greenhouseapplication.backend.service.SensorDataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/sensors")
public class ManualControlController {
    private final SensorDataService sensorDataService;

    public ManualControlController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    /**
     * Endpoint to record a manual‐override reading.
     * The front end should POST a JSON SensorData payload with readings and timestamp.
     */
    @PostMapping("/manual-control")
    @ResponseStatus(HttpStatus.CREATED)
    public SensorData recordManualReading(@RequestBody SensorData manualData) {
        // Ensure override flags are set correctly
        manualData.setManualOverride(true);
        manualData.setControlledBy("Manual");
        return sensorDataService.saveManualReading(manualData);
    }
}