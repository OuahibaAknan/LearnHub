package com.learnhub.service;

import com.learnhub.dto.LearnerRequest;
import com.learnhub.dto.LearnerResponse;
import com.learnhub.entity.Learner;
import com.learnhub.exception.DuplicateResourceException;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.repository.LearnerRepository;
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
class LearnerServiceTest {

    @Mock
    private LearnerRepository repository;

    @InjectMocks
    private LearnerService service;

    private Learner buildLearner(String firstName, String lastName, String email) {
        Learner learner = new Learner();
        learner.setFirstName(firstName);
        learner.setLastName(lastName);
        learner.setEmail(email);
        return learner;
    }

    @Test
    void findAll_retourneTousLesApprenants() {
        when(repository.findAll()).thenReturn(List.of(
                buildLearner("Sara", "Alami", "sara@test.com"),
                buildLearner("Youssef", "Benani", "youssef@test.com")));

        List<LearnerResponse> result = service.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findById_apprenantExistant_leRetourne() {
        when(repository.findById(1L)).thenReturn(Optional.of(buildLearner("Sara", "Alami", "sara@test.com")));

        LearnerResponse result = service.findById(1L);

        assertEquals("Sara", result.firstName());
        assertEquals("sara@test.com", result.email());
    }

    @Test
    void findById_apprenantIntrouvable_lanceResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void create_emailLibre_enregistreEtNormaliseLEmail() {
        LearnerRequest request = new LearnerRequest("Sara", "Alami", "  Sara@Test.com ");
        when(repository.existsByEmail("sara@test.com")).thenReturn(false);
        when(repository.save(any(Learner.class))).thenAnswer(inv -> inv.getArgument(0));

        LearnerResponse result = service.create(request);

        assertEquals("sara@test.com", result.email());
        verify(repository).save(any(Learner.class));
    }

    @Test
    void create_emailDejaUtilise_lanceDuplicateResourceException() {
        LearnerRequest request = new LearnerRequest("Sara", "Alami", "sara@test.com");
        when(repository.existsByEmail("sara@test.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(request));
        verify(repository, never()).save(any(Learner.class));
    }

    @Test
    void update_apprenantExistant_metAJourLesChamps() {
        Learner existing = buildLearner("Ancien", "Nom", "ancien@test.com");
        LearnerRequest request = new LearnerRequest("Nouveau", "Prenom", "nouveau@test.com");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByEmail("nouveau@test.com")).thenReturn(false);
        when(repository.save(any(Learner.class))).thenAnswer(inv -> inv.getArgument(0));

        LearnerResponse result = service.update(1L, request);

        assertEquals("Nouveau", result.firstName());
        assertEquals("nouveau@test.com", result.email());
    }

    @Test
    void update_memeEmail_neVerifiePasLesDoublons() {
        Learner existing = buildLearner("Sara", "Alami", "sara@test.com");
        LearnerRequest request = new LearnerRequest("Sara", "Benani", "Sara@Test.com");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Learner.class))).thenAnswer(inv -> inv.getArgument(0));

        LearnerResponse result = service.update(1L, request);

        assertEquals("Benani", result.lastName());
        verify(repository, never()).existsByEmail(any());
    }

    @Test
    void update_emailDejaPris_lanceDuplicateResourceException() {
        Learner existing = buildLearner("Sara", "Alami", "sara@test.com");
        LearnerRequest request = new LearnerRequest("Sara", "Alami", "pris@test.com");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByEmail("pris@test.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.update(1L, request));
        verify(repository, never()).save(any(Learner.class));
    }

    @Test
    void update_apprenantIntrouvable_lanceResourceNotFoundException() {
        LearnerRequest request = new LearnerRequest("Sara", "Alami", "sara@test.com");
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, request));
        verify(repository, never()).save(any(Learner.class));
    }

    @Test
    void delete_apprenantExistant_supprimeLApprenant() {
        when(repository.findById(1L)).thenReturn(Optional.of(buildLearner("Sara", "Alami", "sara@test.com")));

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_apprenantIntrouvable_lanceResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
        verify(repository, never()).deleteById(any());
    }
}