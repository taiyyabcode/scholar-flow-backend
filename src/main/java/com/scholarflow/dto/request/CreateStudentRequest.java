package com.scholarflow.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudentRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "Roll number is required")
    @Size(max = 20)
    private String rollNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    @NotBlank(message = "Class ID is required")
    private String classId;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "active|inactive|graduated", message = "Status must be: active, inactive, or graduated")
    private String status;

    private String dateOfBirth;

    private String address;

    @Valid
    private GuardianDto guardian;

    private String notes;
}
