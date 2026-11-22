package com.learnhub.service;

import com.learnhub.dto.LearnerRequest;
import com.learnhub.dto.LearnerResponse;
import com.learnhub.entity.Learner;
import com.learnhub.exception.DuplicateResourceException;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.repository.LearnerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LearnerService {

    private final LearnerRepository repository;

    public LearnerService(LearnerRepository repository) {
        this.repository = repository;
    }

    public List<LearnerResponse> findAll() {
        return repository.findAll().stream()
            .map(LearnerResponse::from)
            .toList();
    }

    public LearnerResponse findById(Long id) {
        return LearnerResponse.from(getEntity(id));
    }

    public LearnerResponse create(LearnerRequest request) {
        String email = normalize(request.email());
        if (repository.existsByEmail(email)) {
            throw new DuplicateResourceException("Un apprenant existe déjà avec cet email");
        }
        Learner learner = new Learner();
        apply(learner, request, email);
        return LearnerResponse.from(repository.save(learner));
    }

    public LearnerResponse update(Long id, LearnerRequest request) {
        Learner learner = getEntity(id);
        String email = normalize(request.email());
        if (!learner.getEmail().equals(email) && repository.existsByEmail(email)) {
            throw new DuplicateResourceException("Un apprenant existe déjà avec cet email");
        }
        apply(learner, request, email);
        return LearnerResponse.from(repository.save(learner));
    }

    public void delete(Long id) {
        getEntity(id);
        repository.deleteById(id);
    }

    private void apply(Learner learner, LearnerRequest request, String email) {
        learner.setFirstName(request.firstName().trim());
        learner.setLastName(request.lastName().trim());
        learner.setEmail(email);
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }

    private Learner getEntity(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Apprenant introuvable avec id : " + id));
    }
}