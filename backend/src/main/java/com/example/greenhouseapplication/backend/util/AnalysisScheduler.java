package com.example.greenhouseapplication.backend.util;

import com.example.greenhouseapplication.backend.model.SensorAnalysis;
import com.example.greenhouseapplication.backend.model.SensorData;
import com.example.greenhouseapplication.backend.repository.SensorAnalysisRepository;
import com.example.greenhouseapplication.backend.service.AIAnalysisService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class AnalysisScheduler {
    private static final Logger log = LoggerFactory.getLogger(AnalysisScheduler.class);

    /** The five sensor types we analyze separately */
    private static final List<String> SENSOR_TYPES = List.of(
            "temperature", "humidity", "lightIntensity", "co2Levels", "soilMoisture"
    );

    @Value("${historic.start}")
    private String historicStart;

    @Value("${analysis.batch.minutes}")
    private int batchMinutes;

    private final MongoTemplate mongoTemplate;
    private final SensorAnalysisRepository analysisRepo;
    private final AIAnalysisService aiService;

    // Single-threaded executor for backfill
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Token-bucket state for rate limiting (~30 calls/min)
    private long lastRefillTime = System.nanoTime();
    private double tokens = 30.0;

    public AnalysisScheduler(
            MongoTemplate mongoTemplate,
            SensorAnalysisRepository analysisRepo,
            AIAnalysisService aiService
    ) {
        this.mongoTemplate = mongoTemplate;
        this.analysisRepo  = analysisRepo;
        this.aiService     = aiService;
    }

    @PostConstruct
    public void scheduleBackfill() {
        executor.submit(this::backfillHistoricalAnalysis);
    }

    private List<String> loadAllGreenhouseIds() {
        return mongoTemplate
                .query(SensorData.class)
                .distinct("greenhouseId")
                .as(String.class)
                .all();
    }

    private synchronized void acquirePermit() {
        // refill logic
        final double maxTokens = 30.0;
        final double refillRatePerNano = 30.0 / TimeUnit.MINUTES.toNanos(1);

        long now = System.nanoTime();
        double newTokens = (now - lastRefillTime) * refillRatePerNano;
        tokens = Math.min(maxTokens, tokens + newTokens);
        lastRefillTime = now;

        // wait until at least 1 token
        while (tokens < 1.0) {
            double nanosToWait = (1.0 - tokens) / refillRatePerNano;
            try {
                TimeUnit.NANOSECONDS.sleep((long) nanosToWait);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            now = System.nanoTime();
            newTokens = (now - lastRefillTime) * refillRatePerNano;
            tokens = Math.min(maxTokens, tokens + newTokens);
            lastRefillTime = now;
        }
        tokens -= 1.0;
    }

    private void backfillHistoricalAnalysis() {
        log.info("Starting historical backfill analysis...");
        LocalDateTime windowStart = LocalDateTime.parse(historicStart);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        for (String ghId : loadAllGreenhouseIds()) {
            while (windowStart.isBefore(now)) {
                LocalDateTime windowEnd = windowStart.plusMinutes(batchMinutes);

                // fetch raw SensorData in this interval
                List<SensorData> batch = mongoTemplate
                        .query(SensorData.class)
                        .matching(Query.query(
                                Criteria.where("greenhouseId").is(ghId)
                                        .and("recordedAt").gte(windowStart).lt(windowEnd)
                        ))
                        .all();

                if (!batch.isEmpty()) {
                    for (String sensorType : SENSOR_TYPES) {
                        // skip if already analyzed
                        boolean exists = analysisRepo
                                .existsByGreenhouseIdAndSensorTypeAndIntervalStartAndIntervalEnd(
                                        ghId, sensorType, windowStart, windowEnd);
                        if (exists) continue;

                        try {
                            acquirePermit();
                            SensorAnalysis sa = aiService.analyzeWindow(ghId, batch, sensorType);
                            analysisRepo.save(sa);
                        } catch (Exception e) {
                            log.warn("Backfill skip {} [{}→{}] {}: {}",
                                    sensorType, windowStart, windowEnd, ghId, e.getMessage());
                        }
                    }
                }

                windowStart = windowEnd;
            }
            // reset windowStart for next greenhouse
            windowStart = LocalDateTime.parse(historicStart);
        }

        log.info("Historical backfill analysis completed.");
    }

    @Scheduled(cron = "0 0/15 * * * ?")  // every 15 minutes
    public void analyzeLiveWindow() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime windowStart = now.minusMinutes(batchMinutes);

        for (String ghId : loadAllGreenhouseIds()) {
            // fetch raw SensorData in this live interval
            List<SensorData> batch = mongoTemplate
                    .query(SensorData.class)
                    .matching(Query.query(
                            Criteria.where("greenhouseId").is(ghId)
                                    .and("recordedAt").gte(windowStart).lt(now)
                    ))
                    .all();

            if (!batch.isEmpty()) {
                for (String sensorType : SENSOR_TYPES) {
                    boolean exists = analysisRepo
                            .existsByGreenhouseIdAndSensorTypeAndIntervalStartAndIntervalEnd(
                                    ghId, sensorType, windowStart, now);
                    if (exists) continue;

                    try {
                        acquirePermit();
                        SensorAnalysis sa = aiService.analyzeWindow(ghId, batch, sensorType);
                        analysisRepo.save(sa);
                    } catch (Exception e) {
                        log.warn("Live skip {} [{}→{}] {}: {}",
                                sensorType, windowStart, now, ghId, e.getMessage());
                    }
                }
            }
        }
    }
}
