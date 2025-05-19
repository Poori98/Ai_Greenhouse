package com.example.greenhouseapplication.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "MaintenanceTask")

public class MaintenanceTask {

    @Id
    private String id;
    private String taskName;
    private String dueDate;   // or LocalDate if you prefer
    private String description;
    private String status;    // e.g. "Upcoming", "Overdue", "Completed"

    // Constructors
    public MaintenanceTask() {
    }

    public MaintenanceTask(String taskName, String dueDate, String description, String status) {
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.description = description;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

