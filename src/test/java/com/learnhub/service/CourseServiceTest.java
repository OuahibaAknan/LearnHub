package com.learnhub.service;

import com.learnhub.dto.CourseRequest;
import com.learnhub.dto.CourseResponse;
import com.learnhub.entity.Category;
import com.learnhub.entity.Course;
import com.learnhub.entity.Level;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.repository.CategoryRepository;
import com.learnhub.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CourseService service;

    private Category buildCategory(String name) {
        return new Category(name, "Description " + name);
    }

    private Course buildCourse(String title, Category category) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription("Description " + title);
        course.setDurationHours(10);
        course.setLevel(Level.DEBUTANT);
        course.setCategory(category);
        return course;
    }

    @Test
    void findAll_retourneTousLesCours() {
        Category category = buildCategory("Informatique");
        when(courseRepository.findAll()).thenReturn(List.of(
                buildCourse("Java", category), buildCourse("Python", category)));

        List<CourseResponse> result = service.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findById_coursExistant_leRetourne() {
        Course course = buildCourse("Java", buildCategory("Informatique"));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseResponse result = service.findById(1L);

        assertEquals("Java", result.title());
        assertEquals("Informatique", result.categoryName());
    }

    @Test
    void findById_coursIntrouvable_lanceResourceNotFoundException() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void create_categorieExistante_enregistreLeCours() {
        Category category = buildCategory("Informatique");
        CourseRequest request = new CourseRequest("Java", "Bases de Java", 20, Level.DEBUTANT, 1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        CourseResponse result = service.create(request);

        assertEquals("Java", result.title());
        assertEquals("Informatique", result.categoryName());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void create_categorieIntrouvable_lanceResourceNotFoundException() {
        CourseRequest request = new CourseRequest("Java", "Bases de Java", 20, Level.DEBUTANT, 99L);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void update_coursExistant_metAJourLesChamps() {
        Course existing = buildCourse("Ancien titre", buildCategory("Informatique"));
        Category newCategory = buildCategory("Langues");
        CourseRequest request = new CourseRequest("Nouveau titre", "Nouvelle description", 30, Level.AVANCE, 2L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        CourseResponse result = service.update(1L, request);

        assertEquals("Nouveau titre", result.title());
        assertEquals(Level.AVANCE, result.level());
        assertEquals("Langues", result.categoryName());
    }

    @Test
    void update_coursIntrouvable_lanceResourceNotFoundException() {
        CourseRequest request = new CourseRequest("Titre", "Description", 10, Level.DEBUTANT, 1L);
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, request));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void update_categorieIntrouvable_lanceResourceNotFoundException() {
        Course existing = buildCourse("Java", buildCategory("Informatique"));
        CourseRequest request = new CourseRequest("Java", "Description", 10, Level.DEBUTANT, 99L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, request));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void delete_coursExistant_supprimeLeCours() {
        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(buildCourse("Java", buildCategory("Informatique"))));

        service.delete(1L);

        verify(courseRepository).deleteById(1L);
    }

    @Test
    void delete_coursIntrouvable_lanceResourceNotFoundException() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
        verify(courseRepository, never()).deleteById(any());
    }

    @Test
    void search_titreEtCategorie_utiliseLesDeuxCriteres() {
        Course course = buildCourse("Java", buildCategory("Informatique"));
        when(courseRepository.findByTitleContainingIgnoreCaseAndCategoryNameIgnoreCase("java", "Informatique"))
                .thenReturn(List.of(course));

        List<CourseResponse> result = service.search("java", "Informatique");

        assertEquals(1, result.size());
    }

    @Test
    void search_titreSeulement_filtreParTitre() {
        Course course = buildCourse("Java", buildCategory("Informatique"));
        when(courseRepository.findByTitleContainingIgnoreCase("java")).thenReturn(List.of(course));

        List<CourseResponse> result = service.search("java", null);

        assertEquals("Java", result.get(0).title());
    }

    @Test
    void search_categorieSeulement_filtreParCategorie() {
        Course course = buildCourse("Java", buildCategory("Informatique"));
        when(courseRepository.findByCategoryNameIgnoreCase("Informatique")).thenReturn(List.of(course));

        List<CourseResponse> result = service.search(null, "Informatique");

        assertEquals(1, result.size());
    }

    @Test
    void search_sansCritere_retourneTousLesCours() {
        Category category = buildCategory("Informatique");
        when(courseRepository.findAll()).thenReturn(List.of(
                buildCourse("Java", category), buildCourse("Python", category)));

        List<CourseResponse> result = service.search("  ", null);

        assertEquals(2, result.size());
    }
}