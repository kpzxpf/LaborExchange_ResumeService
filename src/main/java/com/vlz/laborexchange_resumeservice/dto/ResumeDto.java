package com.vlz.laborexchange_resumeservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeDto {
    private Long id;

    @NotNull(message = "userId is required")
    private Long userId;

    @NotBlank(message = "title must not be blank")
    @Size(max = 255, message = "title must be at most 255 characters")
    private String title;

    @Size(max = 5000, message = "summary must be at most 5000 characters")
    private String summary;

    @PositiveOrZero(message = "experienceYears must be zero or positive")
    private Integer experienceYears;

    @Email(message = "contactEmail must be a valid email address")
    @Size(max = 320, message = "contactEmail must be at most 320 characters")
    private String contactEmail;

    @Pattern(regexp = "^[+]?[- 0-9()]{7,20}$", message = "contactPhone must be a valid phone number")
    @Size(max = 20, message = "contactPhone must be at most 20 characters")
    private String contactPhone;
}
