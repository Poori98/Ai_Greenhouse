package com.example.greenhouseapplication.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "Notifications")
public class Notification {

    @Id
    private String id;

    private String title;
    private String message;
    private String type;        // Sensor, Hardware, Environment, …
    private String severity;    // Info, Warning, Critical, …
    private String greenhouse;  // e.g. "GH-01"
    private Instant timestamp;  // ISO time the event occurred
    private boolean acknowledged = false;   // default

    /* ---------- getters & setters ---------- */

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getGreenhouse() { return greenhouse; }
    public void setGreenhouse(String greenhouse) { this.greenhouse = greenhouse; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public boolean isAcknowledged() { return acknowledged; }
    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
}
