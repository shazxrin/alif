package io.github.shazxrin.alif.prayer.service;

import io.github.shazxrin.alif.notification.service.NotificationService;
import io.github.shazxrin.alif.prayer.exception.PrayerTimingNotFoundException;
import io.github.shazxrin.alif.prayer.model.PrayerPeriod;
import io.github.shazxrin.alif.prayer.model.PrayerTiming;
import io.github.shazxrin.alif.prayer.repository.PrayerTimingRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class PrayerTimingService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM YYYY");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final PrayerTimingRepository prayerTimingRepository;
    private final NotificationService notificationService;
    private final TaskScheduler taskScheduler;

    public PrayerTimingService(
        PrayerTimingRepository prayerTimingRepository,
        NotificationService notificationService,
        TaskScheduler taskScheduler
    ) {
        this.prayerTimingRepository = prayerTimingRepository;
        this.notificationService = notificationService;
        this.taskScheduler = taskScheduler;
    }

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

    private void remindPrayerTiming(PrayerPeriod prayerPeriod, LocalDateTime dateTime) {
        String title = "Unknown prayer time.";
        String message = "Time to pray for unknown. Always good to pray for no reason.";
        switch (prayerPeriod) {
            case SUBUH -> {
                title = "It's Subuh prayer time.";
                message = String.format("It is %s. It is time to pray Subuh", dateTime.format(TIME_FORMAT));
            }
            case SYURUK -> {
                title = "It's Syuruk.";
                message = String.format("It is %s. Rise and shine!", dateTime.format(TIME_FORMAT));
            }
            case ZOHOR -> {
                title = "It's Zohor prayer time.";
                message = String.format("It is %s. It is time to pray Zohor", dateTime.format(TIME_FORMAT));
            }
            case ASAR -> {
                title = "It's Asar prayer time.";
                message = String.format("It is %s. It is time to pray Asar", dateTime.format(TIME_FORMAT));
            }
            case MAGHRIB -> {
                title = "It's Maghrib prayer time.";
                message = String.format("It is %s. It is time to pray Maghrib", dateTime.format(TIME_FORMAT));
            }
            case ISYAK -> {
                title = "It's Isyak prayer time.";
                message = String.format("It is %s. It is time to pray Isyak", dateTime.format(TIME_FORMAT));
            }
        }

        notificationService.sendNotification(title, message);
    }

    private void schedulePrayerTimingPeriodReminder(PrayerPeriod period, String time) {
        LocalTime prayerTime = convertTime(time);
        LocalDateTime prayerDateTime = LocalDateTime.of(LocalDate.now(), prayerTime);
        taskScheduler.schedule(
            () -> remindPrayerTiming(period, prayerDateTime),
            getInstant(prayerDateTime)
        );
    }

    public void schedulePrayerTimingReminders() {
        PrayerTiming prayerTiming = getPrayerTimingByDate(LocalDate.now());

        schedulePrayerTimingPeriodReminder(PrayerPeriod.SUBUH, prayerTiming.getSubuh());
        schedulePrayerTimingPeriodReminder(PrayerPeriod.SYURUK, prayerTiming.getSyuruk());
        schedulePrayerTimingPeriodReminder(PrayerPeriod.ZOHOR, prayerTiming.getZohor());
        schedulePrayerTimingPeriodReminder(PrayerPeriod.ASAR, prayerTiming.getAsar());
        schedulePrayerTimingPeriodReminder(PrayerPeriod.MAGHRIB, prayerTiming.getMaghrib());
        schedulePrayerTimingPeriodReminder(PrayerPeriod.ISYAK, prayerTiming.getIsyak());
    }

    public PrayerTiming getPrayerTimingByDate(LocalDate date) {
        PrayerTiming prayerTiming = prayerTimingRepository.getByDate(date);
        if (prayerTiming == null) {
            throw new PrayerTimingNotFoundException("Prayer timing not found!");
        }
        return prayerTiming;
    }

    public void remindPrayerTimingSummary() {
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
