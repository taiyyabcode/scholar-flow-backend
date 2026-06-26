package com.scholarflow.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

/**
 * Response for GET /api/v1/students/list
 * Matches the API documentation format exactly.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentListResponse {

    private List<StudentItem> data;
    private Meta meta;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentItem {
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
        private GuardianInfo guardian;
        private String notes;
        private String createdAt;
        private String updatedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GuardianInfo {
        private String name;
        private String phone;
        private String email;
        private String relation;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Meta {
        private long total;
        private int page;
        private int limit;
        private int totalPages;
    }
}
