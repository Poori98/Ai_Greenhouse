package com.example.greenhouseapplication.backend.service;

import com.example.greenhouseapplication.backend.model.MaintenanceTask;
import com.example.greenhouseapplication.backend.repository.MaintenanceTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceTaskService {

    @Autowired
    private MaintenanceTaskRepository repository;

    public List<MaintenanceTask> getAllTasks() {
        return repository.findAll();
    }

    public MaintenanceTask createTask(MaintenanceTask task) {
        // You can add extra validation or logic here
        return repository.save(task);
    }

    public Optional<MaintenanceTask> getTaskById(String id) {
        return repository.findById(id);
    }

    public MaintenanceTask updateTask(String id, MaintenanceTask updatedTask) {
        Optional<MaintenanceTask> existing = repository.findById(id);
        if (existing.isPresent()) {
            MaintenanceTask task = existing.get();
            task.setTaskName(updatedTask.getTaskName());
            task.setDueDate(updatedTask.getDueDate());
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus());
            return repository.save(task);
        } else {
            return null; // or throw an exception
        }
    }

    public void deleteTask(String id) {
        repository.deleteById(id);
    }
}

