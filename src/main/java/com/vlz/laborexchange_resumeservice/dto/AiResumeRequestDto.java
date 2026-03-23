package com.vlz.laborexchange_resumeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Input for AI resume generation")
@Data
public class AiResumeRequestDto {

    @Schema(description = "Target job title", example = "Senior Java Backend Developer", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "jobTitle must not be blank")
    @Size(max = 255)
    private String jobTitle;

    @Schema(description = "Free-form description: experience, achievements, responsibilities", example = "5 лет разработки микросервисов на Spring Boot...")
    @NotBlank(message = "description must not be blank")
    @Size(max = 3000)
    private String description;

    @Schema(description = "Total years of work experience", example = "5")
    @Min(0) @Max(60)
    private Integer yearsOfExperience;

    @Schema(description = "City / location", example = "Москва")
    @Size(max = 255)
    private String location;
}
