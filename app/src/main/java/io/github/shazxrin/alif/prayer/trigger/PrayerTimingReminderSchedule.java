package io.github.shazxrin.alif.prayer.trigger;

import io.github.shazxrin.alif.prayer.model.PrayerPeriod;
import io.github.shazxrin.alif.prayer.model.PrayerTiming;
import io.github.shazxrin.alif.prayer.service.PrayerTimingService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PrayerTimingReminderSchedule {
    private final PrayerTimingService prayerTimingService;
    private final TaskScheduler taskScheduler;

    private LocalTime convertTime(String time) {
        String[] split = time.split(":");
        if  (split.length != 2) {
            throw new IllegalArgumentException("Invalid time format!");
        }

        int hour = Integer.parseInt(split[0]);
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Invalid hour!");
        }
        int minute = Integer.parseInt(split[1]);
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Invalid minute!");
        }

        return LocalTime.of(hour, minute);
    }

    private Instant getInstant(LocalDateTime dateTime) {
        return dateTime
            .atZone(ZoneId.systemDefault())
            .toInstant();
    }

    private void schedulePrayerTimingPeriodReminder(PrayerPeriod period, String time) {
        LocalTime prayerTime = convertTime(time);
        LocalDateTime prayerDateTime = LocalDateTime.of(LocalDate.now(), prayerTime);
        taskScheduler.schedule(
            () -> prayerTimingService.remindPrayerTiming(period, prayerDateTime),
            getInstant(prayerDateTime)
        );
    }

    public PrayerTimingReminderSchedule(PrayerTimingService prayerTimingService, TaskScheduler taskScheduler) {
        this.prayerTimingService = prayerTimingService;
        this.taskScheduler = taskScheduler;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void schedulePrayerTimingReminders() {
        PrayerTiming prayerTiming = prayerTimingService.getPrayerTimingByDate(LocalDate.now());

        schedulePrayerTimingPeriodReminder(PrayerPeriod.SUBUH, prayerTiming.getSubuh());
        schedulePrayerTimingPeriodReminder(PrayerPeriod.SYURUK, prayerTiming.getSyuruk());
        schedulePrayerTimingPeriodReminder(PrayerPeriod.ZOHOR, prayerTiming.getZohor());
        schedulePrayerTimingPeriodReminder(PrayerPeriod.ASAR, prayerTiming.getAsar());
        schedulePrayerTimingPeriodReminder(PrayerPeriod.MAGHRIB, prayerTiming.getMaghrib());
        schedulePrayerTimingPeriodReminder(PrayerPeriod.ISYAK, prayerTiming.getIsyak());
    }
}
