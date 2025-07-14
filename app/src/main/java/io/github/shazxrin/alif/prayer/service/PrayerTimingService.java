package io.github.shazxrin.alif.prayer.service;

import io.github.shazxrin.alif.notification.service.NotificationService;
import io.github.shazxrin.alif.prayer.configuration.PrayerTimingConfiguration;
import io.github.shazxrin.alif.prayer.exception.PrayerTimingNotFoundException;
import io.github.shazxrin.alif.prayer.model.PrayerPeriod;
import io.github.shazxrin.alif.prayer.model.PrayerTiming;
import io.github.shazxrin.alif.prayer.repository.PrayerTimingRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class PrayerTimingService {
    private static final Logger log = LoggerFactory.getLogger(PrayerTimingService.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM YYYY");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final PrayerTimingConfiguration prayerTimingConfiguration;
    private final PrayerTimingRepository prayerTimingRepository;
    private final NotificationService notificationService;
    private final TaskScheduler taskScheduler;

    public PrayerTimingService(
        PrayerTimingConfiguration prayerTimingConfiguration,
        PrayerTimingRepository prayerTimingRepository,
        NotificationService notificationService,
        TaskScheduler taskScheduler
    ) {
        this.prayerTimingConfiguration = prayerTimingConfiguration;
        this.prayerTimingRepository = prayerTimingRepository;
        this.notificationService = notificationService;
        this.taskScheduler = taskScheduler;
    }

    public PrayerTiming getPrayerTimingByDate(LocalDate date) {
        PrayerTiming prayerTiming = prayerTimingRepository.getByDate(date);
        if (prayerTiming == null) {
            throw new PrayerTimingNotFoundException("Prayer timing not found!");
        }
        return prayerTiming;
    }

    private LocalTime convertTime(String time) {
        String[] split = time.split(":");
        if (split.length != 2) {
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

    /*
        Notification for prayer timing period.
     */

    private void notifyPrayerTimingPeriod(PrayerPeriod prayerPeriod, LocalDateTime dateTime) {
        String title = "Unknown prayer time.";
        String message = "Time to pray for unknown. Always good to pray for no reason.";
        switch (prayerPeriod) {
            case SUBUH, ZOHOR, ASAR, MAGHRIB, ISYAK -> {
                String prayerName = prayerPeriod.name().toLowerCase();

                title = String.format("It is %s prayer time.", prayerName);
                message = String.format("It is time to pray %s at %s.", prayerName, dateTime.format(TIME_FORMAT));
            }
            case SYURUK -> {
                title = "It is syuruk.";
                message = String.format("It is syuruk at %s.", dateTime.format(TIME_FORMAT));
            }
        }

        notificationService.sendNotification(title, message);
    }

    private void scheduleNotifyPrayerTimingPeriod(PrayerPeriod period, String time) {
        LocalTime prayerTime = convertTime(time);

        if (prayerTime.isBefore(LocalTime.now())) {
            log.info("Skipping scheduling prayer timing reminder for {} at {}.", period, prayerTime);
            return;
        }

        LocalDateTime prayerDateTime = LocalDateTime.of(LocalDate.now(), prayerTime);
        taskScheduler.schedule(
            () -> notifyPrayerTimingPeriod(period, prayerDateTime),
            getInstant(prayerDateTime)
        );

        log.info("Scheduled prayer timing reminder for {} at {}.", period, prayerTime);
    }

    public void scheduleAllNotifyPrayerTimingPeriods() {
        PrayerTiming prayerTiming = getPrayerTimingByDate(LocalDate.now());

        scheduleNotifyPrayerTimingPeriod(PrayerPeriod.SUBUH, prayerTiming.getSubuh());
        scheduleNotifyPrayerTimingPeriod(PrayerPeriod.SYURUK, prayerTiming.getSyuruk());
        scheduleNotifyPrayerTimingPeriod(PrayerPeriod.ZOHOR, prayerTiming.getZohor());
        scheduleNotifyPrayerTimingPeriod(PrayerPeriod.ASAR, prayerTiming.getAsar());
        scheduleNotifyPrayerTimingPeriod(PrayerPeriod.MAGHRIB, prayerTiming.getMaghrib());
        scheduleNotifyPrayerTimingPeriod(PrayerPeriod.ISYAK, prayerTiming.getIsyak());
    }

    /*
        Notification for pre-prayer timing period.
     */

    private void notifyPrePrayerTimingPeriod(PrayerPeriod prayerPeriod, LocalDateTime dateTime) {
        String title = "Unknown pre-prayer time.";
        String message = "Time to pray for unknown soon. Always good to pray for no reason.";
        switch (prayerPeriod) {
            case SUBUH, ZOHOR, ASAR, MAGHRIB, ISYAK -> {
                String prayerName = prayerPeriod.name().toLowerCase();

                title = String.format("It is almost %s prayer time.", prayerName);
                message = String.format(
                    "It is almost time to pray %s at %s.",
                    prayerName,
                    dateTime.format(TIME_FORMAT)
                );
            }
            case SYURUK -> {
                title = "It is almost syuruk.";
                message = String.format("It is almost syuruk at %s.", dateTime.format(TIME_FORMAT));
            }
        }

        notificationService.sendNotification(title, message);
    }

    private void scheduleNotifyPrePrayerTimingPeriod(PrayerPeriod period, String time, Duration durationBefore) {
        LocalTime prayerTime = convertTime(time);
        LocalTime prePrayerTime = prayerTime.minus(durationBefore);

        if (prePrayerTime.isBefore(LocalTime.now())) {
            log.info("Skipping scheduling pre-prayer timing reminder for {} at {}.", period, prePrayerTime);
            return;
        }

        LocalDateTime prayerDateTime = LocalDateTime.of(LocalDate.now(), prayerTime);
        LocalDateTime prePrayerDateTime = LocalDateTime.of(LocalDate.now(), prePrayerTime);
        taskScheduler.schedule(
            () -> notifyPrePrayerTimingPeriod(period, prayerDateTime),
            getInstant(prePrayerDateTime)
        );

        log.info("Scheduled pre-prayer timing reminder for {} at {}.", period, prePrayerTime);
    }

    public void scheduleAllNotifyPrePrayerTimingPeriods() {
        PrayerTiming prayerTiming = getPrayerTimingByDate(LocalDate.now());
        Duration durationBefore = prayerTimingConfiguration.getPreReminder().getDurationBefore();

        scheduleNotifyPrePrayerTimingPeriod(PrayerPeriod.SUBUH, prayerTiming.getSubuh(), durationBefore);
        scheduleNotifyPrePrayerTimingPeriod(PrayerPeriod.SYURUK, prayerTiming.getSyuruk(), durationBefore);
        scheduleNotifyPrePrayerTimingPeriod(PrayerPeriod.ZOHOR, prayerTiming.getZohor(), durationBefore);
        scheduleNotifyPrePrayerTimingPeriod(PrayerPeriod.ASAR, prayerTiming.getAsar(), durationBefore);
        scheduleNotifyPrePrayerTimingPeriod(PrayerPeriod.MAGHRIB, prayerTiming.getMaghrib(), durationBefore);
        scheduleNotifyPrePrayerTimingPeriod(PrayerPeriod.ISYAK, prayerTiming.getIsyak(), durationBefore);
    }

    /*
        Notification for all prayer timing periods.
     */

    public void notifyAllPrayerTimingPeriods() {
        PrayerTiming prayerTiming = getPrayerTimingByDate(LocalDate.now());

        String titleTemplate = "Prayer timings for %s";
        String messageTemplate = """
            Here is a summary of today's prayer timings:
            Subuh: %s
            Syuruk: %s
            Zohor: %s
            Asar: %s
            Maghrib: %s
            Isyak: %s
            """;

        String title = String.format(titleTemplate, LocalDate.now().format(DATE_FORMAT));
        String message = String.format(
            messageTemplate,
            convertTime(prayerTiming.getSubuh()).format(TIME_FORMAT),
            convertTime(prayerTiming.getSyuruk()).format(TIME_FORMAT),
            convertTime(prayerTiming.getZohor()).format(TIME_FORMAT),
            convertTime(prayerTiming.getAsar()).format(TIME_FORMAT),
            convertTime(prayerTiming.getMaghrib()).format(TIME_FORMAT),
            convertTime(prayerTiming.getIsyak()).format(TIME_FORMAT)
        );

        notificationService.sendNotification(title, message);
    }
}
