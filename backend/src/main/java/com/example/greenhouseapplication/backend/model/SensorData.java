package com.example.greenhouseapplication.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.LocalDateTime;

@Document(collection = "SensorData")
@Data
public class SensorData {
    @Id
    private String id;                      // Unique sensor data record ID
    private String greenhouseId;            // Link to the greenhouse
    private LocalDateTime recordedAt;       // The date and time when the reading was recorded

    // Environmental sensor readings
    private Readings readings;

    // Fields indicating control source (for AI vs. manual)
    private boolean isManualOverride;       // True if the reading/control was manually set
    private String controlledBy;            // "AI" or "Manual"

    @Data
    public static class Readings {
        private double temperature;         // Temperature reading
        private double humidity;            // Humidity reading
        private double lightIntensity;      // Light intensity reading
        private double co2Levels;           // CO₂ levels reading
        private double soilMoisture;        // Soil moisture reading
    }
}
