package com.vlz.laborexchange_resumeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Schema(description = "Job seeker resume")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeDto {

    @Schema(description = "Resume ID", example = "9")
    private Long id;

    @Schema(description = "Owner user ID", example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "userId is required")
    private Long userId;

    @Schema(description = "Candidate first name", example = "Иван")
    @Size(max = 255)
    private String firstName;

    @Schema(description = "Candidate last name", example = "Иванов")
    @Size(max = 255)
    private String lastName;

    @Schema(description = "Resume title", example = "Java Developer CV", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "title must not be blank")
    @Size(max = 255, message = "title must be at most 255 characters")
    private String title;

    @Schema(description = "Summary / objective (max 5000 characters)", example = "5 years of Java backend development...")
    @Size(max = 5000, message = "summary must be at most 5000 characters")
    private String summary;

    @Schema(description = "Candidate's city/location", example = "Moscow")
    @Size(max = 255, message = "location must be at most 255 characters")
    private String location;

    @Schema(description = "Total years of work experience", example = "5")
    @PositiveOrZero(message = "experienceYears must be zero or positive")
    private Integer experienceYears;

    @Schema(description = "Contact email for employers", example = "ivan@example.com")
    @Email(message = "contactEmail must be a valid email address")
    @Size(max = 320, message = "contactEmail must be at most 320 characters")
    private String contactEmail;

    @Schema(description = "Whether the resume is visible in public search", example = "true")
    private Boolean isPublished;

    @Schema(description = "Contact phone number", example = "+79161234567")
    @Pattern(regexp = "^[+]?[- 0-9()]{7,20}$", message = "contactPhone must be a valid phone number")
    @Size(max = 20, message = "contactPhone must be at most 20 characters")
    private String contactPhone;

    @Schema(description = "Set of skill IDs attached to this resume")
    private Set<Long> skillIds;

    @Schema(description = "Expected monthly salary in rubles", example = "180000")
    @PositiveOrZero(message = "expectedSalary must be zero or positive")
    private Integer expectedSalary;

    @Schema(description = "Portfolio URL (GitHub, Behance, personal site, etc.)", example = "https://github.com/johndoe")
    @Size(max = 512, message = "portfolioUrl must be at most 512 characters")
    private String portfolioUrl;

    @Schema(description = "Languages spoken (e.g. ['English B2', 'Russian C1'])")
    private Set<String> languages;

    @Schema(description = "Professional certifications (e.g. ['AWS Solutions Architect', 'Google Cloud Professional'])")
    private Set<String> certifications;
}
