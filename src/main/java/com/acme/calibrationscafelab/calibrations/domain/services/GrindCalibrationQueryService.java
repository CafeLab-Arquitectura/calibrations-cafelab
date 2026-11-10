package com.acme.calibrationscafelab.calibrations.domain.services;

import com.acme.calibrationscafelab.calibrations.domain.model.aggregates.GrindCalibration;
import com.acme.calibrationscafelab.calibrations.domain.model.queries.GetGrindCalibrationByIdForUserQuery;
import com.acme.calibrationscafelab.calibrations.domain.model.queries.GetGrindCalibrationsByUserIdQuery;

import java.util.List;
import java.util.Optional;

public interface GrindCalibrationQueryService {

    List<GrindCalibration> handle(GetGrindCalibrationsByUserIdQuery query);

    Optional<GrindCalibration> handle(GetGrindCalibrationByIdForUserQuery query);
}
