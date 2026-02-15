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

    @NotNull(message = "startDate (year) is required")
    @Min(1900)
    @Max(2100)
    private Integer startYear;

    @Min(1900)
    @Max(2100)
    private Integer endYear;

    @NotNull(message = "resumeId is required")
    private Long resumeId;
}
