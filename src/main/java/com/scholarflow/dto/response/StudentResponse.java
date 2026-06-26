package com.scholarflow.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Response for POST /api/v1/students and PUT /api/v1/students/{id}
 * Wrapped in {"data": {...}}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String rollNumber;
    private String email;
    private String phone;
    private String classId;
    private String className;
    private Integer year;
    private String status;
    private String dateOfBirth;
    private String address;
    private StudentListResponse.GuardianInfo guardian;
    private String notes;
    private String createdAt;
    private String updatedAt;
}
