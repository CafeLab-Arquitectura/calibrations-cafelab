package com.acme.calibrationscafelab.calibrations.application.internal.queryservices;

import com.acme.calibrationscafelab.calibrations.domain.model.aggregates.GrindCalibration;
import com.acme.calibrationscafelab.calibrations.domain.model.queries.GetGrindCalibrationByIdForUserQuery;
import com.acme.calibrationscafelab.calibrations.domain.model.queries.GetGrindCalibrationsByUserIdQuery;
import com.acme.calibrationscafelab.calibrations.domain.services.GrindCalibrationQueryService;
import com.acme.calibrationscafelab.calibrations.infrastructure.persistence.jpa.repositories.GrindCalibrationRepository;
import com.acme.calibrationscafelab.shared.domain.model.valueobjects.UserId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GrindCalibrationQueryServiceImpl implements GrindCalibrationQueryService {
    private final GrindCalibrationRepository repository;

    public GrindCalibrationQueryServiceImpl(GrindCalibrationRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<GrindCalibration> handle(GetGrindCalibrationsByUserIdQuery query) {
        return repository.findByUserIdOrderByCreatedAtDesc(new UserId(query.userId()));
    }

    @Override
    public Optional<GrindCalibration> handle(GetGrindCalibrationByIdForUserQuery query) {
        return repository.findByIdAndUserId(query.calibrationId(), new UserId(query.userId()));
    }
}
