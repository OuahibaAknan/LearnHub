package com.learnhub.service;

import com.learnhub.dto.CategoryRequest;
import com.learnhub.dto.CategoryResponse;
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

    public List<CategoryResponse> findAll() {
        return repository.findAll().stream()
            .map(CategoryResponse::from)
            .toList();
    }

    public CategoryResponse findById(Long id) {
        return CategoryResponse.from(getEntity(id));
    }

    public CategoryResponse create(CategoryRequest request) {
        if (repository.existsByName(request.name())) {
            throw new DuplicateResourceException("Catégorie déjà existante : " + request.name());
        }
        Category category = new Category(request.name(), request.description());
        return CategoryResponse.from(repository.save(category));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category existing = getEntity(id);
        if (!existing.getName().equals(request.name()) && repository.existsByName(request.name())) {
            throw new DuplicateResourceException("Catégorie déjà existante : " + request.name());
        }
        existing.setName(request.name());
        existing.setDescription(request.description());
        return CategoryResponse.from(repository.save(existing));
    }

    public void delete(Long id) {
        getEntity(id);
        repository.deleteById(id);
    }

    private Category getEntity(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec id : " + id));
    }
}