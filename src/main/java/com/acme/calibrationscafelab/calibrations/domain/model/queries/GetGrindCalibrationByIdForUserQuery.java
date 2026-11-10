package com.acme.calibrationscafelab.calibrations.domain.model.queries;

public record GetGrindCalibrationByIdForUserQuery(Long calibrationId, Long userId) {
}
