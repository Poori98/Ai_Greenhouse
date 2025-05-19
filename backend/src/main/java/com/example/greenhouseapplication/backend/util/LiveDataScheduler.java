package com.example.greenhouseapplication.backend.util;

import com.example.greenhouseapplication.backend.model.Greenhouse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.greenhouseapplication.backend.model.SensorData;
import com.example.greenhouseapplication.backend.model.SensorData.Readings;
import com.example.greenhouseapplication.backend.repository.GreenhouseRepository;
import com.example.greenhouseapplication.backend.repository.SensorDataRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class LiveDataScheduler {
    private final SensorDataRepository sensorRepo;
    private final GreenhouseRepository greenhouseRepo;

    public LiveDataScheduler(SensorDataRepository sensorRepo,
                             GreenhouseRepository greenhouseRepo) {
        this.sensorRepo     = sensorRepo;
        this.greenhouseRepo = greenhouseRepo;
    }

    /** Runs every ${live.insert.fixedRate} ms, inserts an AI reading per greenhouse */
    @Scheduled(fixedRateString = "${live.insert.fixedRate}")
    public void appendLiveData() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        greenhouses().forEach(ghId -> sensorRepo.save(aiReading(ghId, now)));
    }

    private Iterable<String> greenhouses() {
        return greenhouseRepo.findAll().stream().map(Greenhouse::getId)::iterator;
    }

    private SensorData aiReading(String ghId, LocalDateTime ts) {
        SensorData d = new SensorData();
        d.setId(ghId + "_" + ts.toEpochSecond(ZoneOffset.UTC));
        d.setGreenhouseId(ghId);
        d.setRecordedAt(ts);
        d.setManualOverride(false);
        d.setControlledBy("AI");
        d.setReadings(randomReadings());
        return d;
    }

    private Readings randomReadings() {
        Readings r = new Readings();
        r.setTemperature(rand(18,30));
        r.setHumidity(rand(30,90));
        r.setLightIntensity(randInt(200,2000));
        r.setCo2Levels(randInt(300,1000));
        r.setSoilMoisture(rand(10,60));
        return r;
    }

    private double rand(double min, double max) {
        return Math.round((min + Math.random()*(max-min))*10)/10.0;
    }
    private int randInt(int min, int max) {
        return min + (int)(Math.random()*(max-min+1));
    }
}
