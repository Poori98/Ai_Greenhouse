// src/main/java/com/example/greenhouseapplication/backend/model/SensorAnalysis.java
package com.example.greenhouseapplication.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "SensorAnalysis")
@Data
public class SensorAnalysis {
    @Id
    private String id;

    private String greenhouseId;
    private String sensorType;

    private LocalDateTime intervalStart;
    private LocalDateTime intervalEnd;
    private LocalDateTime analysisTimestamp;

    private String summary;
    private List<String> recommendations;
    private List<String> actions;
}
