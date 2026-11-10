package com.acme.calibrationscafelab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CalibrationsCafeLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalibrationsCafeLabApplication.class, args);
    }

}
