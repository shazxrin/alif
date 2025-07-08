package io.github.shazxrin.alif.prayer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Table(name = "prayer_timings")
@Entity
public class PrayerTiming {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String subuh;

    @Column(nullable = false)
    private String syuruk;

    @Column(nullable = false)
    private String zohor;

    @Column(nullable = false)
    private String asar;

    @Column(nullable = false)
    private String maghrib;

    @Column(nullable = false)
    private String isyak;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

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
        this.date = date;
        this.subuh = subuh;
        this.syuruk = syuruk;
        this.zohor = zohor;
        this.asar = asar;
        this.maghrib = maghrib;
        this.isyak = isyak;
    }

    public PrayerTiming(
        Long id,
        LocalDate date,
        String subuh,
        String syuruk,
        String zohor,
        String asar,
        String maghrib,
        String isyak,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.date = date;
        this.subuh = subuh;
        this.syuruk = syuruk;
        this.zohor = zohor;
        this.asar = asar;
        this.maghrib = maghrib;
        this.isyak = isyak;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
