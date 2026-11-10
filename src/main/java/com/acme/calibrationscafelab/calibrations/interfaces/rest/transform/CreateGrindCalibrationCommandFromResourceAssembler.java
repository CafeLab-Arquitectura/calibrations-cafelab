package com.acme.calibrationscafelab.calibrations.interfaces.rest.transform;

import com.acme.calibrationscafelab.calibrations.domain.model.commands.CreateGrindCalibrationCommand;
import com.acme.calibrationscafelab.calibrations.interfaces.rest.resources.CreateGrindCalibrationResource;

public class CreateGrindCalibrationCommandFromResourceAssembler {

    public static CreateGrindCalibrationCommand toCommand(CreateGrindCalibrationResource resource) {
        return new CreateGrindCalibrationCommand(
                resource.userId(),
                resource.name(),
                resource.method(),
                resource.equipment(),
                resource.grindNumber(),
                resource.aperture(),
                resource.cupVolume(),
                resource.finalVolume(),
                resource.calibrationDate(),
                resource.comments(),
                resource.notes(),
                resource.sampleImage());
    }
}
