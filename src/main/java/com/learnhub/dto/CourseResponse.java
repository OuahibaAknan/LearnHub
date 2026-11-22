package com.learnhub.dto;

import com.learnhub.entity.Course;
import com.learnhub.entity.Level;

public record CourseResponse(
        Long id,
        String title,
        String description,
        Integer durationHours,
        Level level,
        Long categoryId,
        String categoryName) {

    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDurationHours(),
                course.getLevel(),
                course.getCategory().getId(),
                course.getCategory().getName());
    }
}