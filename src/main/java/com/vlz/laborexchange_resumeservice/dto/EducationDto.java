package com.vlz.laborexchange_resumeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Education record attached to a resume")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EducationDto {

    @Schema(description = "Education record ID", example = "3")
    private Long id;

    @Schema(description = "Educational institution name", example = "Moscow State University", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "institution must not be blank")
    @Size(max = 255, message = "institution must be at most 255 characters")
    private String institution;

    @Schema(description = "Degree obtained", example = "Bachelor", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "degree must not be blank")
    @Size(max = 255, message = "degree must be at most 255 characters")
    private String degree;

    @Schema(description = "Field of study", example = "Computer Science", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "fieldOfStudy must not be blank")
    @Size(max = 255, message = "fieldOfStudy must be at most 255 characters")
    private String fieldOfStudy;

    @Schema(description = "Start year", example = "2017", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "startDate (year) is required")
    @Min(1900)
    @Max(2100)
    private Integer startYear;

    @Schema(description = "End year (null if currently studying)", example = "2021")
    @Min(1900)
    @Max(2100)
    private Integer endYear;

    @Schema(description = "Resume this education belongs to", example = "9", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "resumeId is required")
    private Long resumeId;
}
