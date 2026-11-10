package com.acme.calibrationscafelab.calibrations.interfaces.rest.transform;

import com.acme.calibrationscafelab.calibrations.domain.model.aggregates.GrindCalibration;
import com.acme.calibrationscafelab.calibrations.interfaces.rest.resources.GrindCalibrationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrindCalibrationResourceFromEntityAssembler {

    private static final Logger log = LoggerFactory.getLogger(GrindCalibrationResourceFromEntityAssembler.class);

    public static GrindCalibrationResource toResourceFromEntity(GrindCalibration e) {
        return new GrindCalibrationResource(
                e.getId(),
                e.getUserId().userId(),
                e.getName(),
                e.getMethod(),
                e.getEquipment(),
                e.getGrindNumber(),
                e.getAperture(),
                e.getCupVolume(),
                e.getFinalVolume(),
                e.getCalibrationDate(),
                e.getComments(),
                e.getNotes(),
                safeSampleImage(e));
    }

    static String safeSampleImage(GrindCalibration calibration) {
        try {
            String raw = calibration.getSampleImage();
            if (raw == null || raw.isBlank()) {
                return null;
            }
            return raw.trim();
        } catch (RuntimeException ex) {
            log.warn(
                    "No se pudo leer sampleImage de la calibracion id={}: {}",
                    calibration.getId(),
                    ex.getMessage());
            return null;
        }
    }
}
