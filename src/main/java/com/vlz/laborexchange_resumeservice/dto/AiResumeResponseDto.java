package com.vlz.laborexchange_resumeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "AI-generated resume draft — not yet persisted")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResumeResponseDto {

    @Schema(description = "Suggested resume title")
    private String title;

    @Schema(description = "Professional summary / about section")
    private String summary;

    @Schema(description = "Location")
    private String location;

    @Schema(description = "Years of experience")
    private Integer experienceYears;

    @Schema(description = "AI-suggested skill names — frontend resolves to IDs via findOrCreate")
    private List<String> suggestedSkillNames;

    @Schema(description = "AI-generated work experience entries")
    private List<AiWorkExperienceItem> workExperiences;

    @Schema(description = "AI-generated education entries")
    private List<AiEducationItem> educations;

    @Schema(description = "Single work experience entry in AI response")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiWorkExperienceItem {
        private String companyName;
        private String position;
        private String description;
        private Integer startYear;
        private Integer startMonth;
        private Integer endYear;
        private Integer endMonth;
        private Boolean isCurrent;
    }

    @Schema(description = "Single education entry in AI response")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiEducationItem {
        private String institution;
        private String degree;
        private String fieldOfStudy;
        private Integer startYear;
        private Integer endYear;
    }
}
