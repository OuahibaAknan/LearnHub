package com.learnhub.dto;

import com.learnhub.entity.Learner;
import java.time.LocalDate;

public record LearnerResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDate enrollmentDate) {

    public static LearnerResponse from(Learner learner) {
        return new LearnerResponse(
                learner.getId(),
                learner.getFirstName(),
                learner.getLastName(),
                learner.getEmail(),
                learner.getEnrollmentDate());
    }
}