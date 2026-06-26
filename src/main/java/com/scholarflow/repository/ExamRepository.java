package com.scholarflow.repository;

import com.scholarflow.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findByExamId(String examId);
}
