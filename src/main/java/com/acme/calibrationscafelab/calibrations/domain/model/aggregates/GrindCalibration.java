package com.acme.calibrationscafelab.calibrations.domain.model.aggregates;

import com.acme.calibrationscafelab.calibrations.domain.model.commands.CreateGrindCalibrationCommand;
import com.acme.calibrationscafelab.calibrations.domain.model.commands.UpdateGrindCalibrationCommand;
import com.acme.calibrationscafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.acme.calibrationscafelab.shared.domain.model.valueobjects.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Registro de calibración de molienda: sin vínculos a lotes ni perfiles de tueste; ámbito por {@code userId}.
 */
@Getter
@Setter
@Entity
public class GrindCalibration extends AuditableAbstractAggregateRoot<GrindCalibration> {

    @Embedded
    @Column(name = "user_id")
    private UserId userId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 120)
    private String method;

    @Column(nullable = false, length = 120)
    private String equipment;

    @Column(name = "grind_number", nullable = false, length = 64)
    private String grindNumber;

    @Column(nullable = false)
    private Double aperture;

    @Column(name = "cup_volume", nullable = false)
    private Double cupVolume;

    @Column(name = "final_volume", nullable = false)
    private Double finalVolume;

    @Column(name = "calibration_date", nullable = false)
    private LocalDate calibrationDate;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "sample_image", columnDefinition = "TEXT")
    private String sampleImage;

    public GrindCalibration() {}

    public GrindCalibration(CreateGrindCalibrationCommand c) {
        this.userId = new UserId(c.userId());
        this.name = c.name().trim();
        this.method = c.method().trim();
        this.equipment = c.equipment().trim();
        this.grindNumber = c.grindNumber().trim();
        this.aperture = c.aperture();
        this.cupVolume = c.cupVolume();
        this.finalVolume = c.finalVolume();
        this.calibrationDate = c.calibrationDate();
        this.comments = blankToNull(c.comments());
        this.notes = blankToNull(c.notes());
        this.sampleImage = blankToNull(c.sampleImage());
    }

    public void applyUpdate(UpdateGrindCalibrationCommand c) {
        this.name = c.name().trim();
        this.method = c.method().trim();
        this.equipment = c.equipment().trim();
        this.grindNumber = c.grindNumber().trim();
        this.aperture = c.aperture();
        this.cupVolume = c.cupVolume();
        this.finalVolume = c.finalVolume();
        this.calibrationDate = c.calibrationDate();
        this.comments = blankToNull(c.comments());
        this.notes = blankToNull(c.notes());
        this.sampleImage = blankToNull(c.sampleImage());
    }

    private static String blankToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.trim();
    }
}
