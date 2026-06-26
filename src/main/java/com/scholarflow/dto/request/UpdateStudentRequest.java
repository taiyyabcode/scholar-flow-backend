package com.scholarflow.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * All fields optional — supports partial updates.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudentRequest {

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Size(max = 20)
    private String rollNumber;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    private String classId;

    private Integer year;

    @Pattern(regexp = "active|inactive|graduated", message = "Status must be: active, inactive, or graduated")
    private String status;

    private String dateOfBirth;

    private String address;

    @Valid
    private GuardianDto guardian;

    private String notes;
}
