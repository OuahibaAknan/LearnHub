package com.learnhub.service;

import com.learnhub.dto.CategoryRequest;
import com.learnhub.dto.CategoryResponse;
import com.learnhub.entity.Category;
import com.learnhub.exception.DuplicateResourceException;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.repository.CategoryRepository;
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
class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryService service;

    private Category buildCategory(String name, String description) {
        return new Category(name, description);
    }

    @Test
    void findAll_retourneToutesLesCategories() {
        when(repository.findAll()).thenReturn(List.of(
                buildCategory("Informatique", "Cours IT"),
                buildCategory("Langues", "Cours de langues")));

        List<CategoryResponse> result = service.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findById_categorieExistante_laRetourne() {
        when(repository.findById(1L)).thenReturn(Optional.of(buildCategory("Informatique", "Cours IT")));

        CategoryResponse result = service.findById(1L);

        assertEquals("Informatique", result.name());
    }

    @Test
    void findById_categorieIntrouvable_lanceResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void create_nomLibre_enregistreLaCategorie() {
        CategoryRequest request = new CategoryRequest("Informatique", "Cours IT");
        when(repository.existsByName("Informatique")).thenReturn(false);
        when(repository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        CategoryResponse result = service.create(request);

        assertEquals("Informatique", result.name());
        verify(repository).save(any(Category.class));
    }

    @Test
    void create_nomDejaExistant_lanceDuplicateResourceException() {
        CategoryRequest request = new CategoryRequest("Informatique", "Cours IT");
        when(repository.existsByName("Informatique")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(request));
        verify(repository, never()).save(any(Category.class));
    }

    @Test
    void update_categorieExistante_metAJourLesChamps() {
        Category existing = buildCategory("Ancien nom", "Ancienne description");
        CategoryRequest request = new CategoryRequest("Nouveau nom", "Nouvelle description");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByName("Nouveau nom")).thenReturn(false);
        when(repository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        CategoryResponse result = service.update(1L, request);

        assertEquals("Nouveau nom", result.name());
        assertEquals("Nouvelle description", result.description());
    }

    @Test
    void update_nomDejaUtilise_lanceDuplicateResourceException() {
        Category existing = buildCategory("Ancien nom", "Description");
        CategoryRequest request = new CategoryRequest("Nom pris", "Description");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByName("Nom pris")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.update(1L, request));
        verify(repository, never()).save(any(Category.class));
    }

    @Test
    void update_categorieIntrouvable_lanceResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(99L, new CategoryRequest("Nom", "Description")));
        verify(repository, never()).save(any(Category.class));
    }

    @Test
    void delete_categorieExistante_supprimeLaCategorie() {
        when(repository.findById(1L)).thenReturn(Optional.of(buildCategory("Informatique", "Cours IT")));

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_categorieIntrouvable_lanceResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
        verify(repository, never()).deleteById(any());
    }
}