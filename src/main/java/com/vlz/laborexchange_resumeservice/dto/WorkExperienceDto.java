package com.vlz.laborexchange_resumeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Schema(description = "Work experience entry")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperienceDto {

    @Schema(description = "Entry ID", example = "1")
    private Long id;

    @Schema(description = "Resume ID", example = "9", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "resumeId is required")
    private Long resumeId;

    @Schema(description = "Company name", example = "Яндекс", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "companyName must not be blank")
    @Size(max = 255)
    private String companyName;

    @Schema(description = "Job position", example = "Senior Java Developer", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "position must not be blank")
    @Size(max = 255)
    private String position;

    @Schema(description = "Job responsibilities and achievements")
    @Size(max = 2000)
    private String description;

    @Schema(description = "Start year", example = "2020", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "startYear is required")
    @Min(1950) @Max(2100)
    private Integer startYear;

    @Schema(description = "Start month (1-12)", example = "3")
    @Min(1) @Max(12)
    private Integer startMonth;

    @Schema(description = "End year (null if current)", example = "2023")
    @Min(1950) @Max(2100)
    private Integer endYear;

    @Schema(description = "End month (1-12)", example = "11")
    @Min(1) @Max(12)
    private Integer endMonth;

    @Schema(description = "Whether this is the current job", example = "false")
    private Boolean isCurrent;
}
