package com.acme.calibrationscafelab.calibrations.interfaces.rest;

import com.acme.calibrationscafelab.calibrations.domain.exceptions.GrindCalibrationNotFoundException;
import com.acme.calibrationscafelab.shared.interfaces.rest.resources.MessageResource;
import com.acme.calibrationscafelab.shared.interfaces.rest.support.CafeLabScopedExceptionHandlerSupport;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.acme.calibrationscafelab.calibrations.interfaces.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CalibrationsExceptionHandler extends CafeLabScopedExceptionHandlerSupport {

    @ExceptionHandler(GrindCalibrationNotFoundException.class)
    public ResponseEntity<MessageResource> handleNotFound(GrindCalibrationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResource(ex.getMessage()));
    }
}
