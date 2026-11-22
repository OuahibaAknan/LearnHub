package com.learnhub.dto;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
    @NotNull(message = "L'identifiant de l'élève est obligatoire")
    Long learnerId,

    @NotNull(message = "L'identifiant du cours est obligatoire")
    Long courseId
) {}