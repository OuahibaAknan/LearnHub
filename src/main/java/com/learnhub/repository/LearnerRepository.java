package com.learnhub.repository;

import com.learnhub.entity.Learner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerRepository extends JpaRepository<Learner, Long> {

    boolean existsByEmail(String email);
}