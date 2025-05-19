package com.example.greenhouseapplication.backend.service;

import com.example.greenhouseapplication.backend.model.Notification;
import com.example.greenhouseapplication.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repo;

    /* ---------- CRUD helpers ---------- */

    public List<Notification> getAll() {
        // newest first
        return repo.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }

    public Notification create(Notification n) {
        // ensure timestamp exists
        if (n.getTimestamp() == null) n.setTimestamp(Instant.now());
        return repo.save(n);
    }

    public Optional<Notification> getById(String id) {
        return repo.findById(id);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }

    /* ---------- special actions ---------- */

    public Optional<Notification> acknowledge(String id, String remark) {
        var opt = repo.findById(id);
        opt.ifPresent(n -> {
            n.setAcknowledged(true);
            // optional “remark” can be appended to message
            if (remark != null && !remark.isBlank()) {
                n.setMessage(n.getMessage() + " — Remark: " + remark.trim());
            }
            repo.save(n);
        });
        return opt;
    }
}

