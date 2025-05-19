package com.example.greenhouseapplication.backend.controller;

import org.springframework.web.bind.annotation.*;
import com.example.greenhouseapplication.backend.model.SensorData;
import com.example.greenhouseapplication.backend.service.SensorDataService;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@CrossOrigin(origins = "*")
public class SensorDataController {
    private final SensorDataService service;

    public SensorDataController(SensorDataService service) {
        this.service = service;
    }

    // Historical: any custom range, last week/month, specific date, etc.
    @GetMapping("/historical")
    public List<SensorData> historical(
            @RequestParam String greenhouseId,
            @RequestParam String start,   // e.g. "2025-04-16T15:19:46.814110Z"
            @RequestParam String end
    ) {
        OffsetDateTime odtStart = OffsetDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME);
        OffsetDateTime odtEnd   = OffsetDateTime.parse(end,   DateTimeFormatter.ISO_DATE_TIME);

        LocalDateTime s = odtStart.toLocalDateTime();
        LocalDateTime e = odtEnd.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();

        if (s.isAfter(now)) s = now;
        if (e.isAfter(now)) e = now;
        if (s.isAfter(e)) throw new IllegalArgumentException("Start > End");

        return service.getHistoricalSensorData(greenhouseId, s, e);
    }

    // Live: single most recent reading
    @GetMapping("/live")
    public SensorData live(@RequestParam String greenhouseId) {
        return service.getLatestReading(greenhouseId);
    }

    // Live batch: returns the last `count` readings
    @GetMapping("/live/batch")
    public List<SensorData> liveBatch(
            @RequestParam String greenhouseId,
            @RequestParam(defaultValue = "20") int count
    ) {
        return service.getLatestReadings(greenhouseId, count);
    }

    @GetMapping("/manual")
    public List<SensorData> getManualOverrides(@RequestParam String greenhouseId) {
        return service.getManualOverrides(greenhouseId);
    }

    // Manual override: save a manual reading
    @PostMapping("/live/batch")
    public SensorData manual(@RequestBody SensorData data) {
        System.out.println("Received manual override: " + data);
        //data.setControlledBy("AI"); //Default value

        if (!data.isManualOverride()) {
            data.setManualOverride(true);
        }
        return service.saveManualReading(data);
    }
}
