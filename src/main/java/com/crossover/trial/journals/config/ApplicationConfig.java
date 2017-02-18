package com.crossover.trial.journals.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("application")
public class ApplicationConfig {
    public static class Aws {
        private String accessKeyId;
        private String secretAccessKey;
        private String region;
        private String journalChangesArn;

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getSecretAccessKey() {
            return secretAccessKey;
        }

        public void setSecretAccessKey(String secretAccessKey) {
            this.secretAccessKey = secretAccessKey;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getJournalChangesArn() {
            return journalChangesArn;
        }

        public void setJournalChangesArn(String journalChangesArn) {
            this.journalChangesArn = journalChangesArn;
        }
    }

    private Aws aws;

    public Aws getAws() {
        return aws;
    }

    public void setAws(Aws aws) {
        this.aws = aws;
    }

}
