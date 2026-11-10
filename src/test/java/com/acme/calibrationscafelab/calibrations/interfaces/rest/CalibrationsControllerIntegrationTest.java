package com.acme.calibrationscafelab.calibrations.interfaces.rest;

import com.acme.calibrationscafelab.calibrations.domain.model.aggregates.GrindCalibration;
import com.acme.calibrationscafelab.calibrations.domain.model.commands.CreateGrindCalibrationCommand;
import com.acme.calibrationscafelab.calibrations.domain.model.commands.UpdateGrindCalibrationCommand;
import com.acme.calibrationscafelab.calibrations.domain.model.queries.GetGrindCalibrationsByUserIdQuery;
import com.acme.calibrationscafelab.calibrations.domain.services.GrindCalibrationCommandService;
import com.acme.calibrationscafelab.calibrations.domain.services.GrindCalibrationQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CalibrationsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GrindCalibrationCommandService commandService;

    @MockBean
    private GrindCalibrationQueryService queryService;

    @Test
    void createCalibrationSuccessfullyTest() throws Exception {
        var calibration = new GrindCalibration(new CreateGrindCalibrationCommand(
                1L, "Base", "v60", "Comandante", "15", 1.0, 200.0, 180.0, LocalDate.of(2026, 5, 5), "ok", "ok", null));
        when(commandService.handle(any(CreateGrindCalibrationCommand.class))).thenReturn(Optional.of(calibration));

        mockMvc.perform(post("/api/v1/calibrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 1,
                                  "name": "Base",
                                  "method": "v60",
                                  "equipment": "Comandante",
                                  "grindNumber": "15",
                                  "aperture": 1.0,
                                  "cupVolume": 200.0,
                                  "finalVolume": 180.0,
                                  "calibrationDate": "2026-05-05",
                                  "comments": "ok",
                                  "notes": "ok",
                                  "sampleImage": null
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Base"));
    }

    @Test
    void listCalibrationsByUserIdTest() throws Exception {
        var calibration = new GrindCalibration(new CreateGrindCalibrationCommand(
                1L, "Base", "v60", "Comandante", "15", 1.0, 200.0, 180.0, LocalDate.of(2026, 5, 5), "ok", "ok", null));
        when(queryService.handle(any(GetGrindCalibrationsByUserIdQuery.class))).thenReturn(List.of(calibration));

        mockMvc.perform(get("/api/v1/calibrations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].method").value("v60"));
    }

    @Test
    void returnBadRequestOnUpdateValidationErrorTest() throws Exception {
        when(commandService.handle(any(UpdateGrindCalibrationCommand.class)))
                .thenThrow(new IllegalArgumentException("invalid payload"));

        mockMvc.perform(put("/api/v1/calibrations/user/1/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Nueva",
                                  "method": "v60",
                                  "equipment": "Comandante",
                                  "grindNumber": "16",
                                  "aperture": 1.1,
                                  "cupVolume": 210.0,
                                  "finalVolume": 190.0,
                                  "calibrationDate": "2026-05-06",
                                  "comments": null,
                                  "notes": null,
                                  "sampleImage": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("invalid payload"));
    }
}
