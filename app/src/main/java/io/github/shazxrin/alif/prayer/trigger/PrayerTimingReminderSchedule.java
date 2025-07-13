package io.github.shazxrin.alif.prayer.trigger;

import io.github.shazxrin.alif.prayer.service.PrayerTimingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PrayerTimingReminderSchedule {
    private static final Logger log = LoggerFactory.getLogger(PrayerTimingReminderSchedule.class);

    private final PrayerTimingService prayerTimingService;

    public PrayerTimingReminderSchedule(PrayerTimingService prayerTimingService) {
        this.prayerTimingService = prayerTimingService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledSchedulePrayerTimingReminders() {
        log.info("Running scheduling prayer timing reminders on new day schedule.");
        prayerTimingService.scheduleAllNotifyPrayerTimingPeriods();
    }

    @Scheduled(cron = "${app.prayer.summary.schedule-cron}")
    public void runScheduledPrayerTimingSummaryReminder() {
        log.info("Running summary reminder schedule.");
        prayerTimingService.notifyAllPrayerTimingPeriods();
    }
}
