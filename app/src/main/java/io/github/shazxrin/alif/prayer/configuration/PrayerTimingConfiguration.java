package io.github.shazxrin.alif.prayer.configuration;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.prayer")
@Configuration
public class PrayerTimingConfiguration {
    public static class Summary {
        public String scheduledCron;

        public String getScheduledCron() {
            return scheduledCron;
        }

        public void setScheduledCron(String scheduledCron) {
            this.scheduledCron = scheduledCron;
        }
    }

    public static class PreReminder {
        public Duration durationBefore;

        public Duration getDurationBefore() {
            return durationBefore;
        }

        public void setDurationBefore(Duration durationBefore) {
            this.durationBefore = durationBefore;
        }
    }

    public PreReminder preReminder;
    public Summary summary;

    public void setPreReminder(PreReminder preReminder) {
        this.preReminder = preReminder;
    }

    public PreReminder getPreReminder() {
        return preReminder;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public Summary getSummary() {
        return summary;
    }
}
