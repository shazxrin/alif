package io.github.shazxrin.alif.prayer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Table(name = "prayer_timings")
@Entity
public class PrayerTiming {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 5)
    private String subuh;

    @Column(nullable = false, length = 5)
    private String syuruk;

    @Column(nullable = false, length = 5)
    private String zohor;

    @Column(nullable = false, length = 5)
    private String asar;

    @Column(nullable = false, length = 5)
    private String maghrib;

    @Column(nullable = false, length = 5)
    private String isyak;

    public PrayerTiming() { }

    public PrayerTiming(
        LocalDate date,
        String subuh,
        String syuruk,
        String zohor,
        String asar,
        String maghrib,
        String isyak
    ) {
        this(null, date, subuh, syuruk, zohor, asar, maghrib, isyak);
    }

    public PrayerTiming(
        Long id,
        LocalDate date,
        String subuh,
        String syuruk,
        String zohor,
        String asar,
        String maghrib,
        String isyak
    ) {
        this.id = id;
        this.date = date;
        this.subuh = subuh;
        this.syuruk = syuruk;
        this.zohor = zohor;
        this.asar = asar;
        this.maghrib = maghrib;
        this.isyak = isyak;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSubuh() {
        return subuh;
    }

    public void setSubuh(String subuh) {
        this.subuh = subuh;
    }

    public String getSyuruk() {
        return syuruk;
    }

    public void setSyuruk(String syuruk) {
        this.syuruk = syuruk;
    }

    public String getZohor() {
        return zohor;
    }

    public void setZohor(String zohor) {
        this.zohor = zohor;
    }

    public String getAsar() {
        return asar;
    }

    public void setAsar(String asar) {
        this.asar = asar;
    }

    public String getMaghrib() {
        return maghrib;
    }

    public void setMaghrib(String maghrib) {
        this.maghrib = maghrib;
    }

    public String getIsyak() {
        return isyak;
    }

    public void setIsyak(String isyak) {
        this.isyak = isyak;
    }
}
