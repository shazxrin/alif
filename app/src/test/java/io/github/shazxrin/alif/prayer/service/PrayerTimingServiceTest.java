package io.github.shazxrin.alif.prayer.service;

import io.github.shazxrin.alif.notification.service.NotificationService;
import io.github.shazxrin.alif.prayer.configuration.PrayerTimingConfiguration;
import io.github.shazxrin.alif.prayer.exception.PrayerTimingNotFoundException;
import io.github.shazxrin.alif.prayer.model.PrayerTiming;
import io.github.shazxrin.alif.prayer.repository.PrayerTimingRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PrayerTimingServiceTest {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Mock
    private PrayerTimingRepository prayerTimingRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private PrayerTimingConfiguration prayerTimingConfiguration;

    @Mock
    private PrayerTimingConfiguration.PreReminder preReminder;

    @InjectMocks
    private PrayerTimingService prayerTimingService;

    @Test
    public void testGetPrayerTimingByDate_whenTimingExists_shouldReturnTiming() {
        // Given
        LocalDate date = LocalDate.of(2023, 1, 1);
        PrayerTiming expectedTiming = new PrayerTiming(
            date,
            "05:30",
            "06:45",
            "12:15",
            "15:30",
            "18:45",
            "20:00"
        );
        when(prayerTimingRepository.getByDate(date)).thenReturn(expectedTiming);

        // When
        PrayerTiming actualTiming = prayerTimingService.getPrayerTimingByDate(date);

        // Then
        assertNotNull(actualTiming);
        assertEquals(expectedTiming, actualTiming);
        verify(prayerTimingRepository).getByDate(date);
    }

    @Test
    public void testGetPrayerTimingByDate_whenTimingDoesNotExist_shouldThrowException() {
        // Given
        LocalDate date = LocalDate.of(2023, 1, 1);
        when(prayerTimingRepository.getByDate(date)).thenReturn(null);

        // When & Then
        assertThrows(
            PrayerTimingNotFoundException.class, () -> {
                prayerTimingService.getPrayerTimingByDate(date);
            }
        );
        verify(prayerTimingRepository).getByDate(date);
    }

    @Test
    public void testNotifyPrayerTimingPeriodSummary_shouldSendNotificationWithSummary() {
        // Given
        LocalDate today = LocalDate.now();
        PrayerTiming prayerTiming = new PrayerTiming(
            today,
            "05:30",
            "06:45",
            "12:15",
            "15:30",
            "18:45",
            "20:00"
        );
        when(prayerTimingRepository.getByDate(today)).thenReturn(prayerTiming);

        // When
        prayerTimingService.notifyAllPrayerTimingPeriods();

        // Then
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(notificationService).sendNotification(any(), messageCaptor.capture());

        String message = messageCaptor.getValue();
        assertTrue(message.contains("Subuh: 05:30"));
        assertTrue(message.contains("Syuruk: 06:45"));
        assertTrue(message.contains("Zohor: 12:15"));
        assertTrue(message.contains("Asar: 15:30"));
        assertTrue(message.contains("Maghrib: 18:45"));
        assertTrue(message.contains("Isyak: 20:00"));
    }

    @Disabled("Unable to stop time in a clean manner for now.")
    @Test
    public void testScheduleAllNotifyPrayerTimingPeriods_shouldScheduleNotificationForAllPrayerTimes() {
        // Given
        LocalDate today = LocalDate.now();

        PrayerTiming prayerTiming = new PrayerTiming(
            today,
            "05:30",
            "06:45",
            "12:15",
            "15:30",
            "18:45",
            "20:00"
        );
        when(prayerTimingRepository.getByDate(today)).thenReturn(prayerTiming);

        // When
        prayerTimingService.scheduleAllNotifyPrayerTimingPeriods();

        // Then
        ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);

        verify(taskScheduler, times(6)).schedule(any(), instantCaptor.capture());

        var instants = instantCaptor.getAllValues();

        var times = instants.stream()
            .map(instant -> instant.atZone(ZoneId.systemDefault()).toLocalTime())
            .map(time -> time.format(TIME_FORMAT))
            .toList();
        assertTrue(times.contains("05:30"));
        assertTrue(times.contains("06:45"));
        assertTrue(times.contains("12:15"));
        assertTrue(times.contains("15:30"));
        assertTrue(times.contains("18:45"));
        assertTrue(times.contains("20:00"));
    }

    @Disabled("Unable to stop time in a clean manner for now.")
    @Test
    public void testScheduleAllPrePrayerTimingPeriods_shouldScheduleAllNotificationsBeforeAllPrayerTimes() {
        // Given
        LocalDate today = LocalDate.now();
        PrayerTiming prayerTiming = new PrayerTiming(
            today,
            "05:30",
            "06:45",
            "12:15",
            "15:30",
            "18:45",
            "20:00"
        );
        Duration durationBefore = Duration.ofMinutes(15);

        when(prayerTimingRepository.getByDate(today)).thenReturn(prayerTiming);
        when(prayerTimingConfiguration.getPreReminder()).thenReturn(preReminder);
        when(preReminder.getDurationBefore()).thenReturn(durationBefore);

        // When
        prayerTimingService.scheduleAllNotifyPrePrayerTimingPeriods();

        // Then
        ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);

        verify(taskScheduler, times(6)).schedule(any(), instantCaptor.capture());

        var instants = instantCaptor.getAllValues();

        var times = instants.stream()
            .map(instant -> instant.atZone(ZoneId.systemDefault()).toLocalTime())
            .map(time -> time.format(TIME_FORMAT))
            .toList();
        assertTrue(times.contains("05:15"));
        assertTrue(times.contains("06:30"));
        assertTrue(times.contains("12:00"));
        assertTrue(times.contains("15:15"));
        assertTrue(times.contains("18:30"));
        assertTrue(times.contains("19:45"));
    }

    @Test
    public void testNotifyAllPrayerTimingPeriods_withInvalidTimeFormat_shouldThrowException() {
        // Given
        LocalDate today = LocalDate.now();
        PrayerTiming prayerTiming = new PrayerTiming(
            today,
            "invalid",
            "06:45",
            "12:15",
            "15:30",
            "18:45",
            "20:00"
        );
        when(prayerTimingRepository.getByDate(today)).thenReturn(prayerTiming);

        // When & Then
        assertThrows(
            IllegalArgumentException.class, () -> {
                prayerTimingService.notifyAllPrayerTimingPeriods();
            }
        );
    }

    @Test
    public void testNotifyAllPrayerTimingPeriods_withInvalidHour_shouldThrowException() {
        // Given
        LocalDate today = LocalDate.now();
        PrayerTiming prayerTiming = new PrayerTiming(
            today,
            "24:30",
            "06:45",
            "12:15",
            "15:30",
            "18:45",
            "20:00"
        );
        when(prayerTimingRepository.getByDate(today)).thenReturn(prayerTiming);

        // When & Then
        assertThrows(
            IllegalArgumentException.class, () -> {
                prayerTimingService.notifyAllPrayerTimingPeriods();
            }
        );
    }

    @Test
    public void testNotifyAllPrayerTimingPeriods_withInvalidMinute_shouldThrowException() {
        // Given
        LocalDate today = LocalDate.now();
        PrayerTiming prayerTiming = new PrayerTiming(
            today,
            "05:60",
            "06:45",
            "12:15",
            "15:30",
            "18:45",
            "20:00"
        );
        when(prayerTimingRepository.getByDate(today)).thenReturn(prayerTiming);

        // When & Then
        assertThrows(
            IllegalArgumentException.class, () -> {
                prayerTimingService.notifyAllPrayerTimingPeriods();
            }
        );
    }
}
