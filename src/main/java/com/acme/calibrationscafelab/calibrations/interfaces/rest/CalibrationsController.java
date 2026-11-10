package com.acme.calibrationscafelab.calibrations.interfaces.rest;

import com.acme.calibrationscafelab.calibrations.domain.exceptions.GrindCalibrationNotFoundException;
import com.acme.calibrationscafelab.calibrations.domain.model.queries.GetGrindCalibrationByIdForUserQuery;
import com.acme.calibrationscafelab.calibrations.domain.model.queries.GetGrindCalibrationsByUserIdQuery;
import com.acme.calibrationscafelab.calibrations.domain.services.GrindCalibrationCommandService;
import com.acme.calibrationscafelab.calibrations.domain.services.GrindCalibrationQueryService;
import com.acme.calibrationscafelab.calibrations.interfaces.rest.resources.CreateGrindCalibrationResource;
import com.acme.calibrationscafelab.calibrations.interfaces.rest.resources.UpdateGrindCalibrationResource;
import com.acme.calibrationscafelab.calibrations.interfaces.rest.transform.CreateGrindCalibrationCommandFromResourceAssembler;
import com.acme.calibrationscafelab.calibrations.interfaces.rest.transform.GrindCalibrationResourceFromEntityAssembler;
import com.acme.calibrationscafelab.calibrations.interfaces.rest.transform.UpdateGrindCalibrationCommandFromResourceAssembler;
import com.acme.calibrationscafelab.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/calibrations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Grind calibrations", description = "Registros de calibración de molienda por perfil")
public class CalibrationsController {
    private final GrindCalibrationCommandService commandService;
    private final GrindCalibrationQueryService queryService;

    public CalibrationsController(
            GrindCalibrationCommandService commandService,
            GrindCalibrationQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear calibración (userId en el cuerpo)")
    public ResponseEntity<?> create(@RequestBody CreateGrindCalibrationResource resource) {
        try {
            var command = CreateGrindCalibrationCommandFromResourceAssembler.toCommand(resource);
            var created = commandService.handle(command);
            if (created.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResource("No se pudo crear la calibración"));
            }
            return new ResponseEntity<>(
                    GrindCalibrationResourceFromEntityAssembler.toResourceFromEntity(created.get()),
                    HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new MessageResource(ex.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar calibraciones de un usuario")
    public ResponseEntity<?> listForUser(@PathVariable Long userId) {
        var list = queryService.handle(new GetGrindCalibrationsByUserIdQuery(userId));
        var resources =
                list.stream()
                        .map(GrindCalibrationResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/user/{userId}/{calibrationId}")
    @Operation(summary = "Obtener calibración por id (del usuario indicado)")
    public ResponseEntity<?> getById(@PathVariable Long userId, @PathVariable Long calibrationId) {
        var found =
                queryService.handle(
                        new GetGrindCalibrationByIdForUserQuery(calibrationId, userId));
        if (found.isEmpty()) {
            throw new GrindCalibrationNotFoundException(calibrationId);
        }
        return ResponseEntity.ok(
                GrindCalibrationResourceFromEntityAssembler.toResourceFromEntity(found.get()));
    }

    @PutMapping(value = "/user/{userId}/{calibrationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar calibración (del usuario indicado)")
    public ResponseEntity<?> update(
            @PathVariable Long userId,
            @PathVariable Long calibrationId,
            @RequestBody UpdateGrindCalibrationResource resource) {
        try {
            var command =
                    UpdateGrindCalibrationCommandFromResourceAssembler.toCommand(
                            calibrationId, userId, resource);
            var updated = commandService.handle(command);
            if (updated.isEmpty()) {
                throw new GrindCalibrationNotFoundException(calibrationId);
            }
            return ResponseEntity.ok(
                    GrindCalibrationResourceFromEntityAssembler.toResourceFromEntity(updated.get()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new MessageResource(ex.getMessage()));
        }
    }
}
