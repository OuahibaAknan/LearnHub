package com.learnhub.controller;

import com.learnhub.dto.CourseRequest;
import com.learnhub.dto.CourseResponse;
import com.learnhub.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
@Tag(name = "Cours", description = "Gestion des cours et recherche")
public class CourseController {

    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lister tous les cours")
    @ApiResponse(responseCode = "200", description = "Liste des cours")
    public List<CourseResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des cours par titre et/ou catégorie")
    @ApiResponse(responseCode = "200", description = "Cours correspondants")
    public List<CourseResponse> search(@RequestParam(required = false) String title,
                                       @RequestParam(required = false) String category) {
        return service.search(title, category);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un cours par son identifiant")
    @ApiResponse(responseCode = "200", description = "Cours trouvé")
    @ApiResponse(responseCode = "404", description = "Cours introuvable")
    public CourseResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @Operation(summary = "Créer un cours")
    @ApiResponse(responseCode = "201", description = "Cours créé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Catégorie introuvable")
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un cours")
    @ApiResponse(responseCode = "200", description = "Cours modifié")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Cours ou catégorie introuvable")
    public CourseResponse update(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un cours")
    @ApiResponse(responseCode = "204", description = "Cours supprimé")
    @ApiResponse(responseCode = "404", description = "Cours introuvable")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}