package com.learnhub.repository;

import com.learnhub.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByLearnerIdAndCourseId(Long learnerId, Long courseId);
    List<Enrollment> findByLearnerId(Long learnerId);
    boolean existsByLearnerIdAndCourseId(Long learnerId, Long courseId);
}