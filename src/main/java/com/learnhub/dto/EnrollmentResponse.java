package com.learnhub.dto;

import com.learnhub.entity.Enrollment;
import com.learnhub.entity.Status;
import java.time.LocalDate;

public record EnrollmentResponse(
    Long id,
    Long learnerId,
    String learnerName,
    Long courseId,
    String courseTitle,
    LocalDate enrollmentDate,
    Integer progress,
    Status status
) {
    public static EnrollmentResponse from(Enrollment e) {
        return new EnrollmentResponse(
            e.getId(),
            e.getLearner().getId(),
            e.getLearner().getFirstName() + " " + e.getLearner().getLastName(),
            e.getCourse().getId(),
            e.getCourse().getTitle(),
            e.getEnrollmentDate(),
            e.getProgress(),
            e.getStatus()
        );
    }
}