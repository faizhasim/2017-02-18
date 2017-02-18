package com.crossover.trial.journals.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


// TODO: Implement
@Service
public class DailyEmailSendingService {

    private static class Job implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(Job.class);
        @Override
        public void run() {
            // Poll SQS, send email, then destroy message from the queue.
            logger.info("Supposedly this should poll SQS, send email, then destroy message from the queue.");
        }
    }

    private ScheduledExecutorService scheduledExecutorService;

    @PostConstruct
    public void postConstruct() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Job(), 0, 1, TimeUnit.DAYS);
    }

    @PreDestroy
    public void preDestroy() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            try {
                if (!scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduledExecutorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduledExecutorService.shutdownNow();
            }
        }
    }
}
