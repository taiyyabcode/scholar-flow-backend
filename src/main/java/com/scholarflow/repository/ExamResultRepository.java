package com.scholarflow.repository;

import com.scholarflow.entity.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    @Query("""
            SELECT er FROM ExamResult er
            JOIN FETCH er.exam
            JOIN FETCH er.classEntity
            LEFT JOIN FETCH er.marks
            WHERE er.student.studentId = :studentId
            """)
    List<ExamResult> findByStudentStudentId(@Param("studentId") String studentId);
}
