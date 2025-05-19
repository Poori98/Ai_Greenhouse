package com.example.greenhouseapplication.backend.util;

import com.example.greenhouseapplication.backend.model.Greenhouse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.example.greenhouseapplication.backend.model.SensorData;
import com.example.greenhouseapplication.backend.model.SensorData.Readings;
import com.example.greenhouseapplication.backend.repository.GreenhouseRepository;
import com.example.greenhouseapplication.backend.repository.SensorDataRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class DataSeeder {
    @Value("${historic.start}")
    private String historicStart;

    @Value("${historic.manual.probability}")
    private double manualProb;

    private final SensorDataRepository sensorRepo;
    private final GreenhouseRepository greenhouseRepo;
    private final Random rnd = new Random();

    public DataSeeder(SensorDataRepository sensorRepo,
                      GreenhouseRepository greenhouseRepo) {
        this.sensorRepo     = sensorRepo;
        this.greenhouseRepo = greenhouseRepo;
    }

    @PostConstruct
    public void seedDataRange() {
        LocalDateTime start = LocalDateTime.parse(historicStart);
        LocalDateTime now   = LocalDateTime.now(ZoneOffset.UTC);

        List<String> ghIds = greenhouseRepo.findAll()
                .stream()
                .map(Greenhouse::getId)
                .toList();

        for (String ghId : ghIds) {
            // Back-fill
            Optional<SensorData> oldestOpt = Optional.ofNullable(
                    sensorRepo.findFirstByGreenhouseIdOrderByRecordedAtAsc(ghId));
            LocalDateTime backFrom = oldestOpt
                    .map(d -> d.getRecordedAt().minusHours(1))
                    .orElse(now);

            while (backFrom.isAfter(start)) {
                sensorRepo.save(makeReading(ghId, backFrom));
                backFrom = backFrom.minusHours(1);
            }

            // Forward-fill
            Optional<SensorData> latestOpt = Optional.ofNullable(
                    sensorRepo.findFirstByGreenhouseIdOrderByRecordedAtDesc(ghId));
            LocalDateTime forwardFrom = latestOpt
                    .map(d -> d.getRecordedAt().plusHours(1))
                    .orElse(start);

            while (!forwardFrom.isAfter(now)) {
                sensorRepo.save(makeReading(ghId, forwardFrom));
                forwardFrom = forwardFrom.plusHours(1);
            }
        }
    }

    private SensorData makeReading(String ghId, LocalDateTime ts) {
        boolean manual = rnd.nextDouble() < manualProb;
        SensorData d = new SensorData();
        d.setId(ghId + "_" + ts.toEpochSecond(ZoneOffset.UTC));
        d.setGreenhouseId(ghId);
        d.setRecordedAt(ts);
        d.setManualOverride(manual);
        d.setControlledBy(manual ? "Manual" : "AI");
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
        return Math.round((min + rnd.nextDouble()*(max-min))*10)/10.0;
    }
    private int randInt(int min, int max) {
        return min + rnd.nextInt(max-min+1);
    }
}
