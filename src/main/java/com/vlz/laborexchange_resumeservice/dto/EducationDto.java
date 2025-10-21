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
public class EducationDto {
    private Long id;

    @NotBlank(message = "institution must not be blank")
    @Size(max = 255, message = "institution must be at most 255 characters")
    private String institution;

    @NotBlank(message = "degree must not be blank")
    @Size(max = 255, message = "degree must be at most 255 characters")
    private String degree;

    @NotBlank(message = "fieldOfStudy must not be blank")
    @Size(max = 255, message = "fieldOfStudy must be at most 255 characters")
    private String fieldOfStudy;

    @NotBlank(message = "startDate (year) is required")
    @Pattern(regexp = "^\\d{4}$", message = "startDate must be a 4-digit year")
    private Integer startDate;

    @Pattern(regexp = "^\\d{4}$", message = "endDate must be a 4-digit year")
    private Integer endDate;

    @NotNull(message = "resumeId is required")
    private Long resumeId;
}
