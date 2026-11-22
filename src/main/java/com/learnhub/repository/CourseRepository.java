package com.learnhub.repository;

import com.learnhub.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTitleContainingIgnoreCase(String title);

    List<Course> findByCategoryNameIgnoreCase(String categoryName);

    List<Course> findByTitleContainingIgnoreCaseAndCategoryNameIgnoreCase(String title, String categoryName);
}