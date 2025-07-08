package io.github.shazxrin.alif.prayer.repository;

import io.github.shazxrin.alif.prayer.model.PrayerTiming;
import java.time.LocalDate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrayerTimingRepository extends CrudRepository<PrayerTiming, Long> {
    PrayerTiming getByDate(LocalDate date);
}
