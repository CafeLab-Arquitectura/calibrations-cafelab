package com.acme.calibrationscafelab.calibrations.interfaces.rest.resources;

import java.time.LocalDate;

public record GrindCalibrationResource(
        Long id,
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
