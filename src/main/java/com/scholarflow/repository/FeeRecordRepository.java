package com.scholarflow.repository;

import com.scholarflow.entity.FeeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeeRecordRepository extends JpaRepository<FeeRecord, Long> {
    Optional<FeeRecord> findByStudentStudentId(String studentId);
}
