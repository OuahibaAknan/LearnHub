package com.learnhub.service;

import com.learnhub.dto.EnrollmentProgressRequest;
import com.learnhub.dto.EnrollmentRequest;
import com.learnhub.dto.EnrollmentResponse;
import com.learnhub.entity.Course;
import com.learnhub.entity.Enrollment;
import com.learnhub.entity.Learner;
import com.learnhub.exception.DuplicateResourceException;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.repository.CourseRepository;
import com.learnhub.repository.EnrollmentRepository;
import com.learnhub.repository.LearnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LearnerRepository learnerRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             LearnerRepository learnerRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.learnerRepository = learnerRepository;
        this.courseRepository = courseRepository;
    }

    public EnrollmentResponse create(EnrollmentRequest request) {
        if (enrollmentRepository.existsByLearnerIdAndCourseId(request.learnerId(), request.courseId())) {
            throw new DuplicateResourceException("Cet élève est déjà inscrit à ce cours");
        }
        Learner learner = learnerRepository.findById(request.learnerId())
            .orElseThrow(() -> new ResourceNotFoundException("Learner non trouvé avec l'id : " + request.learnerId()));
        Course course = courseRepository.findById(request.courseId())
            .orElseThrow(() -> new ResourceNotFoundException("Course non trouvé avec l'id : " + request.courseId()));

        Enrollment enrollment = new Enrollment();
        enrollment.setLearner(learner);
        enrollment.setCourse(course);
        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    @Transactional(readOnly = true)
    public EnrollmentResponse findById(Long id) {
        Enrollment e = enrollmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Enrollment non trouvé avec l'id : " + id));
        return EnrollmentResponse.from(e);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findAll() {
        return enrollmentRepository.findAll().stream().map(EnrollmentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findByLearnerId(Long learnerId) {
        if (!learnerRepository.existsById(learnerId)) {
            throw new ResourceNotFoundException("Learner non trouvé avec l'id : " + learnerId);
        }
        return enrollmentRepository.findByLearnerId(learnerId).stream().map(EnrollmentResponse::from).toList();
    }

    public EnrollmentResponse updateProgress(Long id, EnrollmentProgressRequest request) {
        Enrollment e = enrollmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Enrollment non trouvé avec l'id : " + id));
        e.setProgress(request.progress());
        if (request.status() != null) {
            e.setStatus(request.status());
        }
        return EnrollmentResponse.from(enrollmentRepository.save(e));
    }

    public void delete(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Enrollment non trouvé avec l'id : " + id);
        }
        enrollmentRepository.deleteById(id);
    }
}