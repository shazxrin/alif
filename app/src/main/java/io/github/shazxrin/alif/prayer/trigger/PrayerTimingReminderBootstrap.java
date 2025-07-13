package io.github.shazxrin.alif.prayer.trigger;

import io.github.shazxrin.alif.prayer.service.PrayerTimingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PrayerTimingReminderBootstrap {
    private static final Logger log = LoggerFactory.getLogger(PrayerTimingReminderBootstrap.class);

    private final PrayerTimingService prayerTimingService;

    public PrayerTimingReminderBootstrap(PrayerTimingService prayerTimingService) {
        this.prayerTimingService = prayerTimingService;
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void checkInBootstrap() {
        log.info("Scheduling prayer timing reminders on application startup.");
        prayerTimingService.scheduleAllNotifyPrayerTimingPeriods();
    }
}
