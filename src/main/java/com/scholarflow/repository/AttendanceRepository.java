package com.scholarflow.repository;

import com.scholarflow.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("""
            SELECT a FROM Attendance a
            JOIN FETCH a.classEntity
            WHERE a.student.studentId = :studentId
            ORDER BY a.date DESC
            """)
    List<Attendance> findByStudentStudentIdOrderByDateDesc(@Param("studentId") String studentId);
}
