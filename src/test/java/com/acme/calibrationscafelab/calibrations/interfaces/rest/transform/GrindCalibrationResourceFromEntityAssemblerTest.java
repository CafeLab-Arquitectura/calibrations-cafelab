package com.acme.calibrationscafelab.calibrations.interfaces.rest.transform;

import com.acme.calibrationscafelab.calibrations.domain.model.aggregates.GrindCalibration;
import com.acme.calibrationscafelab.calibrations.domain.model.commands.CreateGrindCalibrationCommand;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GrindCalibrationResourceFromEntityAssemblerTest {

    @Test
    void mapsSampleImageAsPlainStringTest() {
        String sampleImage = "data:image/png;base64,iVBORw0KGgo=";
        var calibration = new GrindCalibration(new CreateGrindCalibrationCommand(
                3L, "Cal", "v60", "Molino", "12", 1.0, 200.0, 180.0,
                LocalDate.of(2026, 5, 5), null, null, sampleImage));

        var resource = GrindCalibrationResourceFromEntityAssembler.toResourceFromEntity(calibration);

        assertEquals(sampleImage, resource.sampleImage());
    }

    @Test
    void keepsInvalidSampleImageAsStringWithoutParsingTest() {
        String invalid = "not-a-valid-image-payload";
        var calibration = new GrindCalibration(new CreateGrindCalibrationCommand(
                3L, "Cal", "v60", "Molino", "12", 1.0, 200.0, 180.0,
                LocalDate.of(2026, 5, 5), null, null, invalid));

        assertEquals(invalid, GrindCalibrationResourceFromEntityAssembler.safeSampleImage(calibration));
    }

    @Test
    void blankSampleImageBecomesNullTest() {
        var calibration = new GrindCalibration(new CreateGrindCalibrationCommand(
                3L, "Cal", "v60", "Molino", "12", 1.0, 200.0, 180.0,
                LocalDate.of(2026, 5, 5), null, null, "   "));

        assertNull(GrindCalibrationResourceFromEntityAssembler.safeSampleImage(calibration));
    }
}
