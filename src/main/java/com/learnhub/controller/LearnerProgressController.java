package com.learnhub.controller;

import com.learnhub.dto.EnrollmentResponse;
import com.learnhub.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/learners")
public class LearnerProgressController {

    private final EnrollmentService service;

    public LearnerProgressController(EnrollmentService service) {
        this.service = service;
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<List<EnrollmentResponse>> getProgress(@PathVariable Long id) {
        return ResponseEntity.ok(service.findByLearnerId(id));
    }
}