package io.github.shazxrin.alif.prayer.service;

import io.github.shazxrin.alif.notification.service.NotificationService;
import io.github.shazxrin.alif.prayer.exception.PrayerTimingNotFoundException;
import io.github.shazxrin.alif.prayer.model.PrayerPeriod;
import io.github.shazxrin.alif.prayer.model.PrayerTiming;
import io.github.shazxrin.alif.prayer.repository.PrayerTimingRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

@Service
public class PrayerTimingService {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final PrayerTimingRepository prayerTimingRepository;
    private final NotificationService notificationService;

    public PrayerTimingService(
        PrayerTimingRepository prayerTimingRepository,
        NotificationService notificationService
    ) {
        this.prayerTimingRepository = prayerTimingRepository;
        this.notificationService = notificationService;
    }

    public PrayerTiming getPrayerTimingByDate(LocalDate date) {
        PrayerTiming prayerTiming = prayerTimingRepository.getByDate(date);
        if (prayerTiming == null) {
            throw new PrayerTimingNotFoundException("Prayer timing not found!");
        }
        return prayerTiming;
    }

    public void remindPrayerTiming(PrayerPeriod prayerPeriod, LocalDateTime dateTime) {
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
}
