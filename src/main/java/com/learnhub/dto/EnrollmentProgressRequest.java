package com.learnhub.dto;

import com.learnhub.entity.Status;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EnrollmentProgressRequest(
    @NotNull(message = "La progression est obligatoire")
    @Min(value = 0, message = "La progression doit être entre 0 et 100")
    @Max(value = 100, message = "La progression doit être entre 0 et 100")
    Integer progress,

    Status status
) {}