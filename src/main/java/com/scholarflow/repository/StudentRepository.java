package com.scholarflow.repository;

import com.scholarflow.entity.Student;
import com.scholarflow.enums.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentId(String studentId);

    boolean existsByEmail(String email);

    boolean existsByStudentId(String studentId);

    /**
     * Paginated search with optional filters.
     * Filters: search (firstName, lastName, rollNumber, email), classId, status, year.
     */
    @Query(value = """
            SELECT s FROM Student s
            JOIN FETCH s.classEntity c
            LEFT JOIN FETCH s.guardian g
            WHERE (:search IS NULL OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:classId IS NULL OR c.classId = :classId)
            AND (:status IS NULL OR s.status = :status)
            AND (:year IS NULL OR s.year = :year)
            """,
            countQuery = """
            SELECT COUNT(s) FROM Student s
            JOIN s.classEntity c
            WHERE (:search IS NULL OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:classId IS NULL OR c.classId = :classId)
            AND (:status IS NULL OR s.status = :status)
            AND (:year IS NULL OR s.year = :year)
            """)
    Page<Student> findAllWithFilters(
            @Param("search") String search,
            @Param("classId") String classId,
            @Param("status") StudentStatus status,
            @Param("year") Integer year,
            Pageable pageable
    );
}
