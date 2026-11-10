package com.acme.calibrationscafelab.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CalibrationsFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAndListCalibrationEndToEndTest() throws Exception {
        long userId = 9201L;

        mockMvc.perform(post("/api/v1/calibrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": %d,
                                  "name": "Calibracion funcional",
                                  "method": "v60",
                                  "equipment": "Comandante",
                                  "grindNumber": "15",
                                  "aperture": 1.1,
                                  "cupVolume": 200.0,
                                  "finalVolume": 180.0,
                                  "calibrationDate": "2026-05-05",
                                  "comments": "ok",
                                  "notes": "ok",
                                  "sampleImage": null
                                }
                                """.formatted(userId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value((int) userId));

        mockMvc.perform(get("/api/v1/calibrations/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Calibracion funcional"));
    }

    @Test
    void updateSampleImageAndListCalibrationsTest() throws Exception {
        long userId = 9202L;
        String sampleImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUg";

        var createResult =
                mockMvc.perform(post("/api/v1/calibrations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "userId": %d,
                                          "name": "Calibracion sin imagen",
                                          "method": "v60",
                                          "equipment": "Comandante",
                                          "grindNumber": "15",
                                          "aperture": 1.1,
                                          "cupVolume": 200.0,
                                          "finalVolume": 180.0,
                                          "calibrationDate": "2026-05-05"
                                        }
                                        """.formatted(userId)))
                        .andExpect(status().isCreated())
                        .andReturn();

        long calibrationId =
                objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/v1/calibrations/user/{userId}/{calibrationId}", userId, calibrationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Calibracion con imagen",
                                  "method": "v60",
                                  "equipment": "Comandante",
                                  "grindNumber": "15",
                                  "aperture": 1.1,
                                  "cupVolume": 200.0,
                                  "finalVolume": 180.0,
                                  "calibrationDate": "2026-05-05",
                                  "sampleImage": "%s"
                                }
                                """.formatted(sampleImage)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sampleImage").value(sampleImage));

        mockMvc.perform(get("/api/v1/calibrations/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value((int) calibrationId))
                .andExpect(jsonPath("$[0].sampleImage").value(sampleImage));
    }

    @Test
    void returnNotFoundForMissingCalibrationTest() throws Exception {
        mockMvc.perform(get("/api/v1/calibrations/user/{userId}/{calibrationId}", 9201L, 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}
