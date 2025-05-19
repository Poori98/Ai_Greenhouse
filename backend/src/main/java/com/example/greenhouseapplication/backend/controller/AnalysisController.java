package com.example.greenhouseapplication.backend.controller;

import com.example.greenhouseapplication.backend.model.SensorAnalysis;
import com.example.greenhouseapplication.backend.repository.SensorAnalysisRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {
    private final SensorAnalysisRepository repo;

    public AnalysisController(SensorAnalysisRepository repo) {
        this.repo = repo;
    }

    /** GET /api/analysis/{ghId}/latest?sensorType=temperature */
    @GetMapping("/{ghId}/latest")
    public SensorAnalysis latest(
            @PathVariable String ghId,
            @RequestParam String sensorType
    ) {
        return repo
                .findTopByGreenhouseIdAndSensorTypeOrderByAnalysisTimestampDesc(ghId, sensorType)
                .orElse(null);
    }

    /** GET /api/analysis/{ghId}?since=...&sensorType=humidity */
    @GetMapping("/{ghId}")
    public List<SensorAnalysis> history(
            @PathVariable String ghId,
            @RequestParam("since")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime since,
            @RequestParam String sensorType
    ) {
        // Now we include sensorType in the query
        return repo.findByGreenhouseIdAndSensorTypeAndAnalysisTimestampAfter(
                ghId, sensorType, since);
    }
}
