package com.learnhub.service;

import com.learnhub.entity.Category;
import com.learnhub.exception.DuplicateResourceException;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> findAll() {
        return repository.findAll();
    }

    public Category findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec id : " + id));
    }

    public Category create(Category category) {
        if (repository.existsByName(category.getName())) {
            throw new DuplicateResourceException("Catégorie déjà existante : " + category.getName());
        }
        return repository.save(category);
    }

    public Category update(Long id, Category updated) {
        Category existing = findById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        return repository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}