package com.example.greenhouseapplication.backend.repository;

import com.example.greenhouseapplication.backend.model.SensorAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SensorAnalysisRepository extends MongoRepository<SensorAnalysis, String> {
    Optional<SensorAnalysis> findTopByGreenhouseIdAndSensorTypeOrderByAnalysisTimestampDesc(
            String greenhouseId, String sensorType);

    // new: fetch by greenhouseId + sensorType + since
    List<SensorAnalysis> findByGreenhouseIdAndSensorTypeAndAnalysisTimestampAfter(
            String greenhouseId,
            String sensorType,
            LocalDateTime since
    );

    boolean existsByGreenhouseIdAndSensorTypeAndIntervalStartAndIntervalEnd(
            String greenhouseId,
            String sensorType,
            LocalDateTime intervalStart,
            LocalDateTime intervalEnd
    );
}
