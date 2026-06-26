package com.scholarflow.mapper;

import com.scholarflow.dto.response.StudentListResponse;
import com.scholarflow.dto.response.StudentResponse;
import com.scholarflow.entity.Student;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Maps Student entities to response DTOs.
 */
@Component
public class StudentMapper {

    public StudentResponse toStudentResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getStudentId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .rollNumber(student.getRollNumber())
                .email(student.getEmail())
                .phone(student.getPhone())
                .classId(student.getClassEntity().getClassId())
                .className(student.getClassEntity().getClassName())
                .year(student.getYear())
                .status(student.getStatus().name().toLowerCase())
                .dateOfBirth(student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : null)
                .address(student.getAddress())
                .guardian(mapGuardian(student))
                .notes(student.getNotes())
                .createdAt(student.getCreatedAt() != null ? student.getCreatedAt().toString() : null)
                .updatedAt(student.getUpdatedAt() != null ? student.getUpdatedAt().toString() : null)
                .build();
    }

    public StudentListResponse.StudentItem toStudentListItem(Student student) {
        return StudentListResponse.StudentItem.builder()
                .id(student.getStudentId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .rollNumber(student.getRollNumber())
                .email(student.getEmail())
                .phone(student.getPhone())
                .classId(student.getClassEntity().getClassId())
                .className(student.getClassEntity().getClassName())
                .year(student.getYear())
                .status(student.getStatus().name().toLowerCase())
                .dateOfBirth(student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : null)
                .address(student.getAddress())
                .guardian(mapGuardian(student))
                .notes(student.getNotes())
                .createdAt(student.getCreatedAt() != null ? student.getCreatedAt().toString() : null)
                .updatedAt(student.getUpdatedAt() != null ? student.getUpdatedAt().toString() : null)
                .build();
    }

    private StudentListResponse.GuardianInfo mapGuardian(Student student) {
        if (student.getGuardian() == null) {
            return null;
        }
        return StudentListResponse.GuardianInfo.builder()
                .name(student.getGuardian().getName())
                .phone(student.getGuardian().getPhone())
                .email(student.getGuardian().getEmail())
                .relation(student.getGuardian().getRelation())
                .build();
    }
}
