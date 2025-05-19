package com.example.greenhouseapplication.backend.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.greenhouseapplication.backend.model.SensorData;
import com.example.greenhouseapplication.backend.repository.SensorDataRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorDataService {
    private final SensorDataRepository repo;

    public SensorDataService(SensorDataRepository repo) {
        this.repo = repo;
    }

    public List<SensorData> getHistoricalSensorData(
            String greenhouseId,
            LocalDateTime start,
            LocalDateTime end) {
        return repo.findByGreenhouseIdAndRecordedAtBetween(greenhouseId, start, end);
    }

    public SensorData getLatestReading(String greenhouseId) {
        return repo.findFirstByGreenhouseIdOrderByRecordedAtDesc(greenhouseId);
    }

    // Optional: fetch last N readings
    public List<SensorData> getLatestReadings(String greenhouseId, int count) {
        return repo.findByGreenhouseIdOrderByRecordedAtDesc(
                greenhouseId, PageRequest.of(0, count));
    }

    public List<SensorData> getManualOverrides(String greenhouseId) {
        return repo.findByGreenhouseIdAndIsManualOverrideTrueOrderByRecordedAtDesc(greenhouseId);
    }


    public SensorData saveManualReading(SensorData data) {
        // you might want to validate greenhouseId, timestamps, etc.
        return repo.save(data);
    }

}
