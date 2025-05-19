package com.example.greenhouseapplication.backend.controller;

import com.example.greenhouseapplication.backend.model.Notification;
import com.example.greenhouseapplication.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")                // allow all front-end origins
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService service;

    /* ---------- basic CRUD ---------- */

    @GetMapping
    public List<Notification> getAll() {
        return service.getAll();           // already sorted (newest first)
    }

    @PostMapping
    public Notification create(@RequestBody Notification n) {
        return service.create(n);
    }

    @GetMapping("/{id}")
    public Optional<Notification> getById(@PathVariable String id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    /* ---------- acknowledge (PUT) ---------- */

    @PutMapping("/{id}/acknowledge")
    public Optional<Notification> acknowledge(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> body) {

        String remark = body == null ? null : body.get("remark");
        return service.acknowledge(id, remark);   // returns the updated doc (if any)
    }
}
