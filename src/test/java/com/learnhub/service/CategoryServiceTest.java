package com.learnhub.service;

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
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    @Test
    void findAll_retourneToutesLesCategories() {
        List<Category> categories = List.of(
                buildCategory("Informatique", "Cours IT"),
                buildCategory("Langues", "Cours de langues"));
        when(repository.findAll()).thenReturn(categories);

        List<Category> result = service.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findById_categorieExistante_laRetourne() {
        Category category = buildCategory("Informatique", "Cours IT");
        when(repository.findById(1L)).thenReturn(Optional.of(category));

        Category result = service.findById(1L);

        assertEquals("Informatique", result.getName());
    }

    @Test
    void findById_categorieIntrouvable_lanceResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void create_nomLibre_enregistreLaCategorie() {
        Category category = buildCategory("Informatique", "Cours IT");
        when(repository.existsByName("Informatique")).thenReturn(false);
        when(repository.save(any(Category.class))).thenReturn(category);

        Category result = service.create(category);

        assertEquals("Informatique", result.getName());
        verify(repository).save(category);
    }

    @Test
    void create_nomDejaExistant_lanceDuplicateResourceException() {
        Category category = buildCategory("Informatique", "Cours IT");
        when(repository.existsByName("Informatique")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(category));
        verify(repository, never()).save(any(Category.class));
    }

    @Test
    void update_categorieExistante_metAJourLesChamps() {
        Category existing = buildCategory("Ancien nom", "Ancienne description");
        Category updated = buildCategory("Nouveau nom", "Nouvelle description");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category result = service.update(1L, updated);

        assertEquals("Nouveau nom", result.getName());
        assertEquals("Nouvelle description", result.getDescription());
    }

    @Test
    void update_categorieIntrouvable_lanceResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(99L, buildCategory("Nom", "Description")));
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