package com.learnhub.dto;

import com.learnhub.entity.Level;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseRequest(
        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 200, message = "Le titre ne doit pas dépasser 200 caractères")
        String title,

        @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
        String description,

        @Min(value = 1, message = "La durée doit être d'au moins 1 heure")
        Integer durationHours,

        Level level,

        @NotNull(message = "La catégorie est obligatoire")
        Long categoryId) {
}