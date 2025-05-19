package com.example.greenhouseapplication.backend.controller;

import com.example.greenhouseapplication.backend.model.MaintenanceTask;
import com.example.greenhouseapplication.backend.service.MaintenanceTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*") // allows requests from any domain; configure as needed
@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceTaskController {

    @Autowired
    private MaintenanceTaskService service;

    @GetMapping
    public List<MaintenanceTask> getAllTasks() {
        return service.getAllTasks();
    }

    @PostMapping
    public MaintenanceTask createTask(@RequestBody MaintenanceTask task) {
        return service.createTask(task);
    }

    @GetMapping("/{id}")
    public Optional<MaintenanceTask> getTaskById(@PathVariable String id) {
        return service.getTaskById(id);
    }

    @PutMapping("/{id}")
    public MaintenanceTask updateTask(@PathVariable String id, @RequestBody MaintenanceTask updatedTask) {
        return service.updateTask(id, updatedTask);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        service.deleteTask(id);
    }
}
