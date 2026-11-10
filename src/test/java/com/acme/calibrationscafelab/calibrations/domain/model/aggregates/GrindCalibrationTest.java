package com.acme.calibrationscafelab.calibrations.domain.model.aggregates;

import com.acme.calibrationscafelab.calibrations.domain.model.commands.CreateGrindCalibrationCommand;
import com.acme.calibrationscafelab.calibrations.domain.model.commands.UpdateGrindCalibrationCommand;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GrindCalibrationTest {

    @Test
    void createCalibrationFromCommandTest() {
        var command = new CreateGrindCalibrationCommand(
                9L, " Espresso base ", " pour-over ", " v60 ", " 14 ",
                1.2, 200.0, 180.0, LocalDate.of(2026, 5, 5), "nota", "obs", "img");

        var calibration = new GrindCalibration(command);

        assertEquals(9L, calibration.getUserId().userId());
        assertEquals("Espresso base", calibration.getName());
        assertEquals("pour-over", calibration.getMethod());
        assertEquals("14", calibration.getGrindNumber());
    }

    @Test
    void normalizeBlankTextToNullOnCreateTest() {
        var command = new CreateGrindCalibrationCommand(
                9L, "Name", "m", "e", "10", 1.0, 100.0, 90.0, LocalDate.of(2026, 1, 1), "   ", "", "  ");

        var calibration = new GrindCalibration(command);

        assertNull(calibration.getComments());
        assertNull(calibration.getNotes());
        assertNull(calibration.getSampleImage());
    }

    @Test
    void applyUpdateAndTrimValuesTest() {
        var calibration = new GrindCalibration(new CreateGrindCalibrationCommand(
                1L, "Name", "m", "e", "10", 1.0, 100.0, 90.0, LocalDate.of(2026, 1, 1), "a", "b", "c"));
        var update = new UpdateGrindCalibrationCommand(
                11L, 1L, " Nuevo ", " espresso ", " grinder ", " 20 ",
                2.0, 250.0, 200.0, LocalDate.of(2026, 2, 2), "  ", "nota", "");

        calibration.applyUpdate(update);

        assertEquals("Nuevo", calibration.getName());
        assertEquals("espresso", calibration.getMethod());
        assertEquals("20", calibration.getGrindNumber());
        assertNull(calibration.getComments());
        assertNull(calibration.getSampleImage());
    }

    @Test
    void keepNumericFieldsAfterUpdateTest() {
        var calibration = new GrindCalibration(new CreateGrindCalibrationCommand(
                1L, "Name", "m", "e", "10", 1.0, 100.0, 90.0, LocalDate.of(2026, 1, 1), null, null, null));

        calibration.applyUpdate(new UpdateGrindCalibrationCommand(
                1L, 1L, "N", "M", "E", "11", 1.5, 210.0, 190.0, LocalDate.of(2026, 3, 3), null, null, null));

        assertEquals(1.5, calibration.getAperture());
        assertEquals(210.0, calibration.getCupVolume());
        assertEquals(190.0, calibration.getFinalVolume());
        assertEquals(LocalDate.of(2026, 3, 3), calibration.getCalibrationDate());
    }
}
