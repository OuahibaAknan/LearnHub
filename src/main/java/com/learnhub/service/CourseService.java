package com.learnhub.service;

import com.learnhub.dto.CourseRequest;
import com.learnhub.dto.CourseResponse;
import com.learnhub.entity.Category;
import com.learnhub.entity.Course;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.repository.CategoryRepository;
import com.learnhub.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream()
            .map(CourseResponse::from)
            .toList();
    }

    public CourseResponse findById(Long id) {
        return CourseResponse.from(getCourse(id));
    }

    public List<CourseResponse> search(String title, String categoryName) {
        boolean hasTitle = title != null && !title.isBlank();
        boolean hasCategory = categoryName != null && !categoryName.isBlank();

        List<Course> courses;
        if (hasTitle && hasCategory) {
            courses = courseRepository.findByTitleContainingIgnoreCaseAndCategoryNameIgnoreCase(
                title.trim(), categoryName.trim());
        } else if (hasTitle) {
            courses = courseRepository.findByTitleContainingIgnoreCase(title.trim());
        } else if (hasCategory) {
            courses = courseRepository.findByCategoryNameIgnoreCase(categoryName.trim());
        } else {
            courses = courseRepository.findAll();
        }
        return courses.stream().map(CourseResponse::from).toList();
    }

    @Transactional
    public CourseResponse create(CourseRequest request) {
        Category category = getCategory(request.categoryId());
        Course course = new Course();
        apply(course, request, category);
        return CourseResponse.from(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = getCourse(id);
        Category category = getCategory(request.categoryId());
        apply(course, request, category);
        return CourseResponse.from(courseRepository.save(course));
    }

    @Transactional
    public void delete(Long id) {
        getCourse(id);
        courseRepository.deleteById(id);
    }

    private void apply(Course course, CourseRequest request, Category category) {
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setDurationHours(request.durationHours());
        course.setLevel(request.level());
        course.setCategory(category);
    }

    private Course getCourse(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable avec id : " + id));
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec id : " + id));
    }
}