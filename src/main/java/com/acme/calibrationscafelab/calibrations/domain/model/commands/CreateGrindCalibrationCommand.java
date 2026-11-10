package com.acme.calibrationscafelab.calibrations.domain.model.commands;

import java.time.LocalDate;

public record CreateGrindCalibrationCommand(
        Long userId,
        String name,
        String method,
        String equipment,
        String grindNumber,
        Double aperture,
        Double cupVolume,
        Double finalVolume,
        LocalDate calibrationDate,
        String comments,
        String notes,
        String sampleImage
) {
}
