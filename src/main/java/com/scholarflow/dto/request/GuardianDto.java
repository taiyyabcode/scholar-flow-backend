package com.scholarflow.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuardianDto {

    @NotBlank(message = "Guardian name is required")
    private String name;

    private String phone;

    @Email(message = "Invalid guardian email")
    private String email;

    private String relation;
}
