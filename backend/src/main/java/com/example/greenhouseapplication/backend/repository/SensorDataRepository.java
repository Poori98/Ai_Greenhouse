package com.example.greenhouseapplication.backend.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.greenhouseapplication.backend.model.SensorData;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorDataRepository extends MongoRepository<SensorData, String> {

    // Historical range query
    List<SensorData> findByGreenhouseIdAndRecordedAtBetween(
            String greenhouseId,
            LocalDateTime start,
            LocalDateTime end);

    // Single newest and oldest record
    SensorData findFirstByGreenhouseIdOrderByRecordedAtDesc(String greenhouseId);
    SensorData findFirstByGreenhouseIdOrderByRecordedAtAsc(String greenhouseId);

    // Last-N readings
    List<SensorData> findByGreenhouseIdOrderByRecordedAtDesc(
            String greenhouseId,
            Pageable pageable);

    List<SensorData> findByGreenhouseIdAndIsManualOverrideTrueOrderByRecordedAtDesc(String greenhouseId);



}
