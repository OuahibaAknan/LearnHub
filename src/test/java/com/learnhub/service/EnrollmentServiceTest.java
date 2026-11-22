package com.learnhub.service;

import com.learnhub.dto.EnrollmentProgressRequest;
import com.learnhub.dto.EnrollmentRequest;
import com.learnhub.dto.EnrollmentResponse;
import com.learnhub.entity.*;
import com.learnhub.exception.DuplicateResourceException;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.repository.CourseRepository;
import com.learnhub.repository.EnrollmentRepository;
import com.learnhub.repository.LearnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock EnrollmentRepository enrollmentRepository;
    @Mock LearnerRepository learnerRepository;
    @Mock CourseRepository courseRepository;
    @InjectMocks EnrollmentService service;

    private Learner learner() {
        Learner l = new Learner();
        l.setId(1L);
        l.setFirstName("Sara");
        l.setLastName("Alami");
        l.setEmail("sara@test.com");
        return l;
    }

    private Course course() {
        Course c = new Course();
        c.setId(1L);
        c.setTitle("Java");
        return c;
    }

    private Enrollment enrollment() {
        Enrollment e = new Enrollment();
        e.setId(1L);
        e.setLearner(learner());
        e.setCourse(course());
        e.setProgress(0);
        e.setStatus(Status.EN_COURS);
        return e;
    }

    @Test
    void create_ok() {
        when(enrollmentRepository.existsByLearnerIdAndCourseId(1L, 1L)).thenReturn(false);
        when(learnerRepository.findById(1L)).thenReturn(Optional.of(learner()));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course()));
        when(enrollmentRepository.save(any())).thenReturn(enrollment());

        EnrollmentResponse res = service.create(new EnrollmentRequest(1L, 1L));

        assertEquals(1L, res.id());
        assertEquals("Sara Alami", res.learnerName());
        assertEquals("Java", res.courseTitle());
    }

    @Test
    void create_duplicate_409() {
        when(enrollmentRepository.existsByLearnerIdAndCourseId(1L, 1L)).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.create(new EnrollmentRequest(1L, 1L)));
    }

    @Test
    void findById_404() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void updateProgress_ok() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment()));
        when(enrollmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        EnrollmentResponse res = service.updateProgress(1L, new EnrollmentProgressRequest(50, Status.EN_COURS));

        assertEquals(50, res.progress());
    }
}