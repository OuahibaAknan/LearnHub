package com.learnhub.controller;

import com.learnhub.dto.LearnerRequest;
import com.learnhub.dto.LearnerResponse;
import com.learnhub.service.LearnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/learners")
@CrossOrigin(origins = "*")
@Tag(name = "Apprenants", description = "Gestion des apprenants")
public class LearnerController {

    private final LearnerService service;

    public LearnerController(LearnerService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lister tous les apprenants")
    @ApiResponse(responseCode = "200", description = "Liste des apprenants")
    public List<LearnerResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un apprenant par son identifiant")
    @ApiResponse(responseCode = "200", description = "Apprenant trouvé")
    @ApiResponse(responseCode = "404", description = "Apprenant introuvable")
    public LearnerResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @Operation(summary = "Créer un apprenant")
    @ApiResponse(responseCode = "201", description = "Apprenant créé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    public ResponseEntity<LearnerResponse> create(@Valid @RequestBody LearnerRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un apprenant")
    @ApiResponse(responseCode = "200", description = "Apprenant modifié")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Apprenant introuvable")
    @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    public LearnerResponse update(@PathVariable Long id, @Valid @RequestBody LearnerRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un apprenant")
    @ApiResponse(responseCode = "204", description = "Apprenant supprimé")
    @ApiResponse(responseCode = "404", description = "Apprenant introuvable")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}