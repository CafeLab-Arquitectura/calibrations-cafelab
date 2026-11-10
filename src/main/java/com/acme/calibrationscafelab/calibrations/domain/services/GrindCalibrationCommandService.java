package com.acme.calibrationscafelab.calibrations.domain.services;

import com.acme.calibrationscafelab.calibrations.domain.model.aggregates.GrindCalibration;
import com.acme.calibrationscafelab.calibrations.domain.model.commands.CreateGrindCalibrationCommand;
import com.acme.calibrationscafelab.calibrations.domain.model.commands.UpdateGrindCalibrationCommand;

import java.util.Optional;

public interface GrindCalibrationCommandService {

    Optional<GrindCalibration> handle(CreateGrindCalibrationCommand command);

    Optional<GrindCalibration> handle(UpdateGrindCalibrationCommand command);
}
